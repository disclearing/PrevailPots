package com.prevailpots.kitmap.kitmap;

public enum KillStreaks
{
    GoldenApple("Golden Apple", 3, "give name 322 1"), 
    Poison("Poison I 0:33 & Slowness 1:07", 6, "debuffs name"), 
    Invis("Invis Potion", 12, "give name 373:16430 1"), 
    GodApple("God Apple", 20, "give name 322:1 1"), 
    Strength("Strength Buff", 25, "effect name INCREASE_DAMAGE 10 0"),
    Rank("Bronze Rank", 100, "pex user name group set Bronze");
    
    public Integer kills;
    public String name;
    public String command;
    
    private KillStreaks(final String name, final Integer kills, final String command) {
        this.kills = kills;
        this.name = name;
        this.command = command;
    }
}
