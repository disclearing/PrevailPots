package com.prevailpots.kitmap.command;

import com.customhcf.util.BukkitUtils;
import com.customhcf.util.ItemBuilder;
import com.customhcf.util.Menu;
import com.google.common.base.MoreObjects;
import com.prevailpots.kitmap.listener.DeathListener;

import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by TREHOME on 10/28/2015.
 */
public class RefundCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender cs, Command cmd, String s, String[] args) {
        String Usage = ChatColor.RED + "/" + s + " <playerName> <reason>";
        if(!(cs instanceof Player)) {
            cs.sendMessage(ChatColor.RED + "You must be a player");
            return true;
        }
        Player p = (Player) cs;
        if(args.length < 2) {
            cs.sendMessage(Usage);
            return true;
        }
        if(Bukkit.getPlayer(args[0]) == null) {
            p.sendMessage(ChatColor.RED + "Player must be online");
            return true;
        }
        Player target = Bukkit.getPlayer(args[0]);
        if(DeathListener.PlayerInventoryContents.containsKey(target.getUniqueId())) {
            Menu menu = new Menu("Refund items", 6);
            for(ItemStack itemStack : DeathListener.PlayerInventoryContents.get(target.getUniqueId())){
                menu.addItem(itemStack);
            }
            int startArmor = 36;
            for(ItemStack armor : DeathListener.PlayerArmorContents.get(target.getUniqueId())){
                menu.setItem(startArmor, MoreObjects.firstNonNull(armor, new ItemStack(Material.AIR)));
                startArmor++;
            }
            for(ItemStack contents : DeathListener.PlayerInventoryContents.get(target.getUniqueId())){
                menu.addItem(contents);
            }
            menu.setItem(45, new ItemBuilder(Material.STAINED_GLASS_PANE).displayName(ChatColor.GREEN + "Refund items").data((short) DyeColor.GREEN.getWoolData()).build());
            menu.setGlobalAction((player, inventory, itemStack, i, inventoryAction) -> {
                if(itemStack.getTypeId() == 160){
                    target.getInventory().setContents(DeathListener.PlayerInventoryContents.get(target.getUniqueId()));
                    target.getInventory().setArmorContents(DeathListener.PlayerArmorContents.get(target.getUniqueId()));
                    DeathListener.PlayerInventoryContents.remove(target.getUniqueId());
                    DeathListener.PlayerArmorContents.remove(target.getUniqueId());
                    return;
                }
            });
            menu.showMenu(p);
            p.sendMessage(ChatColor.GRAY + "If you want to remove an item they received, you can invsee them and remove it!");
            return true;
        } else {
            p.sendMessage(ChatColor.RED + "Player was already refunded items or there was a restart");
            return true;
        }
    }
}
