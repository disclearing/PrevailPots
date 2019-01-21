package com.prevailpots.bunkers.game.managers;

import org.bukkit.entity.*;
import org.bukkit.event.player.*;

import com.prevailpots.bunkers.*;
import com.prevailpots.bunkers.game.*;

import org.bukkit.*;

import ru.tehkode.permissions.bukkit.*;
import java.util.*;
import org.bukkit.event.*;

public class ChatManager implements Listener
{
    private ArrayList<String> teamChat;
    
    public ChatManager() {
        this.teamChat = new ArrayList<String>();
    }
    
    public boolean isTeamChat(final Player p) {
        return this.teamChat.contains(p.getName());
    }
    
    public void setTeamChat(final Player p, final boolean tc) {
        if (this.teamChat.contains(p.getName())) {
            this.teamChat.remove(p.getName());
        }
        if (tc) {
            this.teamChat.add(p.getName());
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void chat(final AsyncPlayerChatEvent e) {
        if (this.isTeamChat(e.getPlayer())) {
            e.setCancelled(true);
            for (final String s : Core.getInstance().getGameHandler().getPlayers().keySet()) {
                final Player p = Bukkit.getPlayer(s);
                if (p != null) {
                    final Team t = Core.getInstance().getGameHandler().getPlayers().get(s);
                    if (t == null || !t.equals(Core.getInstance().getGameHandler().getPlayers().get(e.getPlayer().getName()))) {
                        continue;
                    }
                    p.sendMessage(String.valueOf(t.getColor().toString()) + "[TEAM] " + t.toString() + ": " + e.getPlayer().getName() + " - §7" + e.getMessage());
                }
            }
            return;
        }
        e.setFormat(String.valueOf(Core.getInstance().getGameHandler().getPlayers().get(e.getPlayer().getName()).getColor().toString()) + "[" + Core.getInstance().getGameHandler().getPlayers().get(e.getPlayer().getName()).toString() + "] " + "§r" + PermissionsEx.getUser(e.getPlayer()).getPrefix() + ((PermissionsEx.getUser(e.getPlayer()).getPrefix() == "") ? "" : " ") + e.getPlayer().getDisplayName() + "§7: " + "§r" + e.getMessage());
    }
}
