package fr.pollux28.mccanon.cannon;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import org.bukkit.Location;

public class CannonManager {

    private final Object2ObjectOpenHashMap<String, Cannon> cannons = new Object2ObjectOpenHashMap<>();


    public CannonManager() {
    }

    public void addCannon(Cannon cannon) {
        cannons.put(cannon.getName(), cannon);
    }

    public boolean removeCannon(Cannon cannon) {
        return cannons.remove(cannon.getName()) != null;
    }

    public ObjectCollection<Cannon> getCannons() {
        return cannons.values();
    }

    public Cannon getCannonByLocation(Location location) {
        for (Cannon cannon : cannons.values()) {
            if (cannon.getLocation().equals(location)) {
                return cannon;
            }
        }
        return null;
    }

    public Cannon getCannonByTrigger(Location location) {
        for (Cannon cannon : cannons.values()) {
            if (cannon.getTrigger().equals(location)) {
                return cannon;
            }
        }
        return null;
    }

    public Cannon getCannonByName(String name) {
        return cannons.get(name);
    }

    public boolean removeCannonByName(String name) {
        return cannons.remove(name) != null;
    }

    public void saveCannons() {
        //TODO
    }

    public void loadCannons() {
        //TODO
    }
}
