package fr.pollux28.mccanon.cannon;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Cannon {

    protected Location location;
    protected float power, fuseTime;
    protected Player cannonPlayer;
    protected Location trigger;
    protected final String name;


    public Cannon(String name, Location location, float power, float fuseTime, Player cannonPlayer, Location trigger) {
        this.name = name;
        this.location = location;
        this.power = power;
        this.fuseTime = fuseTime;
        this.cannonPlayer = cannonPlayer;
        this.trigger = trigger;
    }

    public Cannon(String name, Location location, float power, float fuseTime, Location trigger) {
        this(name, location, power, fuseTime, null, trigger);
    }

    public Cannon(String name, Location location, float power, float fuseTime) {
        this(name, location, power, fuseTime, null, null);
    }

    public Cannon(String name, Location location) {
        this(name, location, 1, 1);
    }

    public String getName() {
        return name;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public float getPower() {
        return power;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getFuseTime() {
        return fuseTime;
    }

    public void setFuseTime(float fuseTime) {
        this.fuseTime = fuseTime;
    }

    public Player getCannonPlayer() {
        return cannonPlayer;
    }

    public void setCannonPlayer(Player cannonPlayer) {
        this.cannonPlayer = cannonPlayer;
    }

    public Location getTrigger() {
        return trigger;
    }

    public void setTrigger(Location trigger) {
        this.trigger = trigger;
    }

    public void fire() {
        //TODO
    }

    public void save() {
        //TODO
    }

    @Override
    public String toString() {
        //TODO pretty output
        return "Cannon{" +
                "name='" + name + '\'' +
                ", location=" + location +
                ", power=" + power +
                ", fuseTime=" + fuseTime +
                ", cannonPlayer=" + cannonPlayer +
                ", trigger=" + trigger +
                '}';
    }
}
