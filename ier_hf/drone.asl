/*Initial beliefs*/
charge(100).
		
+auction(N, X, Y, W)[source(S)] : (.my_name(I))
	<- ?pos(AgX, AgY); 
		?weight(C);
		?charge(Q);
		.print("it is I:", I, " and my position is ", agX, " ", agY);
		// agent posX, agent posY, goal pos X, goal pos Y,
		// C -> drone weight capacity
		// W -> weight of the actual item
		// Q -> actual charge of the drone
		// 20 -> atm speed
		
		// SX, SY -> closest refueling stations' coords
		// B -> calculated cost
		calc.calc_cost(AgX, AgY, X, Y, C, W, Q, 20, SX, SY, B);
		.print("Calculated cost: ", B);
		.send(S, tell, place_bid(N, B)).
		
// TODO: modositani azt, hogy mi van ha nyer		
+winner(W)[source(S)] : (.my_name(I) & W == I) <- .print("I WON.... But at what cost?!").
// TODO: modositani azt, hogy mi van ha nem nyer
+winner(W)[source(S)] : (.my_name(I) & not (W == I)) <- .print("I did not win :(").
