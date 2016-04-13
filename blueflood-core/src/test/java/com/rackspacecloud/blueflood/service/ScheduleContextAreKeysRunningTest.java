package com.rackspacecloud.blueflood.service;

import com.rackspacecloud.blueflood.rollup.Granularity;
import com.rackspacecloud.blueflood.rollup.SlotKey;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ScheduleContextAreKeysRunningTest {

    long currentTime;
    final int shard = 0;
    List<Integer> managedShards;
    ScheduleContext ctx;
    int slot;
    SlotKey slotkey;

    @Before
    public void setUp() {

        // given
        currentTime = 1234000L;

        managedShards = new ArrayList<Integer>() {{ add(shard); }};

        ctx = new ScheduleContext(currentTime, managedShards);

        slot = Granularity.MIN_5.slot(currentTime);
        slotkey = SlotKey.of(Granularity.MIN_5, slot, shard);
    }

    @Test
    public void noneScheduledOrRunningReturnsFalse() {

        // precondition
        assertEquals(0, ctx.getScheduledCount());
        assertEquals(0, ctx.getRunningCount());

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(slotkey);

        // then
        assertFalse(areKeysRunning);
    }

    @Test
    public void slotScheduledReturnsTrue() {

        // given
        ctx.update(currentTime - 2, shard);
        ctx.scheduleEligibleSlots(1, 7200000);

        // precondition
        assertEquals(1, ctx.getScheduledCount());
        assertEquals(0, ctx.getRunningCount());

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(slotkey);

        // then
        assertTrue(areKeysRunning);
    }

    @Test
    public void childSlotScheduledReturnsTrue() {

        // given
        ctx.update(currentTime - 2, shard);
        ctx.scheduleEligibleSlots(1, 7200000);

        // precondition
        assertEquals(1, ctx.getScheduledCount());
        assertEquals(0, ctx.getRunningCount());

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(slotkey);

        // then
        assertTrue(areKeysRunning);
    }

    @Test
    public void unrelatedSlotScheduledReturnsFalse() {

        // given
        ctx.update(currentTime - 2, shard);
        ctx.scheduleEligibleSlots(1, 7200000);

        int otherSlot = Granularity.MIN_5.slot(currentTime - 5*60*1000); // check the previous slot from 5 minutes ago
        SlotKey otherSlotkey = SlotKey.of(Granularity.MIN_5, otherSlot, shard);

        // precondition
        assertEquals(1, ctx.getScheduledCount());
        assertEquals(0, ctx.getRunningCount());

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(otherSlotkey);

        // then
        assertFalse(areKeysRunning);
    }

    @Test
    public void slotRunningReturnsTrue() {

        // given
        ctx.update(currentTime - 2, shard);
        ctx.scheduleEligibleSlots(1, 7200000);

        SlotKey runningSlot = ctx.getNextScheduled();

        // precondition
        assertEquals(0, ctx.getScheduledCount());
        assertEquals(1, ctx.getRunningCount());
        assertEquals(slotkey, runningSlot);

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(slotkey);

        // then
        assertTrue(areKeysRunning);
    }

    @Test
    public void childSlotRunningReturnsTrue() {

        // given
        ctx.update(currentTime - 2, shard);
        ctx.scheduleEligibleSlots(1, 7200000);

        SlotKey runningSlot = ctx.getNextScheduled();

        // precondition
        assertEquals(0, ctx.getScheduledCount());
        assertEquals(1, ctx.getRunningCount());

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(slotkey);

        // then
        assertTrue(areKeysRunning);
    }

    @Test
    public void slotPushedBackReturnsTrue() {

        // given
        ctx.update(currentTime - 2, shard);
        ctx.scheduleEligibleSlots(1, 7200000);

        SlotKey runningSlot = ctx.getNextScheduled();

        // precondition
        assertEquals(0, ctx.getScheduledCount());
        assertEquals(1, ctx.getRunningCount());

        ctx.clearFromRunning(runningSlot);
        ctx.pushBackToScheduled(runningSlot, false);

        // precondition
        assertEquals(1, ctx.getScheduledCount());
        assertEquals(0, ctx.getRunningCount());

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(slotkey);

        // then
        assertTrue(areKeysRunning);
    }

    @Test
    public void childSlotPushedBackReturnsTrue() {

        // given
        ctx.update(currentTime - 2, shard);
        ctx.scheduleEligibleSlots(1, 7200000);

        SlotKey runningSlot = ctx.getNextScheduled();

        // precondition
        assertEquals(0, ctx.getScheduledCount());
        assertEquals(1, ctx.getRunningCount());

        ctx.clearFromRunning(runningSlot);
        ctx.pushBackToScheduled(runningSlot, false);

        // precondition
        assertEquals(1, ctx.getScheduledCount());
        assertEquals(0, ctx.getRunningCount());

        // when
        boolean areKeysRunning = ctx.areChildKeysOrSelfKeyScheduledOrRunning(slotkey);

        // then
        assertTrue(areKeysRunning);
    }
}
