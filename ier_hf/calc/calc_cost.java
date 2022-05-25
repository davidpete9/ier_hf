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
    
    public int timeCost(int dist, int speed) {
    	return (int)Math.ceil(dist/speed);
    }
    
    

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception {
        int maxBid = 9999999; // The agent bids an unrealistically high bid, if it cannot do this delivery

	// The last planned position of the drone
        int lastX = (int) ((NumberTerm) terms[0]).solve(); 
        int lastY = (int) ((NumberTerm) terms[1]).solve(); 

	// Order parameters
        int goalX = (int) ((NumberTerm) terms[2]).solve();
        int goalY = (int) ((NumberTerm) terms[3]).solve();
        int itemWeight = (int) ((NumberTerm) terms[4]).solve(); // Weight of the item
        
       
        //Drone parameters
        int cargoCapacity = (int) ((NumberTerm) terms[5]).solve(); // The heaviest object the drone can carry
        int speed = (int) ((NumberTerm) terms[6]).solve(); 
        int chargeWillBe = (int) ((NumberTerm) terms[7]).solve(); // the battery level of the drone, predicted at the time when this order can be completed.
        
        int baseTime = (int) ((NumberTerm) terms[8]).solve(); // The time required to finish the remaining tasks.
        
        

	if (itemWeight > cargoCapacity) {
	ts.getLogger().info("Nem tudja elvinni a dron, tul nagy a csomag.");
	un.unifies(terms[9], new NumberTermImpl(10));//ReturnAtX
	un.unifies(terms[10], new NumberTermImpl(10)); //ReturnAtY
	un.unifies(terms[11], new NumberTermImpl(maxBid));
	un.unifies(terms[12], new NumberTermImpl(0));
	un.unifies(terms[13], new NumberTermImpl(10)); //StartAtX
	un.unifies(terms[14], new NumberTermImpl(10)); //StartAtY
        return un.unifies(terms[15], new NumberTermImpl(0)); 
	}
	

	// Drone has this information in it's belief base, but it is simpler to query this way.
        WorldModel model = WorldModel.get();
        ArrayList<Location> refuelStations = new ArrayList<Location>();
        refuelStations.addAll(model.getDepots());
        refuelStations.add(model.getMainDepot()); 
        
        Location main = model.getMainDepot();
        
        //Cost calculation:
        //  Option 1:
        //  1. Path cost to mainDepot (no cargo) (if not there)
        //  2. Path from mainDepot to destination 
        //  3. From destionation go back to main depot. (or )
        
        
        
        
        
        
        int to_maindepot = timeCost(get_dist(lastX,lastY, main.x,main.y),speed);  
        
        int from_depot_to_goal = timeCost(get_dist(main.x, main.y, goalX, goalY),speed); 
        
        int from_goal_back_to_main_depot = timeCost(get_dist(goalX,goalY,main.x,main.y),speed);
        
        
        int costWithCargo = CalculateEnergyLoss.calculateEnergyPerTime(cargoCapacity,itemWeight);
        int costWithNoCargo = CalculateEnergyLoss.calculateEnergyPerTime(cargoCapacity,0);
        
        int chargeCost = (to_maindepot+from_goal_back_to_main_depot)*costWithNoCargo+from_depot_to_goal*costWithCargo;
        
        
        int chargeT = 0;
        int full_cost = baseTime+to_maindepot+from_depot_to_goal;
        ts.getLogger().info(" main to goalll..."+get_dist(main.x, main.y, goalX, goalY));
        
        ts.getLogger().info("full cost: "+full_cost);
        
        if (chargeWillBe - chargeCost > 0) { //1. OK. No need to charge, it can go back to main AFTER the order!
        	        ts.getLogger().info("option 1, main-main no charge");
        	         un.unifies(terms[9], new NumberTermImpl(main.x));//ReturnAtX
	                 un.unifies(terms[10], new NumberTermImpl(main.y)); //ReturnAtY
			 un.unifies(terms[11], new NumberTermImpl(full_cost));
			 un.unifies(terms[12], new NumberTermImpl(chargeWillBe-chargeCost));
			 un.unifies(terms[13], new NumberTermImpl(main.x)); //StartAtX
			 un.unifies(terms[14], new NumberTermImpl(main.y)); //StartAtY
       	 return un.unifies(terms[15], new NumberTermImpl(0));
           
        }
        else {
           Location closestStationToDestination = get_closest_station(goalX, goalY, refuelStations);
           int from_goal_to_closest_depo = timeCost(get_dist(goalX,goalY,closestStationToDestination.x,closestStationToDestination.y),speed);
           
           int chargeCostToClosest = (to_maindepot+from_goal_to_closest_depo)*costWithNoCargo+from_depot_to_goal*costWithCargo;
           
           if (chargeWillBe - chargeCostToClosest > 0 ) { //2. Ok. No need to charge, but it goes to a refuel depot insted of the main AFTER it completes the order.
           		 ts.getLogger().info("option 2, main-depo no charge");
           	         un.unifies(terms[9], new NumberTermImpl(closestStationToDestination.x));
			 un.unifies(terms[10], new NumberTermImpl(closestStationToDestination.y));
			 un.unifies(terms[11], new NumberTermImpl(full_cost));
			 un.unifies(terms[12], new NumberTermImpl(chargeWillBe-chargeCostToClosest));
			 un.unifies(terms[13], new NumberTermImpl(main.x));
			 un.unifies(terms[14], new NumberTermImpl(main.y));
       	 return un.unifies(terms[15], new NumberTermImpl(0));
           
           }
           else { // It must charge before! 
           	chargeCost = to_maindepot*costWithNoCargo;
           	if (chargeWillBe - chargeCost > 0) { // 3. It can go to main depot to charge first.
           	     //Charge cost need to be enught to after the delivery it should be enought to go to the closest depot.
           	     ts.getLogger().info("option 3, main-depo "+chargeT+" charge.");
           	         chargeT = CalculateEnergyLoss.getChargeTime(chargeCostToClosest-(chargeWillBe-chargeCost));
           	         un.unifies(terms[9], new NumberTermImpl(closestStationToDestination.x));//ReturnAtX
	                 un.unifies(terms[10], new NumberTermImpl(closestStationToDestination.y)); //ReturnAtY
           	     	 un.unifies(terms[11], new NumberTermImpl(full_cost+chargeT));
			 un.unifies(terms[12], new NumberTermImpl(0));
			 un.unifies(terms[13], new NumberTermImpl(main.x));
			 un.unifies(terms[14], new NumberTermImpl(main.y)); //StartAtY
       	    return un.unifies(terms[15], new NumberTermImpl(chargeT));
           	     
           	}
           	else { //Cant go to main depo with this charge level, but may be can to a depot.
           	   Location closestDepot = get_closest_station(lastX, lastY, refuelStations);
           	   int to_closest_depot = timeCost(get_dist(lastX,lastY,closestDepot.x,closestDepot.y),speed);
           	   chargeCost = to_closest_depot*costWithNoCargo;
           	   if (chargeWillBe - chargeCost > 0) {
           	   	//Charge cost: charge need to be enought to go to main depot, and than deliver the package, and than go back to a closest station.
           	   	int from_depot_to_main = timeCost(get_dist(closestDepot.x,closestDepot.y,main.x,main.y),speed);
           	   	int chargeToMain = from_depot_to_main*costWithNoCargo;
           	   	int fromMainToGoalAndClosest = from_depot_to_goal*costWithCargo+from_goal_to_closest_depo*costWithNoCargo;
           	   	
           	   	chargeT = CalculateEnergyLoss.getChargeTime((chargeCost+chargeToMain+fromMainToGoalAndClosest)-chargeWillBe);
           	   	ts.getLogger().info("option 4, depo-depo/main "+chargeT+" charge.");
           	   	un.unifies(terms[9], new NumberTermImpl(closestStationToDestination.x));//ReturnAtX
	                un.unifies(terms[10], new NumberTermImpl(closestStationToDestination.y)); //ReturnAtY
           	     	un.unifies(terms[11], new NumberTermImpl(full_cost+chargeT+to_closest_depot+from_depot_to_main));
			un.unifies(terms[12], new NumberTermImpl(0));
			un.unifies(terms[13], new NumberTermImpl(closestDepot.x)); //StartAtX = ChargeAtX
			un.unifies(terms[14], new NumberTermImpl(closestDepot.y)); //StartAtY
       	        return un.unifies(terms[15], new NumberTermImpl(chargeT));
           	   }
           	   else {
           	      ts.getLogger().info("A dron lezuhan, nincs eleg toltottsege sehova :((");
           	      return true;
           	   }
           	}
           
           }	
		        
        }


      /*  if (printinfo) {
            ts.getLogger().info("Closest station " + closestStationToDestination.x + " " + closestStationToDestination.y);
            ts.getLogger().info("Distance fromt there " + distFromStation);
            ts.getLogger().info("Distance to goal: " + dist);
            ts.getLogger().info("Calculated cost: " + cost + " all dist: " + (dist + distFromStation) + " and speed: " + speed);
            ts.getLogger().info("Charge left: " + chargeLeft);
        }*/

        
     	//return true;
    }
}

