package com.prevailpots.kitmap.listener;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

import com.prevailpots.kitmap.HCF;

public class ElevatorClass implements Listener {

    HCF plugin;

    public ElevatorClass(HCF plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e){
        if(e.getLine(0).equalsIgnoreCase("[Elevator]") && plugin.getHcfHandler().isElevatorSign()){
            e.setLine(0, ChatColor.GREEN + "[Elevator]");
            e.setLine(1, ChatColor.YELLOW + "UP");
        }
    }

    @EventHandler
    public void onSignElevator(PlayerInteractEvent e) {
        if (e.getClickedBlock() == null) return;
        if (!plugin.getHcfHandler().isElevatorSign()) return;
        final Block block = e.getClickedBlock();
        final BlockState state = block.getState();
        if (state instanceof Sign) {
            final Sign sign = (Sign) state;
            String lineZero = sign.getLine(0);
            if (!ChatColor.stripColor(lineZero).equalsIgnoreCase("[Elevator]")) return;
                e.getPlayer().teleport(teleportSpot(sign.getLocation(), sign.getLocation().getBlockY(), 254));
                return;
        }
    }

    public void tele(Player player, Location from){
            player.teleport(teleportSpot(from, from.getBlockY(), 254));
    }

    @EventHandler
    public void onMinecartElevator(VehicleExitEvent e){
        if(e.getVehicle().getType() == EntityType.MINECART && plugin.getHcfHandler().isElevatorMinecart()){
            if(e.getVehicle().getLocation().getBlock() == null) return;
            if(e.getVehicle().getLocation().add(0, 1, 0).getBlock() == null) return;
            if(e.getVehicle().getLocation().getBlock().getType() == Material.FENCE_GATE && e.getVehicle().getLocation().add(0, 1, 0).getBlock().getType() == Material.FENCE_GATE) {
                if (e.getVehicle().getPassenger() != null) {
                    ((Player) e.getExited()).sendMessage(ChatColor.RED + "This server uses elevator signs! \n Format: (line 1) [Elevator]");
                }
            }
            }
        }

    public Location teleportSpot(final Location loc, final int min, final int max) {
        for (int k = min; k < max; ++k) {
            final Material m1 = new Location(loc.getWorld(), (double)loc.getBlockX(), (double)k, (double)loc.getBlockZ()).getBlock().getType();
            final Material m2 = new Location(loc.getWorld(), (double)loc.getBlockX(), (double)(k + 1), (double)loc.getBlockZ()).getBlock().getType();
            if (m1.equals((Object)Material.AIR) && m2.equals((Object)Material.AIR)) {
                return new Location(loc.getWorld(), (double)loc.getBlockX(), (double)k, (double)loc.getBlockZ());
            }
        }
        return new Location(loc.getWorld(), (double)loc.getBlockX(), (double)loc.getWorld().getHighestBlockYAt(loc.getBlockX(), loc.getBlockZ()), (double)loc.getBlockZ());
    }
}
