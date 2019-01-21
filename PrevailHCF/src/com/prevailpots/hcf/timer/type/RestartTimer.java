package com.prevailpots.hcf.timer.type;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.prevailpots.hcf.timer.GlobalTimer;

import java.util.concurrent.TimeUnit;

/**
 * Created by TREHOME on 03/29/2016.
 */
public class RestartTimer extends GlobalTimer {
    public RestartTimer() {
        super("Rebooting", TimeUnit.MINUTES.toMillis(30L));
    }


    @Override
    public void onExpire(){
        Bukkit.savePlayers();
        for(World world : Bukkit.getWorlds()){
            world.save();
        }
        for(Player on : Bukkit.getOnlinePlayers()){
            on.kickPlayer(ChatColor.translateAlternateColorCodes('&', Bukkit.getShutdownMessage()));
        }
        Bukkit.shutdown();
    }

    @Override
    public String getScoreboardPrefix() {
        return ChatColor.RED.toString() + ChatColor.BOLD;
    }
}
