/*Initial beliefs*/
charge(100).
		
// Number of auctions won
routenr(0).

// Actual delivery number
iterator(0).

// Last destination (Starts at 10,10)
lastDest(10, 10).

// Is the drone actually moving/delivering
delivering(false). 

// Minden dronnak vissza kell mennie a main depotba csomag felvetelehez
// A lastDestBol kell vissza menniuk a mainDepotba, majd a main depotbol
// az aktualis celhoz kell menniuk.

// Az utak a kovetkezokeppen neznek ki:
// route(nr, x, y).
// Ahol nr az az, hogy ez hanyadik teljesitendo ut,
// az x es y pedig az, ahol a dron megall

//------------------------------------------------------------------------------
+auction(N, X, Y, W)[source(S)] : (.my_name(I))
	<- ?pos(AgX, AgY); 
		?weight(Capacity);
		?charge(Charge);
		?speed(Speed);
		.print("it is I:", I, " and my position is ", AgX, " ", AgY);
		?lastDest(LastX, LastY);
		
		// agent posX, agent posY, goal pos X, goal pos Y,
		// Capacity -> drone weight capacity
		// W -> weight of the actual item
		// Charge -> actual charge of the drone
		// Speed -> velocity of the drone
		
		// StationX, StationY -> closest (relative to the destination) refueling station's coords
		// B -> calculated cost
		calc.calc_cost(LastX, LastY, X, Y, Capacity, W, Charge, Speed, StationX, StationY, Bid, ChargeLeftAfter, ChargeAtX,ChargeAtY,ChargeT);
		
		?routenr(RouteNr);
		-routenr(RouteNr);
		+routenr(RouteNr+2);
		
		+route(RouteNr, X, Y);
		+route(RouteNr+1, 10, 10);

		
		.print("Calculated cost: ", Bid);
		.print("ChargeT ", ChargeT);
		.send(S, tell, place_bid(N, Bid)).		
		
// TODO: modositani azt, hogy mi van ha nyer		 
+winner(N,W)[source(S)] : (.my_name(I) & winner(N,I) & delivering(false)) 
	<-	!setLastPos;
		-delivering(false);
		+delivering(true);
		?iterator(Iter);
		?route(Iter, NextX, NextY);
		.print("I WON.... But at what cost?!");
		!move(Iter, NextX, NextY).
		/*?next(N, SX, SY);
		!at(N, SX,SY);
		.print("Order delivered!").*/
	
+winner(N,W)[source(S)] : (.my_name(I) & winner(N,I) & delivering(true)) 
	<-	!setLastPos;
		.print("I WON.... But at what cost?!").
	
+!setLastPos : true 
	<- ?lastDest(LD, LY);
		-lastDest(LD, LY);
		?routenr(NR);
		?route(NR-1, NX, NY);
		+lastDest(NX, NY).
	
// TODO: modositani azt, hogy mi van ha nem nyer
+winner(N,W)[source(S)] : (.my_name(I) & not winner(N,I)) 
	<- 	?routenr(RouteNr);
		.print("Route number ", RouteNr);
		?route(RouteNr-2, DX, DY);
		.print("Route number ", RouteNr, " Dest: ", DX, " ", DY);
		
		-route(RouteNr-2, DX, DY);
		-route(RouteNr-1, 10, 10);
		-routenr(RouteNr);
		+routenr(RouteNr-2);
		.print("I did not win :(").
	
+!move(Iter, PX, PY) : pos(PX, PY) & routenr(NR) & (Iter == (NR-1))
	<-	-delivering(true);
		+delivering(false);
		.print("Finished all delivery, awaiting orders").
		
+!move(Iter, PX, PY) : pos(PX, PY) & routenr(NR) & not (Iter == (NR-1))
	<-	-iterator(Iter);
		+iterator(Iter+1);
		?route(Iter+1, NextX, NextY);
		!move(Iter+1, NextX, NextY).
		
+!move(Iter, PX, PY) : not pos(PX, PY)
	<-	.print("Megyek oda: ", PX, " ", PY);
		?pos(AgX, AgY);
		?speed(Speed);
		move_towards(AgX, AgY, PX, PY, Speed);
		!move(Iter, PX, PY).
