package com.prevailpots.hcf.command;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.customhcf.base.BasePlugin;
import com.customhcf.util.BukkitUtils;
import com.customhcf.util.chat.ClickAction;
import com.customhcf.util.chat.Text;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.user.FactionUser;

/**
 * Created by TREHOME on 01/10/2016.
 */
public class PlayerStatsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        Player player = (Player) cs;
        if(args.length  == 0){
            player.sendMessage(ChatColor.RED + "Usage: /" +s + " [player]");
            return true;
        }
            if(args.length == 1) {
                if(Bukkit.getPlayer(args[0]) == null){
                    if(Bukkit.getOfflinePlayer(args[0]) == null){
                        player.sendMessage(ChatColor.YELLOW + "Player named or with UUID '"+ChatColor.YELLOW + args[0] + ChatColor.YELLOW +"' not found");
                        return true;
                    }else{
                        sendInformation(player, Bukkit.getOfflinePlayer(args[0]));
                        return true;
                    }
                }else{
                    sendInformation(player, Bukkit.getPlayer(args[0]));
                    return true;
                }
            }
        return false;
    }
    public void sendInformation(Player player, OfflinePlayer target) {
        FactionUser hcf = HCF.getPlugin().getUserManager().getUser(target.getUniqueId());
        player.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);

        if(HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()) != null ) {
            player.sendMessage(HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getRelation(player).toChatColour() + target.getName());
            new Text(ChatColor.YELLOW + "   Faction: " + HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getDisplayName(player)).setHoverText(ChatColor.GRAY + "Click to view Faction").setClick(ClickAction.RUN_COMMAND, "/f who "+HCF.getPlugin().getFactionManager().getPlayerFaction(target.getUniqueId()).getName()).send(player);
        }else{
            player.sendMessage(ChatColor.RED + target.getName());
        }

        player.sendMessage(ChatColor.YELLOW + "   PlayTime: " + ChatColor.YELLOW + DurationFormatUtils.formatDurationWords(BasePlugin.getPlugin().getPlayTimeManager().getTotalPlayTime(target.getUniqueId()), true, true));

        if(hcf.getDiamondsMined() > 0) {
            player.sendMessage(ChatColor.YELLOW + "   Diamonds Mined: " + ChatColor.YELLOW + hcf.getDiamondsMined());
        }

        if(hcf.getDeathban() != null) {
            new Text(ChatColor.YELLOW + "   Deathbanned: " + (hcf.getDeathban().isActive() ? ChatColor.GREEN + "true" : ChatColor.RED + "false")).setHoverText(ChatColor.YELLOW + "Un-Deathbanned at: " + HCF.getRemaining(hcf.getDeathban().getExpiryMillis(), true, true)).send(player);
        }else{
            player.sendMessage(ChatColor.YELLOW + "   Deathbanned: " + ChatColor.YELLOW + "false" );
        }

        if(hcf.getKills() > 0) {
            player.sendMessage(ChatColor.YELLOW + "   Kills: " + ChatColor.YELLOW + hcf.getKills());
        }

        if(hcf.getDeaths() > 0) {
            player.sendMessage(ChatColor.YELLOW + "   Deaths: " + ChatColor.YELLOW + hcf.getDeaths());
        }
        player.sendMessage(ChatColor.YELLOW + "   Balance: "  + ChatColor.YELLOW + HCF.getPlugin().getEconomyManager().getBalance(target.getUniqueId()));
        player.sendMessage(ChatColor.GRAY + BukkitUtils.STRAIGHT_LINE_DEFAULT);
    }
}