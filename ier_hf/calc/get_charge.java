package calc;

import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Atom;
import jason.asSyntax.NumberTerm;
import jason.asSyntax.NumberTermImpl;
import jason.asSyntax.Term;
import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;



public class get_charge extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] terms) throws Exception {

        int chargeValue = (int) ((NumberTerm) terms[0]).solve(); 

        int energy = ChargeHelper.getChargePerTime();

        return un.unifies(terms[1], new NumberTermImpl(chargeValue+energy));
    }
}
