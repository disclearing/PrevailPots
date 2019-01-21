package com.prevailpots.hcf.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.user.FactionUser;

public class CobblestoneCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String label, String[] args) {
        if(cs instanceof Player){
            FactionUser user = HCF.getPlugin().getUserManager().getUser(((Player) cs).getUniqueId());
            user.setCobblestone(!user.isCobblestone());
            cs.sendMessage(ChatColor.YELLOW + "You have " + (user.isCobblestone() ? ChatColor.GREEN +"enabled" : ChatColor.RED +"disabled") + ChatColor.YELLOW +" picking up cobblestone in miner class!");
        }else{
            cs.sendMessage(ChatColor.RED + "Player only command!");
            return true;
        }
        return false;
    }
}
