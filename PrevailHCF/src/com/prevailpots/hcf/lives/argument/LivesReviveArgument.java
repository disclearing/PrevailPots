package com.prevailpots.hcf.lives.argument;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageRecipient;

import com.customhcf.util.command.CommandArgument;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.prevailpots.hcf.ConfigurationService;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.deathban.Deathban;
import com.prevailpots.hcf.faction.struct.Relation;
import com.prevailpots.hcf.faction.type.PlayerFaction;
import com.prevailpots.hcf.user.FactionUser;

public class LivesReviveArgument extends CommandArgument {
    private static final String REVIVE_BYPASS_PERMISSION = "revive.bypass";
    private static final String REVIVE_DTR = "revive.dtr";
    private static final String PROXY_CHANNEL_NAME = "BungeeCord";
    private final HCF plugin;

    public LivesReviveArgument(final HCF plugin) {
        super("revive", "Revive a death-banned player");
        this.plugin = plugin;
        plugin.getServer().getMessenger().registerOutgoingPluginChannel((Plugin) plugin, PROXY_CHANNEL_NAME);
        this.permission = "command.stafflives." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + " <playerName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if(!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Player '" + ChatColor.YELLOW + args[1] + ChatColor.RED + "' not found.");
            return true;
        }
        final UUID targetUUID = target.getUniqueId();
        final FactionUser factionTarget = this.plugin.getUserManager().getUser(targetUUID);
        final Deathban deathban = factionTarget.getDeathban();
        if(deathban == null || !deathban.isActive()) {
            sender.sendMessage(ChatColor.RED + target.getName() + " is not death-banned.");
            return true;
        }
        Relation relation = Relation.ENEMY;
        if(sender instanceof Player) {
            if(!sender.hasPermission(REVIVE_BYPASS_PERMISSION) && this.plugin.getEotwHandler().isEndOfTheWorld()) {
                sender.sendMessage(ChatColor.RED + "You cannot revive players during EOTW.");
                return true;
            }
            if(!sender.hasPermission(REVIVE_BYPASS_PERMISSION)) {
                final Player player = (Player) sender;
                final UUID playerUUID = player.getUniqueId();
                final int selfLives = this.plugin.getUserManager().getUser(playerUUID).getLives();
                if (selfLives <= 0) {
                    sender.sendMessage(ChatColor.RED + "You do not have any lives.");
                    return true;
                }
                this.plugin.getUserManager().getUser(playerUUID).takeLives(1);
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
                relation = ((playerFaction == null) ? Relation.ENEMY : playerFaction.getFactionRelation(this.plugin.getFactionManager().getPlayerFaction(targetUUID)));
                sender.sendMessage(ChatColor.YELLOW + "You have revived " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
            }else {
                if(sender.hasPermission(REVIVE_DTR)) {
                    if(plugin.getFactionManager().getPlayerFaction(targetUUID) != null) {
                        plugin.getFactionManager().getPlayerFaction(targetUUID).setDeathsUntilRaidable(plugin.getFactionManager().getPlayerFaction(targetUUID).getDeathsUntilRaidable() + 1);
                    }
                    sender.sendMessage(ChatColor.YELLOW + "You have revived and added DTR to " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
                } else {
                    sender.sendMessage(ChatColor.YELLOW + "You have revived " + relation.toChatColour() + target.getName() + ChatColor.YELLOW + '.');
                }
            }

        } else {
            sender.sendMessage(ChatColor.YELLOW + "You have revived " + ConfigurationService.ENEMY_COLOUR + target.getName() + ChatColor.YELLOW + '.');
        }
        if (sender instanceof PluginMessageRecipient) {
            final ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Message");
            out.writeUTF(args[1]);
            final String serverDisplayName = ChatColor.GREEN + "HCF";
            out.writeUTF(relation.toChatColour() + sender.getName() + ChatColor.YELLOW + " has just revived you from " + serverDisplayName + ChatColor.YELLOW + '.');
            ((PluginMessageRecipient)sender).sendPluginMessage((Plugin) this.plugin, PROXY_CHANNEL_NAME, out.toByteArray());
        }
        factionTarget.setDeathban(null);
        return true;
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length != 2) {
            return Collections.emptyList();
        }
        final List<String> results = new ArrayList<String>();
        final Collection<FactionUser> factionUsers = this.plugin.getUserManager().getUsers().values();
        for(final FactionUser factionUser : factionUsers) {
            final Deathban deathban = factionUser.getDeathban();
            if(deathban != null) {
                if(!deathban.isActive()) {
                    continue;
                }
                final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(factionUser.getUserUUID());
                final String offlineName = offlinePlayer.getName();
                if(offlineName == null) {
                    continue;
                }
                results.add(offlinePlayer.getName());
            }
        }
        return results;
    }
}
