package calc;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.ArrayList;
import java.util.List;

import java.lang.Math;

import world.WorldModel;

public class calc_cost extends DefaultInternalAction {
    boolean printinfo = true;

    public int get_dist(int posX, int posY, int goalX, int goalY) {
        int dX = posX - goalX;
        int dY = posY - goalY;

        int dist = (int) Math.round(Math.sqrt(dX * dX + dY * dY));

        return dist;
    }

    public Location get_closest_station(int posX, int posY, ArrayList<Location> stations) {
        int minDist = 100000000;
        Location ret = new Location(99999999,999999999);

        for (Location loc: stations) {
            int tmp = get_dist(posX, posY, loc.x, loc.y);
            if (tmp < minDist) {
                minDist = tmp;
                ret = loc;
            }
        }

        return ret;
    }

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception {
        int maxBid = 9999999; // The agent bids an unrealistically high bid, if it cannot do this delivery

        int posX = (int) ((NumberTerm) terms[0]).solve(); // Drone pos x
        int posY = (int) ((NumberTerm) terms[1]).solve(); // Drone pos y

        int goalX = (int) ((NumberTerm) terms[2]).solve(); // Goal x
        int goalY = (int) ((NumberTerm) terms[3]).solve(); // Goal y

        int cargoCapacity = (int) ((NumberTerm) terms[4]).solve(); // The heaviest object the drone can carry
        int itemWeight = (int) ((NumberTerm) terms[5]).solve(); // Weight of the item
        int charge = (int) ((NumberTerm) terms[6]).solve(); // 0-100% - Charge of the drone's battery
        int speed = (int) ((NumberTerm) terms[7]).solve(); // Speed of the drone

        double energyConsumption = (itemWeight/cargoCapacity) + 0.314; // For one unit of distance

        WorldModel model = WorldModel.get();
        ArrayList<Location> refuelStations = new ArrayList<Location>();
        refuelStations.addAll(model.getDepots());
        refuelStations.add(model.getMainDepot());

        Location closestStationToDestination = get_closest_station(goalX, goalY, refuelStations);
        
        //distance from Goal To Station
        int distFromStation = get_dist(goalX, goalY, closestStationToDestination.x, closestStationToDestination.y);

	//From position to Goal
        int dist = get_dist(posX, posY, goalX, goalY);

        double cost = (dist + distFromStation) / (double) speed; // The bid/cost is the distance it takes for the drone to complete the delivery times the speed


	int chargeRequired = (int)Math.round((dist + distFromStation) * energyConsumption);
        int chargeLeft = charge - chargeRequired; 

        int actualCost = maxBid;
        
        int chargeAtX = 0;
        int chargeAtY = 0;
        double chargeT = 0;
    		
    	double chargePerTValue = 0.5;
    	    
        if (chargeLeft > 0) {
            actualCost = (int) Math.round(cost * 100);
        }
        else { 
        //Charge first. Get the closest station from the actual position
        Location closestStationFromPos = get_closest_station(posX, posY, refuelStations);
        int distToStation = get_dist(posX, posY, closestStationFromPos.x, closestStationFromPos.y);
        int chargeLeftAtStation = charge - (int) Math.round(distToStation * energyConsumption);
        if (chargeLeftAtStation > 0) {
        	chargeAtX = closestStationFromPos.x;
        	chargeAtY = closestStationFromPos.y;
        	chargeT = (chargeRequired-chargeLeftAtStation)*chargePerTValue;
        	chargeLeft = 0;
        }
        }

        if (printinfo) {
            ts.getLogger().info("Closest station " + closestStationToDestination.x + " " + closestStationToDestination.y);
            ts.getLogger().info("Distance fromt there " + distFromStation);
            ts.getLogger().info("Distance to goal: " + dist);
            ts.getLogger().info("Calculated cost: " + cost + " all dist: " + (dist + distFromStation) + " and speed: " + speed);
            ts.getLogger().info("Charge left: " + chargeLeft);
        }

        un.unifies(terms[8], new NumberTermImpl(closestStationToDestination.x));
        un.unifies(terms[9], new NumberTermImpl(closestStationToDestination.y));
        un.unifies(terms[10], new NumberTermImpl(actualCost));
        un.unifies(terms[11], new NumberTermImpl(chargeLeft));
        un.unifies(terms[13], new NumberTermImpl(chargeAtX));
        un.unifies(terms[13], new NumberTermImpl(chargeAtY));
        return un.unifies(terms[14], new NumberTermImpl(chargeT));
        
     	//return true;
    }
}

