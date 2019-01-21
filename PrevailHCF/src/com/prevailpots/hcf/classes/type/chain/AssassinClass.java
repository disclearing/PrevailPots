package com.prevailpots.hcf.classes.type.chain;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.prevailpots.hcf.Cooldowns;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.classes.ClassType;
import com.prevailpots.hcf.classes.PvpClass;
import com.prevailpots.hcf.classes.event.PvpClassUnequipEvent;

import java.util.HashMap;

/**
 * Created by TREHOME on 10/20/2015.
 */
public class AssassinClass extends PvpClass implements Listener {


    private final HCF plugin;
    public HashMap<String, Integer> firstAssassinEffects = new HashMap<>();
    public AssassinClass(final HCF plugin) {
        super(ClassType.ASSASSIN);
        this.plugin = plugin;
        this.passiveEffects.add(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
    }
    @EventHandler
    public void onUnEquip(PvpClassUnequipEvent e){
        Player p = (Player) e.getPlayer();
        for(Player on : Bukkit.getOnlinePlayers()) {
            if (!on.canSee(p) && !on.hasPermission("base.command.vanish")) {
                on.showPlayer(p);
            }
        }
        firstAssassinEffects.remove(p);
    }
    @EventHandler
    public void onDamageSelf(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(plugin.getPvpClassManager().getEquippedClass(p) == null || !plugin.getPvpClassManager().getEquippedClass(p).equals(this)) return;
                if(firstAssassinEffects.containsKey(p.getName()) && firstAssassinEffects.get(p.getName()) == 1){
                    for(Entity entity : p.getNearbyEntities(20, 20, 20)){
                        if(entity instanceof  Player){
                            Player players = (Player) entity;
                            players.sendMessage(ChatColor.YELLOW+"An reaper has taken damage in stealth mode near you: "+ChatColor.GOLD+ChatColor.ITALIC+"(20 x 20)");
                        }

                }
            }
        }
    }
    @EventHandler
    public void onHitOtherPlayers(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
            Player p = (Player) e.getDamager();
            Player ent = (Player) e.getEntity();
                if(firstAssassinEffects.containsKey(p.getName()) && firstAssassinEffects.get(p.getName()) == 1) {
                    afterFiveSeconds(p, true);
                }
            }
            }


    @EventHandler
    public void onClickItem(PlayerInteractEvent e){
        Player p =  e.getPlayer();
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
            final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(p);
        if(equipped == null || !equipped.equals(this)){
            return;
        }
                if(p.getItemInHand().getType() == Material.QUARTZ){

                        if(Cooldowns.isOnCooldown("Assassin_item_cooldown", p)) {
                            p.sendMessage(ChatColor.RED + "You still have an "+ ChatColor.GREEN + ChatColor.BOLD +"Reaper" + ChatColor.RED+ " cooldown for another "  + HCF.getRemaining(Cooldowns.getCooldownForPlayerLong("Assassin_item_cooldown", p), true) +ChatColor.RED + '.');
                            return;
                        }

                        if(p.getItemInHand().getAmount() == 1){
                            p.getInventory().remove(p.getItemInHand());
                        }
                        p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);

                    p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.GRAY + "Stealth" + ChatColor.YELLOW + " Mode");
                    for(Player on: Bukkit.getOnlinePlayers()){
                        on.playEffect(p.getLocation().add(.5, 2, .5), Effect.ENDER_SIGNAL, 5);
                        on.playEffect(p.getLocation().add(.5, 1.5, .5), Effect.ENDER_SIGNAL, 5);
                        on.playEffect(p.getLocation().add(.5, 1, .5), Effect.ENDER_SIGNAL, 5);
                        on.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
                            if(!on.hasPermission("base.command.vanish")){
                                on.hidePlayer(p);
                            }
                        }

                        Cooldowns.addCooldown("Assassin_item_cooldown", p, 60);
                        p.removePotionEffect(PotionEffectType.SPEED);
                        firstAssassinEffects.put(p.getName(), 1);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 4), true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 100, 0), true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100, 0), true);

                    BukkitTask task = new BukkitRunnable(){
                                public void run(){
                                    if(isApplicableFor(p)) {
                                        if(firstAssassinEffects.containsKey(p.getName()) && firstAssassinEffects.get(p.getName()) == 1) {
                                            afterFiveSeconds(p, false);
                                        }
                                    }
                                }
                            }.runTaskLater(plugin, 5 * 20);
                }
                    }
                }
    public void afterFiveSeconds(Player p, boolean force) {
        if(firstAssassinEffects.containsKey(p.getName())) {
            if(isApplicableFor(p)) {
                for(Player on : Bukkit.getOnlinePlayers()) {
                    if(!on.canSee(p) && !on.hasPermission("base.command.vanish")) {
                        on.showPlayer(p);
                    }
                    on.playEffect(p.getLocation().add(0, 2, 0), Effect.ENDER_SIGNAL, 3);
                    on.playEffect(p.getLocation().add(0, 1.5, 0), Effect.ENDER_SIGNAL, 3);
                    on.playEffect(p.getLocation().add(0, 1, 0), Effect.ENDER_SIGNAL, 3);
                    on.playEffect(p.getLocation().add(0, 2, 0), Effect.BLAZE_SHOOT, 5);
                    on.playEffect(p.getLocation().add(0, 1.5, 0), Effect.BLAZE_SHOOT, 5);
                    on.playEffect(p.getLocation().add(0, 1, 0), Effect.BLAZE_SHOOT, 5);
                }
                BukkitTask task1 = new BukkitRunnable(){
                    public void run() {
                        if(firstAssassinEffects.containsKey(p.getName()) && firstAssassinEffects.get(p.getName()) == 2) {
                            firstAssassinEffects.remove(p.getName());
                            p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.GREEN + "Normal" + ChatColor.YELLOW + " Mode");
                            if(isApplicableFor(p)) {
                                p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0), true);
                                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
                                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1), true);
                            }
                        }
                    }
                }.runTaskLater(plugin, 5  * 20);
                if(force){
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0), true);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 1), true);
                    p.removePotionEffect(PotionEffectType.INVISIBILITY);
                    firstAssassinEffects.remove(p.getName());
                    firstAssassinEffects.put(p.getName(), 2);
                    p.sendMessage(ChatColor.YELLOW + "You have been forced into " + ChatColor.RED + "Power" + ChatColor.YELLOW + " Mode" + ChatColor.GRAY.toString() + ChatColor.ITALIC + " (5 Seconds)");
                    return;
                }
                firstAssassinEffects.remove(p.getName());
                firstAssassinEffects.put(p.getName(), 2);
                p.sendMessage(ChatColor.YELLOW + "You are now in " + ChatColor.RED + "Power" + ChatColor.YELLOW + " Mode" + ChatColor.GRAY.toString() + ChatColor.ITALIC + " (5 Seconds)");
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 120, 1), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 120, 0), true);
                p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 120, 1), true);
                p.removePotionEffect(PotionEffectType.INVISIBILITY);
            }
        }
    }


    @Override
    public boolean isApplicableFor(final Player player) {
        final PlayerInventory playerInventory = player.getInventory();
        final ItemStack helmet = playerInventory.getHelmet();

        if(helmet == null || helmet.getType() != Material.CHAINMAIL_HELMET) {
            return false;
        }
        final ItemStack chestplate = playerInventory.getChestplate();
        if(chestplate == null || chestplate.getType() != Material.CHAINMAIL_CHESTPLATE) {
            return false;
        }
        final ItemStack leggings = playerInventory.getLeggings();
        if(leggings == null || leggings.getType() != Material.CHAINMAIL_LEGGINGS) {
            return false;
        }
        final ItemStack boots = playerInventory.getBoots();
        return boots != null && boots.getType() == Material.CHAINMAIL_BOOTS;
    }
}
