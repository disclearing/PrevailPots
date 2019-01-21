package com.prevailpots.hcf.task;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.prevailpots.hcf.HCF;

/**
 * Created by TREHOME on 05/10/2016.
 */
public class SaveDataTask extends BukkitRunnable implements Listener{


    public SaveDataTask(){
        Bukkit.getPluginManager().registerEvents(this, HCF.getPlugin());
    }

    protected static double tps;
    protected static long lastSaved;

    @Override
    public void run() {
        saveBalanceData();
        for (Player allPlayers : Bukkit.getOnlinePlayers()) {
            if (allPlayers.isOp()) {
                System.out.println("Saving: " + tps + " : " + (System.currentTimeMillis() - lastSaved));
                allPlayers.sendMessage(ChatColor.GREEN.toString() + ChatColor.BOLD + "§6Saving MongoDB and Redis instances"  +
                        "\n" + ChatColor.GRAY + "§eCurrent TPS: " + ChatColor.GOLD + Bukkit.spigot().getTPS()[0] + ChatColor.GRAY + " | Saving TPS: " + ChatColor.GOLD + "20" +
                        "\n" + ChatColor.GRAY + "§eTime Taken: " + ChatColor.GOLD + (System.currentTimeMillis() - lastSaved) + ChatColor.GRAY + " ms" + "\n" + ChatColor.YELLOW +
                        "All players have been succesfully saved to the database correctly there were no issues and you are now perfect to restart/reboot the instance without data loss!");
            }
        }
        lastSaved = System.currentTimeMillis();
    }


    public synchronized static void  saveBalanceData() {
        synchronized (HCF.getPlugin()){
           HCF.getPlugin().getEconomyManager().save();
        }
    }
}
