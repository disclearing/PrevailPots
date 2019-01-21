package com.prevailpots.kitmap.scoreboard.provider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.customhcf.base.BasePlugin;
import com.customhcf.util.BukkitUtils;
import com.prevailpots.kitmap.Cooldowns;
import com.prevailpots.kitmap.DateTimeFormats;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.classes.ClassType;
import com.prevailpots.kitmap.classes.PvpClass;
import com.prevailpots.kitmap.classes.type.bard.BardClass;
import com.prevailpots.kitmap.faction.type.PlayerFaction;
import com.prevailpots.kitmap.kitmap.*;
import com.prevailpots.kitmap.kothgame.EventTimer;
import com.prevailpots.kitmap.kothgame.eotw.EOTWHandler;
import com.prevailpots.kitmap.kothgame.faction.ConquestFaction;
import com.prevailpots.kitmap.kothgame.faction.EventFaction;
import com.prevailpots.kitmap.kothgame.tracker.ConquestTracker;
import com.prevailpots.kitmap.scoreboard.SidebarEntry;
import com.prevailpots.kitmap.scoreboard.SidebarProvider;
import com.prevailpots.kitmap.timer.GlobalTimer;
import com.prevailpots.kitmap.timer.PlayerTimer;
import com.prevailpots.kitmap.timer.Timer;
import com.prevailpots.kitmap.timer.type.NotchAppleTimer;
import com.prevailpots.kitmap.timer.type.SandDuneTimer;
import com.prevailpots.kitmap.timer.type.TeleportTimer;

import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.util.gnu.trove.map.TObjectIntMap;

public class   TimerSidebarProvider implements SidebarProvider {
    public static final ThreadLocal<DecimalFormat> CONQUEST_FORMATTER;

    static {
        CONQUEST_FORMATTER = new ThreadLocal<DecimalFormat>() {
            @Override
            protected DecimalFormat initialValue() {
                return new DecimalFormat("00.0");
            }
        };
    }

    
    protected static double tps;
    protected static long lastSaved;
    private final HCF plugin;
    TObjectIntMap<UUID> killStreakMap;

    public TimerSidebarProvider(final HCF plugin) {
        this.plugin = plugin;
    }

    private static String handleBardFormat(final long millis, final boolean trailingZero) {
        return (trailingZero ? DateTimeFormats.REMAINING_SECONDS_TRAILING : DateTimeFormats.REMAINING_SECONDS).get().format(millis * 0.001);
    }
    protected static final String STRAIGHT_LINE = BukkitUtils.STRAIGHT_LINE_DEFAULT.substring(0, 14);
    @Override
    public String getTitle() {
    if(plugin.getHcfHandler() == null || plugin.getHcfHandler().getMapNumber() == null || plugin.getConfig().getString("Scoreboard.title") == null){
        return ChatColor.RED + "Configuration Error!";
    }
        return ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Scoreboard.title").replaceAll("%MAP%", plugin.getHcfHandler().getMapNumber().toString()));
    }
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        List<SidebarEntry> lines = new ArrayList<SidebarEntry>();
        final EOTWHandler.EotwRunnable eotwRunnable = this.plugin.getEotwHandler().getRunnable();

            final PvpClass pvpClass = this.plugin.getPvpClassManager().getEquippedClass(player);
        final EventTimer eventTimer = this.plugin.getTimerManager().eventTimer;
        List<SidebarEntry> conquestLines = null;
        final EventFaction eventFaction = eventTimer.getEventFaction();
        lines.add(new SidebarEntry(String.valueOf(ChatColor.YELLOW.toString()) + " » " + ChatColor.GOLD, "Kills" + ChatColor.GRAY + ": §f", this.plugin.getUserManager().getUser(player.getUniqueId()).getKills()));
        lines.add(new SidebarEntry(String.valueOf(ChatColor.YELLOW.toString()) + " » " + ChatColor.GOLD, "Deaths" + ChatColor.GRAY + ": §f", this.plugin.getUserManager().getUser(player.getUniqueId()).getDeaths()));
        if(pvpClass != null) {
                if(pvpClass instanceof BardClass) {
                    final BardClass bardClass = (BardClass) pvpClass;
                    lines.add(new SidebarEntry(ChatColor.AQUA + "Bard ", ChatColor.AQUA + "Energy", ChatColor.GRAY + ": " + ChatColor.RED+ handleBardFormat(bardClass.getEnergyMillis(player), true)));
                    final long remaining2 = bardClass.getRemainingBuffDelay(player);
                    if(remaining2 > 0L) {
                        lines.add(new SidebarEntry(ChatColor.GREEN + "Bard ", ChatColor.GREEN + "Effect", ChatColor.GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining2, true)));
                    }
                }
            }


        final Collection<Timer> timers = this.plugin.getTimerManager().getTimers();
        for(final Timer timer : timers) {
            if(timer instanceof PlayerTimer && !(timer instanceof NotchAppleTimer) && !(timer instanceof TeleportTimer)) {
                final PlayerTimer playerTimer = (PlayerTimer) timer;
                final long remaining3 = playerTimer.getRemaining(player);
                if(remaining3 <= 0L) {
                    continue;
                }
                String timerName = playerTimer.getName();
                if(timerName.length() > 14) {
                    timerName = timerName.substring(0, timerName.length());
                }
                lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), timerName + ChatColor.GRAY, ": " + ChatColor.RED + HCF.getRemaining(remaining3, true)));
            }
            if(timer instanceof GlobalTimer && !(timer instanceof SandDuneTimer)) {
                final GlobalTimer playerTimer = (GlobalTimer) timer;
                final long remaining3 = playerTimer.getRemaining();
                if(remaining3 <= 0L) {
                    continue;
                }
                String timerName = playerTimer.getName();
                if(timerName.length() > 14) {
                    timerName = timerName.substring(0, timerName.length());
                }
                lines.add(new SidebarEntry(playerTimer.getScoreboardPrefix(), timerName + ChatColor.GRAY, ": " + ChatColor.RED + HCF.getRemaining(remaining3, true)));
            }
        }
        if(eotwRunnable != null) {
            long remaining = eotwRunnable.getTimeUntilStarting();
            lines.add(new SidebarEntry(ChatColor.DARK_RED + ChatColor.BOLD.toString() + "EO", "TW" + ChatColor.GRAY + ": ", ChatColor.GREEN + "Activated"));
            lines.add(new SidebarEntry(ChatColor.DARK_RED + " Â» ", ChatColor.RED + "World", " Border" + ChatColor.GRAY + ": " + ChatColor.RED+
                    HCF.getPlugin().getHcfHandler().getWorldBorder(World.Environment.NORMAL)));
            if(remaining > 0L) {
                lines.add(new SidebarEntry(ChatColor.DARK_RED + " Â» ", ChatColor.RED + "Begins", " In" + ChatColor.GRAY + ": " + ChatColor.RED + HCF.getRemaining(remaining, true)));
            }
        }


        if(eventFaction instanceof ConquestFaction) {
            lines.add(lines.size(), new SidebarEntry(ChatColor.GRAY,  ChatColor.STRIKETHROUGH + STRAIGHT_LINE,ChatColor.STRIKETHROUGH + STRAIGHT_LINE));
            final ConquestFaction conquestFaction = (ConquestFaction) eventFaction;
            final DecimalFormat format = TimerSidebarProvider.CONQUEST_FORMATTER.get();
            conquestLines = new ArrayList<>();
            lines.add(new SidebarEntry(ChatColor.YELLOW.toString() + ChatColor.BOLD, conquestFaction.getName() + ChatColor.GRAY, ":"));
            final ConquestTracker conquestTracker = (ConquestTracker) conquestFaction.getEventType().getEventTracker();
            int count = 0;
            for(final Map.Entry<PlayerFaction, Integer> entry : conquestTracker.getFactionPointsMap().entrySet()) {
                String factionName = entry.getKey().getDisplayName(player);
                if(factionName.length() > 16) {
                    factionName = factionName.substring(0, 16);
                }
                lines.add(new SidebarEntry("  "+ ChatColor.RED, factionName, ChatColor.GRAY + ": " + ChatColor.RED + entry.getValue()));
                if(++count == 3) {
                    break;
                }
            }
        }

        if(plugin.getPvpClassManager().getEquippedClass(player) != null){
            if(plugin.getPvpClassManager().getEquippedClass(player).getClassType() == ClassType.ARCHER && Cooldowns.isOnCooldown("Archer_item_cooldown", player)){
                lines.add(new SidebarEntry(ChatColor.GREEN, "Speed Cooldown" +ChatColor.GRAY + ": ", ChatColor.RED.toString() + HCF.getRemaining(Cooldowns.getCooldownForPlayerLong("Archer_item_cooldown", player), true)));
            }
            if(plugin.getPvpClassManager().getEquippedClass(player).getClassType() == ClassType.MINER){
                lines.add(new SidebarEntry(ChatColor.YELLOW, "Cobblestone" +ChatColor.GRAY + ": ", (plugin.getUserManager().getUser(player.getUniqueId()).isCobblestone() ? ChatColor.GREEN + "Enabled" : ChatColor.RED + "Disabled")));
                lines.add(new SidebarEntry(ChatColor.AQUA, "Diamonds" +ChatColor.GRAY + ": ", ChatColor.RED.toString() + plugin.getUserManager().getUser(player.getUniqueId()).getDiamondsMined()));
            }
        }


        if(player.hasPermission("command.staffmode") && BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isStaffUtil()) {
            lines.clear();
        	if (player.hasPermission("command.vanish")) {
                lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " » " + ChatColor.YELLOW.toString(), "Visibility" + ChatColor.DARK_GRAY + ": ", BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isVanished() ? ChatColor.GREEN + "Vanished" : ChatColor.RED + "Visible"));
            }
            if (player.hasPermission("command.gamemode")) {
                lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " » " + ChatColor.YELLOW.toString(), "Gamemode" + ChatColor.DARK_GRAY + ": ", player.getGameMode() == GameMode.CREATIVE ? ChatColor.GREEN + "Creative" : ChatColor.RED + "Survival"));
            } else if (player.hasPermission("command.fly")) {
                lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " » " + ChatColor.YELLOW.toString(), "Flight" + ChatColor.DARK_GRAY + ": ", player.getAllowFlight() ? ChatColor.GREEN + "True" : ChatColor.RED + "False"));
            }
            if (player.hasPermission("command.staffchat")) {
                lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " » " + ChatColor.YELLOW.toString(), "Chat Mode" + ChatColor.DARK_GRAY + ": ", BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).isInStaffChat() ? ChatColor.GREEN + "Staff" : ChatColor.RED + "Global"));
            }
            if (player.hasPermission("command.ticks")) {
                lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " » " + ChatColor.YELLOW.toString(), "TPS§7:§a " + Bukkit.spigot().getTPS()[0] + ChatColor.GREEN + "", true));
            }

            if(((BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).getClicked()) != (long) 0)) {
                lines.add(new SidebarEntry(ChatColor.GOLD.toString() + " » " + ChatColor.YELLOW.toString(), "Timer" + ChatColor.DARK_GRAY + ": ", ((BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).getClicked()) == (long) 0) ? ChatColor.RED + "Off" : ChatColor.GOLD + HCF.getRemaining(System.currentTimeMillis() - BasePlugin.getPlugin().getUserManager().getUser(player.getUniqueId()).getClicked(), true)));
            }
        }
            if(conquestLines != null && !conquestLines.isEmpty()) {
                conquestLines.addAll(lines);
                lines = conquestLines;
            }
        if (!lines.isEmpty()) {
            if (plugin.getConfig().getBoolean("Scoreboard.lines")) {
                lines.add(0, new SidebarEntry(ChatColor.GRAY, STRAIGHT_LINE, STRAIGHT_LINE));
                lines.add(lines.size(), new SidebarEntry(ChatColor.GRAY, ChatColor.STRIKETHROUGH + STRAIGHT_LINE, STRAIGHT_LINE));
            }
        }
        return lines;
        }
    }

