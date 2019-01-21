package com.prevailpots.kitmap.listener;

import com.customhcf.base.kit.event.KitApplyEvent;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.type.Faction;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class KitListener implements Listener {
    private final HCF plugin;

    public KitListener(final HCF plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onKitApply(final KitApplyEvent event) {
        final Player player = event.getPlayer();
        final Location location = player.getLocation();
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(location);
        final Faction playerFaction;
        if(!factionAt.isSafezone() || plugin.getEotwHandler().isEndOfTheWorld()) {
            player.sendMessage(ChatColor.RED + "This kit can only be used in the spawn");
            event.setCancelled(true);
        }
    }
}
