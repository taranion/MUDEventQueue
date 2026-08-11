package org.prelle.mudevents;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class MUDEventQueueTest {

    @Test
    void moduleIsPresent() {
        Module module = MUDEventPipeline.class.getModule();
        assertNotNull(module.getName(), "Module name should be set");
    }
}
