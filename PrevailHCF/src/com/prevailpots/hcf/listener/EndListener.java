package com.prevailpots.hcf.listener;


import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.PortalType;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityCreatePortalEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.customhcf.util.imagemessage.ImageChar;
import com.customhcf.util.imagemessage.ImageMessage;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.type.Faction;

/**
 * Created by TREHOME on 01/28/2016.
 */
public class EndListener implements Listener {


    @EventHandler
    public void onEntityDeath(final EntityDeathEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            final Faction faction = HCF.getPlugin().getFactionManager().getPlayerFaction(event.getEntity().getKiller().getUniqueId());
            String factionName;
            if(faction == null){
                factionName = "Player: " +event.getEntity().getKiller().getName();
            }else{
                factionName = "Faction: "+faction.getName();
            }
            for (int i = 0; i < 5; ++i) {
                Bukkit.broadcastMessage("");
            }

            for(Player on: Bukkit.getOnlinePlayers()) {
                try {
                    BufferedImage imageToSend = ImageIO.read(HCF.getPlugin().getResource("enderdragon-art.png"));
                    new ImageMessage(
                            imageToSend,
                            15,
                            ImageChar.BLOCK.getChar()
                    ).appendText(
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            ChatColor.RED + "[EnderDragon]",
                            ChatColor.YELLOW + "Slain by",
                            ChatColor.YELLOW.toString() + ChatColor.BOLD + factionName,
                            ChatColor.GRAY + (!factionName.contains("Faction: ") ? "" :  event.getEntity().getKiller().getName())
                    ).sendToPlayer(on);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            final ItemStack dragonEgg = new ItemStack(Material.DRAGON_EGG);
            final ItemMeta itemMeta = dragonEgg.getItemMeta();
            final DateFormat sdf = new SimpleDateFormat("M/d HH:mm:ss");
            itemMeta.setLore(Arrays.asList(ChatColor.DARK_PURPLE.toString() + ChatColor.BOLD + "Enderdragon " + ChatColor.YELLOW + "slain by " + ChatColor.GOLD + event.getEntity().getKiller().getName(), ChatColor.YELLOW + sdf.format(new Date()).replace(" AM", "").replace(" PM", "")));
            dragonEgg.setItemMeta(itemMeta);
            event.getEntity().getKiller().getInventory().addItem(new ItemStack[] { dragonEgg });
            if (!event.getEntity().getKiller().getInventory().contains(Material.DRAGON_EGG)) {
                event.getDrops().add(dragonEgg);
            }
        }
    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.getClickedBlock() != null){
            if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                if (e.getClickedBlock().getType() == Material.DRAGON_EGG) {
                    Player player = (Player) e.getPlayer();
                    ExperienceOrb exp = e.getClickedBlock().getWorld().spawn(e.getClickedBlock().getLocation(), ExperienceOrb.class);
                    exp.setExperience(1);
                    if (e.getClickedBlock().hasMetadata("broken")) {
                        return;
                    }
                    Random random = new Random();
                    Integer breaks = random.nextInt(1000) + 1;
                    if (breaks == 185) {
                        e.getClickedBlock().setMetadata("broken", new FixedMetadataValue(HCF.getPlugin(), "broken"));
                        if (HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId()) != null) {
                            HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId()).broadcast(ChatColor.YELLOW + "Your ender-dragon egg has been broken. It will " + ChatColor.RED + "no longer " + ChatColor.YELLOW + "drop items.");
                        } else {
                            player.sendMessage(ChatColor.YELLOW + "Your ender-dragon egg has been broken. It will " + ChatColor.RED + "no longer " + ChatColor.YELLOW + "drop items.");
                        }
                        for (Entity nearby : player.getNearbyEntities(20, 350, 20)) {
                            if (!(nearby instanceof Player)) {
                                continue;
                            }
                            ((Player) nearby).sendMessage(ChatColor.YELLOW + "Your ender-dragon egg has been broken. It will " + ChatColor.RED + "no longer " + ChatColor.YELLOW + "drop items.");
                            ((Player) nearby).playSound(e.getClickedBlock().getLocation(), Sound.ANVIL_BREAK, 10, 10);
                        }

                    }
                    Integer rand = random.nextInt(100) + 1;
                    Integer gunpowder = random.nextInt(8) + 1;
                    Integer enderpearl = random.nextInt(4) + 1;
                    if (rand == 15) {
                        e.getClickedBlock().getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.SULPHUR, gunpowder));
                    }
                    if (rand == 17) {
                        e.getClickedBlock().getWorld().dropItemNaturally(e.getClickedBlock().getLocation(), new ItemStack(Material.ENDER_PEARL, enderpearl));
                    }
                }
            }
        }
    }
    @EventHandler
    public void onEntityCreatePortal(final EntityCreatePortalEvent event) {
        if (event.getEntity() instanceof Item && event.getPortalType() == PortalType.ENDER) {
            event.getBlocks().clear();
        }
    }

    @EventHandler
    public void onEnderDragonSpawn(final CreatureSpawnEvent event){
        if(event.getEntity().getType() == EntityType.ENDER_DRAGON){
            ((EnderDragon)event.getEntity()).setCustomName(ChatColor.YELLOW + "Ender Dragon " + ChatColor.RED.toString() + ChatColor.BOLD + Math.round(((Damageable)((EnderDragon) event.getEntity())).getHealth()) + " \u2764");
        }
    }
    @EventHandler
    public void onEntityDamage(final EntityDamageEvent event) {
        if (event.getEntity() instanceof EnderDragon && event.getEntity().getWorld().getEnvironment() == World.Environment.THE_END) {
            ((EnderDragon)event.getEntity()).setCustomName(ChatColor.YELLOW + "Ender Dragon " + ChatColor.RED.toString() + ChatColor.BOLD + Math.round(((Damageable)((EnderDragon) event.getEntity())).getHealth()) + " \u2764");
        }
    }
    @EventHandler
    public void onCreatePortal(final EntityCreatePortalEvent event) {
        if (event.getEntity().getType() == EntityType.ENDER_DRAGON) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityExplode(final EntityExplodeEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onEntityPortal(final EntityPortalEvent event) {
        if (event.getEntity() instanceof EnderDragon) {
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onPortal(final PlayerPortalEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) {
            return;
        }
        final Player player = event.getPlayer();
        if (event.getTo().getWorld().getEnvironment() == World.Environment.NORMAL) {
            if (event.getFrom().getWorld().getEntitiesByClass((Class)EnderDragon.class).size() != 0) {
                event.setCancelled(true);
                event.setTo(event.getFrom());
                HCF.getPlugin().getMessage().sendMessage(event.getPlayer(), ChatColor.RED + "You cannot leave the end before the dragon is killed.");
                }
            event.useTravelAgent(false);
            event.setTo(new Location(Bukkit.getWorld("world"), 0, 71, 300.5));
            }
        else if (event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {
            if (HCF.getPlugin().getTimerManager().spawnTagTimer.hasCooldown(player)) {
                event.setCancelled(true);
                HCF.getPlugin().getMessage().sendMessage(event.getPlayer(),ChatColor.RED + "You cannot enter the end while spawn tagged.");
            }

            if (HCF.getPlugin().getTimerManager().pvpProtectionTimer.hasCooldown(player)) {
                event.setCancelled(true);
                HCF.getPlugin().getMessage().sendMessage(event.getPlayer(), ChatColor.RED + "You cannot enter the end while you have pvp protection.");
            }
            if ((HCF.getPlugin().getHcfHandler().isEndEnabled()|| HCF.getPlugin().getEotwHandler().isEndOfTheWorld()) && event.getPlayer().getGameMode() != GameMode.CREATIVE) {
                event.setCancelled(true);
                HCF.getPlugin().getMessage().sendMessage(event.getPlayer(), ChatColor.RED + "The End is currently disabled.");
            }
            event.useTravelAgent(false);
            event.setTo(new Location(Bukkit.getWorld("world_the_end"), 147, 218, -42));
        }
        if (event.getPlayer().hasPotionEffect(PotionEffectType.INCREASE_DAMAGE)) {
            boolean found = false;
            for (final PotionEffect potionEffect : event.getPlayer().getActivePotionEffects()) {
                if (potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                    found = true;
                }
            }
            if (found) {
                event.getPlayer().removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
            }
        }
    }

}
