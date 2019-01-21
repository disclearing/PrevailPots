package com.prevailpots.kitmap.classes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.classes.event.PvpClassEquipEvent;
import com.prevailpots.kitmap.classes.event.PvpClassUnequipEvent;
import com.prevailpots.kitmap.classes.listener.AttemptEquip;
import com.prevailpots.kitmap.classes.type.archer.ArcherClass;
import com.prevailpots.kitmap.classes.type.bard.BardClass;
import com.prevailpots.kitmap.classes.type.chain.AssassinClass;
import com.prevailpots.kitmap.classes.type.chain.RogueClass;
import com.prevailpots.kitmap.classes.type.miner.MinerClass;

import javax.annotation.Nullable;
import java.util.*;

public class PvpClassManager {
    private final Map<UUID, PvpClass> equippedClass;
    private final Set<PvpClass> pvpClasses;
    private AssassinClass assassinClass;
    private BardClass bardClass;
    private MinerClass minerClass;
    private RogueClass rogueClass;
    private ArcherClass archerClass;



    public PvpClassManager(final HCF plugin) {
        equippedClass = new HashMap<>();
        pvpClasses = new HashSet<>();
        pvpClasses.add(assassinClass = new AssassinClass(plugin));
        pvpClasses.add(rogueClass = new RogueClass(plugin));
        pvpClasses.add(archerClass = new ArcherClass(plugin));
        pvpClasses.add(bardClass = new BardClass(plugin));
        pvpClasses.add(minerClass = new MinerClass(plugin));
        if(!assassinClass.getClassType().isEnabled()){
            pvpClasses.remove(assassinClass);
        }
        if(!rogueClass.getClassType().isEnabled()){
            pvpClasses.remove(rogueClass);
        }
        if(!archerClass.getClassType().isEnabled()){
            pvpClasses.remove(archerClass);
        }
        if(!bardClass.getClassType().isEnabled()){
            pvpClasses.remove(bardClass);
        }
        if(!minerClass.getClassType().isEnabled()){
            pvpClasses.remove(minerClass);
        }
        plugin.getServer().getPluginManager().registerEvents(new AttemptEquip(plugin), plugin);
        for(final PvpClass pvpClass : this.pvpClasses) {
            if(pvpClass instanceof Listener) {
                plugin.getServer().getPluginManager().registerEvents((Listener) pvpClass,  plugin);
            }
        }
    }

    public void onDisable() {
        for(final Map.Entry<UUID, PvpClass> entry : new HashMap<>(this.equippedClass).entrySet()) {
            this.setEquippedClass(Bukkit.getPlayer(entry.getKey()), null);
        }
        this.pvpClasses.clear();
        this.equippedClass.clear();
    }

    public Collection<PvpClass> getPvpClasses() {
        return this.pvpClasses;
    }

    public PvpClass getPvpClass(final String name) {
        for(PvpClass cheat : pvpClasses){
            if(cheat.getName().equals(name)){
                return cheat;
            }
        }
        return null;
    }

    public PvpClass getEquippedClass(final Player player) {
        synchronized(this.equippedClass) {
            return this.equippedClass.get(player.getUniqueId());
        }
    }



    public boolean hasClassEquipped(final Player player, final PvpClass pvpClass) {
        final PvpClass equipped = this.getEquippedClass(player);
        return equipped != null && equipped.equals(pvpClass);
    }

    public void setEquippedClass(final Player player, @Nullable final PvpClass pvpClass) {
        final PvpClass equipped = this.getEquippedClass(player);
        if(equipped != null) {
            if(pvpClass == null) {
                this.equippedClass.remove(player.getUniqueId());
                equipped.onUnequip(player);
                Bukkit.getPluginManager().callEvent( new PvpClassUnequipEvent(player, equipped));
                return;
            }
        } else if(pvpClass == null) {
            return;
        }
        if(pvpClass.onEquip(player)) {
            this.equippedClass.put(player.getUniqueId(), pvpClass);
            Bukkit.getPluginManager().callEvent(new PvpClassEquipEvent(player, pvpClass));
        }
    }
}
