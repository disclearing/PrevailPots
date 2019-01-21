package com.prevailpots.bunkers.config;

import org.bukkit.configuration.file.*;
import org.bukkit.util.*;

import com.prevailpots.bunkers.*;
import com.prevailpots.bunkers.game.*;
import com.sk89q.worldedit.Vector;

import java.util.*;
import org.bukkit.entity.*;
import java.io.*;
import org.bukkit.*;

public class ConfigurationService
{
    private static final File databaseFile;
    private static YamlConfiguration database;
    private static final File playerDatabaseFile;
    private static YamlConfiguration playerDatabase;
    public static final String NO_PERMISSIONS = "§c§lNO PERMISSIONS...";
    public static final String PLAYERS_ONLY = "§c§lPLAYERS ONLY!";
    public static final String PERM_SET_LOBBY_SPAWN = "uhcb.setlobbyspawn";
    public static final String PERM_SET_TEAM_SPAWN = "uhcb.setteamspawn";
    public static final String PERM_SET_TEAM_REGION = "uhcb.setteamregion";
    public static final String PERM_SET_TEAM_CAPZONE = "uhcb.setteamcapzone";
    public static final String PERM_FORCE_START = "uhcb.forcestart";
    public static final String SCOREBOARD_TITLE = "§6§lPURGE §c[UHCBunkers]";
    public static final int GAME_MAX_TIME_MINUTES = 35;
    public static final int GAME_MAX_PLAYERS = 16;
    public static final int GAME_MIN_PLAYERS = 4;
    public static final int GAME_MAX_TEAM_SIZE = 4;
    public static final int GAME_COUNTDOWN_TIME = 10;
    public static final int GAME_START_FREEZE_SECONDS = 5;
    public static final int GAME_DEATH_FREEZE_SECONDS = 5;
    public static final double GAME_BALANCE_PER_2_SECONDS = 0.55;
    public static final int GAME_COBBLESTONE_ORE_RESPAWN_SECONDS = 5;
    public static final int TEAM_POINTS_TO_WIN = 125;
    public static final int POINTS_LOST_ON_DEATH = 5;
    public static final int POINTS_GAINED_ON_KILL = 5;
    public static final double IRON_SELL_PRICE = 10.0;
    public static final double GOLD_SELL_PRICE = 15.0;
    public static final double DIAMOND_SELL_PRICE = 20.0;
    private static Vector lobbySpawn;
    private static Map<Team, Vector[]> teamRegions;
    private static Map<Team, Vector[]> teamCapzones;
    private static HashMap<Team, Vector> teamSpawns1;
    private static HashMap<String, Integer> statKills;
    private static HashMap<String, Integer> statDeaths;
	private static Object teamSpawns;
    
    static {
        databaseFile = new File(Core.getInstance().getDataFolder() + File.separator + "database.yml");
        playerDatabaseFile = new File(Core.getInstance().getDataFolder() + File.separator + "playerDatabase.yml");
        ConfigurationService.teamRegions = new HashMap<Team, Vector[]>();
        ConfigurationService.teamCapzones = new HashMap<Team, Vector[]>();
        ConfigurationService.teamSpawns1 = new HashMap<Team, Vector>();
        ConfigurationService.statKills = new HashMap<String, Integer>();
        ConfigurationService.statDeaths = new HashMap<String, Integer>();
    }
    
    public static void init() {
        ConfigurationService.database = YamlConfiguration.loadConfiguration(ConfigurationService.databaseFile);
        ConfigurationService.playerDatabase = YamlConfiguration.loadConfiguration(ConfigurationService.playerDatabaseFile);
        registerShit();
    }
    
    private static void registerShit() {
        ConfigurationService.lobbySpawn = new Vector(ConfigurationService.database.getDouble("lobbySpawn.x"), ConfigurationService.database.getDouble("lobbySpawn.y"), ConfigurationService.database.getDouble("lobbySpawn.z"));
        Team[] values;
        for (int length = (values = Team.values()).length, i = 0; i < length; ++i) {
            final Team t = values[i];
            ConfigurationService.teamSpawns1.put(t, new Vector(ConfigurationService.database.getDouble("teamSpawn." + t.toString().toLowerCase() + ".x"), ConfigurationService.database.getDouble("teamSpawn." + t.toString().toLowerCase() + ".y"), ConfigurationService.database.getDouble("teamSpawn." + t.toString().toLowerCase() + ".z")));
        }
        for (final String s : ConfigurationService.playerDatabase.getKeys(false)) {
            if (s == null) {
                continue;
            }
            if (!ConfigurationService.playerDatabase.contains(String.valueOf(s) + ".kills")) {
                continue;
            }
            if (!ConfigurationService.playerDatabase.contains(String.valueOf(s) + ".deaths")) {
                continue;
            }
            ConfigurationService.statKills.put(s, ConfigurationService.playerDatabase.getInt(String.valueOf(s) + ".kills"));
            ConfigurationService.statDeaths.put(s, ConfigurationService.playerDatabase.getInt(String.valueOf(s) + ".deaths"));
        }
        Team[] values2;
        for (int length2 = (values2 = Team.values()).length, j = 0; j < length2; ++j) {
            final Team t = values2[j];
            ConfigurationService.teamRegions.put(t, new Vector[] { new Vector(ConfigurationService.database.getDouble("teamRegion1." + t.name().toLowerCase() + ".x"), 0.0, ConfigurationService.database.getDouble("teamRegion1." + t.name().toLowerCase() + ".z")), new Vector(ConfigurationService.database.getDouble("teamRegion2." + t.name().toLowerCase() + ".x"), 255.0, ConfigurationService.database.getDouble("teamRegion2." + t.name().toLowerCase() + ".z")) });
            ConfigurationService.teamCapzones.put(t, new Vector[] { new Vector(ConfigurationService.database.getDouble("teamCapzone1." + t.name().toLowerCase() + ".x"), ConfigurationService.database.getDouble("teamCapzone1." + t.name().toLowerCase() + ".y"), ConfigurationService.database.getDouble("teamCapzone1." + t.name().toLowerCase() + ".z")), new Vector(ConfigurationService.database.getDouble("teamCapzone2." + t.name().toLowerCase() + ".x"), 255.0, ConfigurationService.database.getDouble("teamCapzone2." + t.name().toLowerCase() + ".z")) });
        }
    }
    
    public static Integer getStatKills(final Player p) {
        return ConfigurationService.statKills.containsKey(p.getUniqueId().toString()) ? ConfigurationService.statKills.get(p.getUniqueId().toString()) : 0;
    }
    
    public static Vector[] getRegions(final Team t) {
        return ConfigurationService.teamRegions.get(t);
    }
    
    public static Vector[] getCapzone(final Team t) {
        return ConfigurationService.teamCapzones.get(t);
    }
    
    public static void setRegions(final Team t, final Vector[] vectors) {
        if (ConfigurationService.teamRegions.containsKey(t)) {
            ConfigurationService.teamRegions.remove(t);
        }
        ConfigurationService.database.set("teamRegion1." + t.toString().toLowerCase() + ".x", (Object)vectors[0].getX());
        ConfigurationService.database.set("teamRegion1." + t.toString().toLowerCase() + ".y", (Object)vectors[0].getY());
        ConfigurationService.database.set("teamRegion1." + t.toString().toLowerCase() + ".z", (Object)vectors[0].getZ());
        ConfigurationService.database.set("teamRegion2." + t.toString().toLowerCase() + ".x", (Object)vectors[1].getX());
        ConfigurationService.database.set("teamRegion2." + t.toString().toLowerCase() + ".y", (Object)vectors[1].getY());
        ConfigurationService.database.set("teamRegion2." + t.toString().toLowerCase() + ".z", (Object)vectors[1].getZ());
        saveDatabase();
        ConfigurationService.teamRegions.put(t, vectors);
    }
    
    public static void setCapzone(final Team t, final Vector[] points) {
        if (ConfigurationService.teamCapzones.containsKey(t)) {
            ConfigurationService.teamCapzones.remove(t);
        }
        ConfigurationService.database.set("teamCapzone1." + t.toString().toLowerCase() + ".x", (Object)points[0].getX());
        ConfigurationService.database.set("teamCapzone1." + t.toString().toLowerCase() + ".y", (Object)points[0].getY());
        ConfigurationService.database.set("teamCapzone1." + t.toString().toLowerCase() + ".z", (Object)points[0].getZ());
        ConfigurationService.database.set("teamCapzone2." + t.toString().toLowerCase() + ".x", (Object)points[1].getX());
        ConfigurationService.database.set("teamCapzone2." + t.toString().toLowerCase() + ".y", (Object)points[1].getY());
        ConfigurationService.database.set("teamCapzone2." + t.toString().toLowerCase() + ".z", (Object)points[1].getZ());
        saveDatabase();
        ConfigurationService.teamCapzones.put(t, points);
    }
    
    private static void saveDatabase() {
        try {
            ConfigurationService.database.save(ConfigurationService.databaseFile);
        }
        catch (IOException ex) {}
    }
    
    private static void savePlayerDatabase() {
        try {
            ConfigurationService.playerDatabase.save(ConfigurationService.playerDatabaseFile);
        }
        catch (IOException ex) {}
    }
    
    public static void setStatKills(final Player p, final int kills) {
        if (ConfigurationService.statKills.containsKey(p.getUniqueId().toString())) {
            ConfigurationService.statKills.remove(p.getUniqueId().toString());
        }
        ConfigurationService.statKills.put(p.getUniqueId().toString(), kills);
        ConfigurationService.playerDatabase.set(String.valueOf(p.getUniqueId().toString()) + ".kills", (Object)kills);
        savePlayerDatabase();
    }
    
    public static Integer getStatDeaths(final Player p) {
        return ConfigurationService.statDeaths.containsKey(p.getUniqueId().toString()) ? ConfigurationService.statDeaths.get(p.getUniqueId().toString()) : 0;
    }
    
    public static void setStatDeaths(final Player p, final int deaths) {
        if (ConfigurationService.statDeaths.containsKey(p.getUniqueId().toString())) {
            ConfigurationService.statDeaths.remove(p.getUniqueId().toString());
        }
        ConfigurationService.statDeaths.put(p.getUniqueId().toString(), deaths);
        ConfigurationService.playerDatabase.set(String.valueOf(p.getUniqueId().toString()) + ".deaths", (Object)deaths);
        savePlayerDatabase();
    }
    
    public static Vector getLobbySpawn() {
        return ConfigurationService.lobbySpawn;
    }
    
    public static Vector getTeamSpawn(final Team t) {
        return ConfigurationService.teamSpawns1.get(t);
    }
    
    public static void setTeamSpawn(final Location loc, final Team team) {
        if (team != null) {
            if (ConfigurationService.teamSpawns1.containsKey(team)) {
                ConfigurationService.teamSpawns1.remove(team);
            }
            ConfigurationService.teamSpawns1.put(team, new Vector(loc.getX(), loc.getY(), loc.getZ()));
            ConfigurationService.database.set("teamSpawn." + team.toString().toLowerCase() + ".x", (Object)loc.getX());
            ConfigurationService.database.set("teamSpawn." + team.toString().toLowerCase() + ".y", (Object)loc.getY());
            ConfigurationService.database.set("teamSpawn." + team.toString().toLowerCase() + ".z", (Object)loc.getZ());
            saveDatabase();
        }
    }
    
    public static void setLobbySpawn(final Location lobbySpawn) {
        ConfigurationService.lobbySpawn = new Vector(lobbySpawn.getX(), lobbySpawn.getY(), lobbySpawn.getZ());
        ConfigurationService.database.set("lobbySpawn.x", (Object)lobbySpawn.getX());
        ConfigurationService.database.set("lobbySpawn.y", (Object)lobbySpawn.getY());
        ConfigurationService.database.set("lobbySpawn.z", (Object)lobbySpawn.getZ());
        saveDatabase();
    }

	public static void setCapzone(Team fromString, org.bukkit.util.Vector[] vectors) {
		// TODO Auto-generated method stub
		
	}

	public static void setRegions(Team fromString, org.bukkit.util.Vector[] vectors) {
		// TODO Auto-generated method stub
		
	
	}

}
