package com.prevailpots.kitmap;

//This project is licenced for prevailpots.com
//This plugin is not for commercial use and can only be used on minecraft version 1.7.10
//Additional information about license 
//This plugin can only be sold with permission from spirit_pact or s3ries.


import java.io.File;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import com.alexandeh.kraken.Kraken;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.com.google.common.io.ByteArrayDataOutput;
import net.minecraft.util.com.google.common.io.ByteStreams;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.customhcf.base.BasePlugin;
import com.prevailpots.kitmap.balance.EconomyCommand;
import com.prevailpots.kitmap.balance.EconomyManager;
import com.prevailpots.kitmap.balance.FlatFileEconomyManager;
import com.prevailpots.kitmap.balance.PayCommand;
import com.prevailpots.kitmap.balance.ShopSignListener;
import com.prevailpots.kitmap.classes.PvpClass;
import com.prevailpots.kitmap.classes.PvpClassManager;
import com.prevailpots.kitmap.classes.type.archer.ArcherClass;
import com.prevailpots.kitmap.combatlog.CombatLogListener;
import com.prevailpots.kitmap.combatlog.CustomEntityRegistration;
import com.prevailpots.kitmap.command.*;
import com.prevailpots.kitmap.faction.FactionExecutor;
import com.prevailpots.kitmap.faction.FactionManager;
import com.prevailpots.kitmap.faction.FactionMember;
import com.prevailpots.kitmap.faction.FlatFileFactionManager;
import com.prevailpots.kitmap.faction.claim.Claim;
import com.prevailpots.kitmap.faction.claim.ClaimHandler;
import com.prevailpots.kitmap.faction.claim.ClaimWandListener;
import com.prevailpots.kitmap.faction.claim.Subclaim;
import com.prevailpots.kitmap.faction.type.ClaimableFaction;
import com.prevailpots.kitmap.faction.type.EndPortalFaction;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.faction.type.PlayerFaction;
import com.prevailpots.kitmap.faction.type.RoadFaction;
import com.prevailpots.kitmap.faction.type.SpawnFaction;
import com.prevailpots.kitmap.fixes.BeaconStrengthFixListener;
import com.prevailpots.kitmap.fixes.BlockHitFixListener;
import com.prevailpots.kitmap.fixes.BlockJumpGlitchFixListener;
import com.prevailpots.kitmap.fixes.BoatGlitchFixListener;
import com.prevailpots.kitmap.fixes.EnchantLimitListener;
import com.prevailpots.kitmap.fixes.EnderChestRemovalListener;
import com.prevailpots.kitmap.fixes.HungerFixListener;
import com.prevailpots.kitmap.fixes.InfinityArrowFixListener;
import com.prevailpots.kitmap.fixes.NoPermissionClickListener;
import com.prevailpots.kitmap.fixes.PearlGlitchFixListener;
import com.prevailpots.kitmap.fixes.PexCrashFix;
import com.prevailpots.kitmap.fixes.PhaseListener;
import com.prevailpots.kitmap.fixes.PortalTrapFixListener;
import com.prevailpots.kitmap.fixes.PotionLimitListener;
import com.prevailpots.kitmap.fixes.ServerSecurityListener;
import com.prevailpots.kitmap.fixes.VoidGlitchFixListener;
import com.prevailpots.kitmap.fixes.WeatherFixListener;
import com.prevailpots.kitmap.key.KeyListener;
import com.prevailpots.kitmap.key.KeyManager;
import com.prevailpots.kitmap.key.LootExecutor;
import com.prevailpots.kitmap.key.RewardableItemStack;
import com.prevailpots.kitmap.kitmap.KillstreakListener;
import com.prevailpots.kitmap.kitmap.PvPTimerListener;
import com.prevailpots.kitmap.kothgame.CaptureZone;
import com.prevailpots.kitmap.kothgame.EventExecutor;
import com.prevailpots.kitmap.kothgame.EventScheduler;
import com.prevailpots.kitmap.kothgame.EventSignListener;
import com.prevailpots.kitmap.kothgame.NewEventScheduler;
import com.prevailpots.kitmap.kothgame.RefillBox;
import com.prevailpots.kitmap.kothgame.argument.glowstone.GlowstoneExecutor;
import com.prevailpots.kitmap.kothgame.conquest.ConquestExecutor;
import com.prevailpots.kitmap.kothgame.eotw.EOTWHandler;
import com.prevailpots.kitmap.kothgame.eotw.EotwCommand;
import com.prevailpots.kitmap.kothgame.eotw.EotwListener;
import com.prevailpots.kitmap.kothgame.faction.CapturableFaction;
import com.prevailpots.kitmap.kothgame.faction.ConquestFaction;
import com.prevailpots.kitmap.kothgame.faction.GlowstoneFaction;
import com.prevailpots.kitmap.kothgame.faction.KothFaction;
import com.prevailpots.kitmap.kothgame.glowstone.GlowstoneListener;
import com.prevailpots.kitmap.kothgame.koth.KothExecutor;
import com.prevailpots.kitmap.listener.*;
import com.prevailpots.kitmap.lives.LivesExecutor;
import com.prevailpots.kitmap.mongo.MongoManager;
import com.prevailpots.kitmap.scoreboard.ScoreboardHandler;
import com.prevailpots.kitmap.tab.TabListener;
import com.prevailpots.kitmap.task.SaveDataTask;
import com.prevailpots.kitmap.timer.TimerExecutor;
import com.prevailpots.kitmap.timer.TimerManager;
import com.prevailpots.kitmap.user.FactionUser;
import com.prevailpots.kitmap.user.GUIManager;
import com.prevailpots.kitmap.user.UserManager;
import com.prevailpots.kitmap.visualise.ProtocolLibHook;
import com.prevailpots.kitmap.visualise.VisualiseHandler;
import com.prevailpots.kitmap.visualise.WallBorderListener;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;

import redis.clients.jedis.Jedis;

public class HCF extends JavaPlugin {
    private static final long MINUTE;
    private static final long HOUR;
    private static HCF plugin;

    static {
        MINUTE = TimeUnit.MINUTES.toMillis(1L);
        HOUR = TimeUnit.HOURS.toMillis(1L);
    }
    private Jedis jedis;
    private Random random;
    private Message message;
    private MongoManager mongoManager;
    public EventScheduler eventScheduler;
    private HCFHandler hcfHandler;
    private WorldEditPlugin worldEdit;
    private EconomyManager economyManager;
    private ClaimHandler claimHandler;
    private BukkitTask saveDataTask;
    private KeyManager keyManager;
    private EOTWHandler eotwHandler;
    private FactionManager factionManager;
    private PvpClassManager pvpClassManager;
    private ScoreboardHandler scoreboardHandler;
    private TimerManager timerManager;
    private UserManager userManager;
    private VisualiseHandler visualiseHandler;
    public NewEventScheduler newEventScheduler;
    private YamlConfiguration reclaimConfig;
    private YamlConfiguration reclaimSettingsConfig;
    private WorldListener wl;
    @Getter
    private Kraken tabApi;
    @Getter @Setter
    private int players;

    public File reclaimFile = new File(this.getDataFolder(), "reclaims.yml");
    public File reclaimSettingsFile = new File(this.getDataFolder(), "reclaimsettings.yml");

    public static HCF getPlugin() {
        return HCF.plugin;
    }

    public HCF() {
        random = new Random();
    }

    public static String getRemaining(final long millis, final boolean milliseconds) {
        return getRemaining(millis, milliseconds, true);
    }

    public static String getRemaining(final long duration, final boolean milliseconds, final boolean trail) {
        if(milliseconds && duration < HCF.MINUTE) {
            return (trail ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(duration * 0.001) + 's';
        }
        return DurationFormatUtils.formatDuration(duration, ((duration >= HCF.HOUR) ? "HH:" : "") + "mm:ss");
    }

    public void onEnable() {
        HCF.plugin = this;
        this.mongoManager = new MongoManager();
        mongoManager.connect();
        if (!new File(getDataFolder(), "config.yml").exists()) {
            saveDefaultConfig();
        }
        getConfig().options().copyDefaults(true);
        try {
            jedis = new Jedis("localhost");
        }
        catch (Exception e) {
            System.out.println("!Not Connected! Plugin not enabling!!!!");
            System.out.println("!!Not Connected! Plugin not enabling!!!");
            System.out.println("!!!Not Connected! Plugin not enabling!!");
            System.out.println("!!!!Not Connected! Plugin not enabling!");
            e.printStackTrace();
            return;
        }
        CustomEntityRegistration.registerCustomEntities();
        ProtocolLibHook.hook(this);
        final Plugin wep = Bukkit.getPluginManager().getPlugin("WorldEdit");
        worldEdit = ((wep instanceof WorldEditPlugin && wep.isEnabled()) ? ((WorldEditPlugin) wep) : null);
        try {
            if(!reclaimFile.exists())
                reclaimFile.createNewFile();
            if(!reclaimSettingsFile.exists()) {
                saveResource("reclaimsettings.yml", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        reclaimConfig = YamlConfiguration.loadConfiguration(reclaimFile);
        reclaimSettingsConfig = YamlConfiguration.loadConfiguration(reclaimSettingsFile);
        registerConfiguration();
        registerCommands();
        runPrereleaseChecker(this);
        registerManagers();
        registerListeners();
        this.tabApi = new Kraken(this);
        wl = new WorldListener(this);
        Cooldowns.createCooldown("Assassin_item_cooldown");
        Cooldowns.createCooldown("diamond_revive_cooldown");
        Cooldowns.createCooldown("Archer_item_cooldown");
        Cooldowns.createCooldown("alpha_revive_command_cooldown");
        Cooldowns.createCooldown("Archer_item_cooldown1");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "PlayerCount2");
        getServer().getMessenger().registerIncomingPluginChannel(this, "PlayerCount2", wl);
        new BukkitRunnable() {
            @Override
            public void run() {
                updatePlayerCount("HCF");
            }
        }.runTaskTimerAsynchronously(this, 4L, 4L);
        saveDataTask = new SaveDataTask().runTaskTimerAsynchronously(this, 20, 5 * 60 * 20);
        new BukkitRunnable() {
            public void run() {
                saveData();
            }
        }.runTaskTimerAsynchronously(this, 5*60*20, 5*60*20);
    }

    private void saveData() {
        factionManager.saveFactionData();
        newEventScheduler.saveData();
        hcfHandler.saveHCFSettings();
        userManager.saveUserData();
        timerManager.saveTimerData();
        keyManager.saveKeyData();
    }

    private void runPrereleaseChecker(HCF hcf)
    {
        new BukkitRunnable() {
            @Override
            public void run() {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "pex reload");
                    }
                }.runTaskTimer(hcf, 0, 20*30);
            }
        };
    }
    
    public void onDisable() {
        CustomEntityRegistration.unregisterCustomEntities();
        CombatLogListener.removeCombatLoggers();

        // Fixes error where speed/etc never gets removed and you can use it after reboot without having the kit equipped
        for(Player p : Bukkit.getOnlinePlayers()) {
            PvpClass equippedClass = pvpClassManager.getEquippedClass(p);
            if(equippedClass != null) {
                pvpClassManager.setEquippedClass(p, null);
            }
        }

        saveData();
        scoreboardHandler.clearBoards();
        saveDataTask.cancel();
        SaveDataTask.saveBalanceData();
        mongoManager.getClient().close();
        jedis = null;
        HCF.plugin = null;
    }

    private void registerConfiguration() {
        ConfigurationSerialization.registerClass(RefillBox.class);
        ConfigurationSerialization.registerClass(CaptureZone.class);
        ConfigurationSerialization.registerClass(Claim.class);
        ConfigurationSerialization.registerClass(Subclaim.class);
        ConfigurationSerialization.registerClass(ClaimableFaction.class);
        ConfigurationSerialization.registerClass(ConquestFaction.class);
        ConfigurationSerialization.registerClass(RewardableItemStack.class);
        ConfigurationSerialization.registerClass(CapturableFaction.class);
        ConfigurationSerialization.registerClass(FactionUser.class);
        ConfigurationSerialization.registerClass(KothFaction.class);
        ConfigurationSerialization.registerClass(EndPortalFaction.class);
        ConfigurationSerialization.registerClass(Faction.class);
        ConfigurationSerialization.registerClass(FactionMember.class);
        ConfigurationSerialization.registerClass(PlayerFaction.class);
        ConfigurationSerialization.registerClass(SpawnFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.NorthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.EastRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.SouthRoadFaction.class);
        ConfigurationSerialization.registerClass(RoadFaction.WestRoadFaction.class);
        ConfigurationSerialization.registerClass(GlowstoneFaction.class);
    }

    private void registerListeners() {
        final PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new ShopSignListener(this), this);
        manager.registerEvents(new DonorOnlyListener(), this);
        manager.registerEvents(new ArcherClass(this), this);
        manager.registerEvents(new PortalTrapFixListener(), this);
        manager.registerEvents(new MOTDListener(), this);
        manager.registerEvents(new KeyListener(this), this);
        manager.registerEvents(new PexCrashFix(), this);
        manager.registerEvents(new WeatherFixListener(), this);
        manager.registerEvents(new NoPermissionClickListener(), this);
        manager.registerEvents(new ItemFilterListener(this), this);
        manager.registerEvents(new AutoSmeltOreListener(), this);
        manager.registerEvents(new BlockHitFixListener(), this);
        manager.registerEvents(new BlockJumpGlitchFixListener(), this);
        manager.registerEvents(new BoatGlitchFixListener(), this);
        manager.registerEvents(new BookDeenchantListener(), this);
        manager.registerEvents(new BorderListener(), this);
        manager.registerEvents(new BottledExpListener(), this);
        manager.registerEvents(new KillstreakListener(this), this);
        manager.registerEvents(new PvPTimerListener(), this);
        manager.registerEvents(new ChatListener(this), this);
        manager.registerEvents(new ClaimWandListener(this), this);
        manager.registerEvents(new CombatLogListener(this), this);
        manager.registerEvents(new CoreListener(this), this);
        manager.registerEvents(new CrowbarListener(this), this);
        manager.registerEvents(new DeathListener(this), this);
        manager.registerEvents(new DeathMessageListener(this), this);
        manager.registerEvents(new DeathSignListener(this), this);
        manager.registerEvents(new ElevatorClass(this), this);
        manager.registerEvents(new EnchantLimitListener(), this);
        manager.registerEvents(new EnderChestRemovalListener(), this);
        manager.registerEvents(new EntityLimitListener(), this);
        manager.registerEvents(new FlatFileFactionManager(this), this);
        manager.registerEvents(new EndListener(), this);
        manager.registerEvents(new EotwListener(this), this);
        manager.registerEvents(new EventSignListener(), this);
        manager.registerEvents(new ExpMultiplierListener(), this);
        manager.registerEvents(new TabListener(), this);
        manager.registerEvents(new FactionListener(this), this);
        manager.registerEvents(new GlowstoneListener(), this);
        manager.registerEvents(new FoundDiamondsListener(this), this);
        manager.registerEvents(new FurnaceSmeltSpeederListener(), this);
        manager.registerEvents(new InfinityArrowFixListener(), this);
        manager.registerEvents(new KitListener(this), this);
        manager.registerEvents(new ItemStatTrackingListener(),  this);
        manager.registerEvents(new ServerSecurityListener(), this);
        manager.registerEvents(new PhaseListener(), this);
        manager.registerEvents(new HungerFixListener(), this);
        manager.registerEvents(new PotionLimitListener(), this);
        manager.registerEvents(new FactionsCoreListener(this), this);
        manager.registerEvents(new SignSubclaimListener(this), this);
        manager.registerEvents(new FastSmeltListener(), this);
        manager.registerEvents(new SkullListener(), this);
        manager.registerEvents(new TabCommand(), this);
        manager.registerEvents(new BeaconStrengthFixListener(), this);
        manager.registerEvents(new VoidGlitchFixListener(),  this);
        manager.registerEvents(new WallBorderListener(this), this);
        manager.registerEvents(wl = new WorldListener(this), this);
        manager.registerEvents(new UnRepairableListener(), this);
        manager.registerEvents(new PearlGlitchFixListener(this), this);
        manager.registerEvents(new AntiPexCrash(), this);
    }

    private void updatePlayerCount(String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(server);
        out.writeUTF("HCF");
        getServer().sendPluginMessage(this, "PlayerCount2", out.toByteArray());
    }

    private void registerCommands() {
        BasePlugin.getPlugin().getCommandManager().registerCommand(new ReclaimCommand(this));
        getCommand("conquest").setExecutor(new ConquestExecutor(this));
        getCommand("coords").setExecutor(new CoordsCommand());
        getCommand("donor").setExecutor(new ToggleDonorOnly());
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("economy").setExecutor(new EconomyCommand(this));
        getCommand("toggleend").setExecutor(new ToggleEndCommand(this));
        getCommand("sotw").setExecutor(new SOTWCommand());
        getCommand("random").setExecutor(new RandomCommand(this));
        getCommand("crowbar").setExecutor(new CrowbarCommand());
        getCommand("eotw").setExecutor(new EotwCommand(this));
        getCommand("game").setExecutor(new EventExecutor(this));
        getCommand("glowstone").setExecutor(new GlowstoneExecutor(this));
        getCommand("help").setExecutor(new HelpCommand());
        getCommand("faction").setExecutor(new FactionExecutor(this));
        getCommand("gopple").setExecutor(new GoppleCommand(this));
        getCommand("cobblestone").setExecutor(new CobblestoneCommand());
        getCommand("mobdrops").setExecutor(new MobdropsCommand());
        getCommand("stats").setExecutor(new PlayerStatsCommand());
        getCommand("koth").setExecutor(new KothExecutor(this));
        getCommand("mapkit").setExecutor(new MapKitCommand());
        getCommand("stafflives").setExecutor(new LivesExecutor(this));
        getCommand("lives").setExecutor(new LivesCommand(this));
        getCommand("alpha").setExecutor(new AlphaRevives(this));
        getCommand("location").setExecutor(new LocationCommand(this));
        getCommand("focus").setExecutor(new FocusCommand());
        getCommand("logout").setExecutor(new LogoutCommand(this));
        getCommand("pvptimer").setExecutor(new PvpTimerCommand(this));
        getCommand("refund").setExecutor( new RefundCommand());
        getCommand("list").setExecutor(new ListCommand());
        getCommand("servertime").setExecutor(new ServerTimeCommand());
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("timer").setExecutor(new TimerExecutor(this));
        getCommand("setborder").setExecutor(new SetBorderCommand());
        getCommand("key").setExecutor(new LootExecutor(this));
        getCommand("reloadtab").setExecutor(new ReloadTabCommand());
        getCommand("removetab").setExecutor(new RemoveTabCommand());
        getCommand("tab").setExecutor(new TabCommand());
        final Map<String, Map<String, Object>> map =  getDescription().getCommands();
        for(final Map.Entry<String, Map<String, Object>> entry : map.entrySet()) {
            final PluginCommand command = getCommand(entry.getKey());
            command.setPermission("command." + entry.getKey());
            command.setPermissionMessage(ChatColor.RED + "No permission.");
        }
    }

    private void registerManagers() {
        claimHandler = new ClaimHandler(this);
        eotwHandler = new EOTWHandler(this);
        newEventScheduler = new NewEventScheduler(this);
        eventScheduler = new EventScheduler(this);
        factionManager = new FlatFileFactionManager(this);
        pvpClassManager = new PvpClassManager(this);
        timerManager = new TimerManager(this);
        scoreboardHandler = new ScoreboardHandler(this);
        userManager = new UserManager(this);
        visualiseHandler = new VisualiseHandler();
        keyManager = new KeyManager(this);
        hcfHandler = new HCFHandler(this);
        message = new Message(this);
        economyManager = new FlatFileEconomyManager(this);
    }

    public EconomyManager getEconomyManager(){
        return economyManager;
    }


    public Message getMessage(){
        return message;
    }



    public WorldEditPlugin getWorldEdit() {
        return worldEdit;
    }

    public KeyManager getKeyManager() {
        return keyManager;
    }


    public ClaimHandler getClaimHandler() {
        return claimHandler;
    }



    public EOTWHandler getEotwHandler() {
        return eotwHandler;
    }

    public FactionManager getFactionManager() {
        return factionManager;
    }


    public Random getRandom() {
        return random;
    }

    public PvpClassManager getPvpClassManager() {
        return pvpClassManager;
    }

    public ScoreboardHandler getScoreboardHandler() {
        return scoreboardHandler;
    }

    public TimerManager getTimerManager() {
        return timerManager;
    }

    public UserManager getUserManager() {
        return userManager;
    }

    public VisualiseHandler getVisualiseHandler() {
        return visualiseHandler;
    }

    public HCFHandler getHcfHandler(){
        return hcfHandler;
    }

    public YamlConfiguration getReclaimConfig() { return reclaimConfig; }

    public YamlConfiguration getReclaimSettingsConfig() { return reclaimSettingsConfig; }

    public Jedis getJedis() {
        return jedis;
    }

    public MongoManager getMongoManager() {
        return mongoManager;
    }
}
