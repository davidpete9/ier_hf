
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
                //logger.warning(action.toString());

            } else if (action.getFunctor().equals("delete_route")) {

                int routeNr = Integer.parseInt(action.getTerm(0).toString());
                int x = Integer.parseInt(action.getTerm(1).toString());
                int y = Integer.parseInt(action.getTerm(2).toString());

                removePercept(ag, Literal.parseLiteral("route(" + routeNr + "," + x + "," + y + ")"));

            } else if (action.getFunctor().equals("add_route")) {

                int routeNr = Integer.parseInt(action.getTerm(0).toString());
                int x = Integer.parseInt(action.getTerm(1).toString());
                int y = Integer.parseInt(action.getTerm(2).toString());

                addPercept(ag, Literal.parseLiteral("route(" + routeNr + "," + x + "," + y + ")"));

            } else if (action.getFunctor().equals("set_charge")) {

                int oldCharge = Integer.parseInt(action.getTerm(0).toString());
                int newCharge = Integer.parseInt(action.getTerm(1).toString());

                removePercept(ag, Literal.parseLiteral("charge(" + oldCharge + ")"));
                addPercept(ag, Literal.parseLiteral("charge(" + newCharge + ")"));
                model.updateChargeValue(agId,newCharge);

            } else if (action.getFunctor().equals("set_last_charge")) {

                int oldCharge = Integer.parseInt(action.getTerm(0).toString());
                int newCharge = Integer.parseInt(action.getTerm(1).toString());

                removePercept(ag, Literal.parseLiteral("lastCharge(" + oldCharge + ")"));
                addPercept(ag, Literal.parseLiteral("lastCharge(" + newCharge + ")"));

            } else if (action.getFunctor().equals("set_basetime")) {

                int oldBT = Integer.parseInt(action.getTerm(0).toString());
                int newBT = Integer.parseInt(action.getTerm(1).toString());

                removePercept(ag, Literal.parseLiteral("baseTime(" + oldBT + ")"));
                addPercept(ag, Literal.parseLiteral("baseTime(" + newBT + ")"));

            } else if (action.getFunctor().equals("set_last_dest")) {

                logger.warning("last dest bby");
                int oldX = Integer.parseInt(action.getTerm(0).toString());
                int oldY = Integer.parseInt(action.getTerm(1).toString());

                int newX = Integer.parseInt(action.getTerm(2).toString());
                int newY = Integer.parseInt(action.getTerm(3).toString());
                logger.warning("Old pos: " + oldX + " " + oldY + " new pos: " + newX + " " + newY);

                removePercept(ag, Literal.parseLiteral("lastDest(" + oldX + "," + oldY + ")"));
                addPercept(ag, Literal.parseLiteral("lasDest(" + newX + "," + newY  + ")"));

            } else if (action.getFunctor().equals("set_delivering")) {

                Boolean val = Boolean.parseBoolean(action.getTerm(0).toString());

                removePercept(ag, Literal.parseLiteral("delivering(" + !val + ")"));
                addPercept(ag, Literal.parseLiteral("delivering(" + val + ")"));

            } else if (action.getFunctor().equals("set_last_basetime")) {

                int oldBT = Integer.parseInt(action.getTerm(0).toString());
                int newBT = Integer.parseInt(action.getTerm(1).toString());

                removePercept(ag, Literal.parseLiteral("lastBaseTime(" + oldBT + ")"));
                addPercept(ag, Literal.parseLiteral("lastBaseTime(" + newBT + ")"));

            } else if (action.getFunctor().equals("set_routenr")) {

                int oldNr = Integer.parseInt(action.getTerm(0).toString());
                int newNr = Integer.parseInt(action.getTerm(1).toString());

                removePercept(ag, Literal.parseLiteral("routenr(" + oldNr + ")"));
                addPercept(ag, Literal.parseLiteral("routenr(" + newNr + ")"));

            } else if (action.getFunctor().equals("set_iterator")) {

                int oldIt = Integer.parseInt(action.getTerm(0).toString());
                int newIt = Integer.parseInt(action.getTerm(1).toString());

                removePercept(ag, Literal.parseLiteral("iterator(" + oldIt + ")"));
                addPercept(ag, Literal.parseLiteral("iterator(" + newIt + ")"));

            } else if (action.getFunctor().equals("set_charget")) {

                int oldC = Integer.parseInt(action.getTerm(0).toString());
                int newC = Integer.parseInt(action.getTerm(1).toString());

                removePercept(ag, Literal.parseLiteral("chargeT(" + oldC + ")"));
                addPercept(ag, Literal.parseLiteral("chargeT(" + newC + ")"));

            } else if (action.getFunctor().equals("set_rechargelocation")) {

                int oldX = Integer.parseInt(action.getTerm(0).toString());
                int oldY = Integer.parseInt(action.getTerm(1).toString());

                int newX = Integer.parseInt(action.getTerm(2).toString());
                int newY = Integer.parseInt(action.getTerm(3).toString());
                //logger.warning("Old pos: " + oldX + " " + oldY + " new pos: " + newX + " " + newY);

                removePercept(ag, Literal.parseLiteral("rechargeLocation(" + oldX + "," + oldY + ")"));
                addPercept(ag, Literal.parseLiteral("rechargeLocation(" + newX + "," + newY  + ")"));

            }

            return true;
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
            /*if (result) {
                //updateAgPercept(agId);
                return true;
            }*/
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
