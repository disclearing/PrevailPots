package com.prevailpots.kitmap.kitmap;


import net.minecraft.util.gnu.trove.map.*;
import java.util.*;
import net.minecraft.util.gnu.trove.map.hash.*;
import org.bukkit.event.entity.*;

import com.prevailpots.kitmap.HCF;

import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;

public class KillstreakListener implements Listener
{
    TObjectIntMap<UUID> killStreakMap;
    private HCF plugin;
    
    public KillstreakListener(final HCF plugin) {
        this.killStreakMap = (TObjectIntMap<UUID>)new TObjectIntHashMap();
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity().getKiller();
        this.killStreakMap.adjustOrPutValue((UUID)event.getEntity().getKiller().getUniqueId(), 1, 1);
        if (this.killStreakMap.containsKey((Object)event.getEntity().getUniqueId())) {
            this.killStreakMap.remove((Object)event.getEntity().getUniqueId());
        }
        for (final KillStreaks killStreaks : KillStreaks.values()) {
            if (this.killStreakMap.get((Object)player.getUniqueId()) == killStreaks.kills) {
                Bukkit.dispatchCommand((CommandSender)Bukkit.getConsoleSender(), killStreaks.command.replaceAll("name", player.getName()));
                Bukkit.broadcastMessage(ChatColor.RED + player.getName() + ChatColor.YELLOW + " has now reached a killstreak of " + ChatColor.RED + killStreaks.kills + ChatColor.YELLOW + " and received " + ChatColor.RED + killStreaks.name);
                return;
            }
        }
    }
}

