package com.prevailpots.hcf.key;

import com.customhcf.util.Config;
import org.bukkit.ChatColor;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public abstract class Key {
    private String name;

    public Key(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public abstract ChatColor getColour();

    public String getDisplayName() {
        return this.getColour() + this.name;
    }

    public abstract ItemStack getItemStack();

    public void load(final Config config) {
//        Object object = config.get(name + ".items");
//        if(object instanceof MemorySection) {
//            MemorySection section = (MemorySection) object;
//            for(final String id : section.getKeys(false)) {
//                object = this.config.get(section.getCurrentPath() + '.' + id);
//                if(object instanceof MemorySection) {
//                    section = (MemorySection) object;
//                    for(final String key2 : section.getKeys(false)) {
//                        this.depositedCrateMap.put(UUID.fromString(id), key2, this.config.getInt("deposited-key-map." + id + '.' + key2));
//                    }
//                }
//            }
//        }
    }

    public void save(final Config config) {

    }
}
