package com.prevailpots.kitmap.classes.type.miner;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.customhcf.util.BukkitUtils;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.classes.ClassType;
import com.prevailpots.kitmap.classes.PvpClass;
import com.prevailpots.kitmap.classes.event.PvpClassEquipEvent;
import com.prevailpots.kitmap.user.FactionUser;

public class MinerClass extends PvpClass implements Listener {
    private static final PotionEffect HEIGHT_INVISIBILITY;
    private static final PotionEffect STAGE_R;
    private static final PotionEffect STAGE_2;
    private static final PotionEffect STAGE_1;
    private static final PotionEffect STAGE_3;

    static {
        STAGE_3 = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1);
        STAGE_2 = new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1);
        STAGE_R = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0);
        STAGE_1 = new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 2);
        HEIGHT_INVISIBILITY = new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0);
    }

    private final HCF plugin;
    private final Map<PotionEffect, Integer> removeableEffects;

    public MinerClass(final HCF plugin) {
        super(ClassType.MINER);
        this.plugin = plugin;
        removeableEffects = new HashMap<>();
        removeableEffects.put(HEIGHT_INVISIBILITY, 0);
        removeableEffects.put(STAGE_R, 15);
        removeableEffects.put(STAGE_1, 50);
        removeableEffects.put(STAGE_2, 75);
        removeableEffects.put(STAGE_3, 125);
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 0));
    }


    private void removeInvisibilitySafely(final Player player) {
        for(final PotionEffect active : player.getActivePotionEffects()) {
                if(((active.getType().equals(PotionEffectType.FAST_DIGGING)||active.getType().equals(PotionEffectType.DAMAGE_RESISTANCE) || active.getType().equals(PotionEffectType.SPEED) || active.getType().equals(PotionEffectType.FIRE_RESISTANCE) || active.getType().equals(PotionEffectType.INVISIBILITY))) && active.getDuration() > MinerClass.DEFAULT_MAX_DURATION) {
                    player.removePotionEffect(active.getType());
                }
            }
        player.sendMessage(ChatColor.DARK_GREEN + this.getName() + ChatColor.YELLOW + " miner upgrades have been removed");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerDamage(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        if(entity instanceof Player && BukkitUtils.getFinalAttacker(event, false) != null) {
            final Player player = (Player) entity;
            if(this.plugin.getPvpClassManager().hasClassEquipped(player, this)) {
                this.removeInvisibilitySafely(player);
            }
        }
    }


    @EventHandler
    public void onBreakBlock(BlockBreakEvent e){
        FactionUser user = plugin.getUserManager().getUser(e.getPlayer().getUniqueId());
        if(!isApplicableFor(e.getPlayer())) return;
        for(Map.Entry<PotionEffect, Integer> entry : removeableEffects.entrySet()){
            if(entry.getValue() <= user.getDiamondsMined()){
                applyStages(e.getPlayer());
            }
        }
    }

    @Override
    public void onUnequip(final Player player) {
        super.onUnequip(player);
        this.removeInvisibilitySafely(player);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerMove(final PlayerMoveEvent event) {
        this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        this.conformMinerInvisibility(event.getPlayer(), event.getFrom(), event.getTo());
    }

    @EventHandler
    public void onEquip(PvpClassEquipEvent e){
        if(e.getPvpClass().equals(this)){
            if(isApplicableFor(e.getPlayer())) {
                applyStages(e.getPlayer());
            }
        }
    }


    private void conformMinerInvisibility(final Player player, final Location from, final Location to) {
        final int fromY = from.getBlockY();
        final int toY = to.getBlockY();
        if(fromY != toY && this.plugin.getPvpClassManager().hasClassEquipped(player, this)) {
            final boolean isInvisible = player.hasPotionEffect(PotionEffectType.INVISIBILITY);
            if(toY > 50) {
                if(fromY <= 50 && isInvisible) {
                    player.sendMessage(ChatColor.DARK_GREEN + this.getName() + ChatColor.GRAY + " invisibility " + ChatColor.RED + "disabled" + ChatColor.GRAY + ".");
                    this.removeInvisibilitySafely(player);
                }
            }
            else if(toY <= 50 && !isInvisible && isApplicableFor(player)) {
                player.sendMessage(ChatColor.DARK_GREEN + this.getName() + ChatColor.GRAY + " invisibility " + ChatColor.GREEN + "enabled" + ChatColor.GRAY + ".");
                applyStages(player);
            }
        }
    }

    private void applyStages(Player player){
        FactionUser user = plugin.getUserManager().getUser(player.getUniqueId());
        if(!isApplicableFor(player)) return;
        for(Map.Entry<PotionEffect, Integer> entry : removeableEffects.entrySet()){
            if(entry.getValue() <= user.getDiamondsMined() && !player.hasPotionEffect(entry.getKey().getType())){
                player.addPotionEffect(entry.getKey());
            }
        }
    }

    @Override
    public boolean isApplicableFor(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack helmet = playerInventory.getHelmet();
        if(helmet == null || helmet.getType() != Material.IRON_HELMET) {
            return false;
        }
        final ItemStack chestplate = playerInventory.getChestplate();
        if(chestplate == null || chestplate.getType() != Material.IRON_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = playerInventory.getLeggings();
        if(leggings == null || leggings.getType() != Material.IRON_LEGGINGS) {
            return false;
        }
        final ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.IRON_BOOTS;
    }
}
