package com.prevailpots.kitmap.key.type;

import com.customhcf.util.ItemBuilder;
import com.prevailpots.kitmap.ConfigurationService;
import com.prevailpots.kitmap.crowbar.Crowbar;
import com.prevailpots.kitmap.key.EnderChestKey;

import org.apache.commons.lang3.text.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

public class LegendKey extends EnderChestKey {
    public LegendKey() {
        super("Legend", 4);
        this.setupRarity(new ItemStack(Material.ENDER_PEARL, 8), 10);
        this.setupRarity(new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1), 4);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.DIG_SPEED, 3).enchant(Enchantment.LOOT_BONUS_BLOCKS, 1).build(), 6);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_AXE).enchant(Enchantment.DIG_SPEED, 3).enchant(Enchantment.LOOT_BONUS_BLOCKS, 1).build(), 5);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SPADE).enchant(Enchantment.DIG_SPEED, 3).build(), 3);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_SWORD).enchant(Enchantment.DAMAGE_ALL, ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).build(), 8);
        this.setupRarity(new ItemBuilder(Material.DIAMOND_PICKAXE).enchant(Enchantment.LOOT_BONUS_BLOCKS, 3).build(), 3);
        this.setupRarity(new ItemStack(Material.EXP_BOTTLE, 24), 7);
        this.setupRarity(new Crowbar().getItemIfPresent(), 2);
        this.setupRarity(new ItemStack(Material.BEACON, 1), 1);
        this.setupRarity(new ItemBuilder(Material.MOB_SPAWNER).displayName(ChatColor.GREEN + "Spawner").loreLine(ChatColor.YELLOW + WordUtils.capitalizeFully(EntityType.PIG.name())).build(), 3);
        this.setupRarity(new ItemStack(Material.DIAMOND_BLOCK, 3), 6);
        this.setupRarity(new ItemStack(Material.GOLD_BLOCK, 4), 5);
        this.setupRarity(new ItemStack(Material.IRON_BLOCK, 2), 5);
        this.setupRarity(new ItemBuilder(Material.SKULL_ITEM).data((short)1).build(), 3);
        this.setupRarity(new ItemBuilder(Material.GHAST_TEAR).build(), 3);
        this.setupRarity(new ItemStack(Material.QUARTZ, 3), 3);
        this.setupRarity(new ItemStack(Material.MAGMA_CREAM, 5), 3);
        this.setupRarity(new ItemBuilder(Material.MONSTER_EGG).data((short)92).build(), 3);
        this.setupRarity(new ItemBuilder(Material.POTION).data((short)16421).build(), 1);
    }

    @Override
    public ChatColor getColour() {
        return ChatColor.DARK_RED;
    }

    @Override
    public boolean getBroadcastItems() {
        return false;
    }
}
