
package world;

import jason.environment.grid.*;
import java.util.logging.Logger;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.FlowLayout;  
import java.awt.event.*;
import javax.swing.*;


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
        
        JFrame orderFrame = new JFrame("Add order");
        JPanel panel = new JPanel();  
        panel.setLayout(new FlowLayout());  
        JButton addBtn = new JButton("Add!");
        JTextField tf = new JTextField(5);
        tf.setText(1+"");
        panel.add(new JLabel("Weight: "));
        panel.add(tf);
        
        
        
        JComboBox comboBox = new JComboBox();
        for (int i = 0; i < hmodel.getVillages().size();i++) {
           Location l = hmodel.getVillages().get(i);
           comboBox.addItem( new Item(i,i+". ("+l.x+" "+l.y+")"));
        }
        
        panel.add(new JLabel("Destination:"));
        panel.add(comboBox);
        panel.add(addBtn);
        
        addBtn.addActionListener(new ActionListener() { 
	  public void actionPerformed(ActionEvent e) { 
	       
	 	double weight = tf.getText().length() == 0 ? 1 : Double.parseDouble(tf.getText()); 
	 	int selectedVillage = ((Item)comboBox.getSelectedItem()).getId();  
	 	Location target = hmodel.getVillages().get(selectedVillage);
	 	Order newOrd = new Order(target,weight);
	 	
	  } 
	});
        
        
        orderFrame.add(panel);
        orderFrame.setVisible(true);
        orderFrame.setSize(200, 300);  
        orderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
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
        g.setColor(Color.pink);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.drawLine(x * cellSizeW + 2, y * cellSizeH + 2, (x + 1) * cellSizeW - 2, (y + 1) * cellSizeH - 2);
        g.drawLine(x * cellSizeW + 2, (y + 1) * cellSizeH - 2, (x + 1) * cellSizeW - 2, y * cellSizeH + 2);
    }

    public void drawMainDepot(Graphics g, int x, int y) {
        g.setColor(Color.red);
        g.fillRect(x * cellSizeW, y * cellSizeH, cellSizeW, cellSizeH);
        g.setColor(Color.pink);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        g.drawLine(x * cellSizeW + 2, y * cellSizeH + 2, (x + 1) * cellSizeW - 2, (y + 1) * cellSizeH - 2);
        g.drawLine(x * cellSizeW + 2, (y + 1) * cellSizeH - 2, (x + 1) * cellSizeW - 2, y * cellSizeH + 2);
    }

    public void drawVillage(Graphics g, int x, int y) {
        g.setColor(Color.yellow);
        g.drawRect(x * cellSizeW + 2, y * cellSizeH + 2, cellSizeW - 4, cellSizeH - 4);
        int[] vx = new int[4];
        int[] vy = new int[4];
        vx[0] = x * cellSizeW + (cellSizeW / 2);
        vy[0] = y * cellSizeH;
        vx[1] = (x + 1) * cellSizeW;
        vy[1] = y * cellSizeH + (cellSizeH / 2);
        vx[2] = x * cellSizeW + (cellSizeW / 2);
        vy[2] = (y + 1) * cellSizeH;
        vx[3] = x * cellSizeW;
        vy[3] = y * cellSizeH + (cellSizeH / 2);
        g.fillPolygon(vx, vy, 4);
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
        super.drawString(g, x, y, defaultFont, "Robot");
    }
}

   class Item
    {
        private int id;
        private String description;
 
        public Item(int id, String description)
        {
            this.id = id;
            this.description = description;
        }
 
        public int getId()
        {
            return id;
        }
 
        public String getDescription()
        {
            return description;
        }
 
        public String toString()
        {
            return description;
        }
    }
