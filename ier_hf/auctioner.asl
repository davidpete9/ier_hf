// Agent auctioner in project ier_hf.mas2j

/* Initial beliefs and rules */

/* Initial goals */

// this agent manages the auction and identify the winner

+!start_auction(N)   // this goal is created by the GUI of the agent <- kell a guija is
    <- .broadcast(tell, auction(N)).


@pb1[atomic]
+place_bid(N,_)     // receives bids and checks for new winner
   :  .findall(b(V,A),place_bid(N,V)[source(A)],L) &
      .length(L,3)  // ez hotziher hogy nem 3
   <- .min(L,b(V,W));
      .print("Winner is ",W," with ", V);
      show_winner(N,W); // show it in the GUI, kell ez?
      .broadcast(tell, winner(W));
      .abolish(place_bid(N,_)).
