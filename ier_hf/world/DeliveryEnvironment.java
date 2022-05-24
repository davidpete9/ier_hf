
package world;

import jason.asSyntax.Literal;
import jason.asSyntax.Structure;
import jason.asSyntax.Term;
import jason.environment.grid.Location;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.Collection;

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
            //logger.warning("executeAction running");
            int agId = getAgIdBasedOnName(ag);

            if (action.getFunctor().equals("move_towards")) {
                int agX = Integer.parseInt(action.getTerm(0).toString());
                int agY = Integer.parseInt(action.getTerm(1).toString());

                int destX = Integer.parseInt(action.getTerm(2).toString());
                int destY = Integer.parseInt(action.getTerm(3).toString());

                int speed = Integer.parseInt(action.getTerm(4).toString());

                double dirx = destX - agX;
                double diry = destY - agY;

                double length = Math.sqrt(dirx * dirx + diry*diry);
                dirx /= length;
                diry /= length;

                dirx *= 2;
                diry *= 2;

                double actualLength = Math.sqrt(dirx * dirx + diry*diry);
                int nextX = 0; int nextY = 0;
                if (actualLength > length) {
                    model.fly(new Location(destX, destY), agId);
                    //addPercept(ag, Literal.parseLiteral("pos(" + destX + "," + destY + ")"));
                    nextX = destX; nextY = destY;
                } else {
                    model.fly(new Location(agX + (int) dirx, agY + (int) diry), agId);
                    nextX = (agX + (int) dirx); nextY = (agY + (int) diry);
                    //addPercept(ag, Literal.parseLiteral("pos(" + (agX + (int) dirx) + "," + (agY + (int) diry)  + ")"));
                }

                removePercept(ag, Literal.parseLiteral("pos(" + agX + "," + agY + ")"));
                addPercept(ag, Literal.parseLiteral("pos(" + nextX + "," + nextY  + ")"));

                return true;
                //logger.warning(action.toString());
            }
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
                //updateAgPercept(agId);
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
                model.setView(view);
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
        updateAgPercept("drone" + (ag + 1), ag);
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
	 
	 /*for (Location d : model.getVillages()) {
	     addPercept(agName, Literal.parseLiteral("village(" + d.x + "," + d.y + ")"));
	 }*/
    }

}
