package com.prevailpots.bunkers.utils;

import java.math.*;
import java.util.*;

public class MathUtils
{
    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    public static int indexOfMax(final double[] array) {
        final List<Double> list = new ArrayList<Double>();
        for (int i = 0; i < array.length; ++i) {
            list.add(array[i]);
        }
        return list.indexOf(Collections.max(null));
    }
    
    public static int indexOfMin(final double[] array) {
        final List<Double> list = new ArrayList<Double>();
        for (int i = 0; i < array.length; ++i) {
            list.add(array[i]);
        }
        return list.indexOf(Collections.min(null));
    }
}
