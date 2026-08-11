package org.prelle.mudevents;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MUDEventPipelineTest {

    static class SimpleEvent extends AMUDEvent {
        private final String name;

        public SimpleEvent(String name) {
            super(name);
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

    @Test
    void testForwardEvent() {
        List<String> received = new ArrayList<>();

        MUDEventPipeline pipeline = new MUDEventPipeline("test")
                .then(e -> List.of(e))
                .then(e -> {
                    received.add(((SimpleEvent) e).getName());
                    return List.of(e);
                });

        pipeline.publish(new SimpleEvent("E1"));

        assertEquals(List.of("E1"), received);
    }

    @Test
    void testConsumeEvent() {
        List<String> received = new ArrayList<>();

        MUDEventPipeline pipeline = new MUDEventPipeline("test")
                .then(e -> null) // Consumes event
                .then(e -> {
                    received.add(((SimpleEvent) e).getName());
                    return List.of(e);
                });

        pipeline.publish(new SimpleEvent("E1"));

        assertTrue(received.isEmpty());
    }

    @Test
    void testSubstituteSingleEvent() {
        List<String> received = new ArrayList<>();

        MUDEventPipeline pipeline = new MUDEventPipeline("test")
                .then(e -> List.of(new SimpleEvent("Substituted_" + ((SimpleEvent) e).getName())))
                .then(e -> {
                    received.add(((SimpleEvent) e).getName());
                    return List.of(e);
                });

        pipeline.publish(new SimpleEvent("E1"));

        assertEquals(List.of("Substituted_E1"), received);
    }

    @Test
    void testSubstituteMultipleEventsDoesNotReinjectToEarlierProcessors() {
        List<String> p0Seen = new ArrayList<>();
        List<String> p1Seen = new ArrayList<>();
        List<String> p2Seen = new ArrayList<>();

        MUDEventPipeline pipeline = new MUDEventPipeline("test")
                .then(e -> {
                    p0Seen.add(((SimpleEvent) e).getName());
                    return List.of(e);
                })
                .then(e -> {
                    p1Seen.add(((SimpleEvent) e).getName());
                    // Substitute 1 event into 3 events
                    return List.of(
                            new SimpleEvent("A"),
                            new SimpleEvent("B"),
                            new SimpleEvent("C")
                    );
                })
                .then(e -> {
                    p2Seen.add(((SimpleEvent) e).getName());
                    return List.of(e);
                });

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
                .then(e -> List.of(new SimpleEvent("1"), new SimpleEvent("2")))
                .then(e -> {
                    String name = ((SimpleEvent) e).getName();
                    return List.of(new SimpleEvent(name + ".A"), new SimpleEvent(name + ".B"));
                })
                .then(e -> {
                    results.add(((SimpleEvent) e).getName());
                    return List.of(e);
                });

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
            pipeline.then(e -> {
                int count = Integer.parseInt(((SimpleEvent) e).getName());
                return List.of(new SimpleEvent(String.valueOf(count + 1)));
            });
        }

        List<String> output = new ArrayList<>();
        pipeline.then(e -> {
            output.add(((SimpleEvent) e).getName());
            return List.of(e);
        });

        pipeline.publish(new SimpleEvent("0"));

        assertEquals(List.of("500"), output);
    }
}
