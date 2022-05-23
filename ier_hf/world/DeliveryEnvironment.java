
package world;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.grid.Location;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DeliveryEnvironment extends jason.environment.Environment {

    private Logger logger = Logger.getLogger("ier_hf.mas2j." + DeliveryEnvironment.class.getName());

    WorldModel  model;
    WorldView   view;

    int     simId    = 3; // type of environment
    int     nbWorlds = 3;

    int     sleep    = 0;
    boolean running  = true;
    boolean hasGUI   = true;

    public static final int SIM_TIME = 60;  // in seconds

    Term                    pick     = Literal.parseLiteral("do(pick)");
    Term                    drop     = Literal.parseLiteral("do(drop)");
    Term                    land     = Literal.parseLiteral("do(land)");
    Term                    fly      = Literal.parseLiteral("do(fly)");


    @Override
    public void init(String[] args) {
        hasGUI = args[2].equals("yes");
        sleep  = Integer.parseInt(args[1]);
        initWorld();
    }

    public int getSimId() {
        return simId;
    }

    public void setSleep(int s) {
        sleep = s;
    }

    @Override
    public void stop() {
        running = false;
        super.stop();
    }

    @Override
    public boolean executeAction(String ag, Structure action) {
        boolean result = false;
        try {
            if (sleep > 0) {
                Thread.sleep(sleep);
            }
            int agId = getAgIdBasedOnName(ag);
            /*
            // get the agent id based on its name
            int agId = getAgIdBasedOnName(ag);

            if (action.equals(up)) {
                result = model.move(Move.UP, agId);
            } else if (action.equals(down)) {
                result = model.move(Move.DOWN, agId);
            } else if (action.equals(right)) {
                result = model.move(Move.RIGHT, agId);
            } else if (action.equals(left)) {
                result = model.move(Move.LEFT, agId);
            } else if (action.equals(skip)) {
                result = true;
            } else if (action.equals(pick)) {
                result = model.pick(agId);
            } else if (action.equals(drop)) {
                result = model.drop(agId);
                view.udpateCollectedGolds();
            } else {
                logger.info("executing: " + action + ", but not implemented!");
            }*/
            if (result) {
                updateAgPercept(agId);
                return true;
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            logger.log(Level.SEVERE, "error executing " + action + " for " + ag, e);
        }
        return false;
    }

    private int getAgIdBasedOnName(String agName) {
        return (Integer.parseInt(agName.substring(5))) - 1;
    }

    public void initWorld() {
	this.model = WorldModel.create(25,25,2);
            //clearPercepts();
            logger.warning(model.toString());
            updateAgsPercept();
            if (hasGUI) {
                view = new WorldView(model);
                //view.setEnv(this);
            }
            informAgsEnvironmentChanged();
        /*    
        try {
            
        } catch (Exception e) {
            logger.warning("Error creating world "+e);
        }*/
    }

    public void endSimulation() {
        addPercept(Literal.parseLiteral("end_of_simulation(" + simId + ",0)"));
        informAgsEnvironmentChanged();
        if (view != null) view.setVisible(false);
        WorldModel.destroy();
    }

    private void updateAgsPercept() {
        for (int i = 0; i < model.getNbOfAgs(); i++) {
            updateAgPercept(i);
        }
    }

    private void updateAgPercept(int ag) {
        if (ag == 0) {
        updateAgPercept("drone", ag);
        }
        updateAgPercept("drone_" + (ag + 1), ag);
    }

    private void updateAgPercept(String agName, int ag) {
    
        clearPercepts(agName);
        // its location
        Location l = model.getAgPos(ag);
        if (l == null) {return;}
        addPercept(agName, Literal.parseLiteral("pos(" + l.x + "," + l.y + ")"));

	 for (Location d : model.getDepots()) {
	     addPercept(agName, Literal.parseLiteral("depot(" + d.x + "," + d.y + ")"));
	 }
	 
	 addPercept(agName, Literal.parseLiteral("mainDepot(" + model.getMainDepot().x + "," + model.getMainDepot().y + ")"));
	 
	 for (Location d : model.getVillages()) {
	     addPercept(agName, Literal.parseLiteral("village(" + d.x + "," + d.y + ")"));
	 }
    }

}
