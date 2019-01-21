package com.prevailpots.bunkers.game.managers;

import org.bukkit.potion.*;

import com.prevailpots.bunkers.*;
import com.prevailpots.bunkers.game.*;
import com.prevailpots.bunkers.utils.*;

import org.bukkit.enchantments.*;
import org.bukkit.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.entity.*;

import java.util.*;

import org.bukkit.event.inventory.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;
import org.bukkit.event.*;

public class ShopManager implements Listener
{
    private List<ShopItem> shopItems;
    private Inventory shopGui;
    
    public ShopManager() {
        (this.shopItems = new ArrayList<ShopItem>()).add(new ShopItem(ItemUtil.getItem("§fDiamond Sword", Material.DIAMOND_SWORD, 1, (String[])null), 100.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getItem("§fDiamond Helmet", Material.DIAMOND_HELMET, 1, (String[])null), 150.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getItem("§fDiamond Chestplate", Material.DIAMOND_CHESTPLATE, 1, (String[])null), 150.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getItem("§fDiamond Leggings", Material.DIAMOND_LEGGINGS, 1, (String[])null), 150.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getItem("§fDiamond Boots", Material.DIAMOND_BOOTS, 1, (String[])null), 150.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getEnchantedItem("§bSharpness 1", Material.DIAMOND_SWORD, 1, Enchantment.DAMAGE_ALL, 1, "§bApplies sharpness 1 to your sword."), 150.0, ShopItemType.ENCHANT, Enchantment.DAMAGE_ALL, 1));
        this.shopItems.add(new ShopItem(ItemUtil.getItem("§fFishing Rod", Material.FISHING_ROD, 1, (String[])null), 75.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getPotion("§fHealth Pot", PotionType.INSTANT_HEAL, true, 2, false, 1, (String[])null), 10.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getPotion("§fSpeed Pot", PotionType.SPEED, false, 2, true, 1, (String[])null), 75.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getPotion("§fInvis Pot", PotionType.INVISIBILITY, false, 1, false, 1, (String[])null), 300.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getItem("§fEnder Pearl", Material.ENDER_PEARL, 3, (String[])null), 125.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getPotion("§fSlowness Pot", PotionType.SLOWNESS, true, 1, false, 1, (String[])null), 120.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getPotion("§fPoison Pot", PotionType.POISON, true, 1, false, 1, (String[])null), 120.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getItem("§fBow", Material.BOW, 1, (String[])null), 150.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getItem("§fArrow", Material.ARROW, 16, (String[])null), 50.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getItem("§fGapple", Material.GOLDEN_APPLE, 1, (String[])null), 25.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getItem("§fSteak", Material.COOKED_BEEF, 16, (String[])null), 25.0, ShopItemType.ITEM));
        this.shopItems.add(new ShopItem(ItemUtil.getEnchantedItem("§bProt 1 Helmet", Material.DIAMOND_HELMET, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, "§bApplies protection 1", "§bto your helmet."), 260.0, ShopItemType.ENCHANT, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        this.shopItems.add(new ShopItem(ItemUtil.getEnchantedItem("§bProt 1 Chestplate", Material.DIAMOND_CHESTPLATE, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, "§bApplies protection 1", "§bto your chestplate."), 300.0, ShopItemType.ENCHANT, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        this.shopItems.add(new ShopItem(ItemUtil.getEnchantedItem("§bProt 1 Leggings", Material.DIAMOND_LEGGINGS, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, "§bApplies protection 1", "§bto your leggings."), 300.0, ShopItemType.ENCHANT, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        this.shopItems.add(new ShopItem(ItemUtil.getEnchantedItem("§bProt 1 Boots", Material.DIAMOND_BOOTS, 1, Enchantment.PROTECTION_ENVIRONMENTAL, 1, "§bApplies protection 1", "§bto your boots."), 260.0, ShopItemType.ENCHANT, Enchantment.PROTECTION_ENVIRONMENTAL, 1));
        this.shopItems.add(new ShopItem(ItemUtil.getEnchantedItem("§bPower 1", Material.BOW, 1, Enchantment.ARROW_DAMAGE, 1, "§bApplies power 1 to your bow."), 150.0, ShopItemType.ENCHANT, Enchantment.ARROW_DAMAGE, 1));
        this.shopGui = Bukkit.createInventory((InventoryHolder)null, 54, "§aShop");
        int index = 10;
        for (final ShopItem x : this.shopItems) {
            final ItemStack ix = x.getItem();
            final ItemMeta meta = ix.getItemMeta();
            final ArrayList<String> lore = new ArrayList<String>();
            if (meta.getLore() != null) {
                lore.addAll(meta.getLore());
            }
            if (ix.getItemMeta().hasLore()) {
                lore.add(" ");
            }
            lore.add("§7Price: $" + x.getPrice());
            meta.setLore((List)lore);
            ix.setItemMeta(meta);
            this.shopGui.setItem(index, ix);
            if (index == 16 || index == 25 || index == 34 || index == 43 || index == 52 || index == 61 || index == 70 || index == 79 || index == 87) {
                index += 2;
            }
            ++index;
        }
        this.shopGui.setItem(49, ItemUtil.getItem("§bSell items", Material.GOLD_BLOCK, 1, "§7Sells all gold, iron and diamonds", "§7in your inventory."));
    }
    
    private void buyItem(final Player p, final ItemStack i) {
        if (this.getActualItem(i) == null) {
            return;
        }
        if (Core.getInstance().getBalanceManager().hasEnoughMoney(p, this.getPrice(i))) {
            if (this.getActualItem(i).getType().equals(ShopItemType.ITEM)) {
                final ItemStack toadd = i.clone();
                final ItemMeta nigmeta = toadd.getItemMeta();
                nigmeta.setDisplayName(new ItemStack(i.getType()).getItemMeta().getDisplayName());
                nigmeta.setLore((List)Arrays.asList("§7Just a regular " + this.getActualItem(i).getItem().getItemMeta().getDisplayName()));
                toadd.setItemMeta(nigmeta);
                p.getInventory().addItem(new ItemStack[] { toadd });
                p.sendMessage("§aBought §e" + i.getAmount() + " " + i.getItemMeta().getDisplayName() + " §afor §e" + "$" + this.getPrice(i) + "§a.");
                Core.getInstance().getBalanceManager().removeBalance(p, this.getPrice(i));
            }
            else {
                ItemStack[] contents;
                for (int length = (contents = p.getInventory().getContents()).length, j = 0; j < length; ++j) {
                    final ItemStack x = contents[j];
                    if (x != null) {
                        if (x.getType() != null) {
                            if (x.getType().equals((Object)i.getType()) && !x.getEnchantments().containsKey(this.getActualItem(i).ench)) {
                                x.addUnsafeEnchantment(this.getActualItem(i).ench, this.getActualItem(i).enchLevel);
                                p.sendMessage("§aBought §e" + i.getAmount() + " " + i.getItemMeta().getDisplayName() + " §afor §e" + "$" + this.getPrice(i) + "§a.");
                                Core.getInstance().getBalanceManager().removeBalance(p, this.getPrice(i));
                                return;
                            }
                        }
                    }
                }
                ItemStack[] armorContents;
                for (int length2 = (armorContents = p.getInventory().getArmorContents()).length, k = 0; k < length2; ++k) {
                    final ItemStack x = armorContents[k];
                    if (x != null) {
                        if (x.getType() != null) {
                            if (x.getType().equals((Object)i.getType()) && !x.getEnchantments().containsKey(this.getActualItem(i).ench)) {
                                x.addUnsafeEnchantment(this.getActualItem(i).ench, this.getActualItem(i).enchLevel);
                                p.sendMessage("§aBought §e" + i.getAmount() + " " + i.getItemMeta().getDisplayName() + " §afor §e" + "$" + this.getPrice(i) + "§a.");
                                Core.getInstance().getBalanceManager().removeBalance(p, this.getPrice(i));
                                return;
                            }
                        }
                    }
                }
            }
        }
        else {
            p.sendMessage("§cYou don't have enough money!");
        }
    }
    
    public void sellItems(final Player p) {
        final ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        ItemStack[] contents;
        for (int length = (contents = p.getInventory().getContents()).length, k = 0; k < length; ++k) {
            final ItemStack i = contents[k];
            if (i != null) {
                if (i.getType().equals((Object)Material.GOLD_INGOT) || i.getType().equals((Object)Material.IRON_INGOT) || i.getType().equals((Object)Material.DIAMOND)) {
                    items.add(i);
                }
            }
        }
        for (final ItemStack x : items) {
            if (x.getType().equals((Object)Material.GOLD_INGOT)) {
                for (int j = 0; j < x.getAmount(); ++j) {
                    Core.getInstance().getBalanceManager().addBalance(p, 15.0);
                }
                p.getInventory().remove(x);
            }
            if (x.getType().equals((Object)Material.IRON_INGOT)) {
                for (int j = 0; j < x.getAmount(); ++j) {
                    Core.getInstance().getBalanceManager().addBalance(p, 10.0);
                }
                p.getInventory().remove(x);
            }
            if (x.getType().equals((Object)Material.DIAMOND)) {
                for (int j = 0; j < x.getAmount(); ++j) {
                    Core.getInstance().getBalanceManager().addBalance(p, 20.0);
                }
                p.getInventory().remove(x);
            }
        }
        items.clear();
    }
    
    private double getPrice(final ItemStack i) {
        return this.getActualItem(i).getPrice();
    }
    
    private ShopItem getActualItem(final ItemStack i) {
        for (int ix = 0; ix < this.shopItems.size(); ++ix) {
            if (this.shopItems.get(ix).getItem().equals((Object)i)) {
                return this.shopItems.get(ix);
            }
        }
        return null;
    }
    
    public void openShop(final Player p) {
        p.openInventory(this.shopGui);
    }
    
    @EventHandler
    public void click(final InventoryClickEvent e) {
        if (!Core.getInstance().getGameHandler().getGameState().equals(GameState.GAME)) {
            return;
        }
        if (e.getInventory() == null) {
            return;
        }
        if (e.getInventory().getHolder() != null) {
            return;
        }
        if (!e.getInventory().getTitle().toLowerCase().contains("shop")) {
            return;
        }
        if (e.getCurrentItem() == null) {
            return;
        }
        if (e.getWhoClicked() == null || !(e.getWhoClicked() instanceof Player)) {
            return;
        }
        if (!e.getCurrentItem().hasItemMeta()) {
            return;
        }
        if (e.getClick().equals((Object)ClickType.CONTROL_DROP)) {
            e.setCancelled(true);
            return;
        }
        if (e.getClick().equals((Object)ClickType.NUMBER_KEY)) {
            e.setCancelled(true);
            return;
        }
        if (e.getClick().equals((Object)ClickType.DOUBLE_CLICK)) {
            e.setCancelled(true);
            return;
        }
        if (e.getClick().equals((Object)ClickType.DROP)) {
            e.setCancelled(true);
            return;
        }
        if (e.getClick().toString().toLowerCase().contains("shift")) {
            e.setCancelled(true);
            return;
        }
        if (!e.getSlotType().equals((Object)InventoryType.SlotType.CONTAINER) || e.getSlotType().equals((Object)InventoryType.SlotType.OUTSIDE)) {
            return;
        }
        e.setCancelled(true);
        final Player p = (Player)e.getWhoClicked();
        if (e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§bSell items")) {
            this.sellItems(p);
        }
        else {
            this.buyItem(p, e.getCurrentItem());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void bloks(final PlayerInteractEvent e) {
        if (!Core.getInstance().getGameHandler().getGameState().equals(GameState.GAME)) {
            return;
        }
        if (!e.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        if (e.getClickedBlock() != null && e.getClickedBlock().getType() != null && e.getClickedBlock().getType().equals((Object)Material.WORKBENCH)) {
            e.setCancelled(true);
            this.openShop(e.getPlayer());
        }
    }
    
    public enum ShopItemType
    {
        ITEM("ITEM", 0), 
        ENCHANT("ENCHANT", 1);
        
        private ShopItemType(final String s, final int n) {
        }
    }
    
    public class ShopItem
    {
        private ItemStack item;
        private double price;
        private ShopItemType type;
        public Enchantment ench;
        public int enchLevel;
        
        public ShopItem(final ItemStack item, final double price, final ShopItemType type) {
            this.item = item;
            this.price = price;
            this.type = type;
        }
        
        public ShopItem(final ItemStack item, final double price, final ShopItemType type, final Enchantment ench, final int enchLevel) {
            this.item = item;
            this.price = price;
            this.type = type;
            this.ench = ench;
            this.enchLevel = enchLevel;
        }
        
        public ItemStack getItem() {
            return this.item;
        }
        
        public ShopItemType getType() {
            return this.type;
        }
        
        public double getPrice() {
            return this.price;
        }
    }
}
