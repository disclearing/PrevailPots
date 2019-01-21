package com.prevailpots.bunkers.game.managers;

import java.util.*;
import org.bukkit.entity.*;

public class PointManager
{
    public HashMap<String, Integer> points;
    public static final int STARTING_POINTS = 5;
    
    public PointManager() {
        this.points = new HashMap<String, Integer>();
    }
    
    public int getPoints(final Player p) {
        return this.points.containsKey(p.getName()) ? this.points.get(p.getName()) : 5;
    }
    
    public void addPoints(final Player p, final int points) {
        final int oldBal = this.getPoints(p);
        if (this.points.containsKey(p.getName())) {
            this.points.remove(p.getName());
        }
        this.points.put(p.getName(), oldBal + points);
    }
    
    public void removePoints(final Player p, final int points) {
        final int oldBal = this.getPoints(p);
        if (oldBal - points < 0) {
            return;
        }
        if (this.points.containsKey(p.getName())) {
            this.points.remove(p.getName());
        }
        this.points.put(p.getName(), oldBal - points);
    }
    
    public void setPoints(final Player p, final int points) {
        if (points < 0) {
            return;
        }
        if (this.points.containsKey(p.getName())) {
            this.points.remove(p.getName());
        }
        this.points.put(p.getName(), points);
    }
}
