package com.ruloweb.abm.economics.simplemarket.utils;

import ec.util.MersenneTwisterFast;

public final class Utils {
    public static <T> void shuffleArray(MersenneTwisterFast random, T[] array)
    {
        int index;
        T temp;
        for (int i = array.length - 1; i > 0; i--) {
            index = random.nextInt(i + 1);
            temp = array[index];
            array[index] = array[i];
            array[i] = temp;
        }
    }
}
