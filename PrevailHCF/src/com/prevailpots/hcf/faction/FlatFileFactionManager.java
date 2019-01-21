package com.prevailpots.hcf.faction;


import com.customhcf.util.Config;
import com.customhcf.util.JavaUtils;
import com.customhcf.util.cuboid.CoordinatePair;
import com.google.common.base.Preconditions;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.claim.Claim;
import com.prevailpots.hcf.faction.event.*;
import com.prevailpots.hcf.faction.event.cause.ClaimChangeCause;
import com.prevailpots.hcf.faction.struct.ChatChannel;
import com.prevailpots.hcf.faction.struct.Relation;
import com.prevailpots.hcf.faction.struct.Role;
import com.prevailpots.hcf.faction.type.*;
import com.prevailpots.hcf.kothgame.faction.GlowstoneFaction;

import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class FlatFileFactionManager implements Listener, FactionManager {
    private final WarzoneFaction warzone;
    private final WildernessFaction wilderness;
    private final Map<CoordinatePair, Claim> claimPositionMap = new HashMap<>();
    private final Map<UUID, UUID> factionPlayerUuidMap = new ConcurrentHashMap<>();
    private final Map<UUID, Faction> factionUUIDMap = new HashMap<>();
    private final Map<String, UUID> factionNameMap = new CaseInsensitiveMap<>();
    private final HCF plugin;
    private Config config;

    public FlatFileFactionManager(final HCF plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents( this,  plugin);
        this.warzone = new WarzoneFaction();
        this.wilderness = new WildernessFaction();
        this.reloadFactionData();
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoinedFaction(final PlayerJoinedFactionEvent event) {
        this.factionPlayerUuidMap.put(event.getUniqueID(), event.getFaction().getUniqueID());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLeftFaction(final PlayerLeftFactionEvent event) {
        this.factionPlayerUuidMap.remove(event.getUniqueID());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionRename(final FactionRenameEvent event) {
        this.factionNameMap.remove(event.getOriginalName());
        this.factionNameMap.put(event.getNewName(), event.getFaction().getUniqueID());
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onFactionClaim(final FactionClaimChangedEvent event) {
        for(final Claim claim : event.getAffectedClaims()) {
            this.cacheClaim(claim, event.getCause());
        }
    }

    @Deprecated
    public Map<String, UUID> getFactionNameMap() {
        return this.factionNameMap;
    }

    public List<Faction> getFactions() {
        List<Faction> asd = new ArrayList<>();
        for(Faction fac : factionUUIDMap.values()){
            asd.add(fac);
        }
        return asd;
    }

    @Override
    public List<ClaimableFaction> getClaimableFactions(){
        List<ClaimableFaction> factions = new ArrayList<>();
        for(Faction faction : getFactions()){
            if(faction instanceof ClaimableFaction){
                factions.add((ClaimableFaction) faction);
            }
        }
        return factions;
    }

    @Override
    public Collection<PlayerFaction> getPlayerFactions() {
        List<PlayerFaction> factions = new ArrayList<>();
        for(Faction faction : getFactions()){
            if(faction instanceof PlayerFaction){
                factions.add((PlayerFaction) faction);
            }
        }
        return factions;
    }

    public Claim getClaimAt(final World world, final int x, final int z) {
        return this.claimPositionMap.get(new CoordinatePair(world, x, z));
    }

    public Claim getClaimAt(final Location location) {
        return this.getClaimAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    public Faction getFactionAt(final World world, final int x, final int z) {
        final World.Environment environment = world.getEnvironment();
        final Claim claim = this.getClaimAt(world, x, z);
        if(claim != null) {
            final Faction faction = claim.getFaction();
            if(faction != null) {
                return faction;
            }
        }
        if(environment == World.Environment.THE_END) {
            return this.wilderness;
        }
        int warzoneRadius = 600;
        if(environment == World.Environment.NETHER) {
            warzoneRadius = 400;
        }
        return (Math.abs(x) > warzoneRadius || Math.abs(z) > warzoneRadius) ? this.wilderness : this.warzone;
    }

    public Faction getFactionAt(final Location location) {
        return this.getFactionAt(location.getWorld(), location.getBlockX(), location.getBlockZ());
    }

    public Faction getFactionAt(final Block block) {
        return this.getFactionAt(block.getLocation());
    }

    public Faction getFaction(final String factionName) {
        final UUID uuid = this.factionNameMap.get(factionName);
        return (uuid == null) ? null : this.factionUUIDMap.get(uuid);
    }

    public Faction getFaction(final UUID factionUUID) {
        return this.factionUUIDMap.get(factionUUID);
    }

    public PlayerFaction getPlayerFaction(final UUID playerUUID) {
        final UUID uuid = this.factionPlayerUuidMap.get(playerUUID);
        final Faction faction = (uuid == null) ? null : this.factionUUIDMap.get(uuid);
        return (faction instanceof PlayerFaction) ? ((PlayerFaction) faction) : null;
    }

    public PlayerFaction getPlayerFaction(final Player player) {
        return this.getPlayerFaction(player.getUniqueId());
    }

    public PlayerFaction getContainingPlayerFaction(final String search) {
        final OfflinePlayer target = JavaUtils.isUUID(search) ? Bukkit.getOfflinePlayer(UUID.fromString(search)) : Bukkit.getOfflinePlayer(search);
        return (target.hasPlayedBefore() || target.isOnline()) ? this.getPlayerFaction(target.getUniqueId()) : null;
    }

    public Faction getContainingFaction(final String search) {
        final Faction faction = this.getFaction(search);
        if(faction != null) {
            return faction;
        }
        final UUID playerUUID = Bukkit.getOfflinePlayer(search).getUniqueId();
        if(playerUUID != null) {
            return this.getPlayerFaction(playerUUID);
        }
        return null;
    }

    public boolean containsFaction(final Faction faction) {
        return this.factionNameMap.containsKey(faction.getName());
    }

    public boolean createFaction(final Faction faction) {
        return this.createFaction(faction,  Bukkit.getConsoleSender());
    }

    public boolean createFaction(final Faction faction, final CommandSender sender) {
        if(this.factionUUIDMap.putIfAbsent(faction.getUniqueID(), faction) != null) {
            return false;
        }
        this.factionNameMap.put(faction.getName(), faction.getUniqueID());
        if(faction instanceof PlayerFaction && sender instanceof Player) {
            final Player player = (Player) sender;
            final PlayerFaction playerFaction = (PlayerFaction) faction;
            if(!playerFaction.setMember(player, new FactionMember(player, ChatChannel.PUBLIC, Role.LEADER))) {
                return false;
            }
        }
        final FactionCreateEvent createEvent = new FactionCreateEvent(faction, sender);
        Bukkit.getPluginManager().callEvent( createEvent);
        return !createEvent.isCancelled();
    }

    public boolean removeFaction(final Faction faction, final CommandSender sender) {
        if(this.factionUUIDMap.remove(faction.getUniqueID()) == null) {
            return false;
        }
        this.factionNameMap.remove(faction.getName());
        final FactionRemoveEvent removeEvent = new FactionRemoveEvent(faction, sender);
        Bukkit.getPluginManager().callEvent( removeEvent);
        if(removeEvent.isCancelled()) {
            return false;
        }
        if(faction instanceof ClaimableFaction) {
            Bukkit.getPluginManager().callEvent( new FactionClaimChangedEvent(sender, ClaimChangeCause.UNCLAIM, ((ClaimableFaction) faction).getClaims()));
        }
        if(faction instanceof PlayerFaction) {
            final PlayerFaction playerFaction = (PlayerFaction) faction;
            for(final PlayerFaction ally : playerFaction.getAlliedFactions()) {
                Bukkit.getPluginManager().callEvent(new FactionRelationRemoveEvent(playerFaction, ally, Relation.ENEMY));
                ally.getRelations().remove(faction.getUniqueID());
            }

            for(final UUID uuid : playerFaction.getMembers().keySet()) {
                playerFaction.setMember(uuid, null, true);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        HCF.getPlugin().getTabApi().onPlayerQuitEvent(Bukkit.getPlayer(uuid));
                        HCF.getPlugin().getTabApi().onPlayerJoinEvent(Bukkit.getPlayer(uuid));
                    }
                }.runTaskLaterAsynchronously(HCF.getPlugin(), 4L);
            }
        }

        return true;
    }

    private void cacheClaim(final Claim claim, final ClaimChangeCause cause) {
        Preconditions.checkNotNull((Object) claim, "Claim cannot be null");
        Preconditions.checkNotNull((Object) cause, "Cause cannot be null");
        Preconditions.checkArgument(cause != ClaimChangeCause.RESIZE,  "Cannot cache claims of resize type");
        final World world = claim.getWorld();
        if(world == null) {
            return;
        }
        final int minX = Math.min(claim.getX1(), claim.getX2());
        final int maxX = Math.max(claim.getX1(), claim.getX2());
        final int minZ = Math.min(claim.getZ1(), claim.getZ2());
        final int maxZ = Math.max(claim.getZ1(), claim.getZ2());
        for(int x = minX; x <= maxX; ++x) {
            for(int z = minZ; z <= maxZ; ++z) {
                final CoordinatePair coordinatePair = new CoordinatePair(world, x, z);
                if(cause == ClaimChangeCause.CLAIM) {
                    this.claimPositionMap.put(coordinatePair, claim);
                } else if(cause == ClaimChangeCause.UNCLAIM) {
                    this.claimPositionMap.remove(coordinatePair);
                }
            }
        }
    }

    private void cacheFaction(final Faction faction) {
        this.factionNameMap.put(faction.getName(), faction.getUniqueID());
        this.factionUUIDMap.put(faction.getUniqueID(), faction);
        if(faction instanceof ClaimableFaction) {
            ClaimableFaction claimableFaction = (ClaimableFaction) faction;
            for(final Claim claim : claimableFaction.getClaims()) {
                this.cacheClaim(claim, ClaimChangeCause.CLAIM);
            }
        }
        if(faction instanceof PlayerFaction) {
            for(final FactionMember factionMember : ((PlayerFaction) faction).getMembers().values()) {
                this.factionPlayerUuidMap.put(factionMember.getUniqueId(), faction.getUniqueID());
            }
        }
    }

    public void reloadFactionData() {
        this.factionNameMap.clear();
        this.config = new Config( this.plugin, "factions");
        final Object object = this.config.get("factions");
        if(object instanceof MemorySection) {
            final MemorySection section = (MemorySection) object;
            for(final String factionName : section.getKeys(false)) {
                final Object next = this.config.get(section.getCurrentPath() + '.' + factionName);
                if(next instanceof Faction) {
                    this.cacheFaction((Faction) next);
                }
            }
        } else if(object instanceof List) {
            final List<?> list = (List<?>) object;
            for(final Object next2 : list) {
                if(next2 instanceof Faction) {
                    this.cacheFaction((Faction) next2);
                }
            }
        }
        final Set<Faction> adding = new HashSet<>();
        if(!this.factionNameMap.containsKey("Warzone")){
            adding.add(new WarzoneFaction());
        }

        if(!this.factionNameMap.containsKey("Spawn")) {
            adding.add(new SpawnFaction());
        }
        if(!this.factionNameMap.containsKey("Glowstone")){
            adding.add(new GlowstoneFaction());
        }
        if(!this.factionNameMap.containsKey("NorthRoad")) {
            adding.add(new RoadFaction.NorthRoadFaction());
            adding.add(new RoadFaction.EastRoadFaction());
            adding.add(new RoadFaction.SouthRoadFaction());
            adding.add(new RoadFaction.WestRoadFaction());
        }
        if(!this.factionNameMap.containsKey("EndPortal")) {
            adding.add(new EndPortalFaction());
        }
        for(final Faction added : adding) {
            this.cacheFaction(added);
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + "Faction " + added.getName() + " not found, created.");
        }
    }

    public void saveFactionData() {
        this.config.set("factions",  new ArrayList<>(factionUUIDMap.values()));
        this.config.save();
    }
}