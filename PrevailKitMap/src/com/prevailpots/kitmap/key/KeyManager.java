package com.prevailpots.kitmap.key;

import com.customhcf.util.Config;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.key.type.ConquestKey;
import com.prevailpots.kitmap.key.type.CustomKey;
import com.prevailpots.kitmap.key.type.KothKey;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.configuration.MemorySection;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class KeyManager {
    private final KothKey kothKey;
    private final ConquestKey conquestKey;
    private final Table<UUID, String, Integer> depositedCrateMap;
    private final Set<Key> keys;
    private final Config config;

    public KeyManager(final HCF plugin) {
        this.depositedCrateMap = HashBasedTable.create();
        this.config = new Config(plugin, "key-data");
        this.keys = Sets.newHashSet(new Key[]{this.kothKey = new KothKey(), this.conquestKey = new ConquestKey(),});
        this.reloadKeyData();
        File[] folders = plugin.getDataFolder().listFiles();
        if(folders == null){
            return;
        }
        for(File files : folders){
            Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + files.getName());
            if(files.getName().startsWith("key_")){
                String name = files.getName().replace("key_", "").replace(".yml", "");
                System.out.println("Adding key!");
                keys.add(new CustomKey(name));
            }
        }
    }

    public Map<String, Integer> getDepositedCrateMap(final UUID uuid) {
        return (Map<String, Integer>) this.depositedCrateMap.row(uuid);
    }

    public Set<Key> getKeys() {
        return this.keys;
    }

    public ConquestKey getConquestKey() {
        return conquestKey;
    }

    public KothKey getEventKey() {
        return this.kothKey;
    }


    public Key getKey(final String name) {
        for(final Key key : this.keys) {
            if(key.getName().equalsIgnoreCase(name)) {
                return key;
            }
        }
        return null;
    }

    @Deprecated
    public Key getKey(final Class<? extends Key> clazz) {
        for(final Key key : this.keys) {
            if(clazz.isAssignableFrom(key.getClass())) {
                return key;
            }
        }
        return null;
    }

    public Key getKey(final ItemStack stack) {
        if(stack == null || !stack.hasItemMeta()) {
            return null;
        }
        for(final Key key : this.keys) {
            final ItemStack item = key.getItemStack();
            if(item.getItemMeta().getDisplayName().equals(stack.getItemMeta().getDisplayName())) {
                return key;
            }
        }
        return null;
    }

    public void reloadKeyData() {
        for(final Key key : this.keys) {
            key.load(this.config);
        }
        Object object = this.config.get("deposited-key-map");
        if(object instanceof MemorySection) {
            MemorySection section = (MemorySection) object;
            for(final String id : section.getKeys(false)) {
                object = this.config.get(section.getCurrentPath() + '.' + id);
                if(object instanceof MemorySection) {
                    section = (MemorySection) object;
                    for(final String key2 : section.getKeys(false)) {
                        this.depositedCrateMap.put(UUID.fromString(id), key2, this.config.getInt("deposited-key-map." + id + '.' + key2));
                    }
                }
            }
        }
    }

    public void saveKeyData() {
        for(final Key key : this.keys) {
            key.save(this.config);
        }
        final Map<String, Map<String, Integer>> saveMap = new LinkedHashMap<String, Map<String, Integer>>(this.depositedCrateMap.size());
        for(final Map.Entry<UUID, Map<String, Integer>> entry : this.depositedCrateMap.rowMap().entrySet()) {
            saveMap.put(entry.getKey().toString(), entry.getValue());
        }
        this.config.set("deposited-key-map", (Object) saveMap);
        this.config.save();
    }
}
