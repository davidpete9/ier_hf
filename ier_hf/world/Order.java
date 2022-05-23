package world;

import jason.environment.grid.Location;


public class Order {


 Location loc;
 double w;

 public Order(Location l, double weight) {
 this.loc = l;
 this.w = weight;
 }
 
 
 public Location getLocation() {
    return this.loc;
 }
 
 public double getWeight() {
   return this.w;
 }


}
