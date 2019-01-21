package com.prevailpots.hcf.key.type;

import com.customhcf.util.ItemBuilder;
import com.prevailpots.hcf.ConfigurationService;
import com.prevailpots.hcf.crowbar.Crowbar;
import com.prevailpots.hcf.key.EnderChestKey;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

public class ExoKey extends EnderChestKey {
    public ExoKey() {
        super("Quartz", 6);
        this.setupRarity(new ItemBuilder(Material.IRON_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Quartz Miner").build(), 6);
        this.setupRarity(new ItemBuilder(Material.IRON_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Quartz Miner").build(), 7);
        this.setupRarity(new ItemBuilder(Material.IRON_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Quartz Miner").build(), 7);
        this.setupRarity(new ItemBuilder(Material.IRON_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Quartz Miner").build(), 7);
        setupRarity(new ItemStack(Material.QUARTZ_ORE, 32), 5);
        setupRarity(new ItemStack(Material.BOOKSHELF, 12), 5);
        setupRarity(new ItemStack(Material.ENCHANTMENT_TABLE), 5);
        setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).enchant(Enchantment.DURABILITY, 3).displayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Quartz Sword").build(), 5);
        setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 4).enchant(Enchantment.DURABILITY, 3).displayName(ChatColor.YELLOW.toString() + ChatColor.BOLD + "Quartz Pick").build(), 5);
        setupRarity(new ItemStack(383, 6, (short) 92), 10);
        setupRarity(new ItemStack(Material.GOLDEN_APPLE, 5), 4);
        this.setupRarity(new Crowbar().getItemIfPresent(), 4);
        this.setupRarity(new ItemStack(Material.WEB, 8), 8);
        this.setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 8), 10);
        this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 8), 10);
        this.setupRarity(new ItemStack(Material.IRON_BLOCK, 8), 10);
        this.setupRarity(new ItemBuilder(Material.LEATHER_HELMET).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Quartz Helmet").build(), 6);
        this.setupRarity(new ItemBuilder(Material.LEATHER_BOOTS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Quartz Boots").build(), 7);
        this.setupRarity(new ItemBuilder(Material.LEATHER_CHESTPLATE).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Quartz Chestplate").build(), 4);
        this.setupRarity(new ItemBuilder(Material.LEATHER_LEGGINGS).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Quartz Leggings").build(), 3);
    }

    @Override
    public ChatColor getColour() {
        return ChatColor.YELLOW;
    }

    @Override
    public boolean getBroadcastItems() {
        return false;
    }
}
