package com.prevailpots.hcf.listener;

import com.customhcf.util.BukkitUtils;
import com.customhcf.util.cuboid.Cuboid;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.event.CaptureZoneEnterEvent;
import com.prevailpots.hcf.faction.event.CaptureZoneLeaveEvent;
import com.prevailpots.hcf.faction.event.PlayerClaimEnterEvent;
import com.prevailpots.hcf.faction.struct.Raidable;
import com.prevailpots.hcf.faction.struct.Role;
import com.prevailpots.hcf.faction.type.*;
import com.prevailpots.hcf.kothgame.CaptureZone;
import com.prevailpots.hcf.kothgame.faction.CapturableFaction;
import com.prevailpots.hcf.kothgame.faction.EventFaction;
import com.prevailpots.hcf.kothgame.faction.GlowstoneFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TravelAgent;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockFormEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.material.Cauldron;
import org.bukkit.material.MaterialData;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Objects;

public class FactionsCoreListener implements Listener {
    public static final String PROTECTION_BYPASS_PERMISSION = "faction.protection.bypass";
    private static final ImmutableMultimap<Object, Object> ITEM_BLOCK_INTERACTABLES = ImmutableMultimap.builder().put(Material.DIAMOND_HOE, Material.GRASS).put(Material.GOLD_HOE, Material.GRASS).put(Material.IRON_HOE, Material.GRASS).put(Material.STONE_HOE, Material.GRASS).put(Material.WOOD_HOE, Material.GRASS).build();
    private static final ImmutableSet<Material> BLOCK_INTERACTABLES = Sets.immutableEnumSet(Material.BED,  new Material[]{Material.BED_BLOCK, Material.BEACON, Material.FENCE_GATE, Material.IRON_DOOR, Material.TRAP_DOOR, Material.WOOD_DOOR, Material.WOODEN_DOOR, Material.IRON_DOOR_BLOCK, Material.CHEST, Material.TRAPPED_CHEST, Material.FURNACE, Material.BURNING_FURNACE, Material.BREWING_STAND, Material.HOPPER, Material.DROPPER, Material.DISPENSER, Material.STONE_BUTTON, Material.WOOD_BUTTON, Material.ENCHANTMENT_TABLE, Material.WORKBENCH, Material.ANVIL, Material.LEVER, Material.FIRE});
    private final HCF plugin;

    public FactionsCoreListener(final HCF plugin) {
        this.plugin = plugin;
    }

    public static boolean attemptBuild(final Entity entity, final Location location, final String denyMessage) {
        return attemptBuild(entity, location, denyMessage, false);
    }
    @EventHandler
    public void checkblock(BlockPlaceEvent e){
        final Faction factionatt = HCF.getPlugin().getFactionManager().getFactionAt(e.getPlayer().getLocation());
        if(factionatt instanceof GlowstoneFaction){
            if (!e.getPlayer().isOp()){
                e.setCancelled(true);
                e.getPlayer().sendMessage(ChatColor.YELLOW + "You may not place blocks in the territory of " + ChatColor.GOLD + "Glowstone Mountain" + ChatColor.YELLOW + ".");
            }
        }
    }

    public static boolean attemptBuild(final Entity entity, final Location location, final String denyMessage, final boolean isInteraction) {
        boolean result = false;
        if(entity instanceof Player) {
            final Player player = (Player) entity;
            if(player.getGameMode() == GameMode.CREATIVE && player.hasPermission(PROTECTION_BYPASS_PERMISSION)) {
                return true;
            }
            if(player.getWorld().getEnvironment() == World.Environment.THE_END) {
                player.sendMessage(ChatColor.RED + "You cannot build in the end.");
                return false;
            }
            final Faction factionAt = HCF.getPlugin().getFactionManager().getFactionAt(location);
            if(!(factionAt instanceof ClaimableFaction)) {
                result = true;
            } else if(factionAt instanceof Raidable && ((Raidable) factionAt).isRaidable()) {
                result = true;
            }

            if(factionAt instanceof WarzoneFaction) {
                return false;
            }

            if(factionAt instanceof GlowstoneFaction){
                if(entity.getWorld().getBlockAt(location).getType() == Material.GLOWSTONE){
                    if(((GlowstoneFaction) factionAt).getGlowstoneArea().contains(location)){
                        result = true;
                    }
                }
            }
            if(factionAt instanceof RoadFaction){
                if(player.getLocation().getBlockY() < 30){
                    result = true;
                }
            }
            if(factionAt instanceof PlayerFaction) {
                final PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());
                if(playerFaction != null && playerFaction.equals(factionAt)) {
                    result = true;
                }
            }

            if(result) {
                if(!isInteraction && Math.abs(location.getBlockX()) <= HCF.getPlugin().getHcfHandler().getSpawnRadius() && Math.abs(location.getBlockZ()) <= HCF.getPlugin().getHcfHandler().getSpawnRadius()) {
                    if(denyMessage != null) {
                        player.sendMessage(ChatColor.YELLOW + "You cannot build within " + ChatColor.GRAY + HCF.getPlugin().getHcfHandler().getSpawnRadius() + ChatColor.YELLOW + " blocks from spawn.");
                    }
                    return false;
                }
            } else if(denyMessage != null) {
                player.sendMessage(String.format(denyMessage, factionAt.getDisplayName(player)));
            }
        }

            return result;
        }


    public static boolean canBuildAt(final Location from, final Location to) {
        final Faction toFactionAt = HCF.getPlugin().getFactionManager().getFactionAt(to);
        if(toFactionAt instanceof Raidable && !((Raidable) toFactionAt).isRaidable()) {
            final Faction fromFactionAt = HCF.getPlugin().getFactionManager().getFactionAt(from);
            if(!toFactionAt.equals(fromFactionAt)) {
                return false;
            }
        }
        return true;
    }

    private void handleMove(final PlayerMoveEvent event, final PlayerClaimEnterEvent.EnterCause enterCause) {
        final Location from = event.getFrom();
        final Location to = event.getTo();
        if(from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        final Player player = event.getPlayer();
        boolean cancelled = false;
        final Faction fromFaction = this.plugin.getFactionManager().getFactionAt(from);
        final Faction toFaction = this.plugin.getFactionManager().getFactionAt(to);
        if(!Objects.equals(fromFaction, toFaction)) {
            final PlayerClaimEnterEvent calledEvent = new PlayerClaimEnterEvent(player, from, to, fromFaction, toFaction, enterCause);
            Bukkit.getPluginManager().callEvent((Event) calledEvent);
            cancelled = calledEvent.isCancelled();
        } else if(toFaction instanceof CapturableFaction) {
            final CapturableFaction capturableFaction = (CapturableFaction) toFaction;
            for(final CaptureZone captureZone : capturableFaction.getCaptureZones()) {
                final Cuboid cuboid = captureZone.getCuboid();
                if(cuboid != null) {
                    final boolean containsFrom = cuboid.contains(from);
                    final boolean containsTo = cuboid.contains(to);
                    if(containsFrom && !containsTo) {
                        final CaptureZoneLeaveEvent calledEvent2 = new CaptureZoneLeaveEvent(player, capturableFaction, captureZone);
                        Bukkit.getPluginManager().callEvent((Event) calledEvent2);
                        cancelled = calledEvent2.isCancelled();
                        break;
                    }
                    if(!containsFrom && containsTo) {
                        final CaptureZoneEnterEvent calledEvent3 = new CaptureZoneEnterEvent(player, capturableFaction, captureZone);
                        Bukkit.getPluginManager().callEvent((Event) calledEvent3);
                        cancelled = calledEvent3.isCancelled();
                        break;
                    }
                    continue;
                }
            }
        }
        if(cancelled) {
            if(enterCause == PlayerClaimEnterEvent.EnterCause.TELEPORT) {
                event.setCancelled(true);
            } else {
                from.add(0.5, 0, 0.5);
                event.setTo(from);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.handleMove(event, PlayerClaimEnterEvent.EnterCause.MOVEMENT);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPlayerMove(final PlayerTeleportEvent event) {
        this.handleMove((PlayerMoveEvent) event, PlayerClaimEnterEvent.EnterCause.TELEPORT);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockIgnite(final BlockIgniteEvent event) {
        switch(event.getCause()) {
            case FLINT_AND_STEEL:
            case ENDER_CRYSTAL: {
            }
            default: {
                final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
                if(factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStickyPistonExtend(final BlockPistonExtendEvent event) {
        final Block block = event.getBlock();
        final Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);
        if(targetBlock.isEmpty() || targetBlock.isLiquid()) {
            final Faction targetFaction = this.plugin.getFactionManager().getFactionAt(targetBlock.getLocation());
            if(targetFaction instanceof Raidable && !((Raidable) targetFaction).isRaidable() && !targetFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onStickyPistonRetract(final BlockPistonRetractEvent event) {
        if(!event.isSticky()) {
            return;
        }
        final Location retractLocation = event.getRetractLocation();
        final Block retractBlock = retractLocation.getBlock();
        if(!retractBlock.isEmpty() && !retractBlock.isLiquid()) {
            final Block block = event.getBlock();
            final Faction targetFaction = this.plugin.getFactionManager().getFactionAt(retractLocation);
            if(targetFaction instanceof Raidable && !((Raidable) targetFaction).isRaidable() && !targetFaction.equals(this.plugin.getFactionManager().getFactionAt(block))) {
                event.setCancelled(true);
            }
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFromTo(final BlockFromToEvent event) {
        final Block toBlock = event.getToBlock();
        final Block fromBlock = event.getBlock();
        final Material fromType = fromBlock.getType();
        final Material toType = toBlock.getType();
        if((toType == Material.REDSTONE_WIRE || toType == Material.TRIPWIRE) && (fromType == Material.AIR || fromType == Material.STATIONARY_LAVA || fromType == Material.LAVA)) {
            toBlock.setType(Material.AIR);
        }
        if((toBlock.getType() == Material.WATER || toBlock.getType() == Material.STATIONARY_WATER || toBlock.getType() == Material.LAVA || toBlock.getType() == Material.STATIONARY_LAVA) && !canBuildAt(fromBlock.getLocation(), toBlock.getLocation())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if(event.getCause() == PlayerTeleportEvent.TeleportCause.ENDER_PEARL) {
            final Faction toFactionAt = this.plugin.getFactionManager().getFactionAt(event.getTo());
            if(toFactionAt.isSafezone() && !this.plugin.getFactionManager().getFactionAt(event.getFrom()).isSafezone()) {
                final Player player = event.getPlayer();
                player.sendMessage(ChatColor.RED + "You cannot Enderpearl into safe-zones, used Enderpearl has been refunded.");
                this.plugin.getTimerManager().enderPearlTimer.refund(player);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerPortal(PlayerPortalEvent event) {
        if (event.getCause() == PlayerTeleportEvent.TeleportCause.NETHER_PORTAL) {
            Location from = event.getFrom();
            Location to = event.getTo();
            Player player = event.getPlayer();

            Faction fromFac = plugin.getFactionManager().getFactionAt(from);
            if (fromFac.isSafezone()) { // teleport player to spawn point of target if came from safe-zone.
                event.setTo(to.getWorld().getSpawnLocation().add(0, 1, 0));
                event.useTravelAgent(false);
                player.sendMessage(ChatColor.YELLOW + "You have been teleported to Overworld Spawn.");
                return;
            }

            if (event.useTravelAgent() && to.getWorld().getEnvironment() == World.Environment.NORMAL) {
                TravelAgent travelAgent = event.getPortalTravelAgent();
                if (!travelAgent.getCanCreatePortal()) return;

                Location foundPortal = travelAgent.findPortal(to);
                if (foundPortal != null)
                    return; // there is already an exit portal, so ignore

                Faction factionAt = plugin.getFactionManager().getFactionAt(to);
                if (factionAt instanceof ClaimableFaction) {
                    Faction playerFaction = plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
                    if (playerFaction != factionAt) {
                        player.sendMessage(ChatColor.YELLOW + "Portal would have created portal in territory of " + factionAt.getDisplayName(player) + ChatColor.YELLOW + '.');
                        event.setCancelled(true);
                    } else if (factionAt instanceof SpawnFaction) {
                        player.sendMessage(ChatColor.YELLOW + "This portal would send you to spawn so the teleport cancelled.");
                        event.setCancelled(true);
                    } else if (factionAt instanceof EventFaction) {
                        player.sendMessage(ChatColor.YELLOW + "This portal would send you to an event faction so the portal event cancelled.");
                        event.setCancelled(true);
                    }
                }
            }
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamage(final EntityDamageEvent event) {
        final Entity entity = event.getEntity();
        if(entity instanceof Player) {
            final Player player = (Player) entity;
            final Faction playerFactionAt = this.plugin.getFactionManager().getFactionAt(player.getLocation());
            final EntityDamageEvent.DamageCause cause = event.getCause();
            if(playerFactionAt.isSafezone() && cause != EntityDamageEvent.DamageCause.SUICIDE) {
                event.setCancelled(true);
            }
            final Player attacker = BukkitUtils.getFinalAttacker(event, true);
            if(attacker != null) {
                final Faction attackerFactionAt = this.plugin.getFactionManager().getFactionAt(attacker.getLocation());
                if(attackerFactionAt.isSafezone()) {
                    event.setCancelled(true);
                    plugin.getMessage().sendMessage(attacker, ChatColor.RED + "You cannot attack players whilst in safe-zones.");
                    return;
                }
                if(playerFactionAt.isSafezone()) {
                    plugin.getMessage().sendMessage(attacker, ChatColor.RED + "You cannot attack players that are in safe-zones.");
                    return;
                }
                final PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
                final PlayerFaction attackerFaction;
                if(playerFaction != null && (attackerFaction = this.plugin.getFactionManager().getPlayerFaction(attacker.getUniqueId())) != null) {
                    final Role role = playerFaction.getMember(player).getRole();
                    final String astrix = role.getAstrix();
                    if(attackerFaction.equals(playerFaction)) {
                        plugin.getMessage().sendMessage(attacker, ChatColor.YELLOW + "You cannot hurt your faction member " + ChatColor.GRAY + player.getName() + '.');
                        event.setCancelled(true);
                    } else if(attackerFaction.getAllied().contains(playerFaction.getUniqueID())) {
                        plugin.getMessage().sendMessage(attacker, ChatColor.AQUA + "Watch out! " + ChatColor.GOLD + player.getName() + ChatColor.AQUA + " is an ally.");
                    }
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        final Entity entered = event.getEntered();
        if(entered instanceof Player) {
            final Vehicle vehicle = event.getVehicle();
            if(vehicle instanceof Horse) {
                final Horse horse = (Horse) event.getVehicle();
                final AnimalTamer owner = horse.getOwner();
                if(owner != null && !owner.equals(entered)) {
                    ((Player) entered).sendMessage(ChatColor.YELLOW + "You cannot ride a horse that belongs to " + ChatColor.RED + owner.getName() + ChatColor.YELLOW + '.');
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        final Entity entity = (Entity) event.getEntity();
        if(entity instanceof Player && ((Player) entity).getFoodLevel() < event.getFoodLevel() && this.plugin.getFactionManager().getFactionAt(entity.getLocation()).isSafezone()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onPotionSplash(final PotionSplashEvent event) {
        final ThrownPotion potion = event.getEntity();
        if(!BukkitUtils.isDebuff(potion)) {
            return;
        }
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(potion.getLocation());
        if(factionAt.isSafezone()) {
            event.setCancelled(true);
            return;
        }
        final ProjectileSource source = potion.getShooter();
        if(source instanceof Player) {
            final Player player = (Player) source;
            for(final LivingEntity affected : event.getAffectedEntities()) {
                if(affected instanceof Player && !player.equals(affected)) {
                    final Player target = (Player) affected;
                    if(target.equals(source)) {
                        continue;
                    }
                    if(!this.plugin.getFactionManager().getFactionAt(target.getLocation()).isSafezone()) {
                        continue;
                    }
                    event.setIntensity(affected, 0.0);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityTarget(final EntityTargetEvent event) {
        switch(event.getReason()) {
            case CLOSEST_PLAYER:
            case RANDOM_TARGET: {
                final Entity target = event.getTarget();
                if(event.getEntity() instanceof LivingEntity && target instanceof Player) {
                    final Faction factionAt = this.plugin.getFactionManager().getFactionAt(target.getLocation());
                    final Faction playerFaction;
                    if(factionAt.isSafezone() || ((playerFaction = this.plugin.getFactionManager().getPlayerFaction(target.getUniqueId())) != null && factionAt.equals(playerFaction))) {
                        event.setCancelled(true);
                    }
                    break;
                }
                break;
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(!event.hasBlock()) {
            return;
        }

        final Block block = event.getClickedBlock();
        final Action action = event.getAction();
        final Faction factionAt = HCF.getPlugin().getFactionManager().getFactionAt(block.getLocation());

        if(action == Action.PHYSICAL) {
            if(block.getType() == Material.STONE_PLATE && factionAt instanceof SpawnFaction) {}
            else if(!attemptBuild( event.getPlayer(), block.getLocation(), null)) {
                event.setCancelled(true);
            }
        }
        if(action == Action.RIGHT_CLICK_BLOCK) {
            if(block.getType() == Material.WORKBENCH && factionAt instanceof SpawnFaction) {}
            else {
                boolean canBuild = !FactionsCoreListener.BLOCK_INTERACTABLES.contains(block.getType());
                if (canBuild) {
                    final Material itemType = event.hasItem() ? event.getItem().getType() : null;
                    if (itemType != null && FactionsCoreListener.ITEM_BLOCK_INTERACTABLES.containsKey((Object) itemType) && FactionsCoreListener.ITEM_BLOCK_INTERACTABLES.get(itemType).contains((Object) event.getClickedBlock().getType())) {
                        canBuild = false;
                    } else {
                        final MaterialData materialData = block.getState().getData();
                        if (materialData instanceof Cauldron) {
                            final Cauldron cauldron = (Cauldron) materialData;
                            if (!cauldron.isEmpty() && event.hasItem() && event.getItem().getType() == Material.GLASS_BOTTLE) {
                                canBuild = false;
                            }
                        }
                    }
                }
                if (!canBuild && !attemptBuild((Entity) event.getPlayer(), block.getLocation(), ChatColor.YELLOW + "You cannot do this in the territory of %1$s" + ChatColor.YELLOW + '.', true)) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBurn(final BlockBurnEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if(factionAt instanceof WarzoneFaction || (factionAt instanceof Raidable && !((Raidable) factionAt).isRaidable())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockFade(final BlockFadeEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if(factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onLeavesDelay(final LeavesDecayEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if(factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockForm(final BlockFormEvent event) {
        final Faction factionAt = this.plugin.getFactionManager().getFactionAt(event.getBlock().getLocation());
        if(factionAt instanceof ClaimableFaction && !(factionAt instanceof PlayerFaction)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityChangeBlock(final EntityChangeBlockEvent event) {
        final Entity entity = event.getEntity();
        if(entity instanceof LivingEntity && !attemptBuild(entity, event.getBlock().getLocation(), null)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockBreak(final BlockBreakEvent event) {
        if(!attemptBuild((Entity) event.getPlayer(), event.getBlock().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBlockPlace(final BlockPlaceEvent event) {
        if(!attemptBuild((Entity) event.getPlayer(), event.getBlockPlaced().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketFill(final PlayerBucketFillEvent event) {
        if(!attemptBuild((Entity) event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBucketEmpty(final PlayerBucketEmptyEvent event) {
        if(!attemptBuild((Entity) event.getPlayer(), event.getBlockClicked().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingBreakByEntity(final HangingBreakByEntityEvent event) {
        final Entity remover = event.getRemover();
        if(remover instanceof Player && !attemptBuild(remover, event.getEntity().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingPlace(final HangingPlaceEvent event) {
        if(!attemptBuild((Entity) event.getPlayer(), event.getEntity().getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onHangingDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if(entity instanceof Hanging) {
            final Player attacker = BukkitUtils.getFinalAttacker((EntityDamageEvent) event, false);
            if(!attemptBuild((Entity) attacker, entity.getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    public void onHangingInteractByPlayer(final PlayerInteractEntityEvent event) {
        final Entity entity = event.getRightClicked();
        if(entity instanceof Hanging && !attemptBuild((Entity) event.getPlayer(), entity.getLocation(), ChatColor.YELLOW + "You cannot build in the territory of %1$s" + ChatColor.YELLOW + '.')) {
            event.setCancelled(true);
        }
    }


}
