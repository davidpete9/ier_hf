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
		// C -> drone weight capacity
		// W -> weight of the actual item
		// Q -> actual charge of the drone
		// 20 -> atm speed
		
		// SX, SY -> closest refueling stations' coords
		// B -> calculated cost
		calc.calc_cost(AgX, AgY, X, Y, Capacity, W, Charge, Speed, StationX, StationY, Bid);
		+near(StationX,StationY);
		.print("Calculated cost: ", Bid);
		.send(S, tell, place_bid(N, Bid)).
		
// TODO: modositani azt, hogy mi van ha nyer		
+winner(N,W)[source(S)] : (.my_name(I) & winner(N,I)) 
	<- ?near(SX,SY);
		!at(SX,SY);
		.print("I WON.... But at what cost?!").
	
	
// TODO: modositani azt, hogy mi van ha nem nyer
+winner(N,W)[source(S)] : (.my_name(I) & not winner(N,I)) 
	<- .print("I did not win :(").
	
+!at(PX, PY) : pos(PX,PY) <- .print("YEA").	
+!at(PX, PY) : not pos(PX,PY) 
	<- .print("Megyek oda: ", PX, " ", PY);
		?near(SX,SY);
		?pos(AgX, AgY);
		?speed(Speed);	
		move_towards(AgX, AgY, SX, SY, Speed);
		!at(SX,SY).		
