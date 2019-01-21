package com.prevailpots.bunkers.game.managers;

import org.bukkit.entity.*;

import com.prevailpots.bunkers.*;
import com.prevailpots.bunkers.game.*;

import org.bukkit.*;

import java.util.*;

public class TeamManager
{
    public HashMap<String, Integer> kills;
    public HashMap<String, Integer> deaths;
    
    public TeamManager() {
        this.kills = new HashMap<String, Integer>();
        this.deaths = new HashMap<String, Integer>();
    }
    
    public int getKills(final Player p) {
        return this.kills.containsKey(p.getName()) ? this.kills.get(p.getName()) : 0;
    }
    
    public int getDeaths(final Player p) {
        return this.deaths.containsKey(p.getName()) ? this.deaths.get(p.getName()) : 0;
    }
    
    public void setDeaths(final Player p, final int deaths) {
        if (this.deaths.containsKey(p.getName())) {
            this.deaths.remove(p.getName());
        }
        this.deaths.put(p.getName(), deaths);
    }
    
    public void setKills(final Player p, final int kills) {
        if (this.kills.containsKey(p.getName())) {
            this.kills.remove(p.getName());
        }
        this.kills.put(p.getName(), kills);
    }
    
    public double getTeamBalance(final Team t) {
        double totalBal = 0.0;
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final String s : Core.getInstance().getGameHandler().getPlayers().keySet()) {
                if (p.getName().equalsIgnoreCase(s) && Core.getInstance().getGameHandler().getPlayers().get(s).equals(t)) {
                    totalBal += Core.getInstance().getBalanceManager().getBalance(p);
                }
            }
        }
        return totalBal;
    }
    
    public String getTeamBalanceFormatted(final Team t) {
        double totalBal = 0.0;
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final String s : Core.getInstance().getGameHandler().getPlayers().keySet()) {
                if (p.getName().equalsIgnoreCase(s) && Core.getInstance().getGameHandler().getPlayers().get(s).equals(t)) {
                    totalBal += Core.getInstance().getBalanceManager().getBalance(p);
                }
            }
        }
        return "$" + totalBal;
    }
    
    public double getTeamPoints(final Team t) {
        double totalBal = 0.0;
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final String s : Core.getInstance().getGameHandler().getPlayers().keySet()) {
                if (p.getName().equalsIgnoreCase(s) && Core.getInstance().getGameHandler().getPlayers().get(s).equals(t)) {
                    totalBal += Core.getInstance().getPointManager().getPoints(p);
                }
            }
        }
        return totalBal;
    }
    
    public String[] getMembers(final Team t) {
        final ArrayList<String> members = new ArrayList<String>();
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final String s : Core.getInstance().getGameHandler().getPlayers().keySet()) {
                if (p.getName().equalsIgnoreCase(s) && Core.getInstance().getGameHandler().getPlayers().get(s).equals(t)) {
                    members.add(p.getName());
                }
            }
        }
        return members.toArray(new String[0]);
    }
    
    public int getTotalKills(final Team t) {
        int totalKills = 0;
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final String s : Core.getInstance().getGameHandler().getPlayers().keySet()) {
                if (p.getName().equalsIgnoreCase(s) && Core.getInstance().getGameHandler().getPlayers().get(s).equals(t)) {
                    totalKills += this.getKills(p);
                }
            }
        }
        return totalKills;
    }
    
    public int getTotalDeaths(final Team t) {
        int totalDeaths = 0;
        for (final Player p : Bukkit.getOnlinePlayers()) {
            for (final String s : Core.getInstance().getGameHandler().getPlayers().keySet()) {
                if (p.getName().equalsIgnoreCase(s) && Core.getInstance().getGameHandler().getPlayers().get(s).equals(t)) {
                    totalDeaths += this.getDeaths(p);
                }
            }
        }
        return totalDeaths;
    }
}
