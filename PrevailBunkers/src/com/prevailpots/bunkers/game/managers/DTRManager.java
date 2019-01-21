package com.prevailpots.bunkers.game.managers;

import java.util.*;

import com.prevailpots.bunkers.Core;
import com.prevailpots.bunkers.game.*;

public class DTRManager
{
    public HashMap<Team, Double> dtr;
    public static final double STARTING_DTR = 5.0;
    
    public DTRManager() {
        this.dtr = new HashMap<Team, Double>();
    }
    
    public double getDTR(final Team p) {
        return this.dtr.containsKey(p) ? this.dtr.get(p) : 5.0;
    }
    
    public String getDTRFormatted(final Team t) {
        final double dtr = this.getDTR(t);
        if (dtr <= 0.0) {
            return "§4§lRAIDABLE";
        }
        if (dtr >= 5.0) {
            return "§a§l" + dtr;
        }
        if (dtr <= 2.0 && dtr > 1.0) {
            return "§e§l" + dtr;
        }
        if (dtr <= 1.0) {
            return "§c§l" + dtr;
        }
        return "§2§l" + dtr;
    }
    
    public boolean isRaidable(final Team t) {
        return this.getDTR(t) <= 0.0;
    }
    
    public void addDTR(final Team p, final double dtr) {
        final double oldBal = this.getDTR(p);
        if (this.dtr.containsKey(p)) {
            this.dtr.remove(p);
        }
        this.dtr.put(p, oldBal + dtr);
    }
    
    public void removeDTR(final Team p, final double dtr) {
        final double oldBal = this.getDTR(p);
        if (oldBal - dtr < 0.0) {
            return;
        }
        if (this.dtr.containsKey(p)) {
            this.dtr.remove(p);
        }
        this.dtr.put(p, oldBal - dtr);
    }
    
    
    
    public void setDTR(final Team p, final double dtr) {
        if (dtr < 0.0) {
            return;
        }
        if (this.dtr.containsKey(p)) {
            this.dtr.remove(p);
        }
        this.dtr.put(p, dtr);
    }
}
