package com.prevailpots.hcf.classes.type.bard;

import org.bukkit.potion.PotionEffect;

public class BardEffect {
    public final int energyCost;
    public final PotionEffect clickable;
    public final PotionEffect heldable;


    public BardEffect(final int energyCost, final PotionEffect clickable, final PotionEffect heldable) {
        this.energyCost = energyCost;
        this.clickable = clickable;
        this.heldable = heldable;
    }
}
