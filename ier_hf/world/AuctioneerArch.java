
package world;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.awt.FlowLayout;  


import jason.environment.grid.Location;
import jason.architecture.*;
import jason.asSemantics.ActionExec;
import jason.asSyntax.ASSyntax;
import jason.asSyntax.Literal;

import javax.swing.*;

/** example of agent architecture's functions overriding */
public class AuctioneerArch extends AgArch {

    int auctionId = 0;
    JFrame orderFrame;
    public AuctioneerArch() {
        
         
        this.orderFrame = new JFrame("Add order");
        JPanel panel = new JPanel();  
        panel.setLayout(new FlowLayout());  
        JButton addBtn = new JButton("Add!");
        JTextField tf = new JTextField(5);
        tf.setText(1+"");
        panel.add(new JLabel("Weight: "));
        panel.add(tf);
        
        
        
        WorldModel hmodel = WorldModel.get();
        
        
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
	 	auctionId++;
	 	Literal goal = ASSyntax.createLiteral("start_auction", ASSyntax.createNumber(auctionId),ASSyntax.createNumber(target.x),ASSyntax.createNumber(target.y),ASSyntax.createNumber(weight));
               getTS().getC().addAchvGoal(goal, null);
	 	
	  } 
	});
        
        
        orderFrame.add(panel);
        orderFrame.setVisible(true);
        orderFrame.setSize(200, 300);  
        orderFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
        
        
    }

    @Override
    public void stop() {
        orderFrame.dispose();
        super.stop();
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
