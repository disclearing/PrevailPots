package com.prevailpots.bunkers.world;

import org.bukkit.configuration.file.*;
import java.io.*;

public class ConfigManager
{
    String path;
    String plugin;
    File Data;
    FileConfiguration DataFile;
    
    public ConfigManager(final String plugin, final String path) {
        this.path = null;
        this.plugin = null;
        this.path = path;
        this.plugin = plugin;
        this.Data = new File("plugins/" + plugin + "/" + path + ".yml");
        this.DataFile = (FileConfiguration)YamlConfiguration.loadConfiguration(this.Data);
    }
    
    public void createFile() {
        try {
            this.Data.createNewFile();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public FileConfiguration getFile() {
        return this.DataFile;
    }
    
    public void saveFile() {
        try {
            this.getFile().save(this.Data);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
