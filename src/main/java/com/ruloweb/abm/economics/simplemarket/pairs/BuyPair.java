package com.ruloweb.abm.economics.simplemarket.pairs;

import com.ruloweb.abm.economics.simplemarket.ItemList;

public final class BuyPair {
    private ItemList shoppingList;
    private double spent;

    public BuyPair(ItemList shoppingList, double spent) {
        this.shoppingList = shoppingList;
        this.spent = spent;
    }

    public ItemList getShoppingList() {
        return shoppingList;
    }

    public void setShoppingList(ItemList shoppingList) {
        this.shoppingList = shoppingList;
    }

    public double getSpent() {
        return spent;
    }

    public void setSpent(double spent) {
        this.spent = spent;
    }

}
