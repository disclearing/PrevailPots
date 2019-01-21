package com.prevailpots.hcf.faction.type;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Map;

public class EndPortalFaction extends ClaimableFaction implements ConfigurationSerializable {
    public EndPortalFaction() {
        super("EndPortal");
    }

    public EndPortalFaction(final Map<String, Object> map) {
        super(map);
    }

    public String getDisplayName(final CommandSender sender) {
        return ChatColor.GOLD + this.getName().replace("EndPortal", "End Portal");
    }

    public boolean isDeathban() {
        return true;
    }
}
