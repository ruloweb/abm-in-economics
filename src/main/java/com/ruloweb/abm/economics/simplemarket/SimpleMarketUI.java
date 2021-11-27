package com.ruloweb.abm.economics.simplemarket;

import com.ruloweb.abm.economics.simplemarket.agent.Shopper;
import com.ruloweb.abm.economics.simplemarket.agent.ShopperState;
import com.ruloweb.abm.economics.simplemarket.agent.Trader;
import sim.engine.*;
import sim.display.*;
import sim.portrayal.DrawInfo2D;
import sim.portrayal.Inspector;
import sim.portrayal.grid.SparseGridPortrayal2D;
import sim.portrayal.simple.*;
import javax.swing.*;
import java.awt.*;

public class SimpleMarketUI extends GUIState {
    public Display2D display;
    public JFrame displayFrame;
    SparseGridPortrayal2D tradersPortrayal = new SparseGridPortrayal2D();
    SparseGridPortrayal2D shoppersPortrayal = new SparseGridPortrayal2D();

    public static void main(String[] args) {
        SimpleMarketUI vid = new SimpleMarketUI();
        Console c = new Console(vid);
        c.setVisible(true);
    }

    public SimpleMarketUI() {
        super(new SimpleMarket(System.currentTimeMillis()));
    }

    public SimpleMarketUI(SimState state) {
        super(state);
    }

    public static String getName() {
        return "Simple Market";
    }

    public Object getSimulationInspectedObject() {
        return state;
    }

    public Inspector getInspector() {
        Inspector i = super.getInspector();
        i.setVolatile(true);
        return i;
    }

    public void start() {
        super.start();
        setupPortrayals();
    }

    public void load(SimState state) {
        super.load(state);
        setupPortrayals();
    }

    public void setupPortrayals() {
        SimpleMarket students = (SimpleMarket) state;

        // tell the portrayals what to portray and how to portray them
        tradersPortrayal.setField(students.traders);
        tradersPortrayal.setPortrayalForClass(Trader.class, new RectanglePortrayal2D(new Color(150, 50, 50)));
        shoppersPortrayal.setField(students.shoppers);
        shoppersPortrayal.setPortrayalForClass(Shopper.class, new OvalPortrayal2D() {
            public void draw(Object object, Graphics2D graphics, DrawInfo2D info) {
                Shopper shopper = (Shopper)object;
                paint = shopper.getShoppingList().size() == 0 ? Color.gray : new Color(40, 70, 200);
                super.draw(object, graphics, info);
            }
        });

        // reschedule the displayer
        display.reset();
        display.setBackdrop(Color.white);

        // redraw the display
        display.repaint();
    }

    public void init(Controller c) {
        super.init(c);
        display = new Display2D(600, 600, this);
        display.setClipping(false);

        displayFrame = display.createFrame();
        displayFrame.setTitle("Simple Market Display");
        c.registerFrame(displayFrame); // so the frame appears in the "Display" list
        displayFrame.setVisible(true);
        display.attach(tradersPortrayal, "Traders");
        display.attach(shoppersPortrayal, "Shoppers");
    }

    public void quit() {
        super.quit();
        if (displayFrame!=null) displayFrame.dispose();
        displayFrame =null;
        display =null;
    }

}
