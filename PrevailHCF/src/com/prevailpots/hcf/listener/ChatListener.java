package com.prevailpots.hcf.listener;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.event.FactionChatEvent;
import com.prevailpots.hcf.faction.struct.ChatChannel;
import com.prevailpots.hcf.faction.type.PlayerFaction;

import ru.tehkode.permissions.bukkit.PermissionsEx;

public class ChatListener implements Listener {
    private static final Pattern PATTERN;

    static {
        final ImmutableSet.Builder<UUID> builder = ImmutableSet.builder();
        PATTERN = Pattern.compile("\\W");
    }

    private final ConcurrentMap<Object, Object> messageHistory;
    private final ConcurrentMap<Object, Object> lastMessage;
    private final HCF plugin;

    public ChatListener(HCF plugin) {
        this.plugin = plugin;
        this.lastMessage = CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.MINUTES).build().asMap();
        this.messageHistory = CacheBuilder.newBuilder().expireAfterWrite(2L, TimeUnit.MINUTES).build().asMap();
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerChat(final AsyncPlayerChatEvent event) {
        String message = event.getMessage();
        final Player player = event.getPlayer();
        messageHistory.putIfAbsent(player.getUniqueId(), 0);
        messageHistory.put(player.getUniqueId(), (int)messageHistory.get(player.getUniqueId()) +1);
        lastMessage.put(player.getUniqueId(), System.currentTimeMillis());
        final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        final ChatChannel chatChannel = (playerFaction == null) ? ChatChannel.PUBLIC : playerFaction.getMember(player).getChatChannel();
        final Set<Player> recipients = event.getRecipients();
        if(chatChannel == ChatChannel.FACTION || chatChannel == ChatChannel.ALLIANCE) {
            if(!this.isGlobalChannel(message)) {
                final Collection<Player> online = playerFaction.getOnlinePlayers();
                if(chatChannel == ChatChannel.ALLIANCE) {
                    final Collection<PlayerFaction> allies = playerFaction.getAlliedFactions();
                    for(final PlayerFaction ally : allies) {
                        online.addAll(ally.getOnlinePlayers());
                    }
                }
                recipients.retainAll(online);
                event.setFormat(chatChannel.getRawFormat(player));
                Bukkit.getPluginManager().callEvent((Event) new FactionChatEvent(true, playerFaction, player, chatChannel, recipients, event.getMessage()));
                return;
            }
            message = message.substring(1, message.length()).trim();
            event.setMessage(message);
        }
        for(String blockedWords : plugin.getConfig().getStringList("blockedWords")){
            if(!message.toLowerCase().contains(blockedWords.toLowerCase())) break;
            for(Player on : Bukkit.getOnlinePlayers()){
                if(on.hasPermission("command.staffchat")){
                    on.sendMessage(ChatColor.GRAY + "[" +ChatColor.DARK_RED.toString() + "Alert" + ChatColor.GRAY + "] " + ChatColor.RED + player.getDisplayName() + ChatColor.GRAY + " has stated a word that is not allowed:" +ChatColor.DARK_RED + " "+ message);
                }
            }
            player.sendMessage(ChatColor.YELLOW + "Calm down, one of the words you said isn't allowed!");
            event.setCancelled(true);
            return;
        }

        final boolean usingRecipientVersion = false;
        if(!usingRecipientVersion) {
            event.setCancelled(true);
        }
        final String rank = ChatColor.translateAlternateColorCodes('&', "&7"+PermissionsEx.getUser(player).getPrefix().replace("/u", "\u273F").replace("/a", "\u2740").replace("/e", "\u2742").replace("/s", "\u2748")).replace("_", " ");
        String displayName = player.getDisplayName();
        displayName = rank + displayName;
        final ConsoleCommandSender console = Bukkit.getConsoleSender();
        String tag = (playerFaction == null) ? ChatColor.GOLD + "["+ChatColor.RED+"-"+ChatColor.GOLD+"]" :   playerFaction.getDisplayName(console);
        if(!usingRecipientVersion) {
            console.sendMessage(tag + displayName + " "+ ChatColor.DARK_GRAY + message);
        }
        for(final Player recipient : event.getRecipients()) {
            tag = (playerFaction == null) ? "" :  ChatColor.GOLD + "[" + playerFaction.getDisplayName(recipient) + ChatColor.GOLD + "]";

            if(usingRecipientVersion) {
                continue;
            }
            //refractor code
            if(isIP(message) && !recipient.isOp() && !recipient.equals(player)){
                continue;
            }
//                if(!PermissionsEx.getUser(player).getSuffix().replace("_", " ").equalsIgnoreCase("")) {
//                    String information = ChatColor.GRAY + "Rank name: " + ChatColor.translateAlternateColorCodes('&', "&8" + PermissionsEx.getUser(player).getSuffix().replace("_", " "));
//                    new Text(tag).append(new Text(displayName).setHoverText(information)).append(new Text(ChatColor.GRAY + ": ")).append(ChatColor.YELLOW + message).send(recipient);
//                }
                    recipient.sendMessage(tag + " " + displayName + ChatColor.WHITE + ": " + message);

        }
    }

    private boolean isIP(String message) {
        if(message.contains(".")){
            String[] messages = message.split(" ");
            for(String msg : messages){
                if(msg.contains(".") && msg.length() > 8 && msg.length() < 12){
                    String[] split = msg.split(".");
                    if(split.length == 4){
                        return true;
                    }
                }
            }
        }
        return false;
    }



    private boolean isGlobalChannel(final String input) {
        final int length = input.length();
        if(length <= 1 || !input.startsWith("!")) {
            return false;
        }
        int i = 1;
        while(i < length) {
            final char character = input.charAt(i);
            if(character == ' ') {
                ++i;
            } else {
                if(character == '/') {
                    return false;
                }
                break;
            }
        }
        return true;
    }
}
