package com.prevailpots.kitmap.timer.type;

import com.customhcf.util.Config;
import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.timer.PlayerTimer;
import com.prevailpots.kitmap.timer.TimerRunnable;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutSetSlot;
import net.minecraft.server.v1_7_R4.PlayerInventory;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

public class EnderPearlTimer extends PlayerTimer implements Listener {
    private static final long REFRESH_DELAY_TICKS = 2L;
    private static final long REFRESH_DELAY_TICKS_18 = 20L;
    private static final long EXPIRE_SHOW_MILLISECONDS = 1500L;
    private final ConcurrentMap<Object, Object> itemNameFakes;
    private final JavaPlugin plugin;

    public EnderPearlTimer(final JavaPlugin plugin) {
        super("Enderpearl", MoreObjects.firstNonNull(plugin.getConfig().getLong("Timer.EnderPearl"), TimeUnit.SECONDS.toMillis(16L)));
        this.plugin = plugin;
        this.itemNameFakes = CacheBuilder.newBuilder().expireAfterWrite(this.defaultCooldown + EXPIRE_SHOW_MILLISECONDS + 5000L, TimeUnit.MILLISECONDS).build().asMap();
    }

    public String getScoreboardPrefix() {
        return ChatColor.DARK_PURPLE.toString() +  ChatColor.BOLD;
    }

    @Override
    public void load(final Config config) {
        super.load(config);
        final Collection<UUID> cooldowned = this.cooldowns.keySet();
        for(final UUID uuid : cooldowned) {
            final Player player = Bukkit.getPlayer(uuid);
            if(player == null) {
                continue;
            }
            this.startDisplaying(player);
        }
    }


    @Override
    public void onExpire(final UUID userUUID) {
        super.onExpire(userUUID);
        final Player player = Bukkit.getPlayer(userUUID);
        if(player != null) {
            player.sendMessage(ChatColor.GREEN + "You can now enderpearl!");
        }
    }

    @Override
    public TimerRunnable clearCooldown(final UUID playerUUID) {
        final TimerRunnable runnable = super.clearCooldown(playerUUID);
        if(runnable != null) {
            this.itemNameFakes.remove(playerUUID);
            return runnable;
        }
        return null;
    }

    @Override
    public void clearCooldown(final Player player) {
        this.stopDisplaying(player);
        super.clearCooldown(player);
    }

    public void refund(final Player player) {
        player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.ENDER_PEARL, 1)});
        this.clearCooldown(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onProjectileLaunch(final ProjectileLaunchEvent event) {
        final Projectile projectile = event.getEntity();
        if(projectile instanceof EnderPearl) {
            final EnderPearl enderPearl = (EnderPearl) projectile;
            final ProjectileSource source = enderPearl.getShooter();
            if(source instanceof Player) {
                final Player shooter = (Player) source;
                final long remaining = this.getRemaining(shooter);
                if(remaining > 0L) {
                    shooter.sendMessage(ChatColor.RED + "You cannot use this for another "+ ChatColor.BOLD +  HCF.getRemaining(remaining, true, false) + ChatColor.RED + '.');
                    event.setCancelled(true);
                    return;
                }
                if(this.setCooldown(shooter, shooter.getUniqueId(), this.defaultCooldown, true)) {
                    this.startDisplaying(shooter);
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        this.clearCooldown(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(final PlayerKickEvent event) {
        this.clearCooldown(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerItemHeld(final PlayerItemHeldEvent event) {
        final Player player = event.getPlayer();
        final PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
        if(pearlNameFaker != null) {
            final int previousSlot = event.getPreviousSlot();
            final ItemStack item = player.getInventory().getItem(previousSlot);
            if(item == null) {
                return;
            }
            pearlNameFaker.setFakeItem(((CraftItemStack) item).handle, previousSlot);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryDrag(final InventoryDragEvent event) {
        final HumanEntity humanEntity = event.getWhoClicked();
        if(humanEntity instanceof Player) {
            final Player player = (Player) humanEntity;
            final PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
            if(pearlNameFaker == null) {
                return;
            }
            for(final Map.Entry<Integer, ItemStack> entry : event.getNewItems().entrySet()) {
                if(entry.getKey() == player.getInventory().getHeldItemSlot()) {
                    pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(player.getItemInHand()), player.getInventory().getHeldItemSlot());
                    break;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onInventoryClick(final InventoryClickEvent event) {
        final HumanEntity humanEntity = event.getWhoClicked();
        if(humanEntity instanceof Player) {
            final Player player = (Player) humanEntity;
            final PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.get(player.getUniqueId());
            if(pearlNameFaker == null) {
                return;
            }
            final int heldSlot = player.getInventory().getHeldItemSlot();
            if(event.getSlot() == heldSlot) {
                pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(player.getItemInHand()), heldSlot);
            } else if(event.getHotbarButton() == heldSlot) {
                pearlNameFaker.setFakeItem(CraftItemStack.asNMSCopy(event.getCurrentItem()), event.getSlot());
                new BukkitRunnable() {
                    public void run() {
                        player.updateInventory();
                    }
                }.runTask(plugin);
            }
        }
    }

    public void startDisplaying(final Player player) {
        final PearlNameFaker pearlNameFaker;
        if(this.getRemaining(player) > 0L && this.itemNameFakes.putIfAbsent(player.getUniqueId(), pearlNameFaker = new PearlNameFaker(this, player)) == null) {
            final long ticks = (((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47) ? REFRESH_DELAY_TICKS_18 : REFRESH_DELAY_TICKS;
            pearlNameFaker.runTaskTimerAsynchronously(this.plugin, ticks, ticks);
        }
    }

    public void stopDisplaying(final Player player) {
        final PearlNameFaker pearlNameFaker = (PearlNameFaker) this.itemNameFakes.remove(player.getUniqueId());
        if(pearlNameFaker != null) {
            pearlNameFaker.cancel();
        }
    }

    public static class PearlNameFaker extends BukkitRunnable {
        private final PlayerTimer timer;
        private final Player player;

        public PearlNameFaker(final PlayerTimer timer, final Player player) {
            this.timer = timer;
            this.player = player;
        }


        public void run() {
            final ItemStack stack = this.player.getItemInHand();
            if(stack != null && stack.getType() == Material.ENDER_PEARL) {
                final long remaining = this.timer.getRemaining(this.player);
                net.minecraft.server.v1_7_R4.ItemStack item = ((CraftItemStack) stack).handle;
                if(remaining > 0L) {
                    item = item.cloneItemStack();
                    item.c(ChatColor.YELLOW + "Enderpearl Cooldown: " + ChatColor.RED + HCF.getRemaining(remaining, true, true));
                    this.setFakeItem(item, this.player.getInventory().getHeldItemSlot());
                } else {
                    this.cancel();
                }
            }
        }

        public synchronized void cancel() throws IllegalStateException {
            super.cancel();
            this.setFakeItem(CraftItemStack.asNMSCopy(this.player.getItemInHand()), this.player.getInventory().getHeldItemSlot());
        }

        public void setFakeItem(final net.minecraft.server.v1_7_R4.ItemStack nms, int index) {
            final EntityPlayer entityPlayer = ((CraftPlayer) this.player).getHandle();
            if(index < PlayerInventory.getHotbarSize()) {
                index += 36;
            } else if(index > 35) {
                index = 8 - (index - 36);
            }
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutSetSlot(entityPlayer.activeContainer.windowId, index, nms));
        }
    }
}
