package com.prevailpots.kitmap.fixes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.prevailpots.kitmap.ConfigurationService;

import java.util.Map;

public class PotionLimitListener implements Listener {
    private static final int EMPTY_BREW_TIME = 400;

    public int getMaxLevel(final PotionType type) {
        return ConfigurationService.POTION_LIMITS.getOrDefault(type, type.getMaxLevel());
    }

    @EventHandler
    public void onSplash(PotionSplashEvent e){
            if(e.getEntity() != null) {
                for(PotionEffect effect : e.getEntity().getEffects()){
                    if(effect != null){
                        if(effect.getType() != null){
                            if(effect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                                e.setCancelled(true);
                                ((Player)e.getEntity().getShooter()).getItemInHand().setType(Material.AIR);
                            }
                        }
                    }
                }
            }
    }


    @EventHandler
    public void onConsume(PlayerItemConsumeEvent e) {
        if(e.getItem() != null && e.getItem().getType() == Material.POTION && e.getItem().getDurability() != 0) {
            final Potion potion = Potion.fromItemStack(e.getItem());
            if(potion != null) {

                final PotionType type = potion.getType();
                if(type != null) {
                    if(type == PotionType.STRENGTH){
                        e.setCancelled(true);
                        e.setItem(null);
                    }
                }
            }
        }
    }


    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getInventory().getType() == InventoryType.BREWING){
            if(e.getCurrentItem().getType() == Material.POTION){
                if(!testValidity(new ItemStack[]{e.getCurrentItem()})){
                    e.setCurrentItem(null);
                    return;
                }
            }
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onBrew(final BrewEvent event) {
        if(event.getContents() == null) return;
        for(ItemStack stack : event.getContents().getContents()) {
            if(stack == null) {
                return;

            }
            if(stack.getType() == null){
                return;
            }
            if (stack.getType() == Material.FERMENTED_SPIDER_EYE) {
                event.setCancelled(true);
            }
            if(stack.getType() == Material.BLAZE_POWDER){
                event.setCancelled(true);
            }

        }
    }


    private boolean testValidity(final ItemStack[] contents) {
        for(final ItemStack stack : contents) {
            if(stack != null && stack.getType() == Material.POTION && stack.getDurability() != 0) {
                final Potion potion = Potion.fromItemStack(stack);
                if(potion == null){
                    continue;
                }
                    final PotionType type = potion.getType();
                if(type == null){
                    continue;
                }
                        if(potion.hasExtendedDuration() || potion.getLevel() != 1) {
                            if(potion.getLevel() > this.getMaxLevel(type)) {
                                return false;
                            }
                        }
                        for(Map.Entry<PotionType, Integer> potionType : ConfigurationService.POTION_LIMITS.entrySet()){
                            if(potionType.getKey() == type){
                                if(potion.getLevel() > potionType.getValue()){
                                    return false;
                                }
                            }
                        }
                        if(type == PotionType.STRENGTH){
                            return false;
                        }
                        if(type == PotionType.POISON ){
                                return false;
                        }
                        if(type == PotionType.SLOWNESS){
                                return false;
                        }

            }
            }
        return true;
    }
}
