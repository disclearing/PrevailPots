package com.prevailpots.hcf.classes.listener;

import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.classes.PvpClass;
import com.prevailpots.hcf.classes.event.PvpClassEquipEvent;
import com.prevailpots.hcf.classes.event.PvpClassUnequipEvent;

import java.util.Collection;

/**
 * Created by TREHOME on 03/29/2016.
 */
public class AttemptEquip implements Listener {

    HCF plugin;
    public AttemptEquip(HCF plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onEquip(PvpClassEquipEvent e){
        e.getPlayer().sendMessage(ChatColor.YELLOW + "Class " + ChatColor.DARK_GREEN + e.getPvpClass().getName() + ChatColor.YELLOW + " has been "+ ChatColor.GREEN +"enabled"+ChatColor.YELLOW+".");
    return;
    }
    @EventHandler
       public void onEquip(PvpClassUnequipEvent e){
        e.getPlayer().sendMessage(ChatColor.YELLOW + "Class " + ChatColor.DARK_GREEN + e.getPvpClass().getName() + ChatColor.YELLOW + " has been "+ ChatColor.RED +"disabled"+ChatColor.YELLOW+".");
        return;
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onEquipmentSet(final EquipmentSetEvent event) {
        final HumanEntity humanEntity = event.getHumanEntity();
        if(humanEntity instanceof Player) {
            this.attemptEquip((Player) humanEntity);
        }
    }
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerQuitEvent event) {
        this.plugin.getPvpClassManager().setEquippedClass(event.getPlayer(), null);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.attemptEquip(event.getPlayer());
    }


    private void attemptEquip(final Player player) {
        final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(player);
                if(equipped != null) {
                    if(equipped.isApplicableFor(player)) {
                        return;
                    }
                    this.plugin.getPvpClassManager().setEquippedClass(player, null);
                }

                final Collection<PvpClass> pvpClasses = this.plugin.getPvpClassManager().getPvpClasses();
                for(final PvpClass pvpClass : pvpClasses) {
                    if(pvpClass.isApplicableFor(player)) {
                this.plugin.getPvpClassManager().setEquippedClass(player, pvpClass);
                break;
            }
        }
    }

}
