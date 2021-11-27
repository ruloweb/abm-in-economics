package com.ruloweb.abm.economics.simplemarket;

import com.ruloweb.abm.economics.simplemarket.utils.Utils;
import ec.util.MersenneTwisterFast;

import java.util.*;
import java.util.stream.Collectors;

public class ItemList extends HashMap<String, Double> {
    /**
     * Choose randomly n elements from a set of items.
     * TODO: Could it be simplified?
     *
     * @param random random generator
     * @param items items to select from, randomly
     * @param n number of items to select
     * @return n number of elements, selected randomly from items.
     */
    private static Map<String, Double> createMap(MersenneTwisterFast random, Map<String, Double> items, int n) {
        Set<String> keySet = items.keySet();
        String[] keys = new String[keySet.size()];
        keySet.toArray(keys);

        Utils.shuffleArray(random, keys);

        Set<String> subKeys = new HashSet<>(Collections.emptySet());
        subKeys.addAll(Arrays.asList(keys).subList(0, n));

        return items.entrySet()
                .stream().filter(x -> subKeys.contains(x.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public ItemList(MersenneTwisterFast random, Map<String, Double> items, int n) {
        super(createMap(random, items, n));
    }

    @Override
    public ItemList clone() {
        return (ItemList) super.clone();
    }
}
