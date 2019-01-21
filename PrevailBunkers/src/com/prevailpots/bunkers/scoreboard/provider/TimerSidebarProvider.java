package com.prevailpots.bunkers.scoreboard.provider;

import org.bukkit.entity.*;

import com.prevailpots.bunkers.*;
import com.prevailpots.bunkers.game.*;
import com.prevailpots.bunkers.game.managers.*;
import com.prevailpots.bunkers.scoreboard.*;


import java.util.*;

import org.apache.commons.lang3.*;
import org.bukkit.*;

public class TimerSidebarProvider implements SidebarProvider
{
    public static final String STRAIGHT_LINE;
    private final Core plugin;
	private boolean add;
    
    static {
        STRAIGHT_LINE = "------------------------------------".substring(0, 14);
    }
    
    public TimerSidebarProvider(final Core plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public String getTitle() {
        return "§b§lPrevailPots §c[Bunkers]";
    }
    
    @Override
    public List<SidebarEntry> getLines(final Player player) {
        final List<SidebarEntry> lines = new ArrayList<SidebarEntry>();
        if (!this.plugin.getGameHandler().getTime().equalsIgnoreCase("pre game")) {
            lines.add(new SidebarEntry("", "§aGame Time§7: ", "§f" + this.plugin.getGameHandler().getTime()));
            final Team t = this.plugin.getGameHandler().getPlayers().get(player.getName());
            lines.add(new SidebarEntry("", "§aTeam§7: ", t.getColor() + StringUtils.capitalize(t.toString().toLowerCase())));
            lines.add(new SidebarEntry(" §b» ", "§aKills§7: ", "§f" + this.plugin.getTeamManager().getKills(player)));
            lines.add(new SidebarEntry(" §b» §aBala", "§ance§7: §f", this.plugin.getBalanceManager().getBalanceFormatted(player)));
            lines.add(new SidebarEntry(" §b» ", "§aPoints§7: ", "§f" + this.plugin.getPointManager().getPoints(player)));
            lines.add(new SidebarEntry(" §b» ", "§aDTR§7: ", this.plugin.getDTRManager().getDTRFormatted(Core.getInstance().getGameHandler().getPlayers().get(player.getName()))));
        }
        else {
            if (this.plugin.getGameHandler().gameStartCountdown != 10L) {
                lines.add(new SidebarEntry("", "§6Countdown§7: ", "§f" + this.plugin.getGameHandler().gameStartCountdown + "s"));
            }
            else {
                lines.add(new SidebarEntry("", "§6Game State§7: ", "§eLobby"));
            }
            lines.add(new SidebarEntry("", "§6Players§7: ", "§e" + String.valueOf(this.plugin.getGameHandler().getPlayers().size()) + "/" + 16 + ":"));
            lines.add(new SidebarEntry("  §b»  ", "§cRed§7: ", "§e" + this.plugin.getGameHandler().teamSize(Team.RED) + "/" + 4));
            lines.add(new SidebarEntry("  §b»  ", "§aGreen§7: ", "§e" + this.plugin.getGameHandler().teamSize(Team.GREEN) + "/" + 4));
            lines.add(new SidebarEntry("  §b»  ", "§9Blue§7: ", "§e" + this.plugin.getGameHandler().teamSize(Team.BLUE) + "/" + 4));
            lines.add(new SidebarEntry("  §b»  ", "§eYellow§7: ", "§e" + this.plugin.getGameHandler().teamSize(Team.YELLOW) + "/" + 4));
        }
        if (Core.getInstance().getCooldownManager().hasCooldown(player, CooldownManager.Type.EPEARL)) {
            lines.add(new SidebarEntry(" * ", "§9§lEnder", "pearl§7: §f" + Core.getInstance().getCooldownManager().getCooldownFormatted(player, CooldownManager.Type.EPEARL)));
        }
        if (Core.getInstance().getCooldownManager().hasCooldown(player, CooldownManager.Type.GAPPLE)) {
            lines.add(new SidebarEntry(" * ", "§6§lApple", "§7: §f" + Core.getInstance().getCooldownManager().getCooldownFormatted(player, CooldownManager.Type.GAPPLE)));
        }
        if (!lines.isEmpty()) {
            lines.add(0, new SidebarEntry(String.valueOf(ChatColor.GRAY.toString()) + ChatColor.STRIKETHROUGH.toString(), TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
            lines.add(lines.size(), new SidebarEntry(ChatColor.GRAY.toString(), String.valueOf(ChatColor.STRIKETHROUGH.toString()) + TimerSidebarProvider.STRAIGHT_LINE, TimerSidebarProvider.STRAIGHT_LINE));
        }
        return lines;
    }
}
