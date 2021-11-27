package com.ruloweb.abm.economics.simplemarket;

import com.ruloweb.abm.economics.simplemarket.agent.Shopper;
import com.ruloweb.abm.economics.simplemarket.agent.ShopperState;
import com.ruloweb.abm.economics.simplemarket.agent.Trader;
import ec.util.MersenneTwisterFast;
import sim.engine.*;
import sim.field.grid.SparseGrid2D;
import sim.util.*;

import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;

public class SimpleMarket extends SimState {
    public SparseGrid2D traders = new SparseGrid2D(20, 20);
    public SparseGrid2D shoppers = new SparseGrid2D(20, 20);
    public Map<String, Double> fruitsAndVegs = new HashMap<>();

    public int numShoppers = 10;
    int[] traderPositions = {2, 4, 6, 8, 10, 12, 14, 16, 18};
    public int traderStockSize = 5;
    public int shopperShoppingListSize = 8;
    public int numberOfScans = 3;

    public int getNumShoppers() { return numShoppers; }
    public void setNumShoppers(int val) { numShoppers = val; }
    public Object domNumShoppers() { return new sim.util.Interval(1, 100); }

    public int getTraderStockSize() { return traderStockSize; }
    public void setTraderStockSize(int val) { traderStockSize = val; }
    public Object domTraderStockSize() { return new sim.util.Interval(1, 12); }

    public int getShopperShoppingListSize() { return shopperShoppingListSize; }
    public void setShopperShoppingListSize(int val) { shopperShoppingListSize = val; }
    public Object domShopperShoppingListSize() { return new sim.util.Interval(1, 12); }

    public int getNumberOfScans() { return numberOfScans; }
    public void setNumberOfScans(int val) { numberOfScans = val; }
    public Object domNumberOfScans() { return new sim.util.Interval(1, 9); }

    public int getNumPendingShoppers() {
        int acc = 0;
        Bag shopperBag = shoppers.getAllObjects();

        for (int i = 0; i < shopperBag.numObjs; i++) {
            if (((Shopper)shopperBag.objs[i]).getShopperState() != ShopperState.Stop) {
                acc += 1;
            }
        }

        return acc;
    }

    public double getMeanShoppingListSize() {
        double acc = 0;
        Bag shopperBag = shoppers.getAllObjects();

        for (int i = 0; i < shopperBag.numObjs; i++) {
            acc += ((Shopper)shopperBag.objs[i]).getShoppingList().size();
        }

        return acc / shopperBag.size();
    }

    public double getTotalSpent() {
        double acc = 0;
        Bag shopperBag = shoppers.getAllObjects();

        for (int i = 0; i < shopperBag.numObjs; i++) {
            acc += ((Shopper)shopperBag.objs[i]).getSpent();
        }

        return acc;
    }

    public double[] getSpentDistribution() {
        Bag shopperBag = shoppers.getAllObjects();
        double[] distro = new double[shopperBag.numObjs];

        for (int i = 0; i < shopperBag.numObjs; i++) {
            distro[i] = ((Shopper)(shopperBag.objs[i])).getSpent();
        }

        return distro;
    }

    public double[] getPriceDistribution() {
        double[] distro = new double[fruitsAndVegs.size()];
        int i = 0;

        for (String key : fruitsAndVegs.keySet()) {
            distro[i++] = fruitsAndVegs.get(key);
        }

        return distro;
    }

    /**
     * TODO: try with JAVA Random functions
     * @param seed seed for random generator
     */
    public SimpleMarket(long seed) {
        super(seed);
    }

    public void start() {
        super.start();

        fruitsAndVegs = Map.ofEntries(
                entry("apples", random.nextDouble() * 100),
                entry("bananas", random.nextDouble() * 100),
                entry("oranges", random.nextDouble() * 100),
                entry("plumbs", random.nextDouble() * 100),
                entry("mangoes", random.nextDouble() * 100),
                entry("grapes", random.nextDouble() * 100),
                entry("cabbage", random.nextDouble() * 100),
                entry("potatoes", random.nextDouble() * 100),
                entry("carrots", random.nextDouble() * 100),
                entry("lettuce", random.nextDouble() * 100),
                entry("tomatoes", random.nextDouble() * 100),
                entry("beans", random.nextDouble() * 100)
        );

        // clear the yards
        traders.clear();
        shoppers.clear();

        // add traders
        for (int traderPosition : traderPositions) {
            Trader trader = new Trader(random, fruitsAndVegs, traderStockSize);
            traders.setObjectLocation(trader, new Int2D(traderPosition, traders.getHeight() / 2));
            schedule.scheduleRepeating(trader);
        }

        // new seed because Shopper executes a number numberOfScans of shuffles,
        // which modify the general random sequence and breaks reproducibility
        MersenneTwisterFast randomShopper = new MersenneTwisterFast(seed());

        // add shoppers
        for (int i = 0; i < numShoppers; i++) {
            Int2D pos = new Int2D(random.nextInt(shoppers.getWidth()), random.nextInt(shoppers.getHeight()));
            Shopper shopper = new Shopper(randomShopper, fruitsAndVegs, shopperShoppingListSize,
                    traders.allObjects, numberOfScans);
            shoppers.setObjectLocation(shopper, pos);
            schedule.scheduleRepeating(shopper);
        }

        // anonymous agent
        schedule.scheduleRepeating(Schedule.EPOCH,1, (Steppable) state -> {
            if (getNumPendingShoppers() == 0) finish();
        });
    }

    public static void main(String[] args) {
        doLoop(SimpleMarket.class, args);
        System.exit(0);
    }
}
