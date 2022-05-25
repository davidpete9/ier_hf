/*Initial beliefs*/
// Current charge, its value lies between 0 and 100
charge(100).

// Last charge - in case this drone does not win the auction
lastCharge(100).
		
// Number of auctions won
routenr(0).

// Actual delivery number
iterator(0).

// Last destination (Starts at 10,10)
lastDest(10, 10).

// Is the drone actually moving/delivering
delivering(false).

// Cycles it takes for the drone to fully charge
chargeT(0).

// Place where the drone recharges
rechargeLocation(0,0).

// Base time
baseTime(0).

lastBaseTime(0).

//------------------------------------------------------------------------------

// Minden dronnak vissza kell mennie a main depotba csomag felvetelehez
// A lastDestBol kell vissza menniuk a mainDepotba, majd a main depotbol
// az aktualis celhoz kell menniuk.

// Az utak a kovetkezokeppen neznek ki:
// route(nr, x, y).
// Ahol nr az az, hogy ez hanyadik teljesitendo ut,
// az x es y pedig az, ahol a dron megall

//------------------------------------------------------------------------------
+auction(N, X, Y, W)[source(S)] : (.my_name(I))
	<-	?pos(AgX, AgY); 
		?weight(Capacity);
		?charge(Charge);
		?speed(Speed);
		.print("it is I:", I, " and my position is ", AgX, " ", AgY);
		?lastDest(LastX, LastY);
		?baseTime(BaseTime);
		
		// agent posX, agent posY, goal pos X, goal pos Y,
		// Capacity -> drone weight capacity
		// W -> weight of the actual item
		// Charge -> actual charge of the drone
		// Speed -> velocity of the drone
		
		// StationX, StationY -> closest (relative to the destination) refueling station's coords
		// B -> calculated cost
		calc.calc_cost(LastX, LastY, X, Y, W, Capacity, Speed, Charge, BaseTime, ReturnX, ReturnY, Bid, ChargeLeftAfter, StartX, StartY, ChargeT);
		+auctionData(N, X, Y, W, ReturnX, ReturnY, Bid, ChargeLeftAfter, StartX, StartY, ChargeT);
		.send(S, tell, place_bid(N, Bid)).		
		
		
+!makeRoute(N) : true
	<-	.print("Making a route!");
		?auctionData(N, GoalX, GoalY, Weight, ReturnX, ReturnY, Bid, ChargeLeftAfter, StartX, StartY, ChargeT);
		.print("ChargeT: ", ChargeT);
		
		?baseTime(BaseTime);	
		?lastBaseTime(LB);
		-lastBaseTime(LB);
		+lastBaseTime(BaseTime);
		
		-baseTime(BaseTime);
		+baseTime(BaseTime + Bid);
		
		?charge(Charge);
		?lastCharge(LC);
		-lastCharge(LC);
		+lastCharge(Charge);
		-charge(Charge);
		+charge(ChargeLeftAfter);
	
		?chargeT(C);
		-chargeT(C);
		+chargeT(ChargeT);
		
		?rechargeLocation(RX, RY);
		-rechargeLocation(RX, RY);
		+rechargeLocation(StartX, StartY);
		
		?routenr(RouteNr);
		-routenr(RouteNr);
		+routenr(RouteNr+4);
		
		+route(RouteNr, StartX, StartY);
		+route(RouteNr+1, 10, 10);
		+route(RouteNr+2, GoalX, GoalY);
		+route(RouteNr+3, ReturnX, ReturnY).
		
+!setLastPos : true 
	<- 	?lastDest(LD, LY);
		-lastDest(LD, LY);
		?routenr(NR);
		?route(NR-1, NX, NY);
		+lastDest(NX, NY).
		
// TODO: modositani azt, hogy mi van ha nyer		 
+winner(N,W)[source(S)] : (.my_name(I) & winner(N,I) & delivering(false)) 
	<-	!makeRoute(N);
		!setLastPos;
		-delivering(false);
		+delivering(true);
		?iterator(Iter);
		?route(Iter, NextX, NextY);
		.print("I WON.... But at what cost?!");
		!move(Iter, NextX, NextY).
	
+winner(N,W)[source(S)] : (.my_name(I) & winner(N,I) & delivering(true)) 
	<-	!makeRoute(N);
		!setLastPos;
		.print("I WON.... But at what cost?!").
	
		
// TODO: modositani azt, hogy mi van ha nem nyer
+winner(N,W)[source(S)] : (.my_name(I) & chargeT(C) & not winner(N,I)) 
	<- 	.print("I did not win :(").
	
		
// Movement / charge related		
//------------------------------------------------------------------------------		
+!autocharge: delivering(true) | (charge(C) & (C >= 100))
  <- 	true.

+!autocharge: delivering(false) & (charge(C) & (C < 100))
  <- 	//set_charge(C,C+1);
  		?charge(Charge);
		-charge(Charge);
		+charge(Charge+10);
     	!autocharge.

+!charge(ChargeT) : (ChargeT == 0) <- true.
+!charge(ChargeT) : not (ChargeT == 0) 
	<- 	!lowerBaseTime;
		.print("CHARGING TIME BABY");
		?chargeT(ChargeTime);
		-chargeT(ChargeTime);
		+chargeT(ChargeTime-1);
		
		-charge(Charge);
		+charge(100); // Should be temporary
		
		!charge(ChargeTime-1).
		
+!move(Iter, PX, PY) : pos(PX, PY) & routenr(NR) & (Iter == (NR-1))
	<-	!lowerBaseTime;
		-delivering(true);
		+delivering(false);
		!autocharge;
		//-route(Iter, PX, PY);
		.print("Finished all delivery, awaiting orders").

+!move(Iter, PX, PY) : pos(PX, PY) & rechargeLocation(PX, PY) & routenr(NR) & not (Iter == (NR-1))
	<-	!lowerBaseTime;
		-iterator(Iter);
		+iterator(Iter+1);
		
		?chargeT(ChargeTime);
		!charge(ChargeTime);
		
		//-route(Iter, PX, PY);
		
		?route(Iter+1, NextX, NextY);
		!move(Iter+1, NextX, NextY).
		
+!move(Iter, PX, PY) : pos(PX, PY) & not rechargeLocation(PX, PY) & routenr(NR) & not (Iter == (NR-1))
	<-	!lowerBaseTime;
		-iterator(Iter);
		+iterator(Iter+1);
		
		//-route(Iter, PX, PY);
		
		?route(Iter+1, NextX, NextY);
		!move(Iter+1, NextX, NextY).
		
+!move(Iter, PX, PY) : not pos(PX, PY)
	<-	//.print("Megyek oda: ", PX, " ", PY);
		!lowerBaseTime;
		?pos(AgX, AgY);
		?speed(Speed);
		move_towards(AgX, AgY, PX, PY, Speed);
		!move(Iter, PX, PY).
		
+!lowerBaseTime : baseTime(BT) & (BT > 0) 
	<- 	?baseTime(BT);
		-baseTime(BT);
		+baseTime(BT-1);
		true.
		
+!lowerBaseTime : baseTime(BT) & (BT <= 0) 
	<- 	true.
