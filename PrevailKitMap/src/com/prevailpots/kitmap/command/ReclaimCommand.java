package com.prevailpots.kitmap.command;

import com.customhcf.base.BasePlugin;
import com.customhcf.base.command.BaseCommand;
import com.customhcf.base.user.BaseUser;
import com.customhcf.base.user.ServerParticipator;
import com.customhcf.util.PersistableLocation;
import com.prevailpots.kitmap.ConfigurationService;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.HCFHandler;
import com.prevailpots.kitmap.deathban.Deathban;
import com.prevailpots.kitmap.lives.LivesType;

import lombok.Getter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class ReclaimCommand extends BaseCommand {
    private HCF hcfPlugin;
    private List<Rank> ranks;

    public ReclaimCommand(HCF hcfPlugin) {
        super("reclaim", "Reclaim manager");
        this.hcfPlugin = hcfPlugin;
        this.setUsage("/reclaim");

        ranks = new ArrayList<>();

        for(String key : hcfPlugin.getReclaimSettingsConfig().getKeys(false)) {
            String name = ChatColor.translateAlternateColorCodes('&', hcfPlugin.getReclaimSettingsConfig().getString(key + ".name"));
            String rankName = hcfPlugin.getReclaimSettingsConfig().getString(key + ".rankname");

            List<String> commands = new ArrayList<>();
            if(hcfPlugin.getReclaimSettingsConfig().get(key + ".commands") instanceof List) {
                commands.addAll((Collection<? extends String>) hcfPlugin.getReclaimSettingsConfig().get(key + ".commands"));
            } else {
                commands.addAll(Arrays.asList((String[]) hcfPlugin.getReclaimSettingsConfig().get(key + ".commands")));
            }

            this.ranks.add(new Rank(name, rankName, commands));
            Bukkit.getLogger().info("Loaded rank " + rankName);
        }
    }

    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        Player player = (Player) cs;

        if(HCF.getPlugin().getReclaimConfig().contains(player.getUniqueId().toString())) {
            player.sendMessage(ChatColor.RED + "You have already reclaimed.");
            return true;
        }

        Rank shitToApply = null;

        String playerRank = PermissionsEx.getPermissionManager().getUser(player).getGroupNames()[0];
        for(Rank rank : ranks) {
            if(rank.getRankName().equalsIgnoreCase(playerRank)) {
                shitToApply = rank;
                break;
            }
        }

        if(shitToApply == null) {
            player.sendMessage(ChatColor.RED + "No rank to reclaim.");
            return true;
        }

        shitToApply.apply(player);

        try {
            hcfPlugin.getReclaimConfig().set(player.getUniqueId().toString(), 1);
            hcfPlugin.getReclaimConfig().save(hcfPlugin.reclaimFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean isPlayerOnlyCommand() {
        return true;
    }

    private class Rank {
        @Getter private String name, rankName;
        @Getter private List<String> commandsToRun;

        public Rank(String name, String rankName, List<String> commandsToRun) {
            this.name = name;
            this.rankName = rankName;
            this.commandsToRun = commandsToRun;
        }

        public void apply(Player player) {
            if(commandsToRun.size() > 0) {
                for(String cmd : commandsToRun) {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%PLAYER%", player.getName()).replace("%RANK%", rankName));
                }
                Bukkit.getLogger().info("Applied rank " + name + " to player " + player.getName() + ".");
            }
        }
    }
}
