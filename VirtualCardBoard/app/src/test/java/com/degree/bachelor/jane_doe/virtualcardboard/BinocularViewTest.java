package com.degree.bachelor.jane_doe.virtualcardboard;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by Jane-Doe on 4/18/2016.
 */
public class BinocularViewTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void binocular() throws Exception {
        BinocularView bv = new BinocularView(960, 480);

        bv.SetCustomBinocularParams(600, 300, 600, 360);
        bv.CalcAdaptedViews(600, 400);

        BinocularView.BinocularInfo info = bv.getBinocularInfo();

        assertEquals(4, 2 + 2);

        bv.SetCustomBinocularParams(600, 300, 500, 300);
        bv.CalcAdaptedViews(120, 240);

        BinocularView.BinocularInfo info2 = bv.getBinocularInfo();

        assertEquals(4, 2 + 2);

        bv.SetCustomBinocularParams(600, 300, 500, 300);
        bv.CalcAdaptedViews(1980, 300);

        BinocularView.BinocularInfo info3 = bv.getBinocularInfo();

        assertEquals(4, 2 + 2);
    }
}

