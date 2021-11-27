package com.ruloweb.abm.economics.simplemarket.agent;

import com.ruloweb.abm.economics.simplemarket.ItemList;
import com.ruloweb.abm.economics.simplemarket.SimpleMarket;
import ec.util.MersenneTwisterFast;
import sim.engine.SimState;
import sim.engine.Steppable;

import java.util.Map;

public class Trader implements Steppable {
    ItemList stock;

    public ItemList getStock() {
        return stock;
    }

    public Trader(MersenneTwisterFast random, Map<String, Double> stock, int n) {
        this.stock = new ItemList(random, stock, n);
    }

    public void step(SimState state) {}
}
