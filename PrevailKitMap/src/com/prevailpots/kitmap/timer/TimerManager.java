package com.prevailpots.kitmap.timer;

import com.customhcf.util.Config;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.kothgame.EventTimer;
import com.prevailpots.kitmap.timer.type.*;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class TimerManager implements Listener {
    public final LogoutTimer logoutTimer;
    public final EnderPearlTimer enderPearlTimer;
    public final NotchAppleTimer notchAppleTimer;
    public final PvpProtectionTimer pvpProtectionTimer;
    public final StuckTimer stuckTimer;
    public final SpawnTagTimer spawnTagTimer;
    public final SOTWTimer sotw;
    public final TeleportTimer teleportTimer;
    public final EventTimer eventTimer;
    public final RestartTimer restartTimer;
    public final SandDuneTimer sandDuneTimer;
    public final ArcherTimer archerTimer;
    private final Set<Timer> timers;
    private Config config;
    private final HCF plugin;


    public TimerManager(final HCF plugin) {
        this.timers = new HashSet<Timer>();
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents((Listener) this, (Plugin) plugin);
        this.registerTimer(this.sotw = new SOTWTimer());
        this.registerTimer(this.archerTimer = new ArcherTimer(plugin));
        this.registerTimer(this.enderPearlTimer = new EnderPearlTimer(plugin));
        this.registerTimer(this.logoutTimer = new LogoutTimer());
        this.registerTimer(this.notchAppleTimer = new NotchAppleTimer(plugin));
        this.registerTimer(this.stuckTimer = new StuckTimer());
        this.registerTimer(this.restartTimer = new RestartTimer());
        this.registerTimer(this.sandDuneTimer = new SandDuneTimer());
        this.registerTimer(this.pvpProtectionTimer = new PvpProtectionTimer(plugin));
        this.registerTimer(this.spawnTagTimer = new SpawnTagTimer(plugin));
        this.registerTimer(this.teleportTimer = new TeleportTimer(plugin));
        this.registerTimer(this.eventTimer = new EventTimer(plugin));

        this.reloadTimerData();

    }

    public Collection<Timer> getTimers() {
        return this.timers;
    }

    public void registerTimer(final Timer timer) {
        this.timers.add(timer);
        if(timer instanceof Listener) {
            this.plugin.getServer().getPluginManager().registerEvents((Listener) timer, this.plugin);
        }
    }

    public Collection<Timer> getActiveTimers(Player player){
        List<Timer> activeTimers = new ArrayList<>();
        for(Timer timer : timers){
            if(timer instanceof PlayerTimer){
                if(((PlayerTimer) timer).hasCooldown(player)){
                    activeTimers.add(timer);
                }
            }
        }
        return activeTimers;
    }

    public void unregisterTimer(final Timer timer) {
        this.timers.remove(timer);
    }



    public void reloadTimerData() {
//        this.config = new Config(this.plugin, "timers");
//        for(final Timer timer : this.timers) {
//            timer.load(this.config);
//        }
    }
    public void saveTimerData() {
//        for(final Timer timer : this.timers) {
//            timer.onDisable(this.config);
//        }
//        this.config.save();
    }





}
