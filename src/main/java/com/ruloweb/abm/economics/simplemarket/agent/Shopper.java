package com.ruloweb.abm.economics.simplemarket.agent;

import com.ruloweb.abm.economics.simplemarket.ItemList;
import com.ruloweb.abm.economics.simplemarket.SimpleMarket;
import com.ruloweb.abm.economics.simplemarket.pairs.BuyPair;
import com.ruloweb.abm.economics.simplemarket.utils.Utils;
import ec.util.MersenneTwisterFast;
import sim.engine.*;
import sim.util.*;

import java.util.*;

public class Shopper implements Steppable {
    MersenneTwisterFast random;
    ShopperState shopperState = ShopperState.Moving;
    ItemList shoppingList;
    LinkedList<Trader> traders;
    double spent = 0;
    Trader trader;
    Int2D traderLoc;

    public ShopperState getShopperState() { return shopperState; }
    public ItemList getShoppingList() { return shoppingList; }
    public double getSpent() { return spent; }

    public Shopper(MersenneTwisterFast random, Map<String, Double> shoppingList, int shoppingListSize, Bag traders, int numScans) {
        this.random = random;
        this.shoppingList = new ItemList(random, shoppingList, shoppingListSize);
        this.traders = searchBeforeBuying(traders, numScans);
    }

    private LinkedList<Trader> searchBeforeBuying(Bag traders, int numScans) {
        LinkedList<Trader> route;
        LinkedList<Trader> cheapestRoute = new LinkedList<>();
        double cheapestPrice = Double.MAX_VALUE;
        double spent;


        for (int i = 0; i < numScans; i++) {
            LinkedList<Trader> selectedTraders = selectTraders(traders);
            ItemList toBuy = shoppingList.clone();
            route = new LinkedList<>();
            spent = 0;

            do {
                Trader trader = selectedTraders.removeFirst();
                route.add(trader);

                BuyPair buyPair = buyFromTrader(toBuy, trader);
                toBuy = buyPair.getShoppingList();
                spent += buyPair.getSpent();
            } while (!toBuy.isEmpty() && selectedTraders.size() > 0);

            if (spent < cheapestPrice) {
                cheapestPrice = spent;
                cheapestRoute = route;
            }
        }

        return cheapestRoute;
    }

    /**
     * Shuffle a bag of traders and return them as a linked list.
     *
     * @param traders bag of traders
     * @return linked list of traders
     */
    private LinkedList<Trader> selectTraders(Bag traders) {
        LinkedList<Trader> items = new LinkedList<>();
        Integer[] keys = new Integer[traders.numObjs];

        for (int i = 0; i < traders.numObjs; i++) keys[i] = i;
        Utils.shuffleArray(random, keys);

        for (int i = 0; i < traders.numObjs; i++) {
            items.add((Trader)traders.objs[keys[i]]);
        }

        return items;
    }

    private void move(SimpleMarket simpleMarket) {
        Int2D location = simpleMarket.shoppers.getObjectLocation(this);

        // Choose the next trader
        if (trader == null) {
            if (traders.size() == 0) {
                shopperState = ShopperState.Done;
                return;
            }

            trader = traders.removeFirst();
            traderLoc = simpleMarket.traders.getObjectLocation(trader);
        }

        int x = location.x;
        int y = location.y;

        if (x == traderLoc.x && y == traderLoc.y) {
            shopperState = ShopperState.Buying;
        }
        else {
            if (x < traderLoc.x) {
                x += 1;
            }
            else if (x > traderLoc.x) {
                x -= 1;
            }

            if (y < traderLoc.y) {
                y += 1;
            }
            else if (y > traderLoc.y) {
                y -= 1;
            }

            simpleMarket.shoppers.setObjectLocation(this, x, y);
        }
    }

    private void buy() {
        BuyPair buyPair = buyFromTrader(shoppingList, trader);
        shoppingList = buyPair.getShoppingList();
        spent += buyPair.getSpent();

        trader = null;

        if (shoppingList.size() == 0) {
            shopperState = ShopperState.Done;
        }
        else {
            shopperState = ShopperState.Moving;
        }
    }

    private BuyPair buyFromTrader(ItemList shoppingList, Trader trader) {
        ItemList shoppingListClone = shoppingList.clone();
        double spent = 0.0;

        for (String item : trader.stock.keySet()) {
            if (shoppingListClone.containsKey(item)) {
                spent += trader.stock.get(item);
                shoppingListClone.remove(item);
            }
        }

        return new BuyPair(shoppingListClone, spent);
    }

    private void finish(SimpleMarket simpleMarket) {
        Int2D location = simpleMarket.shoppers.getObjectLocation(this);
        simpleMarket.shoppers.setObjectLocation(this, location.x, simpleMarket.shoppers.getHeight() - 1);
        shopperState = ShopperState.Stop;
    }

    public void step(SimState state) {
        SimpleMarket simpleMarket = (SimpleMarket) state;

        switch (shopperState) {
            case Moving -> move(simpleMarket);
            case Buying -> buy();
            case Done -> finish(simpleMarket);
        }
    }
}
