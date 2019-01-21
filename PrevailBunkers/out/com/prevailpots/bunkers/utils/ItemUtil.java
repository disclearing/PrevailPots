package com.prevailpots.bunkers.utils;

import org.bukkit.*;
import org.bukkit.inventory.*;
import java.util.*;
import org.bukkit.inventory.meta.*;
import org.bukkit.enchantments.*;
import org.bukkit.potion.*;

public class ItemUtil
{
    public static boolean isInteractable(final Material m) {
        switch (m) {
            case ENCHANTMENT_TABLE: {
                return true;
            }
            case ANVIL: {
                return true;
            }
            case BREWING_STAND: {
                return true;
            }
            case BREWING_STAND_ITEM: {
                return true;
            }
            case CAULDRON: {
                return true;
            }
            case CAULDRON_ITEM: {
                return true;
            }
            case TRAPPED_CHEST: {
                return true;
            }
            case TRAP_DOOR: {
                return true;
            }
            case FENCE_GATE: {
                return true;
            }
            case NOTE_BLOCK: {
                return true;
            }
            case FURNACE: {
                return true;
            }
            case JUKEBOX: {
                return true;
            }
            case BOAT: {
                return true;
            }
            case GOLD_PLATE: {
                return true;
            }
            case IRON_PLATE: {
                return true;
            }
            case STONE_PLATE: {
                return true;
            }
            case WOOD_PLATE: {
                return true;
            }
            case BED: {
                return true;
            }
            case IRON_DOOR: {
                return true;
            }
            case WOOD_DOOR: {
                return true;
            }
            case IRON_DOOR_BLOCK: {
                return true;
            }
            case WOODEN_DOOR: {
                return true;
            }
            case CHEST: {
                return true;
            }
            case WORKBENCH: {
                return true;
            }
            case MINECART: {
                return true;
            }
            case COMMAND_MINECART: {
                return true;
            }
            case EXPLOSIVE_MINECART: {
                return true;
            }
            case HOPPER_MINECART: {
                return true;
            }
            case POWERED_MINECART: {
                return true;
            }
            case STORAGE_MINECART: {
                return true;
            }
            case HOPPER: {
                return true;
            }
            case BEACON: {
                return true;
            }
            case COMMAND: {
                return true;
            }
            case CAKE_BLOCK: {
                return true;
            }
            case CAKE: {
                return true;
            }
            case WATER: {
                return true;
            }
            case STATIONARY_WATER: {
                return true;
            }
            case LAVA: {
                return true;
            }
            case STATIONARY_LAVA: {
                return true;
            }
            case BURNING_FURNACE: {
                return true;
            }
            case LEVER: {
                return true;
            }
            case DISPENSER: {
                return true;
            }
            case DROPPER: {
                return true;
            }
            default: {
                return false;
            }
        }
    }
    
    public static ItemStack getItem(final String displayName, final Material type, final int count, final String... lore) {
        final ItemStack toReturn = new ItemStack(type, count);
        final ItemMeta meta = toReturn.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null) {
            meta.setLore((List)Arrays.asList(lore));
        }
        toReturn.setItemMeta(meta);
        return toReturn;
    }
    
    public static ItemStack getEnchantedItem(final String displayName, final Material type, final int count, final Enchantment enchantment, final int enchLevel, final String... lore) {
        final ItemStack toReturn = new ItemStack(type, count);
        toReturn.addUnsafeEnchantment(enchantment, enchLevel);
        final ItemMeta meta = toReturn.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null) {
            meta.setLore((List)Arrays.asList(lore));
        }
        toReturn.setItemMeta(meta);
        return toReturn;
    }
    
    public static ItemStack getPotion(final String displayName, final PotionType type, final boolean splash, final int level, final boolean extendDuration, final int count, final String... lore) {
        final ItemStack toReturn = getItem(displayName, Material.POTION, count, lore);
        final Potion pot = new Potion(1);
        pot.setType(type);
        if (!type.toString().toLowerCase().contains("instant") && extendDuration) {
            pot.setHasExtendedDuration(true);
        }
        pot.setSplash(splash);
        pot.setLevel(level);
        pot.apply(toReturn);
        return toReturn;
    }
}
