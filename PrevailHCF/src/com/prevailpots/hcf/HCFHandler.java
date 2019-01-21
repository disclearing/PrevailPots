package com.prevailpots.hcf;

import java.util.concurrent.TimeUnit;

import org.bukkit.World;

import com.customhcf.util.Config;
import com.customhcf.util.PersistableLocation;

/**
 * Created by TREHOME on 03/19/2016.
 */
public class HCFHandler{

    Config settings;
    private Integer worldBorder;
    private Integer netherBorder;
    private Integer endBorder;
    private boolean donorOnly;
    private boolean endEnabled;
    private PersistableLocation endExit;
    private Integer allyLimit;
    private Integer factionLimit;
    private Integer mapNumber;
    private Integer conquestWinPoints;
    private Integer spawnRadius;
    private Integer warZoneRadius;
    private Double archerTagDamage;
    private boolean elevatorMinecart;
    private boolean elevatorSign;
    private String timeZone;
    private Long defaultDeathban;
    private String donateURL;
    HCF plugin;


    public HCFHandler(HCF plugin){
        this.plugin =plugin;
        settings = new Config(plugin, "settings");
        reloadHCFSettings();
    }


    public void saveHCFSettings(){
        settings.set("conquestWinPoints", conquestWinPoints);
        settings.set("mapNumber", mapNumber);
        settings.set("allyLimit", allyLimit);
        settings.set("timeZone", timeZone);
        settings.set("donateURL", donateURL);
        settings.set("factionLimit", factionLimit);
        settings.set("spawnRadius", spawnRadius);
        settings.set("archerTagDamage", archerTagDamage);
        settings.set("worldBorder", worldBorder);
        settings.set("endBorder", endBorder);
        settings.set("netherBorder", netherBorder);
        settings.set("warZoneRadius", warZoneRadius);
        settings.set("MinecartElevator", elevatorMinecart);
        settings.set("elevatorSign", elevatorSign);
        settings.set("donorOnly", donorOnly);
        settings.set("endEnabled", endEnabled);
        settings.set("defaultDeathban", defaultDeathban);
        settings.set("endExit", endExit);
        settings.save();
    }

    private void reloadHCFSettings() {
        settings = new Config(plugin, "settings");
        endExit = (PersistableLocation) settings.get("endExit");
        conquestWinPoints = settings.getInt("conquestWinPoints", 300);
        mapNumber = settings.getInt("mapNumber", 1);
        allyLimit = settings.getInt("allyLimit", 0);
        timeZone = settings.getString("timeZone", "EST");
        factionLimit = settings.getInt("factionLimit", 10);
        spawnRadius = settings.getInt("spawnRadius", 128);
        warZoneRadius = settings.getInt("warZoneRadius", 800);
        elevatorMinecart = settings.getBoolean("MinecartElevator", true);
        elevatorSign = settings.getBoolean("SignElevator", true);
        endEnabled = settings.getBoolean("endEnabled", true);
        donorOnly = settings.getBoolean("donorOnly", false);
        worldBorder = settings.getInt("worldBorder", 3000);
        netherBorder = settings.getInt("endBorder", 1000);
        endBorder = settings.getInt("netherBorder", 3000);
        archerTagDamage = settings.getDouble("archerTagDamage", .25);
        defaultDeathban = settings.getLong("defaultDeathban", TimeUnit.HOURS.toMillis(2L));
        donateURL = settings.getString("donateURL", donateURL);
    }

    public PersistableLocation getEndExit() {
        return endExit;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public String getDonateURL(){
        return donateURL;
    }

    public Integer getWorldBorder(World.Environment environment) {
        if(environment == World.Environment.NORMAL){
            return worldBorder;
        }
        if(environment == World.Environment.NETHER){
            return netherBorder;
        }
        if(environment == World.Environment.THE_END){
            return endBorder;
        }
        return null;
    }

    public void setWorldBorder(World.Environment environment, Integer worldBorder) {
        if(environment == World.Environment.NORMAL){
            this.worldBorder = worldBorder;
            return;
        }
        if(environment == World.Environment.NETHER){
            this.netherBorder = worldBorder;
            return;
        }
        if(environment == World.Environment.THE_END){
            this.endBorder = worldBorder;
        }
    }


    public boolean isElevatorSign() {
        return elevatorSign;
    }

    public boolean isElevatorMinecart() {
        return elevatorMinecart;
    }

    public boolean isDonorOnly() {
        return donorOnly;
    }

    public void setDonorOnly(boolean donorOnly) {
        this.donorOnly = donorOnly;
    }
    public boolean isEndEnabled() {
        return endEnabled;
    }


    public void setEndEnabled(boolean endEnabled) {
        this.endEnabled = endEnabled;
    }
    public Integer getWarZoneRadius() {
        return warZoneRadius;
    }

    public void setWarZoneRadius(Integer warZoneRadius) {
        this.warZoneRadius = warZoneRadius;
    }

    public Integer getSpawnRadius() {
        return spawnRadius;
    }

    public void setSpawnRadius(Integer spawnRadius) {
        this.spawnRadius = spawnRadius;
    }



    public Integer getFactionLimit() {
        return factionLimit;
    }

    public void setFactionLimit(Integer factionLimit) {
        this.factionLimit = factionLimit;
    }

    public Integer getAllyLimit() {
        return allyLimit;
    }

    public void setAllyLimit(Integer allyLimit) {
        this.allyLimit = allyLimit;
    }

    public Integer getMapNumber() {
        return mapNumber;
    }

    public void setMapNumber(Integer mapNumber) {
        this.mapNumber = mapNumber;
    }

    public Integer getConquestWinPoints() {
        return conquestWinPoints;
    }

    public void setConquestWinPoints(Integer conquestWinPoints) {
        this.conquestWinPoints = conquestWinPoints;
    }

    public Double getArcherTagDamage() {
        return archerTagDamage;
    }

    public void setArcherTagDamage(Double archerTagDamage) {
        this.archerTagDamage = archerTagDamage;
    }

    public void setDefaultDeathban(long value){
        this.defaultDeathban = value;
    }

    public Long getDefaultDeathban() {
        return defaultDeathban;
    }
}
