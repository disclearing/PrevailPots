package com.prevailpots.kitmap;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionType;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public final class ConfigurationService {
    public static final TimeZone SERVER_TIME_ZONE;
    public static final Map<Enchantment, Integer> ENCHANTMENT_LIMITS;
    public static final Map<PotionType, Integer> POTION_LIMITS;
    public static final Map<World.Environment, Double> SPAWN_RADIUS_MAP;
    public static final ChatColor TEAMMATE_COLOUR;
    public static final ChatColor ALLY_COLOUR;
    public static final ChatColor ENEMY_COLOUR;
    public static final ChatColor SAFEZONE_COLOUR;
    public static final ChatColor WARZONE_COLOUR;
    public static final ChatColor WILDERNESS_COLOUR;
    public static final long DTR_MILLIS_BETWEEN_UPDATES;
    public static final String DTR_WORDS_BETWEEN_UPDATES;
    public static boolean CRATE_BROADCASTS;

    static {
        SERVER_TIME_ZONE = TimeZone.getTimeZone("EST");
        ENCHANTMENT_LIMITS = new HashMap<>();
        POTION_LIMITS = new EnumMap<>(PotionType.class);
        SPAWN_RADIUS_MAP = new EnumMap<>(World.Environment.class);
        POTION_LIMITS.put(PotionType.STRENGTH, 0);
        POTION_LIMITS.put(PotionType.WEAKNESS, 0);
        POTION_LIMITS.put(PotionType.SLOWNESS, 1);
        POTION_LIMITS.put(PotionType.POISON, 1);
        POTION_LIMITS.put(PotionType.INSTANT_DAMAGE, 0);
        POTION_LIMITS.put(PotionType.REGEN, 0);
        POTION_LIMITS.put(PotionType.WATER_BREATHING, 0);
        POTION_LIMITS.put(PotionType.INVISIBILITY, 1);
        ENCHANTMENT_LIMITS.put(Enchantment.PROTECTION_ENVIRONMENTAL, 2);
        ENCHANTMENT_LIMITS.put(Enchantment.DAMAGE_ALL, 2);
        ENCHANTMENT_LIMITS.put(Enchantment.ARROW_KNOCKBACK, 0);
        ENCHANTMENT_LIMITS.put(Enchantment.KNOCKBACK, 0);
        ENCHANTMENT_LIMITS.put(Enchantment.FIRE_ASPECT, 0);
        ENCHANTMENT_LIMITS.put(Enchantment.THORNS, 0);
        ENCHANTMENT_LIMITS.put(Enchantment.ARROW_FIRE, 0);
        ENCHANTMENT_LIMITS.put(Enchantment.ARROW_DAMAGE, 3);
        SPAWN_RADIUS_MAP.put(World.Environment.NORMAL, 75.5);
        SPAWN_RADIUS_MAP.put(World.Environment.NETHER, 15.5);
        SPAWN_RADIUS_MAP.put(World.Environment.THE_END, 0.0);
        TEAMMATE_COLOUR = ChatColor.GREEN;
        ALLY_COLOUR = ChatColor.AQUA;
        ENEMY_COLOUR = ChatColor.RED;
        SAFEZONE_COLOUR = ChatColor.GREEN;
        WARZONE_COLOUR = ChatColor.RED;
        WILDERNESS_COLOUR = ChatColor.GRAY;
        DTR_MILLIS_BETWEEN_UPDATES = TimeUnit.SECONDS.toMillis(45L);
        DTR_WORDS_BETWEEN_UPDATES = DurationFormatUtils.formatDurationWords(ConfigurationService.DTR_MILLIS_BETWEEN_UPDATES, true, true);
        CRATE_BROADCASTS = false;
    }
}
