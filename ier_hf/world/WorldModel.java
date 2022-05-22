import jason.environment.grid.GridWorldModel;
import jason.environment.grid.Location;

public class WorldModel extends GridWorldModel {

    public static final int   MAIN_DEPOT  = 16;
    public static final int   DEPOT = 32;
    public static final int   VILLAGE = 64;

    Location mainDepot;

    // singleton pattern
    protected static WorldModel model = null;

    synchronized public static WorldModel create(int w, int h, int nbAgs) {
        if (model == null) {
            model = new WorldModel(w, h, nbAgs);
        }
        return model;
    }

    public static WorldModel get() {
        return model;
    }

    public static void destroy() {
        model = null;
    }

    public Location getMainDepot() {
        return depot;
    }

    public void setMainDepot(int x, int y) {
        mainDepot = new Location(x, y);
        data[x][y] = MAIN_DEPOT;
    }


}