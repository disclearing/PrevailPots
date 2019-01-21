package com.prevailpots.kitmap.key.type;

import com.customhcf.util.ItemBuilder;
import com.prevailpots.kitmap.ConfigurationService;
import com.prevailpots.kitmap.crowbar.Crowbar;
import com.prevailpots.kitmap.key.EnderChestKey;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class GodKey extends EnderChestKey {
    public GodKey() {
        super("Emerald", 8);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Emerald Helmet").build(), 6);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Emerald Boots").build(), 7);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Emerald Chestplate").build(), 4);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Emerald Leggings").build(), 3);
        this.setupRarity(new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Emerald Bard").build(), 6);
        this.setupRarity(new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Emerald Bard").build(), 7);
        this.setupRarity(new ItemBuilder(Material.GOLD_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Emerald Bard").build(), 4);
        this.setupRarity(new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Emerald Bard").build(), 3);
        setupRarity(new ItemStack(Material.EMERALD_ORE, 32), 5);
        setupRarity(new ItemStack(Material.BOOKSHELF, 12), 3);
        setupRarity(new ItemStack(Material.ENCHANTMENT_TABLE), 3);
        setupRarity(new ItemStack(Material.BEACON), 3);
        setupRarity(new ItemStack(Material.ENDER_PORTAL_FRAME, 3), 3);
        setupRarity(new ItemStack(Material.OBSIDIAN, 10), 3);
        setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).enchant(Enchantment.DURABILITY, 4).displayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Emerald Sword").build(), 5);
        setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 5).enchant(Enchantment.DURABILITY, 4).displayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Emerald Pick").build(), 5);
        setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 4).enchant(Enchantment.DURABILITY, 4).enchant(Enchantment.LOOT_BONUS_BLOCKS, 2).displayName(ChatColor.GREEN.toString() + ChatColor.BOLD + "Emerald Pick").build(), 5);
        setupRarity(new ItemStack(383, 6, (short) 92), 16);
        setupRarity(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), 6);
        this.setupRarity(new Crowbar().getItemIfPresent(), 8);
        this.setupRarity(new ItemStack(Material.WEB, 16), 4);
        this.setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 16), 10);
        this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 16), 10);
        this.setupRarity(new ItemStack(Material.IRON_BLOCK, 16), 10);
    }

    @Override
    public ChatColor getColour() {
        return ChatColor.GREEN;
    }

    @Override
    public boolean getBroadcastItems() {
        return false;
    }
}
