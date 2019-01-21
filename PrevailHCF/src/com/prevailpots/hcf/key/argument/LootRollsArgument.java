package com.prevailpots.hcf.key.argument;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.key.Key;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class LootRollsArgument extends CommandArgument {
    private final HCF plugin;

    public LootRollsArgument(final HCF plugin) {
        super("list", "Lists keys");
        this.plugin = plugin;
        this.permission = "command.key." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + "";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;
        List<String> arrayList = new ArrayList<>();
        for(Key key : plugin.getKeyManager().getKeys()){
            arrayList.add(key.getName());
        }
        String list = Strings.join(arrayList, ChatColor.GRAY + ", " + ChatColor.GREEN);
        player.sendMessage( ChatColor.GREEN + list);
        return true;
    }


}