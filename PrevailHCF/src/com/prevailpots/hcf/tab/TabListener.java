package com.prevailpots.hcf.tab;

import com.alexandeh.kraken.tab.PlayerTab;
import com.alexandeh.kraken.tab.event.PlayerTabCreateEvent;
import com.google.common.base.Preconditions;
import com.prevailpots.hcf.ConfigurationService;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.FactionMember;
import com.prevailpots.hcf.faction.type.Faction;
import com.prevailpots.hcf.faction.type.PlayerFaction;
import com.prevailpots.hcf.user.FactionUser;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by Spirit on 04/08/2017.
 */
public class TabListener implements Listener {
    private HCF hcf;
    private TabUpdateTask tabUpdateTask;

    public TabListener() {
        this.hcf = HCF.getPlugin();
    }

    public String format(final Number number, final int decimalPlaces) {
        return this.format(number, decimalPlaces, RoundingMode.HALF_DOWN);
    }

    public String format(final Number number, final int decimalPlaces, final RoundingMode roundingMode) {
        Preconditions.checkNotNull((Object)number, (Object)"The number cannot be null");
        return new BigDecimal(number.toString()).setScale(decimalPlaces, roundingMode).stripTrailingZeros().toPlainString();
    }

    private void clearTabSlots(final PlayerTab playerTab) {
        for (int i = 0; i < 60; ++i) {
            final int x = i % 3;
            final int y = i / 3;
            playerTab.getByPosition(x, y).text(ChatColor.RESET.toString()).send();
        }
    }

    private String getCardinalDirection(final Player player) {
        double rotation = (player.getLocation().getYaw() - 90.0f) % 360.0f;
        if (rotation < 0.0) {
            rotation += 360.0;
        }
        if (0.0 <= rotation && rotation < 22.5) {
            return "N";
        }
        if (22.5 <= rotation && rotation < 67.5) {
            return "NW";
        }
        if (67.5 <= rotation && rotation < 112.5) {
            return "N";
        }
        if (112.5 <= rotation && rotation < 157.5) {
            return "NE";
        }
        if (157.5 <= rotation && rotation < 202.5) {
            return "E";
        }
        if (202.5 <= rotation && rotation < 247.5) {
            return "SE";
        }
        if (247.5 <= rotation && rotation < 292.5) {
            return "S";
        }
        if (292.5 <= rotation && rotation < 337.5) {
            return "SW";
        }
        if (337.5 <= rotation && rotation < 360.0) {
            return "W";
        }
        return null;
    }

    @EventHandler
    public void onPlayerTabCreateEvent(final PlayerTabCreateEvent event) {
        final PlayerTab playerTab = event.getPlayerTab();
        (this.tabUpdateTask = new TabUpdateTask(playerTab)).runTaskTimerAsynchronously(this.hcf, 0L, 4L);
    }

    private class TabUpdateTask extends BukkitRunnable
    {
        private final Player player;
        private final PlayerTab playerTab;

        public TabUpdateTask(final PlayerTab playerTab) {
            this.player = playerTab.getPlayer();
            this.playerTab = playerTab;
        }

        public void run() {
            try {
                if (this.player.isOnline()) {
                    if(this.playerTab != null) {
                        playerTab.getByPosition(1, 0).text(ChatColor.AQUA + ChatColor.BOLD.toString() + "PrevailPots").send();

                        int cur = 2;

                        PlayerFaction faction = HCF.getPlugin().getFactionManager().getPlayerFaction(this.player.getUniqueId());
                        FactionUser user = HCF.getPlugin().getUserManager().getUser(this.player.getUniqueId());
                        if(faction != null) {
                            double dtr = faction.getDeathsUntilRaidable();
                            dtr = Math.round(dtr * 100.0) / 100.0;
                            playerTab.getByPosition(0, cur).text(ChatColor.GOLD + ChatColor.BOLD.toString() + "Faction Info").send(); cur++;
                            playerTab.getByPosition(0, cur).text(ChatColor.AQUA + "DTR: " + ChatColor.RESET + dtr).send(); cur++; cur++;
                        }

                        playerTab.getByPosition(0, cur).text(ChatColor.GOLD + ChatColor.BOLD.toString() + "Player Stats").send(); cur++;
                        playerTab.getByPosition(0, cur).text(ChatColor.AQUA + "Kills: " + ChatColor.RESET +  user.getKills()).send(); cur++;
                        playerTab.getByPosition(0, cur).text(ChatColor.AQUA + "Deaths: " + ChatColor.RESET + user.getDeaths()).send(); cur++; cur++;

                        playerTab.getByPosition(0, cur).text(ChatColor.GOLD + ChatColor.BOLD.toString() + "Coordinates").send(); cur++;
                        Faction factionAt = HCF.getPlugin().getFactionManager().getFactionAt(this.player.getLocation());
                        //playerTab.getByPosition(0, cur).text(((factionAt.isSafezone()) ? ChatColor.GREEN : ChatColor.RED) + factionAt.getName()); cur++; cur++;
                        String facname = null;
                        if(factionAt instanceof PlayerFaction) {
                            PlayerFaction playerFactionAt = (PlayerFaction)factionAt;
                            facname = (playerFactionAt.getMembers().keySet().contains(this.player.getUniqueId()) ? ChatColor.GREEN : ChatColor.RED) + playerFactionAt.getName();
                        } else {
                            facname = (factionAt.isSafezone() ? ChatColor.GREEN : ChatColor.RED) + factionAt.getName();
                        }
                        if(facname != null) {
                            playerTab.getByPosition(0, cur).text(facname).send(); cur++;
                        }
                        playerTab.getByPosition(0, cur).text(this.player.getLocation().getBlockX() + ", " + this.player.getLocation().getBlockZ() + ChatColor.GOLD + " [" + getCardinalDirection(this.player) + "]").send(); cur++; cur++;
                        // 0, 0 [S]

                        playerTab.getByPosition(0, cur).text(ChatColor.GOLD + ChatColor.BOLD.toString() + "Players Online").send(); cur++;
                        playerTab.getByPosition(0, cur).text(ChatColor.AQUA + (HCF.getPlugin().getPlayers() + " players")).send(); cur++;

                        // Middle
                        cur = 2;
                        if(faction != null) {
                            playerTab.getByPosition(1, cur).text(ChatColor.GREEN + faction.getName()).send(); cur++;
                            for(FactionMember member : faction.getMembers().values()) {
                                if(Bukkit.getPlayer(member.getUniqueId()) != null) {
                                    playerTab.getByPosition(1, cur).text(ChatColor.GOLD + member.getName()).send(); cur++;
                                }
                            }
                            cur++;

                            if(faction.getAlliedFactions().size() > 0) {
                                PlayerFaction ally = faction.getAlliedFactions().get(0);
                                if(ally != null) {
                                    playerTab.getByPosition(1, cur).text(ChatColor.BLUE + ally.getName()).send(); cur++;
                                    for(FactionMember member : ally.getMembers().values()) {
                                        playerTab.getByPosition(1, cur).text(ChatColor.GOLD + member.getName()).send(); cur++;
                                    }
                                }
                            }
                        } else {

                        }

                        // Right
                        cur = 2;
                        playerTab.getByPosition(2, cur).text(ChatColor.GOLD + ChatColor.BOLD.toString() + "Faction Types").send(); cur++;
                        playerTab.getByPosition(2, cur).text(ChatColor.AQUA + "5 Man 1 Ally").send(); cur++; cur++;

                        playerTab.getByPosition(2, cur).text(ChatColor.GOLD + ChatColor.BOLD.toString() + "Map Kit").send(); cur++;
                        playerTab.getByPosition(2, cur).text(ChatColor.AQUA + "Protection " + ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.PROTECTION_ENVIRONMENTAL)).send(); cur++;
                        playerTab.getByPosition(2, cur).text(ChatColor.AQUA + "Sharpness " + ConfigurationService.ENCHANTMENT_LIMITS.get(Enchantment.DAMAGE_ALL)).send(); cur++; cur++;

                        playerTab.getByPosition(2, cur).text(ChatColor.GOLD + ChatColor.BOLD.toString() + "Coordinates").send(); cur++;
                        playerTab.getByPosition(2, cur).text(ChatColor.AQUA + "End: 1500, 1500").send(); cur++;
                        playerTab.getByPosition(2, cur).text(ChatColor.GRAY + "(each quadrant)").send(); cur++;
                    }
                } else {
                    this.cancel();
                }
            } catch (Exception e) {}
        }
    }
}
