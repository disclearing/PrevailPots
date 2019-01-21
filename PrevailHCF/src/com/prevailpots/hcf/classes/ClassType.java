package com.prevailpots.hcf.classes;

import com.prevailpots.hcf.HCF;

/**
 * Created by TREHOME on 03/18/2016.
 */
public enum ClassType {

    ARCHER("Archer", HCF.getPlugin().getConfig().getBoolean("classes.archer.enabled")),
    BARD("Bard",  HCF.getPlugin().getConfig().getBoolean("classes.bard.enabled")),
    ASSASSIN("Assassin",  HCF.getPlugin().getConfig().getBoolean("classes.assassin.enabled")),
    ROUGE("Rouge",  HCF.getPlugin().getConfig().getBoolean("classes.rouge.enabled")),
    MINER("Miner",  HCF.getPlugin().getConfig().getBoolean("classes.miner.enabled"));

    private final String name;
    private final boolean enabled;

    ClassType(String name, boolean enabled){
        this.name = name;
        this.enabled = enabled;
    }

    public boolean isEnabled(){
        return enabled;
    }

    public String getName() {
        return name;
    }


}
