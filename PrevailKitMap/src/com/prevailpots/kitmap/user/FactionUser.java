package com.prevailpots.kitmap.user;


import com.customhcf.util.GenericUtils;
import com.google.common.collect.Maps;
import com.prevailpots.kitmap.deathban.Deathban;
import com.prevailpots.kitmap.faction.type.PlayerFaction;

import lombok.Getter;
import lombok.Setter;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class FactionUser implements ConfigurationSerializable {

    @Getter
    private final Set<UUID> factionChatSpying;
    @Getter
    private final UUID userUUID;
    @Getter
    @Setter
    private boolean showClaimMap;
    @Getter
    @Setter
    private Deathban deathban;
    @Getter
    @Setter
    private long lastFactionLeaveMillis;
    @Setter
    @Getter
    private int kills;
    @Setter
    @Getter
    private int diamondsMined;
    @Getter
    @Setter
    private int deaths;
    @Getter
    @Setter
    private boolean cobblestone;
    @Getter
    @Setter
    private boolean mobdrops;
    @Getter
    @Setter
    private int lives;
    @Getter
    @Setter
    private int souLives;
    @Getter
    @Setter
    private PlayerFaction focused;

    public FactionUser(final UUID userUUID) {
        this.factionChatSpying = new HashSet<>();
        this.userUUID = userUUID;
        lives = 0;
        souLives = 0;
        this.focused = null;
        mobdrops = true;
        cobblestone = true;
    }

    @Override
    public Map<String, Object> serialize() {
        final Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("faction-chat-spying", this.factionChatSpying.stream().map(UUID::toString).collect(Collectors.toList()));
        map.put("userUUID", this.userUUID.toString());
        map.put("showClaimMap", this.showClaimMap);
        map.put("cobblestone", cobblestone);
        map.put("mobdrops", mobdrops);
        map.put("diamondsMined", this.diamondsMined);
        map.put("lives", lives);
        map.put("souLives", souLives);
        map.put("deathban", this.deathban);
        map.put("lastFactionLeaveMillis", Long.toString(this.lastFactionLeaveMillis));
        map.put("kills", this.kills);
        map.put("deaths", this.deaths);
        return map;
    }

    public FactionUser(final Map<String, Object> map) {
        this.factionChatSpying = new HashSet<>();
        this.factionChatSpying.addAll(GenericUtils.createList(map.get("faction-chat-spying"), String.class).stream().map(UUID::fromString).collect(Collectors.toList()));
        this.userUUID = UUID.fromString((String) map.get("userUUID"));
        this.diamondsMined = (int) map.get("diamondsMined");
        this.deathban = (Deathban) map.get("deathban");
        this.cobblestone = (boolean) map.get("cobblestone");
        this.mobdrops = (boolean) map.get("mobdrops");
        this.lives = (Integer) map.getOrDefault("lives", 0);
        this.souLives = (Integer) map.getOrDefault("souLives", 0);
        this.lastFactionLeaveMillis = Long.parseLong((String) map.get("lastFactionLeaveMillis"));
        this.kills = (int) map.get("kills");
        this.deaths = (int) map.get("deaths");
    }

    public int takeLives(final Integer amount) {
        return this.lives -= amount;
    }
    
    public int takeSoulLives(final Integer amount) {
        return this.souLives -= amount;
    }
    
    public void addLives(final Integer amount) {
        this.lives += amount;
    }
    
    public void addSoulLives(final Integer amount) {
        this.souLives += amount;
    }
    
    public Set<UUID> getFactionChatSpying() {
        return this.factionChatSpying;
    }
    
    public UUID getUserUUID() {
        return this.userUUID;
    }
    
    public boolean isShowClaimMap() {
        return this.showClaimMap;
    }
    
    public void setShowClaimMap(final boolean showClaimMap) {
        this.showClaimMap = showClaimMap;
    }
    
    public Deathban getDeathban() {
        return this.deathban;
    }
    
    public void setDeathban(final Deathban deathban) {
        this.deathban = deathban;
    }
    
    public long getLastFactionLeaveMillis() {
        return this.lastFactionLeaveMillis;
    }
    
    public void setLastFactionLeaveMillis(final long lastFactionLeaveMillis) {
        this.lastFactionLeaveMillis = lastFactionLeaveMillis;
    }
    
    public void setKills(final int kills) {
        this.kills = kills;
    }
    
    public int getKills() {
        return this.kills;
    }
    
    public void setDiamondsMined(final int diamondsMined) {
        this.diamondsMined = diamondsMined;
    }
    
    public int getDiamondsMined() {
        return this.diamondsMined;
    }
    
    public int getDeaths() {
        return this.deaths;
    }
    
    public void setDeaths(final int deaths) {
        this.deaths = deaths;
    }
    
    public boolean isCobblestone() {
        return this.cobblestone;
    }
    
    public void setCobblestone(final boolean cobblestone) {
        this.cobblestone = cobblestone;
    }
    
    public boolean isMobdrops() {
        return this.mobdrops;
    }
    
    public void setMobdrops(final boolean mobdrops) {
        this.mobdrops = mobdrops;
    }
    
    public int getLives() {
        return this.lives;
    }
    
    public void setLives(final int lives) {
        this.lives = lives;
    }
    
    public int getSouLives() {
        return this.souLives;
    }
    
    public void setSouLives(final int souLives) {
        this.souLives = souLives;
    }
    
    public PlayerFaction getFocused() {
        return this.focused;
    }
    
    public void setFocused(final PlayerFaction focused) {
        this.focused = focused;
    }


}
