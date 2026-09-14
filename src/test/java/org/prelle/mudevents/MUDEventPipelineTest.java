package org.prelle.mudevents;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.junit.jupiter.api.Test;
import org.prelle.mudevents.MUDEventPipelineTest.SimpleEvent;

import lombok.AllArgsConstructor;

class MUDEventPipelineTest {

    static class SimpleEvent implements PipeEvent {
        private final String name;

        public SimpleEvent(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    @AllArgsConstructor
    static class DummyProcessor implements MUDEventProcessor {
    	private Function<PipeEvent,List<PipeEvent>> func;
		@Override
		public List<PipeEvent> onReceiveFromRemote(PipeEvent event) { return func.apply(event); }
		@Override
		public List<PipeEvent> onSendToRemote(PipeEvent event) { return null; }
    	
    }

    @Test
    void testForwardEvent() {
        List<String> received = new ArrayList<>();

        MUDEventPipeline pipeline = new MUDEventPipeline("test")
                .then(new DummyProcessor(e -> List.of(e) ))
                .then(new DummyProcessor(e -> {
                    received.add(((SimpleEvent) e).getName());
                    return List.of(e);
                }));

        pipeline.publish(new SimpleEvent("E1"));

        assertEquals(List.of("E1"), received);
    }

    @Test
    void testConsumeEvent() {
        List<String> received = new ArrayList<>();

        MUDEventPipeline pipeline = new MUDEventPipeline("test")
                .then(new DummyProcessor(e -> null)) // Consumes event
                .then(new DummyProcessor(e -> {
                    received.add(((SimpleEvent) e).getName());
                    return List.of(e);
                }));

        pipeline.publish(new SimpleEvent("E1"));

        assertTrue(received.isEmpty());
    }

    @Test
    void testSubstituteSingleEvent() {
        List<String> received = new ArrayList<>();

        MUDEventPipeline pipeline = new MUDEventPipeline("test")
                .then(new DummyProcessor(e -> List.of(new SimpleEvent("Substituted_" + ((SimpleEvent) e).getName()))))
                .then(new DummyProcessor(e -> {
                    received.add(((SimpleEvent) e).getName());
                    return List.of(e);
                }));

        pipeline.publish(new SimpleEvent("E1"));

        assertEquals(List.of("Substituted_E1"), received);
    }

    @Test
    void testSubstituteMultipleEventsDoesNotReinjectToEarlierProcessors() {
        List<String> p0Seen = new ArrayList<>();
        List<String> p1Seen = new ArrayList<>();
        List<String> p2Seen = new ArrayList<>();

        MUDEventPipeline pipeline = new MUDEventPipeline("test")
                .then(new DummyProcessor(e -> {
                    p0Seen.add(((SimpleEvent) e).getName());
                    return List.of(e);
                }))
                .then(new DummyProcessor(e -> {
                    p1Seen.add(((SimpleEvent) e).getName());
                    // Substitute 1 event into 3 events
                    return List.of(
                            new SimpleEvent("A"),
                            new SimpleEvent("B"),
                            new SimpleEvent("C")
                    );
                }))
                .then(new DummyProcessor(e -> {
                    p2Seen.add(((SimpleEvent) e).getName());
                    return List.of(e);
                }));

        pipeline.publish(new SimpleEvent("Original"));

        // P0 should only see "Original" once - never the substituted events A, B, C!
        assertEquals(List.of("Original"), p0Seen);

        // P1 should only see "Original" once - never A, B, C!
        assertEquals(List.of("Original"), p1Seen);

        // P2 (downstream) should see all 3 substituted events in order
        assertEquals(List.of("A", "B", "C"), p2Seen);
    }

    @Test
    void testMultiStageSplittingAndOrdering() {
        List<String> results = new ArrayList<>();

        MUDEventPipeline pipeline = new MUDEventPipeline("test")
                .then(new DummyProcessor(e -> List.of(new SimpleEvent("1"), new SimpleEvent("2"))))
                .then(new DummyProcessor(e -> {
                    String name = ((SimpleEvent) e).getName();
                    return List.of(new SimpleEvent(name + ".A"), new SimpleEvent(name + ".B"));
                }))
                .then(new DummyProcessor(e -> {
                    results.add(((SimpleEvent) e).getName());
                    return List.of(e);
                }));

        pipeline.publish(new SimpleEvent("Start"));

        // Depth-first stream ordering: 1.A, 1.B, 2.A, 2.B
        assertEquals(List.of("1.A", "1.B", "2.A", "2.B"), results);
    }

    @Test
    void testDeepPipelineWithHundredsOfProcessors() {
        int processorCount = 500;
        MUDEventPipeline pipeline = new MUDEventPipeline("test");

        // 500 processors each incrementing a counter in the event name
        for (int i = 0; i < processorCount; i++) {
            pipeline.then(new DummyProcessor(e -> {
                int count = Integer.parseInt(((SimpleEvent) e).getName());
                return List.of(new SimpleEvent(String.valueOf(count + 1)));
            }));
        }

        List<String> output = new ArrayList<>();
        pipeline.then(new DummyProcessor(e -> {
            output.add(((SimpleEvent) e).getName());
            return List.of(e);
        }));

        pipeline.publish(new SimpleEvent("0"));

        assertEquals(List.of("500"), output);
    }

    @Test
    void testBidirectionalFlow() {
        List<String> inOrder = new ArrayList<>();
        List<String> outOrder = new ArrayList<>();

        class BidirectionalProcessor implements MUDEventProcessor {
            private final String name;
            public BidirectionalProcessor(String name) { this.name = name; }
            @Override public String getName() { return name; }
            @Override
            public List<PipeEvent> onReceiveFromRemote(PipeEvent event) {
                inOrder.add(name);
                return List.of(event);
            }
            @Override
            public List<PipeEvent> onSendToRemote(PipeEvent event) {
                outOrder.add(name);
                return List.of(event);
            }
        }

        BidirectionalProcessor p0 = new BidirectionalProcessor("P0_Socket");
        BidirectionalProcessor p1 = new BidirectionalProcessor("P1_Middle");
        BidirectionalProcessor p2 = new BidirectionalProcessor("P2_App");

        MUDEventPipeline pipeline = new MUDEventPipeline("bidirectional")
                .then(p0)
                .then(p1)
                .then(p2);

        // Inbound: from socket (0) up to app (2)
        pipeline.fireReceive(new SimpleEvent("Inbound"));
        assertEquals(List.of("P0_Socket", "P1_Middle", "P2_App"), inOrder);

        // Outbound: from app (2) down to socket (0)
        pipeline.fireSend(new SimpleEvent("Outbound"));
        assertEquals(List.of("P2_App", "P1_Middle", "P0_Socket"), outOrder);
    }

    @Test
    void testIntermediateSendAndReceive() {
        List<String> inSeen = new ArrayList<>();
        List<String> outSeen = new ArrayList<>();

        class TestProc implements MUDEventProcessor {
            private final String name;
            public TestProc(String name) { this.name = name; }
            @Override public String getName() { return name; }
            @Override
            public List<PipeEvent> onReceiveFromRemote(PipeEvent event) {
                inSeen.add(name);
                return List.of(event);
            }
            @Override
            public List<PipeEvent> onSendToRemote(PipeEvent event) {
                outSeen.add(name);
                return List.of(event);
            }
        }

        TestProc p0 = new TestProc("P0");
        TestProc p1 = new TestProc("P1");
        TestProc p2 = new TestProc("P2");

        MUDEventPipeline pipeline = new MUDEventPipeline("test")
                .then(p0)
                .then(p1)
                .then(p2);

        // P1 sends outbound towards socket: should ONLY visit P0 (p1 itself and p2 must not be called)
        pipeline.sendToRemote(p1, new SimpleEvent("MsgToSocket"));
        assertEquals(List.of("P0"), outSeen);

        // P1 receives inbound towards app: should ONLY visit P2 (p1 itself and p0 must not be called)
        pipeline.receiveFromRemote(p1, new SimpleEvent("MsgToApp"));
        assertEquals(List.of("P2"), inSeen);
    }

    @Test
    void testReplaceProcessor() {
        List<String> inSeen = new ArrayList<>();
        List<String> outSeen = new ArrayList<>();

        class SimpleProc implements MUDEventProcessor {
            private final String name;
            public SimpleProc(String name) { this.name = name; }
            @Override public String getName() { return name; }
            @Override
            public List<PipeEvent> onReceiveFromRemote(PipeEvent event) {
                inSeen.add(name);
                return List.of(event);
            }
            @Override
            public List<PipeEvent> onSendToRemote(PipeEvent event) {
                outSeen.add(name);
                return List.of(event);
            }
        }

        SimpleProc p0 = new SimpleProc("P0");
        SimpleProc p1Old = new SimpleProc("P1_OLD");
        SimpleProc p1New = new SimpleProc("P1_NEW");
        SimpleProc p2 = new SimpleProc("P2");

        MUDEventPipeline pipeline = new MUDEventPipeline("test")
                .then(p0)
                .then(p1Old)
                .then(p2);

        pipeline.replace(p1Old, p1New);

        pipeline.fireReceive(new SimpleEvent("In"));
        assertEquals(List.of("P0", "P1_NEW", "P2"), inSeen);

        pipeline.fireSend(new SimpleEvent("Out"));
        assertEquals(List.of("P2", "P1_NEW", "P0"), outSeen);
    }
}
