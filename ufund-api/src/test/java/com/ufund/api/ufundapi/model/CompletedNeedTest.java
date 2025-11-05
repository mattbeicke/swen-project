package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-tier")
public class CompletedNeedTest {
    @Test
    void testCreation() {
        int uid = 1;
        String name = "abc";
        Need need = new Need("name", 1, "desc");
        long timestamp = 1000000;

        CompletedNeed cn = new CompletedNeed(need, uid, name, timestamp);

        assertEquals(name, cn.getContributorUsername());
        assertEquals(uid, cn.getContributorID());
        assertEquals(need, cn.getNeed());
        assertEquals(timestamp, cn.getTimestamp());
    }

    @Test
    void testUpdateUsername() {
        int uid = 1;
        String name = "abc";
        Need need = new Need("name", 1, "desc");
        long timestamp = 1000000;

        CompletedNeed cn = new CompletedNeed(need, uid, name, timestamp);

        assertEquals(name, cn.getContributorUsername());
        String newName = "helloworld";
        cn.setContributorUsername(newName);
        assertEquals(newName, cn.getContributorUsername());
    }
}