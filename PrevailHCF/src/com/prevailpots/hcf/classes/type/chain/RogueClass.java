package com.prevailpots.hcf.classes.type.chain;

import com.google.common.collect.Maps;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.classes.ClassType;
import com.prevailpots.hcf.classes.PvpClass;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RogueClass  extends PvpClass implements Listener {
    private final HCF plugin;
    Map<UUID, Long> clickDelay;
    private static final PotionEffect ARCHER_SPEED_EFFECT;


    static {
        ARCHER_SPEED_EFFECT = new PotionEffect(PotionEffectType.SPEED, 10*20, 5);
    }

    public RogueClass(final HCF plugin) {
        super(ClassType.ROUGE);
        this.plugin = plugin;
        clickDelay = Maps.newHashMap();
        this.passiveEffects.add(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 0));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.JUMP, Integer.MAX_VALUE, 1));
        this.passiveEffects.add(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        final Entity entity = event.getEntity();
        final Entity damager = event.getDamager();
        if(entity instanceof Player && damager instanceof Player) {
            final Player attacker = (Player) damager;
            final PvpClass equipped = this.plugin.getPvpClassManager().getEquippedClass(attacker);
            if(equipped != null && equipped.equals(this)) {
                final ItemStack stack = attacker.getItemInHand();
                if(stack != null && stack.getType() == Material.GOLD_SWORD && stack.getEnchantments().isEmpty() && !attacker.hasPotionEffect(PotionEffectType.SLOW)) {
                    final Player player = (Player) entity;
                    if(rpGetPlayerDirection(attacker).equals(rpGetPlayerDirection(player))) {
                        player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        attacker.setItemInHand(new ItemStack(Material.AIR, 1));
                        attacker.playSound(player.getLocation(), Sound.ITEM_BREAK, 1.0f, 1.0f);
                        attacker.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3 * 20, 4));
                        event.setDamage(5.0);
                    }
                }
            }
        }
    }
    public String rpGetPlayerDirection(Player playerSelf){
        String dir = "";
        float y = playerSelf.getLocation().getYaw();
        if( y < 0 ){y += 360;}
        y %= 360;
        int i = (int)((y+8) / 22.5);
        if(i == 0 || i == 1  || i == 15){dir = "west";}
        else if(i == 4 || i == 5 || i == 6 || i == 2 || i == 3){dir = "north";}
        else if(i == 8 || i == 7 || i == 9){dir = "east";}
        else if(i == 11 || i == 10 || i == 12 || i == 13 || i == 14 ){dir = "south";}
        else {dir = "west";}
        return dir;
    }


    @EventHandler
    public void onClick(PlayerInteractEvent e){
        if(plugin.getPvpClassManager().getEquippedClass(e.getPlayer()) != null && plugin.getPvpClassManager().getEquippedClass(e.getPlayer()).equals(this)) {
            if (e.getItem().getType().equals(Material.SUGAR)) {
                if (clickDelay.containsKey(e.getPlayer().getUniqueId())) {
                    e.getPlayer().sendMessage(ChatColor.RED + "You cannot use this for another " + HCF.getRemaining(System.currentTimeMillis() - clickDelay.get(e.getPlayer().getUniqueId()), true));
                    e.setCancelled(true);
                    return;
                }
                Player p = e.getPlayer();
                p.removePotionEffect(PotionEffectType.SPEED);
                clickDelay.put(e.getPlayer().getUniqueId(), System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(90));
                p.addPotionEffect(ARCHER_SPEED_EFFECT);
                if (p.getItemInHand().getAmount() == 1) {
                    p.getInventory().remove(p.getItemInHand());
                } else {
                    p.getItemInHand().setAmount(p.getItemInHand().getAmount() - 1);
                }
                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (isApplicableFor(p)) {
                            p.removePotionEffect(PotionEffectType.SPEED);
                            p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 2));
                        }
                    }
                }.runTaskLater(plugin, 10 * 20);
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
