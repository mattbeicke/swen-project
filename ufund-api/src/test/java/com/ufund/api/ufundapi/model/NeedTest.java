package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-tier")
class NeedTest {
    @Test
    void testCreation() {
        String name = "U-Fund Testing";
        int id = 123;
        String description = "Requires that someone dedicate time to test.";
        
        Need need = new Need(name, id, description);
        
        assertEquals(name, need.getName());
        assertEquals(id, need.getId());
        assertEquals(description, need.getDescription());
    }

    @Test
    void testUpdateNeed() {
        String name = "U-Fund Testing";
        int id = 123;
        String description = "Requires that someone dedicate time to test.";

        String updated_name = "Tested U-Fund";
        String updated_description = "Will require tests to be added.";

        
        Need need = new Need(name, id, description);
        need.updateNeed(updated_name, updated_description);

        assertEquals(updated_name, need.getName());
        assertEquals(updated_description, need.getDescription());
    }

    @Test
    void testToString() {
        String name = "UFund";
        int id = 124;
        String description = "TestDesc";

        Need need = new Need(name, id, description);
        
        String expectation = "Need [id=124, name=UFund, description=TestDesc]";

        assertEquals(expectation, need.toString());
    }
}
