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
        int distFromStation = get_dist(goalX, goalY, closestStationToDestination.x, closestStationToDestination.y);

        int dist = get_dist(posX, posY, goalX, goalY);

        double cost = (dist + distFromStation) / (double) speed; // The bid/cost is the distance it takes for the drone to complete the delivery times the speed

        int chargeLeft = charge - (int) Math.round((dist + distFromStation) * energyConsumption);

        int actualCost = maxBid;
        if (chargeLeft > 0) {
            actualCost = (int) Math.round(cost * 100);
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
        return un.unifies(terms[10], new NumberTermImpl(actualCost));
     	//return true;
    }
}

