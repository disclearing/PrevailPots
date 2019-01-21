package com.prevailpots.kitmap.fixes;

import com.customhcf.util.BukkitUtils;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class BlockHitFixListener implements Listener {
    private static final long THRESHOLD = 850L;
    private static final ImmutableSet<Material> NON_TRANSPARENT_ATTACK_BREAK_TYPES = Sets.immutableEnumSet( Material.GLASS,  new Material[]{Material.STAINED_GLASS, Material.STAINED_GLASS_PANE});
    private static final ImmutableSet<Material> NON_TRANSPARENT_ATTACK_INTERACT_TYPES = Sets.immutableEnumSet(Material.IRON_DOOR_BLOCK, new Material[]{Material.IRON_DOOR, Material.WOODEN_DOOR, Material.WOOD_DOOR, Material.TRAP_DOOR});
    private final ConcurrentMap<Object, Object> lastInteractTimes;

    public BlockHitFixListener() {
        this.lastInteractTimes = CacheBuilder.newBuilder().expireAfterWrite(THRESHOLD, TimeUnit.MILLISECONDS).build().asMap();
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(event.hasBlock() && event.getAction() != Action.PHYSICAL && BlockHitFixListener.NON_TRANSPARENT_ATTACK_INTERACT_TYPES.contains((Object) event.getClickedBlock().getType())) {
            this.cancelAttackingMillis(event.getPlayer().getUniqueId(), THRESHOLD);
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.MONITOR)
    public void onBlockBreak(final BlockBreakEvent event) {
        if(event.isCancelled() && BlockHitFixListener.NON_TRANSPARENT_ATTACK_BREAK_TYPES.contains((Object) event.getBlock().getType())) {
            this.cancelAttackingMillis(event.getPlayer().getUniqueId(), THRESHOLD);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onEntityDamageByEntity(final EntityDamageEvent event) {
        final Player attacker = BukkitUtils.getFinalAttacker(event, true);
        if(attacker != null) {
            final Long lastInteractTime = (Long) this.lastInteractTimes.get(attacker.getUniqueId());
            if(lastInteractTime != null && lastInteractTime - System.currentTimeMillis() > 0L) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerLogout(final PlayerQuitEvent event) {
        this.lastInteractTimes.remove(event.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        this.lastInteractTimes.remove(event.getPlayer().getUniqueId());
    }

    public void cancelAttackingMillis(final UUID uuid, final long delay) {
        this.lastInteractTimes.put(uuid, System.currentTimeMillis() + delay);
    }


}
