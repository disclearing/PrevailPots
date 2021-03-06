package com.prevailpots.hcf.listener;

import com.google.common.collect.Lists;
import com.prevailpots.hcf.DateTimeFormats;
import com.prevailpots.hcf.HCF;

import net.minecraft.server.v1_7_R4.ItemBow;
import net.minecraft.server.v1_7_R4.ItemSword;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DeathSignListener implements Listener {
    private static final String DEATH_SIGN_ITEM_NAME;

    static {
        DEATH_SIGN_ITEM_NAME = ChatColor.GREEN + "Death Sign";
    }

    public DeathSignListener(final HCF plugin) {
        if(!plugin.getConfig().getBoolean("death-signs", true)) {
            Bukkit.getScheduler().runTaskLater( plugin, () -> HandlerList.unregisterAll( this), 5L);
        }
    }

    public static ItemStack getDeathSign(final String playerName, final String killerName) {
        final ItemStack stack = new ItemStack(Material.SIGN, 1);
        final ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(DeathSignListener.DEATH_SIGN_ITEM_NAME);
        meta.setLore(Lists.newArrayList(
                ChatColor.GREEN + playerName,
                ChatColor.YELLOW + "slain by",
                ChatColor.DARK_RED + killerName,
                ChatColor.GRAY + DateTimeFormats.DAY_MTH_HR_MIN.format(System.currentTimeMillis())));
        stack.setItemMeta(meta);
        return stack;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onSignChange(final SignChangeEvent event) {
        if(this.isDeathSign(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Block block = event.getBlock();
        if(this.isDeathSign(block)) {
            final BlockState state = block.getState();
            final Sign sign = (Sign) state;
            final ItemStack stack = new ItemStack(Material.SIGN, 1);
            final ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(DeathSignListener.DEATH_SIGN_ITEM_NAME);
            meta.setLore(Arrays.asList(sign.getLines()));
            stack.setItemMeta(meta);
            final Player player = event.getPlayer();
            final World world = player.getWorld();
            if(player.getGameMode() != GameMode.CREATIVE && world.isGameRule("doTileDrops")) {
                world.dropItemNaturally(block.getLocation(), stack);
            }
            event.setCancelled(true);
            block.setType(Material.AIR);
            state.update();
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final ItemStack stack = event.getItemInHand();
        final BlockState state = event.getBlock().getState();
        if(state instanceof Sign && stack.hasItemMeta()) {
            final ItemMeta meta = stack.getItemMeta();
            if(meta.hasDisplayName() && meta.getDisplayName().equals(DeathSignListener.DEATH_SIGN_ITEM_NAME)) {
                final Sign sign = (Sign) state;
                final List<String> lore = (List<String>) meta.getLore();
                int count = 0;
                for(final String loreLine : lore) {
                    sign.setLine(count++, loreLine);
                    if(count == 4) {
                        break;
                    }
                }
                sign.update();
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();
        if(killer != null) {
            event.getDrops().add(getDeathSign(player.getName(), killer.getName()));
            if(killer.getItemInHand() != null)
                applyDeathMeta(player, killer, killer.getItemInHand());
        }
    }

    private void applyDeathMeta(Player player, Player killer, ItemStack stack) {
        if(stack.getType() == null)
            return;

        if(stack.getType() == Material.BOW || stack.getType() == Material.DIAMOND_SWORD) {
            ItemMeta meta = stack.getItemMeta();
            List<String> list = meta.getLore();
            list.add(ChatColor.GREEN + player.getName() + ChatColor.YELLOW + " slain by " + ChatColor.DARK_RED + killer.getName() + ChatColor.GRAY + " " + DateTimeFormats.DAY_MTH_HR_MIN.format(System.currentTimeMillis()));
            meta.setLore(list);
            stack.setItemMeta(meta);
        }
    }

    private boolean isDeathSign(final Block block) {
        final BlockState state = block.getState();
        if(state instanceof Sign) {
            final String[] lines = ((Sign) state).getLines();
            return lines.length > 0 && lines[1] != null && lines[1].equals(ChatColor.YELLOW + "slain by");
        }
        return false;
    }
}
