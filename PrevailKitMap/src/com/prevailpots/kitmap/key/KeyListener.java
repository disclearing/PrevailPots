package com.prevailpots.kitmap.key;


import com.customhcf.util.InventoryUtils;
import com.customhcf.util.chat.Text;
import com.prevailpots.kitmap.HCF;

import net.minecraft.server.v1_7_R4.IChatBaseComponent;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import net.minecraft.util.org.apache.commons.lang3.text.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KeyListener implements Listener {
    private final HCF plugin;

    public KeyListener(final HCF plugin) {
        this.plugin = plugin;
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Key key = this.plugin.getKeyManager().getKey(event.getItemInHand());
        if (key != null) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClose(final InventoryCloseEvent event) {
        final Inventory inventory = event.getInventory();
        final Inventory topInventory = event.getView().getTopInventory();
        if (inventory != null && topInventory != null && topInventory.equals(inventory) && (topInventory.getTitle().contains(ChatColor.YELLOW  +"") &&topInventory.getTitle().endsWith(" Key Reward"))) {
            final Player player = (Player) event.getPlayer();
            final Location location = player.getLocation();
            final World world = player.getWorld();
            boolean isEmpty = true;
            for (final ItemStack stack : topInventory.getContents()) {
                if (stack != null && stack.getType() != Material.AIR) {
                    world.dropItemNaturally(location, stack);
                    isEmpty = false;
                }
            }
            if (!isEmpty) {
                player.sendMessage(ChatColor.YELLOW.toString() + ChatColor.BOLD + "You closed your loot crate reward inventory, dropped on the ground for you.");
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryDrag(final InventoryDragEvent event) {
        final Inventory inventory = event.getInventory();
        final Inventory topInventory = event.getView().getTopInventory();
        if (inventory != null && topInventory != null && topInventory.equals(inventory) && (topInventory.getTitle().endsWith(" Key Reward") && topInventory.getTitle().contains(ChatColor.YELLOW  +""))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInventoryClick(final InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        final Inventory topInventory = event.getView().getTopInventory();
        if (clickedInventory == null || topInventory == null || !topInventory.getTitle().endsWith(" Key Reward") || !topInventory.getTitle().contains(ChatColor.YELLOW  +"")) {
            return;
        }
        final InventoryAction action = event.getAction();
        if (!topInventory.equals(clickedInventory) && action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
            event.setCancelled(true);
        } else if (topInventory.equals(clickedInventory) && (action == InventoryAction.PLACE_ALL || action == InventoryAction.PLACE_ONE || action == InventoryAction.PLACE_SOME)) {
            event.setCancelled(true);
        }
    }

    private void decrementHand(final Player player) {
        final ItemStack stack = player.getItemInHand();
        if (stack.getAmount() <= 1) {
            player.setItemInHand(new ItemStack(Material.AIR, 1));
        } else {
            stack.setAmount(stack.getAmount() - 1);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        final Action action = event.getAction();
        final ItemStack stack = event.getItem();
        if (action != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        final Key key = this.plugin.getKeyManager().getKey(stack);
        if (key == null) {
            return;
        }
        final Block block = event.getClickedBlock();
        final BlockState state = block.getState();
        if (key instanceof EnderChestKey && block.getType() == Material.ENDER_CHEST) {
            final InventoryView openInventory = player.getOpenInventory();
            final Inventory topInventory = openInventory.getTopInventory();
            if (topInventory != null && (topInventory.getTitle().endsWith(" Key Reward") && topInventory.getTitle().contains(ChatColor.YELLOW  +""))) {
                return;
            }
            final EnderChestKey enderChestKey = (EnderChestKey) key;
            final boolean broadcastLoot = enderChestKey.getBroadcastItems();
            final int rolls = enderChestKey.getRolls();
            final int size = InventoryUtils.getSafestInventorySize(rolls);
            final Inventory inventory = Bukkit.createInventory(player, size, ChatColor.YELLOW + enderChestKey.getName() + " Key Reward");
            final ItemStack[] loot = enderChestKey.getLoot();
            if (loot == null) {
                player.sendMessage(ChatColor.RED + "That key has no loot defined, please inform an admin.");
                return;
            }
            final List<ItemStack> finalLoot = new ArrayList<ItemStack>();
            final List<String> finalLootName = new ArrayList<>();
            final Random random = this.plugin.getRandom();
            for (int i = 0; i < rolls; ++i) {
                final ItemStack item = loot[random.nextInt(loot.length)];
                if (item != null && item.getType() != Material.AIR) {
                    if(item.getAmount() == 0){
                        item.setAmount(1);
                    }
                    finalLoot.add(item);
                    inventory.setItem(i, item);
                    if(item.hasItemMeta()){
                        finalLootName.add(item.getItemMeta().getDisplayName() + ChatColor.BLUE + " x " + item.getAmount());
                        continue;
                    }
                    finalLootName.add(ChatColor.YELLOW + WordUtils.capitalize(item.getType().toString().toLowerCase(), null).replace("_", " ") + ChatColor.BLUE + " x" + item.getAmount());
                }
            }
            if (broadcastLoot) {
                final Text text = new Text();
                new Text(ChatColor.GOLD + "[KingOfTheHill] " + player.getName() + ChatColor.YELLOW + " has obtained loot from " + ChatColor.GOLD + enderChestKey.getDisplayName()).setColor(ChatColor.YELLOW).broadcast();
                text.append((IChatBaseComponent) new Text("Loot: ").setColor(enderChestKey.getColour()));
                String lootName = StringUtils.join(finalLootName, ChatColor.GOLD + ", ");
        //        text.append((IChatBaseComponent) TextUtils.joinItemList(finalLoot, ChatColor.GOLD + ", ", true));
                text.append(lootName).setColor(ChatColor.YELLOW);
                text.broadcast();
            }
            final Location location = block.getLocation();
            player.openInventory(inventory);
            player.playSound(location, Sound.LEVEL_UP, 1.0f, 1.0f);
            this.decrementHand(player);
            event.setCancelled(true);
        }

    }
}
