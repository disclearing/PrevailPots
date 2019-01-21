package com.prevailpots.hcf.key.type;

import com.customhcf.util.ItemBuilder;
import com.prevailpots.hcf.ConfigurationService;
import com.prevailpots.hcf.key.EnderChestKey;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

/**
 * Created by HelpMe on 12/17/2015.
 */
public class ConquestKey extends EnderChestKey {

    public ConquestKey() {
        super("Weekend", 6);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.FIRE_ASPECT, 3).enchant(Enchantment.DAMAGE_ALL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).displayName(ChatColor.DARK_RED + "Weekend Fire").build(), 3);
        this.setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 64), 15);
        this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 64), 15);
        this.setupRarity(new ItemStack(Material.IRON_BLOCK, 64), 15);
        this.setupRarity(new ItemBuilder(Material.GOLD_HELMET).enchant(Enchantment.DURABILITY, 6).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_RED+"Bard Helmet").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_CHESTPLATE).enchant(Enchantment.DURABILITY, 6).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_RED+"Bard Chestplate").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_LEGGINGS).enchant(Enchantment.DURABILITY, 6).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_RED+"Bard Leggings").build(), 1);
        this.setupRarity(new ItemBuilder(Material.GOLD_BOOTS).enchant(Enchantment.DURABILITY, 6).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_RED+"Bard Boots").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.LOOT_BONUS_MOBS, 5).enchant(Enchantment.DAMAGE_ALL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).displayName(ChatColor.DARK_RED + "Weekend Looting").build(), 7);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 5).displayName(ChatColor.DARK_RED + "Weekend Fortune").build(), 5);
        this.setupRarity(new ItemStack(Material.BEACON, 2), 2);
        this.setupRarity(new ItemBuilder(Material.GOLDEN_APPLE, 20).data((short) 1).build(), 1);
        this.setupRarity(new ItemBuilder(Material.BOW).enchant(Enchantment.ARROW_DAMAGE, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.ARROW_DAMAGE)).enchant(Enchantment.ARROW_FIRE, 1).enchant(Enchantment.ARROW_INFINITE, 1).displayName(ChatColor.DARK_RED + "Weekend Bow").build(), 6);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_HELMET).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_RED + "Weekend Helmet").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_CHESTPLATE).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_RED + "Weekend Chestplate").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_LEGGINGS).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_RED + "Weekend Leggings").build(), 1);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_BOOTS).enchant(Enchantment.DURABILITY, 5).enchant(Enchantment.PROTECTION_ENVIRONMENTAL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).displayName(ChatColor.DARK_RED + "Weekend Boots").build(), 1);
        this.setupRarity(new ItemStack(Material.SULPHUR, 64), 7);
        this.setupRarity(new ItemStack(Material.SULPHUR, 64), 7);
        this.setupRarity(new ItemStack(Material.SPECKLED_MELON, 64), 5);
        this.setupRarity(new ItemStack(Material.SPECKLED_MELON, 64), 5);
        this.setupRarity(new ItemStack(Material.GLOWSTONE, 64), 5);
        this.setupRarity(new ItemStack(Material.GLOWSTONE, 64), 5);

    }


    @Override
    public ChatColor getColour() {
        return ChatColor.DARK_RED;
    }
    @Override
    public boolean getBroadcastItems() {
        return true;
    }
}
