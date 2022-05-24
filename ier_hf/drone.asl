/*Initial beliefs*/
charge(100).
near(10,10).
		
+auction(N, X, Y, W)[source(S)] : (.my_name(I))
	<- ?pos(AgX, AgY); 
		?weight(Capacity);
		?charge(Charge);
		?speed(Speed);
		.print("it is I:", I, " and my position is ", AgX, " ", AgY);
		// agent posX, agent posY, goal pos X, goal pos Y,
		// Capacity -> drone weight capacity
		// W -> weight of the actual item
		// Charge -> actual charge of the drone
		// Speed -> velocity of the drone
		
		// StationX, StationY -> closest (relative to the destination) refueling station's coords
		// B -> calculated cost
		calc.calc_cost(AgX, AgY, X, Y, Capacity, W, Charge, Speed, StationX, StationY, Bid);
		+next(X, Y);
		+near(StationX,StationY);
		.print("Calculated cost: ", Bid);
		.send(S, tell, place_bid(N, Bid)).		
		
// TODO: modositani azt, hogy mi van ha nyer		
+winner(N,W)[source(S)] : (.my_name(I) & winner(N,I)) 
	<-	.print("I WON.... But at what cost?!");
		?next(SX,SY);
		!at(SX,SY);
		.print("Order delivered!").
	
	
// TODO: modositani azt, hogy mi van ha nem nyer
+winner(N,W)[source(S)] : (.my_name(I) & not winner(N,I)) 
	<- .print("I did not win :(").
	
+!at(PX, PY) : pos(PX,PY) & near(SX, SY) & not (SX == PX) & not (SY == PY)
	<- .print("Arrived at the destination");
		?near(SX, SY);
		.print("Next: ", SX, " ", SY);
		-next(PX, PY);
		!at(SX, SY).	

+!at(PX, PY) : pos(PX, PY) & near(SX, SY) & (SX == PX) & (SY = PY)
	<- -near(SX, SY);
		.print("Arrived at the station").

+!at(PX, PY) : not pos(PX,PY) 
	<- .print("Megyek oda: ", PX, " ", PY);
		//?near(SX,SY);
		?pos(AgX, AgY);
		?speed(Speed);	
		move_towards(AgX, AgY, PX, PY, Speed);
		!at(PX, PY).		
