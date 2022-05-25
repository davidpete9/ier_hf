package agent;

import jason.asSemantics.Agent;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;
import jason.bb.DefaultBeliefBase;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import java.util.logging.Logger;


public class DroneParamsBB extends DefaultBeliefBase {
    static private Logger logger = Logger.getLogger(DroneParamsBB.class.getName());

    Map<String, Literal> uniqueBels = new HashMap<String, Literal>();
    Unifier u = new Unifier();

    public void init(Agent ag, String[] args) {
        for (int i = 0; i < args.length; i++) {
            Literal arg = Literal.parseLiteral(args[i]);
            logger.warning(arg.toString());
            super.add(arg);
        }
    }

}
