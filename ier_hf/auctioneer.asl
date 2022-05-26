// this agent manages the auction and identify the winner

!start.

+!start : true 
	<- .print("Program started!").

			  //N-nth auction
			  //Village posX: X
			  //Village posY: Y
			  //Weight of the item: W
+!start_auction(N, X, Y, W)   // this goal is created by the GUI of the agent
    <- .print("Auction number ", N, " started!");
	.broadcast(tell, auction(N, X, Y, W)).

+place_bid(N,_)     // receives bids and checks for new winner
   :  .findall(b(V,A),place_bid(N,V)[source(A)],L) &
      .length(L,3)  // all 3 expected bids was received
   <- .min(L,b(V,W));
      .print("Winner is ",W," with ", V);
      .broadcast(tell, winner(N,W));
      .abolish(place_bid(N,_)).
