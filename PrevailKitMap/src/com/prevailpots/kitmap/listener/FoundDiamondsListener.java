package com.prevailpots.kitmap.listener;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import com.prevailpots.kitmap.HCF;

public class FoundDiamondsListener implements Listener {
    public static final Material SEARCH_TYPE;

    static {
        SEARCH_TYPE = Material.DIAMOND_ORE;
    }

    public final Set<String> foundLocations;
    private final HCF plugin;

    public FoundDiamondsListener(final HCF plugin) {
        this.foundLocations = new HashSet<String>();
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPistonExtend(final BlockPistonExtendEvent event) {
        for(final Block block : event.getBlocks()) {
            if(block.getType() == FoundDiamondsListener.SEARCH_TYPE) {
                this.foundLocations.add(block.getLocation().toString());
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Block block = event.getBlock();
        if(block.getType() == FoundDiamondsListener.SEARCH_TYPE) {
            this.foundLocations.add(block.getLocation().toString());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if(player.getGameMode() == GameMode.CREATIVE) {
            return;
        }
        if(player.getItemInHand().getEnchantments().containsKey(Enchantment.SILK_TOUCH)) return;
        final Block block = event.getBlock();
        final Location blockLocation = block.getLocation();
        if(block.getType() == FoundDiamondsListener.SEARCH_TYPE && this.foundLocations.add(blockLocation.toString())) {
            int count = 1;
            for(int x = -5; x < 5; ++x) {
                for(int y = -5; y < 5; ++y) {
                    for(int z = -5; z < 5; ++z) {
                        final Block otherBlock = blockLocation.clone().add((double) x, (double) y, (double) z).getBlock();
                        if(!otherBlock.equals(block) && otherBlock.getType() == FoundDiamondsListener.SEARCH_TYPE && this.foundLocations.add(otherBlock.getLocation().toString())) {
                            ++count;
                        }
                    }
                }
            }
            plugin.getUserManager().getUser(player.getUniqueId()).setDiamondsMined(plugin.getUserManager().getUser(player.getUniqueId()).getDiamondsMined() + count);
            String message;
            for(Player on : Bukkit.getOnlinePlayers()) {
                if(plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) != null) {
                     message = plugin.getFactionManager().getPlayerFaction(player.getUniqueId()).getRelation(on).toChatColour() + player.getName() + ChatColor.YELLOW + " has found" + ChatColor.GOLD + " Diamonds " + ChatColor.YELLOW + '[' + ChatColor.GOLD + count + ChatColor.YELLOW + ']';
                    on.sendMessage(message);
                }else{
                    message = ChatColor.RED  + player.getName() + ChatColor.YELLOW + " has found" + ChatColor.GOLD + " Diamonds " + ChatColor.YELLOW + '[' + ChatColor.GOLD + count + ChatColor.YELLOW + ']';
                    on.sendMessage(message);
                }
            }
        }
    }

}
