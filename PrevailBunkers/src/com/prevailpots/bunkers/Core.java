package com.prevailpots.bunkers;

import java.io.File;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.prevailpots.bunkers.commands.ForceStart;
import com.prevailpots.bunkers.commands.LobbySpawn;
import com.prevailpots.bunkers.commands.TeamCommand;
import com.prevailpots.bunkers.config.ConfigurationService;
import com.prevailpots.bunkers.game.GameHandler;
import com.prevailpots.bunkers.game.managers.BalanceManager;
import com.prevailpots.bunkers.game.managers.ChatManager;
import com.prevailpots.bunkers.game.managers.CooldownManager;
import com.prevailpots.bunkers.game.managers.DTRManager;
import com.prevailpots.bunkers.game.managers.FreezeManager;
import com.prevailpots.bunkers.game.managers.ItemManager;
import com.prevailpots.bunkers.game.managers.PointManager;
import com.prevailpots.bunkers.game.managers.ShopManager;
import com.prevailpots.bunkers.game.managers.TeamJoinManager;
import com.prevailpots.bunkers.game.managers.TeamManager;
import com.prevailpots.bunkers.scoreboard.ScoreboardHandler;
import com.prevailpots.bunkers.world.ConfigManager;
import com.prevailpots.bunkers.world.WorldManager;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

public class Core extends JavaPlugin
{
    private static Core instance;
    private static Plugin plugin;
    private GameHandler gameHandler;
    private BalanceManager balanceManager;
    private PointManager pointManager;
    private TeamManager teamManager;
    private FreezeManager freezeManager;
    private ScoreboardHandler scoreboardHandler;
    private ItemManager itemManager;
    private DTRManager dtrManager;
    private ChatManager chatManager;
    private CooldownManager cooldownManager;
    private WorldEditPlugin worldEdit;
    public static final Random RANDOM;
    public WorldManager wm;
    public ConfigManager config;
	private TeamJoinManager teamjoinManager;
    
    static {
        RANDOM = new Random();
    }
    
    public static Core getInstance() {
        if (Core.instance == null) {
            Core.instance = new Core();
        }
        return Core.instance;
    }
    
    public FreezeManager getFreezeManager() {
        if (this.freezeManager == null) {
            this.freezeManager = new FreezeManager();
        }
        return this.freezeManager;
    }
    
    public Core() {
        this.wm = new WorldManager();
        this.config = new ConfigManager("WorldRollback", "config");
    }
    
    public BalanceManager getBalanceManager() {
        if (this.balanceManager == null) {
            this.balanceManager = new BalanceManager();
        }
        return this.balanceManager;
    }
    
    public DTRManager getDTRManager() {
        if (this.dtrManager == null) {
            this.dtrManager = new DTRManager();
        }
        return this.dtrManager;
    }
    
    public ChatManager getChatManager() {
        if (this.chatManager == null) {
            this.chatManager = new ChatManager();
        }
        return this.chatManager;
    }
    
    public ItemManager getItemManager() {
        if (this.itemManager == null) {
            this.itemManager = new ItemManager();
        }
        return this.itemManager;
    }
    
    public PointManager getPointManager() {
        if (this.pointManager == null) {
            this.pointManager = new PointManager();
        }
        return this.pointManager;
    }
    
    public ScoreboardHandler getScoreboardHandler() {
        if (this.scoreboardHandler == null) {
            this.scoreboardHandler = new ScoreboardHandler(getInstance());
        }
        return this.scoreboardHandler;
    }
    
    public GameHandler getGameHandler() {
        if (this.gameHandler == null) {
            this.gameHandler = new GameHandler();
        }
        return this.gameHandler;
    }
    
    public TeamManager getTeamManager() {
        if (this.teamManager == null) {
            this.teamManager = new TeamManager();
        }
        return this.teamManager;
    }
    
    public CooldownManager getCooldownManager() {
        if (this.cooldownManager == null) {
            this.cooldownManager = new CooldownManager();
        }
        return this.cooldownManager;
    }
    
    public void onEnable() {
        Core.instance = this;
        this.gameHandler = new GameHandler();
        this.balanceManager = new BalanceManager();
        this.itemManager = new ItemManager();
        this.pointManager = new PointManager();
        this.chatManager = new ChatManager();
        this.cooldownManager = new CooldownManager();
        this.freezeManager = new FreezeManager();
        this.scoreboardHandler = new ScoreboardHandler(getInstance());
        this.teamManager = new TeamManager();
        this.teamjoinManager = new TeamJoinManager();
        this.dtrManager = new DTRManager();
        final Plugin p = Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (p instanceof WorldEditPlugin) {
            this.worldEdit = (WorldEditPlugin)p;
        }
        ConfigurationService.init();
        this.registerEvents();
        this.registerCommands();
        Bukkit.getScheduler().runTaskTimer((Plugin)getInstance(), (Runnable)this.getGameHandler(), 20L, 20L);
        Bukkit.broadcastMessage("�cThis server is running pBunkers by PrevailPots");
        Core.plugin = (Plugin)this;
        if (this.config.getFile() == null) {
            this.config.createFile();
            this.config.saveFile();
        }
        if (this.config.getFile().getString("world") == null) {
            this.config.getFile().set("world", (Object)"world");
            this.config.getFile().set("map", (Object)"_map");
            this.config.saveFile();
        }
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bunkers] " + ChatColor.YELLOW + "The world has been refreshed for the next game!");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bunkers] " + ChatColor.YELLOW + "This network is running p-1-29-Version-BETA");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bunkers] " + ChatColor.YELLOW + "This plugin is only licensed to prevailpots.com");
        Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "[Bunkers] " + ChatColor.YELLOW + "This plugin was coded by S3ries, Clipped");
    }
    
    
    public void onDisable() {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            p.kickPlayer("�aServer reloading, please try again later.");
            final World delete = Bukkit.getWorld(this.config.getFile().getString("world"));
            final File deleteFolder = delete.getWorldFolder();
            this.wm.deleteWorld(deleteFolder);
            Bukkit.getServer().createWorld(new WorldCreator(this.config.getFile().getString("map")));
            final World source = Bukkit.getWorld(this.config.getFile().getString("map"));
            final File sourceFolder = source.getWorldFolder();
            final World target = Bukkit.getWorld(this.config.getFile().getString("world"));
            final File targetFolder = target.getWorldFolder();
            this.wm.copyWorld(sourceFolder, targetFolder);
        }
    }
    
    public WorldEditPlugin getWorldEdit() {
        return this.worldEdit;
    }
    
    private void registerEvents() {
        final PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents((Listener)this.getGameHandler(), (Plugin)getInstance());
        pm.registerEvents((Listener)new ShopManager(), (Plugin)getInstance());
        pm.registerEvents((Listener)this.scoreboardHandler, (Plugin)getInstance());
        pm.registerEvents((Listener)this.freezeManager, (Plugin)getInstance());
        pm.registerEvents((Listener)this.cooldownManager, (Plugin)getInstance());
        pm.registerEvents((Listener)this.chatManager, (Plugin)getInstance());
        pm.registerEvents((Listener)this.teamjoinManager, (Plugin)getInstance());
    }
    
    private void registerCommands() {
        this.getCommand("setlobbyspawn").setExecutor((CommandExecutor)new LobbySpawn());
        this.getCommand("team").setExecutor((CommandExecutor)new TeamCommand());
        this.getCommand("forcestart").setExecutor((CommandExecutor)new ForceStart());
    }
}
