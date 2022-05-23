package world;

import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

import java.util.logging.Level;
import java.util.logging.Logger;

import java.util.List;
import java.util.ArrayList;

public class WorldModel extends GridWorldModel {

    private Logger logger = Logger.getLogger("ier_hf.mas2j." + WorldModel.class.getName());


    public static final int   MAIN_DEPOT  = 16;
    public static final int   DEPOT = 32;
    public static final int   VILLAGE = 64;

    Location mainDepot;

    int num_of_depots = 2;
    int num_of_villags = 5;
    
    int nbAgs = 0;

    List<Location> depots = new ArrayList<Location>();

    List<Location> villages =  new ArrayList<Location>();

    // singleton pattern
    protected static WorldModel model = null;

    private WorldModel(int w, int h, int nbAgs) {
        super(w, h, nbAgs);
        this.nbAgs = nbAgs;
        this.initWorld();
    }

    synchronized public static WorldModel create(int w, int h, int nbAgs) {
        if (model == null) {
            model = new WorldModel(w, h, nbAgs);
        }
        return model;
    }

    public List<Location> getDepots() { return this.depots; }

    public List<Location> getVillages() {return this.villages; }

    public static WorldModel get() {
        return model;
    }

    public static void destroy() {
        model = null;
    }

    public Location getMainDepot() {
        return mainDepot;
    }

    public void setMainDepot(int x, int y) {
        mainDepot = new Location(x, y);
        this.add(WorldModel.MAIN_DEPOT,x,y);
    }

    private void addVillage(int x, int y) {
        this.villages.add(new Location(x,y));
        this.add(WorldModel.VILLAGE, x,y);
    }

    private void addDepot(int x, int y) {
        this.depots.add(new Location(x, y));
        this.add(WorldModel.DEPOT, x,y);
    }


    public WorldModel initWorld() {
        this.setMainDepot(10,10);
        this.addDepot(2,4);
        this.addDepot(10,3);

        this.addVillage(10,13);
        this.addVillage(14,2);
        this.addVillage(5,12);
        this.addVillage(23,5);
        this.addVillage(1,1);
        this.addVillage(5,5);
        
         for (int i = 0; i < this.nbAgs; i++) {
          setAgPos(i, mainDepot.x,mainDepot.y	);
        }
        return model;

    }

    public void fly(Location dest, int agId) {
        Location l = getAgPos(agId);
        setAgPos(agId, dest);
        logger.info("ITT VAGYUNK");
        logger.info("Pos: " + l.x + " " + l.y);
        l = getAgPos(agId);
        logger.info("Pos: " + l.x + " " + l.y);
        logger.info("DEst: " + dest.x + " " + dest.y);

        view.update(mainDepot.x, mainDepot.y);
        view.repaint();

        return;
    }

}
