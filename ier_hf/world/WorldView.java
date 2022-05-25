
package world;

import jason.environment.grid.*;
import java.util.logging.Logger;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.*;
import javax.swing.*;
import java.lang.String;


/**
 * class that implements the View of Domestic Robot application
 */
public class WorldView extends GridWorldView {

    WorldModel hmodel;
    private Logger logger = Logger.getLogger("ier_hf.mas2j." + WorldView.class.getName());

    public WorldView(WorldModel model) {
        super(model, "Delivery drones", 700);
        hmodel = model;
        defaultFont = new Font("Arial", Font.BOLD, 16); // change default font
        setVisible(true);
       
        repaint();
    }

    /**
     * draw application objects
     */
    @Override
    public void draw(Graphics g, int x, int y, int object) {
        switch (object) {
            case WorldModel.DEPOT:
                drawDepot(g, x, y);
                break;
            case WorldModel.VILLAGE:
                drawVillage(g, x, y);
                break;
            case WorldModel.MAIN_DEPOT:
                drawMainDepot(g, x, y);
                break;
        }
        
    }

    public void drawDepot(Graphics g, int x, int y) {
        g.setColor(Color.gray);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        g.setColor(Color.yellow);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.drawLine(x * cellSizeW + 2, y * cellSizeH + 2, (x + 1) * cellSizeW - 2, (y + 1) * cellSizeH - 2);
        g.drawLine(x * cellSizeW + 2, (y + 1) * cellSizeH - 2, (x + 1) * cellSizeW - 2, y * cellSizeH + 2);
    }

    public void drawMainDepot(Graphics g, int x, int y) {
        g.setColor(Color.black);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        g.setColor(Color.yellow);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.drawLine(x * cellSizeW + 2, y * cellSizeH + 2, (x + 1) * cellSizeW - 2, (y + 1) * cellSizeH - 2);
        g.drawLine(x * cellSizeW + 2, (y + 1) * cellSizeH - 2, (x + 1) * cellSizeW - 2, y * cellSizeH + 2);
    }

    public void drawVillage(Graphics g, int x, int y) {
        g.setColor (Color.pink);
		int[] vx = new int[7];
        int[] vy = new int[7];
        vx[0] = x * cellSizeW + (cellSizeW / 5);
        vy[0] = (y + 1) * cellSizeH;
        vx[1] = (x + 1) * cellSizeW - (cellSizeW / 5);
        vy[1] = (y + 1)* cellSizeH;
        vx[2] = (x + 1) * cellSizeW - (cellSizeW / 5);
        vy[2] = y * cellSizeH + 5 * (cellSizeH / 10);
        vx[3] = (x + 1) * cellSizeW;
        vy[3] = y * cellSizeH + 5 * (cellSizeH / 10);
        vx[4] = x * cellSizeW + (cellSizeW / 2);
		vy[4] = y * cellSizeH;
		vx[5] = x * cellSizeW;
		vy[5] = y * cellSizeH + 5 * (cellSizeH / 10);
		vx[6] = x * cellSizeW + (cellSizeW / 5);
		vy[6] = y * cellSizeH + 5 * (cellSizeH / 10);
		g.fillPolygon(vx, vy, 7);
		g.setColor(Color.black);
		super.drawString(g, x, y, defaultFont, "" + x + ", " + y);
    }

    public void drawEnemy(Graphics g, int x, int y) {
        g.setColor(Color.red);
        g.fillOval(x * cellSizeW + 7, y * cellSizeH + 7, cellSizeW - 8, cellSizeH - 8);
    }

    @Override
    public void drawAgent(Graphics g, int x, int y, Color c, int id) {
        Location lRobot = hmodel.getAgPos(0);
        c = Color.yellow;
        super.drawAgent(g, x, y, c, -1);
        g.setColor(Color.black);
        int chargeValue = hmodel.getChargeValues().get(id);
        int auctionOrderId = hmodel.getAuctionIds().get(id);
        String toWrite = "Drón"+id+" ("+chargeValue+")";
        if (auctionOrderId != -1) {
        	toWrite += " Áru: "+auctionOrderId+".";
        }
        super.drawString(g, x, y, defaultFont, toWrite);
      
   }
}

