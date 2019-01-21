package com.prevailpots.hcf.listener;

import com.customhcf.util.ItemBuilder;
import com.customhcf.util.ParticleEffect;
import com.google.common.primitives.Ints;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashSet;
import java.util.Set;

public class BookDeenchantListener implements Listener {
    private static final ItemStack EMPTY_BOOK;
    private static final String PERMISSION_1;
    private static final String PERMISSION_2;
    private static final Set<Inventory> tracked;



    static {
        EMPTY_BOOK = new ItemStack(Material.BOOK, 1);
        PERMISSION_1 = "hcf.deenchant.1";
        PERMISSION_2 = "hcf.deenchant.2";
        tracked = new HashSet<>();
    }
    @EventHandler
    public void onClose(InventoryCloseEvent e){
        if(tracked.contains(e.getInventory())){
            ParticleEffect.CRITICAL_HIT.display((Player) e.getPlayer(), e.getPlayer().getLocation().add(0, 1, 0), 15, 10);
            e.getInventory().clear();
            tracked.remove(e.getInventory());
        }
    }

    @EventHandler
    public void onClickBook(InventoryClickEvent e){
        if(tracked.contains(e.getInventory())){
            e.setCancelled(true);
            String levels = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getLore().get(0).replace("To remove this enchant it will cost ", "").replace(" levels", ""));
            Integer level = (Ints.tryParse(levels) == null ? 0 : Ints.tryParse(levels));
            if(((Player) e.getWhoClicked()).getLevel() >= level){
                ((Player) e.getWhoClicked()).setLevel(((Player) e.getWhoClicked()).getLevel() - level);
                e.setCancelled(true);
            }else {
                ((Player) e.getWhoClicked()).sendMessage(ChatColor.RED + "You do not have enough levels");
                e.setCancelled(true);
                return;
            }
            for (Enchantment enchantment : e.getCurrentItem().getEnchantments().keySet()){
              e.getWhoClicked().getItemInHand().removeEnchantment(enchantment);
            }
            ParticleEffect.CRITICAL_HIT.display((Player) e.getWhoClicked(), e.getWhoClicked().getLocation().add(0, 1, 0), 15, 10);
            e.getWhoClicked().closeInventory();
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(event.getAction() == Action.LEFT_CLICK_BLOCK && event.hasItem() && !event.getPlayer().isSneaking()) {
            final Player player = event.getPlayer();
            if(event.getClickedBlock().getType() == Material.ENCHANTMENT_TABLE && player.getGameMode() != GameMode.CREATIVE) {
                final ItemStack stack = event.getItem();
                if(stack != null && stack.getType() == Material.ENCHANTED_BOOK) {
                    final ItemMeta meta = stack.getItemMeta();
                    if(meta instanceof EnchantmentStorageMeta) {
                        final EnchantmentStorageMeta enchantmentStorageMeta = (EnchantmentStorageMeta) meta;
                        for(final Enchantment enchantment : enchantmentStorageMeta.getStoredEnchants().keySet()) {
                            enchantmentStorageMeta.removeStoredEnchant(enchantment);
                        }
                        event.setCancelled(true);
                        player.setItemInHand(BookDeenchantListener.EMPTY_BOOK);
                    }
                }else  if(stack != null && stack.getItemMeta().hasEnchants()){
                      if (event.getPlayer().hasPermission(PERMISSION_1)){
                        Inventory trackedInv = Bukkit.createInventory(event.getPlayer(), 9, ChatColor.GREEN + "Item-DeEnchanter");
                        tracked.add(trackedInv);
                        for (Enchantment enchantment : stack.getEnchantments().keySet()){
                            trackedInv.addItem(new ItemBuilder(stack.getType()).enchant(enchantment, stack.getEnchantmentLevel(enchantment)).lore(ChatColor.GREEN + "To remove this enchant it will cost " + ChatColor.YELLOW + stack.getEnchantmentLevel(enchantment) * 5  + ChatColor.GREEN + " levels").build());
                        }
                        event.getPlayer().openInventory(trackedInv);
                    }
                }
            }
        }
    }
}
