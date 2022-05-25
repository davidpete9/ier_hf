/*Initial beliefs*/
// Current charge, its value lies between 0 and 100
/*charge(100).

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

lastBaseTime(0).*/

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
		//.print("Make route stuff: ", X, " ", Y, " ", ChargeAtX, " ", ChargeAtY, " ", ChargeT);
		
		?lastBaseTime(LB);
		set_last_basetime(LB, BaseTime);
		set_basetime(BaseTime, BaseTime + Bid);
		
		?lastCharge(LC);
		set_last_charge(LC, Charge);
		set_charge(Charge, ChargeLeftAfter);
		
		!makeRoute(X, Y, StartX, StartY, ReturnX, ReturnY, ChargeT);
		
		?routenr(NR);
		+auctionRouteNr(N, NR);
		
		.print("Calculated cost: ", Bid);
		.print("Charge left: ", ChargeLeftAfter);
		.print("ChargeT ", ChargeT);
		.send(S, tell, place_bid(N, Bid)).		
		
+!makeRoute(GoalX, GoalY, StartX, StartY, ReturnX, ReturnY, ChargeT) : (StartX == 10) & (StartY == 10) 
	<-	?chargeT(C);
		set_charget(C, ChargeT);
		
		?rechargeLocation(RX, RY);
		set_rechargelocation(RX, RY, StartX, StartY);
		
		?routenr(RouteNr);
		set_routenr(RouteNr, RouteNr + 4);
		
		.print("Start ", StartX, " ", StartY);
		.print("Goal ", GoalX, " ", GoalY);
		.print("Return ", ReturnX, " ", ReturnY);
		
		add_route(RouteNr, StartX, StartY);
		add_route(RouteNr+1, StartX, StartY); // Route duplication so the number of routes to delete is the same
		add_route(RouteNr+2, GoalX, GoalY);
		add_route(RouteNr+3, ReturnX, ReturnY).
		
+!makeRoute(GoalX, GoalY, StartX, StartY, ReturnX, ReturnX, ChargeT) : not (StartX == 10) |  not (StartY == 10) 
	<-	?chargeT(C);
		set_charget(C, ChargeT);
		
		?rechargeLocation(RX, RY);
		set_rechargelocation(RX, RY, StartX, StartY);

		?routenr(RouteNr);
		set_routenr(RouteNr, RouteNr + 4);
		
		add_route(RouteNr, StartX, StartY);
		add_route(RouteNr+1, 10, 10);
		add_route(RouteNr+2, X, Y);
		add_route(RouteNr+3, ReturnX, ReturnY).
		
+!setLastPos(N) : true 
	<- ?lastDest(LD, LY);
		//-lastDest(LD, LY);
		?auctionRouteNr(N, NR);
		.print("Route nr, for debugging purposes: ", NR, " and routenr-1: ", NR-1);
		?route(NR-1, NX, NY);
		set_last_dest(LD, LY, NX, NY).
		//+lastDest(NX, NY).
		
// TODO: modositani azt, hogy mi van ha nyer		 
+winner(N,W)[source(S)] : (.my_name(I) & winner(N,I) & delivering(false)) 
	<-	!setLastPos(N);
		set_delivering(true);
		?iterator(Iter);
		?route(Iter, NextX, NextY);
		.print("I WON.... But at what cost?!");
		!move(Iter, NextX, NextY).
	
+winner(N,W)[source(S)] : (.my_name(I) & winner(N,I) & delivering(true)) 
	<-	!setLastPos(N);
		.print("I WON.... But at what cost?!").
	
		
// TODO: modositani azt, hogy mi van ha nem nyer
+winner(N,W)[source(S)] : (.my_name(I) & chargeT(C) & not winner(N,I)) 
	<- 	?charge(Charge);
		?lastCharge(LC);
		?baseTime(BT);
		?lastBaseTime(LBT);
		
		.print("Charge and lc ", Charge, " ", LC);
		
		set_basetime(BT, LBT);
		set_charge(Charge, LC);
		
		//?routenr(RouteNr);
		?auctionRouteNr(N, RouteNr);
		
		.print("Route number: ", RouteNr);
		?route(RouteNr-1, AX, AY);
		?route(RouteNr-2, BX, BY);
		?route(RouteNr-3, CX, CY);
		?route(RouteNr-4, DX, DY);
		
		delete_route(RouteNr-4, DX, DY);
		delete_route(RouteNr-3, CX, CY);
		delete_route(RouteNr-2, BX, BY);
		delete_route(RouteNr-1, AX, AY);
		set_routenr(RouteNr, RouteNr - 4);
		.print("I did not win :(").
	
		
// Movement / charge related		
//------------------------------------------------------------------------------		

+!autocharge: delivering(true) | (charge(C) & C >= 100)
  <- true.

+!autocharge: delivering(false) & charge(C) & C < 100
  <- set_charge(C,C+1);
     !autocharge.

+!charge(ChargeT) : (ChargeT == 0) <- true.
+!charge(ChargeT) : not (ChargeT == 0) 
	<- 	!lowerBaseTime;
		.print("CHARGING TIME BABY");
		?chargeT(ChargeTime);
		set_charget(ChargeTime, ChargeTime-1);
		
		set_charge(Charge, 100);// Should be temporary
		
		!charge(ChargeTime-1).
		
+!move(Iter, PX, PY) : pos(PX, PY) & routenr(NR) & (Iter == (NR-1))
	<-	!lowerBaseTime;
		set_delivering(false);
		!autocharge;
		.print("Finished all delivery, awaiting orders").

+!move(Iter, PX, PY) : pos(PX, PY) & rechargeLocation(PX, PY) & routenr(NR) & not (Iter == (NR-1))
	<-	!lowerBaseTime;
		set_iterator(Iter, Iter+1);
		
		?chargeT(ChargeTime);
		!charge(ChargeTime);
		
		//-route(Iter, PX, PY);
		
		?route(Iter+1, NextX, NextY);
		!move(Iter+1, NextX, NextY).
		
+!move(Iter, PX, PY) : pos(PX, PY) & not rechargeLocation(PX, PY) & routenr(NR) & not (Iter == (NR-1))
	<-	!lowerBaseTime;
		set_iterator(Iter, Iter+1);
		
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
	<- 	set_basetime(BT, BT-1).
		
+!lowerBaseTime : baseTime(BT) & (BT <= 0) 
	<- 	true.
