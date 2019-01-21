package com.prevailpots.hcf.fixes;

import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

/**
 * Created by TREHOME on 12/20/2015.
 */
public class WeatherFixListener implements Listener {

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent e){
        boolean rain = e.toWeatherState();
        if(rain)
            e.setCancelled(true);
    }

    @EventHandler
    public void onThunderChange(ThunderChangeEvent e) {
        boolean thunder = e.toThunderState();
        if(thunder)
            e.getWorld().setStorm(false);
    }
}
