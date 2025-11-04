package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("Model-tier")
public class CompletedNeedTest {
    @Test
    public void testCreation() {
        int uid = 1;
        Need need = new Need("name", 1, "desc");
        long timestamp = 1000000;

        CompletedNeed cn = new CompletedNeed(need, uid, timestamp);

        assertEquals(uid, cn.getContributorID());
        assertEquals(need, cn.getNeed());
        assertEquals(timestamp, cn.getTimestamp());
    }
}