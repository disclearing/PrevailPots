package com.prevailpots.bunkers.game;

import org.bukkit.*;

public enum Team
{
    RED("RED", 0, ChatColor.RED), 
    GREEN("GREEN", 1, ChatColor.GREEN), 
    BLUE("BLUE", 2, ChatColor.BLUE), 
    YELLOW("YELLOW", 3, ChatColor.YELLOW);
    
    private ChatColor color;
    
    private Team(final String s, final int n, final ChatColor color) {
        this.color = color;
    }
    
    public static Team fromString(final String s) {
        Team[] values;
        for (int length = (values = values()).length, i = 0; i < length; ++i) {
            final Team t = values[i];
            if (t.toString().equalsIgnoreCase(s)) {
                return t;
            }
        }
        return null;
    }
    
    public ChatColor getColor() {
        return this.color;
    }
}
