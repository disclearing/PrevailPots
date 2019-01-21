package com.prevailpots.kitmap.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.user.FactionUser;

public class MobdropsCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if(cs instanceof Player){
            FactionUser user = HCF.getPlugin().getUserManager().getUser(((Player) cs).getUniqueId());
            user.setMobdrops(!user.isMobdrops());
            cs.sendMessage(ChatColor.YELLOW + "You have " +  ChatColor.GOLD + (user.isMobdrops() ? "enabled" : "disabled") + ChatColor.YELLOW +" picking up mob drops!");
        }else{
            cs.sendMessage(ChatColor.RED + "Evil console go away!!!");
            return true;
        }
        return false;
    }
}
