package com.prevailpots.kitmap.kothgame;

import com.customhcf.util.Config;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.kothgame.faction.EventFaction;

import org.bukkit.configuration.MemorySection;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class NewEventScheduler {

    private HCF plugin;
    private Map<Long, String> map;
    private Config file;


    public NewEventScheduler(HCF plugin) {
        this.plugin = plugin;
        map = new LinkedHashMap<>();
        reloadSchedule();
    }

    public String getSchedule(Long value){
        return map.get(value);
    }

    public void addSchedule(Long when, EventFaction faction){
        this.map.put(when, faction.getName());
    }

    public Map<Long, String> getMap(){
        return map;
    }



    public static LocalDateTime getDateTimeFromTimestamp(long timestamp) {
        if (timestamp == 0)
            return null;
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), TimeZone
                .getDefault().toZoneId());
    }



    private void reloadSchedule()
    {
        file = new Config(plugin, "schedule-new");
        Object location = file.get("schedule");
        if(location instanceof MemorySection){
            MemorySection memorySection = (MemorySection) location;
            List<String> keys = (List<String>) memorySection.getKeys(false);
            this.map = new LinkedHashMap<>();
            for(String id : keys){
//                Long time = Longs.
//                this.map.put((Long) id, this.file.getString(memorySection.getCurrentPath() + "." + id));
            }
        }else{
            map = new LinkedHashMap<>();
        }
    }

    public void saveData(){
        this.file.set("lives", map);
        this.file.save();
    }
}
