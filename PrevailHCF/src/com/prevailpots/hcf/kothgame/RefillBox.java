package com.prevailpots.hcf.kothgame;

import com.customhcf.util.GenericUtils;
import com.customhcf.util.cuboid.Cuboid;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class RefillBox implements ConfigurationSerializable {
    private String name;
    private String prefix;
    private Cuboid cuboid;
    private List<Location> glowstone = new ArrayList<>();
    private List<Location> valuables = new ArrayList<>();

    public RefillBox(final String name, final Cuboid cuboid) {
        this(name, "", cuboid);
    }

    public RefillBox(final String name, final String prefix, final Cuboid cuboid) {
        this.name = name;
        this.prefix = prefix;
        this.cuboid = cuboid;
    }

    public RefillBox(final Map<String, Object> map) {
        this.name = (String) map.get("name");
        Object obj = map.get("prefix");
        if(obj instanceof String) {
            this.prefix = (String) obj;
        }
        obj = map.get("cuboid");
        if(obj instanceof Cuboid) {
            this.cuboid = (Cuboid) obj;
        }
        this.glowstone.addAll(GenericUtils.createList(map.get("glowstone"), Location.class));
        this.valuables.addAll(GenericUtils.createList(map.get("valuables"), Location.class));

    }

    public Map<String, Object> serialize() {
        final Map<String, Object> map = Maps.newLinkedHashMap();

        map.put("name", this.name);

        if(this.prefix != null) {
            map.put("prefix", this.prefix);
        }
        if(this.cuboid != null) {
            map.put("cuboid", this.cuboid);
        }
        map.put("glowstone", new ArrayList(this.glowstone));
        map.put("valuables", new ArrayList(this.valuables));

        return map;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getPrefix() {
        if(this.prefix == null) {
            this.prefix = "";
        }
        return this.prefix;
    }

    public String getDisplayName() {
        return getPrefix() + this.name;
    }

    public Cuboid getCuboid() {
        return this.cuboid;
    }

    public Collection<Location> getGlowstoneLocation(){
        return glowstone;
    }
    public Collection<Location> getValuableLocation(){
        return valuables;
    }

    public void addGlowstoneLocation(Location location){
        glowstone.add(location);
    }

    public void addValuableLocation(Location location){
        valuables.add(location);
    }





}
