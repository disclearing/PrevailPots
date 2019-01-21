package com.prevailpots.kitmap.listener;

import com.google.common.primitives.Ints;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemStatTrackingListener implements Listener {


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final Player killer = player.getKiller();
        if(killer != null) {
            final ItemStack stack = killer.getItemInHand();
            if(stack != null && EnchantmentTarget.WEAPON.includes(stack)) {
                this.addDeathLore(stack);
            }
        }
    }

    private void addDeathLore(final ItemStack stack) {
        final ItemMeta meta = stack.getItemMeta();
        final List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>(2);
        if(lore.isEmpty() || !lore.get(0).startsWith(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Kills ")) {
            lore.add(0, ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Kills "+ ChatColor.RED +1);
        }else{
            final String killsString = lore.get(0).replace(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Kills ", "").replace(ChatColor.YELLOW + "]", "");
            int kills;
            if(Ints.tryParse(killsString) == null) return;
            kills = Ints.tryParse(killsString);
            Integer killafteradd = kills +1;
            lore.set(0, ChatColor.DARK_RED + ChatColor.BOLD.toString() + "Kills "+ ChatColor.RED +killafteradd);
        }
        meta.setLore( lore.subList(0, Math.min(6, lore.size())));
        stack.setItemMeta(meta);
    }
}
