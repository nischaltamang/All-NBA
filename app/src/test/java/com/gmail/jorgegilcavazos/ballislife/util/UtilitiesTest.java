package com.gmail.jorgegilcavazos.ballislife.util;

import static org.junit.Assert.*;
import org.junit.Test;

public class UtilitiesTest {
    @Test
    public void testGetPeriodString() {
        String firstQtr = Utilities.getPeriodString("1", "Qtr");
        String fourthQtr = Utilities.getPeriodString("4", "Qtr");
        String overTime1 = Utilities.getPeriodString("5", "OT");
        String overTime2 = Utilities.getPeriodString("6", "OT");
        String overTime6 = Utilities.getPeriodString("10", "OT");

        assertEquals("1 Qtr", firstQtr);
        assertEquals("4 Qtr", fourthQtr);
        assertEquals("OT1", overTime1);
        assertEquals("OT2", overTime2);
        assertEquals("OT6", overTime6);
    }
}
