package jia;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.Term;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.ArrayList;
import java.util.List;

import mining.WorldModel;

public class calc_cost extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception {
        try {
            /*
              - tömeg
              - hova
              - meddig hova  
            */
           
         }
         return un.unifies(terms[4], new Atom(sAction));
        } catch (Throwable e) {
            e.printStackTrace();
            return false;
        }
    }
}

