package com.prevailpots.hcf.classes;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public abstract class PvpClass {
    public static final long DEFAULT_MAX_DURATION;

    static {
        DEFAULT_MAX_DURATION = TimeUnit.MINUTES.toMillis(8L);
    }

    protected final Set<PotionEffect> passiveEffects;
    protected final String name;
    protected final ClassType classType;

    public PvpClass(final ClassType classType) {
        this.passiveEffects = new HashSet<>();
        this.classType = classType;
        this.name = classType.getName();
    }

    public String getName() {
        return this.name;
    }

    public ClassType getClassType(){
        return classType;
    }

    public boolean onEquip(final Player player) {
        for(final PotionEffect effect : this.passiveEffects) {
            if (effect == null) continue;
            try {
                player.addPotionEffect(effect, true);
            } catch (NullPointerException e) {
                continue;
            }
        }
        return true;
    }

    public void onUnequip(final Player player) {
        for(final PotionEffect effect : this.passiveEffects) {
            for(final PotionEffect active : player.getActivePotionEffects()) {
                if(active.getDuration() > PvpClass.DEFAULT_MAX_DURATION && active.getType().equals( effect.getType()) && active.getAmplifier() == effect.getAmplifier()) {
                    player.removePotionEffect(effect.getType());
                    break;
                }
            }
        }
    }

    public abstract boolean isApplicableFor(final Player p0);
}
