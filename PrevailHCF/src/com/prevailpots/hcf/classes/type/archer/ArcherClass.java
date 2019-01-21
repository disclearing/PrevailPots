package com.prevailpots.hcf.classes.type.archer;

import net.minecraft.util.gnu.trove.map.TObjectLongMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectLongHashMap;
import org.bukkit.*;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.google.common.collect.Sets;
import com.prevailpots.hcf.Cooldowns;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.classes.ClassType;
import com.prevailpots.hcf.classes.PvpClass;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ArcherClass extends PvpClass implements Listener {
    public static final HashMap<UUID, UUID> TAGGED = new HashMap<>();
    private static final PotionEffect ARCHER_SPEED_EFFECT;
    private static final PotionEffect ARCHER_JUMP_EFFECT;
    private static final int ARCHER_SPEED_COOLDOWN_DELAY;

    static {
        ARCHER_JUMP_EFFECT = new PotionEffect(PotionEffectType.SPEED, 9*20, 5);
        ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 9*20, 3);
        ARCHER_SPEED_COOLDOWN_DELAY = 30;
    }

    private final TObjectLongMap<UUID> archerSpeedCooldowns;
    private final HCF plugin;

    public ArcherClass(final HCF plugin) {
        super(ClassType.ARCHER);
        this.archerSpeedCooldowns = new TObjectLongHashMap();
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.REGENERATION, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }
    @Override
    public boolean onEquip(Player player){
        if(!super.onEquip(player)) {
            return false;
        }
        return true;
    }


    @EventHandler
    public void onPlayerClickFeather(PlayerInteractEvent e) {
        Player p = (Player) e.getPlayer();
        if(plugin.getPvpClassManager().getEquippedClass(p) != null && plugin.getPvpClassManager().getEquippedClass(p).equals(this)) {
            if(p.getItemInHand().getType() == Material.FEATHER) {
                if(Cooldowns.isOnCooldown("Archer_item_cooldown1", p)) {
                    p.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + HCF.getRemaining(Cooldowns.getCooldownForPlayerLong("Archer_item_cooldown1", p), true) + ChatColor.RED + '.');
                    e.setCancelled(true);
                    return;
                }
                Cooldowns.addCooldown("Archer_item_cooldown1", p, ARCHER_SPEED_COOLDOWN_DELAY);
                if(p.getItemInHand().getAmount() == 1) {
                    p.getInventory().remove(p.getItemInHand());
                } else {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
                p.addPotionEffect(ARCHER_JUMP_EFFECT);
            }
        }
    }
    @EventHandler
    public void onPlayerClickSugar(PlayerInteractEvent e) {
        Player p = (Player) e.getPlayer();
        if(plugin.getPvpClassManager().getEquippedClass(p) != null && plugin.getPvpClassManager().getEquippedClass(p).equals(this)) {
            if(p.getItemInHand().getType() == Material.SUGAR) {
                if(Cooldowns.isOnCooldown("Archer_item_cooldown", p)) {
                    p.sendMessage(ChatColor.RED + "You cannot use this for another " + ChatColor.BOLD.toString() + HCF.getRemaining(Cooldowns.getCooldownForPlayerLong("Archer_item_cooldown", p), true) + ChatColor.RED + '.');
                    e.setCancelled(true);
                    return;
                }
                Cooldowns.addCooldown("Archer_item_cooldown", p, ARCHER_SPEED_COOLDOWN_DELAY);
                if(p.getItemInHand().getAmount() == 1) {
                    p.getInventory().remove(p.getItemInHand());
                } else {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
                p.removePotionEffect(PotionEffectType.SPEED);
                p.addPotionEffect(ARCHER_SPEED_EFFECT);
                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if(isApplicableFor(p)) {
                            p.removePotionEffect(PotionEffectType.SPEED);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                        }
                    }
                }.runTaskLater(plugin, 9 * 20);
            }
        }
    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e){
        if(TAGGED.containsKey(e.getPlayer().getUniqueId())){
            TAGGED.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();
        if (entity instanceof Player && damager instanceof Arrow) {
            final Arrow arrow = (Arrow) damager;
            final ProjectileSource source = arrow.getShooter();
            if (source instanceof Player) {
                Player damaged = (Player) event.getEntity();
                final Player shooter = (Player) source;
                final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(shooter);
                if (equipped == null || !equipped.equals(this)) {
                    return;
                }

                if (plugin.getTimerManager().archerTimer.getRemaining((Player) entity) == 0 || plugin.getTimerManager().archerTimer.getRemaining((Player) entity) < TimeUnit.SECONDS.toMillis(5)) {
                    if(plugin.getPvpClassManager().getEquippedClass(damaged) != null && plugin.getPvpClassManager().getEquippedClass(damaged).equals(this)) return;
                    plugin.getTimerManager().archerTimer.setCooldown((Player) entity, entity.getUniqueId());
                    TAGGED.put(damaged.getUniqueId(), shooter.getUniqueId());
                    for(Player player : Bukkit.getOnlinePlayers()){
                        plugin.getScoreboardHandler().getPlayerBoard(player.getUniqueId()).addUpdates(Sets.newHashSet(Bukkit.getOnlinePlayers()));
                    }
                    shooter.sendMessage(ChatColor.YELLOW + "You marked " + ChatColor.GOLD + damaged.getName() + " for 10 seconds " + ChatColor.GRAY + "[Damage: " +ChatColor.RED + event.getDamage()+ChatColor.GRAY + "]");
                    damaged.sendMessage(ChatColor.GOLD + "Archer Tagged! " + ChatColor.YELLOW + "You have been Archer Tagged, any damage you take by a player will be increased by 25%");
                }
            }
        }
    }

    @Override
    public boolean isApplicableFor(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack helmet = playerInventory.getHelmet();
        if(helmet == null || helmet.getType() != Material.LEATHER_HELMET) {
            return false;
        }
        final ItemStack chestplate = playerInventory.getChestplate();
        if(chestplate == null || chestplate.getType() != Material.LEATHER_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = playerInventory.getLeggings();
        if(leggings == null || leggings.getType() != Material.LEATHER_LEGGINGS) {
            return false;
        }
        final ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.LEATHER_BOOTS;
    }
}
