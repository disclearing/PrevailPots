package com.prevailpots.hcf.balance;

import com.customhcf.base.BasePlugin;
import com.customhcf.util.ItemBuilder;
import com.google.common.base.Enums;
import com.google.common.base.Optional;
import com.customhcf.util.InventoryUtils;
import com.google.common.primitives.Ints;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.crowbar.Crowbar;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Pattern;

public class ShopSignListener implements Listener {
    private static final long SIGN_TEXT_REVERT_TICKS = 100L;
    private static final Pattern ALPHANUMERIC_REMOVER;

    static {
        ALPHANUMERIC_REMOVER = Pattern.compile("[^A-Za-z0-9]");
    }

    private final HCF plugin;

    public ShopSignListener(final HCF plugin) {
        this.plugin = plugin;
    }

    /*
        &9- Sell -
        amount
        item name
        $price
     */
     /*
        &9- Buy -
        amount
        item name
        $price
     */

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGH)
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            final Block block = event.getClickedBlock();
            final BlockState state = block.getState();
            if(state instanceof Sign) {
                final Sign sign = (Sign) state;
                final String[] lines = sign.getLines();
                final Integer quantity = Ints.tryParse(lines[1]);
                if(quantity == null) {
                    return;
                }
                final Integer price = Ints.tryParse(ShopSignListener.ALPHANUMERIC_REMOVER.matcher(lines[3]).replaceAll(""));
                if(price == null) {
                    return;
                }

                ItemStack stack;
                if(lines[2].equalsIgnoreCase("Crowbar")) {
                    stack = new Crowbar().getItemIfPresent();
                } else if(((stack = BasePlugin.getPlugin().getItemDb().getItem(ShopSignListener.ALPHANUMERIC_REMOVER.matcher(lines[2].replaceAll(" ", "_")).replaceAll(""), (int) quantity)) == null) && !lines[2].contains("s_")) {
                    return;
                }

                final Player player = event.getPlayer();

                //skeleton zombie spider
                if(lines[2].startsWith("s_")) {
                    String s = lines[2].replace("s_", "");
                    final Optional<EntityType> optionalType = Enums.getIfPresent(EntityType.class, s.toUpperCase());
                    if (!optionalType.isPresent()) {
                        player.sendMessage(ChatColor.RED + "Not an entity named '" + s + "'.");
                        return;
                    }
                    player.sendMessage(".");
                    stack = new ItemBuilder(Material.MOB_SPAWNER).displayName(net.md_5.bungee.api.ChatColor.YELLOW + "Spawner").lore(net.md_5.bungee.api.ChatColor.BLUE + optionalType.get().getName()).build();
                }

                final String[] fakeLines = Arrays.copyOf(sign.getLines(), 4);
                if(lines[0].equalsIgnoreCase(ChatColor.BLUE + "- Sell -")) {
                    final int sellQuantity = Math.min(quantity, InventoryUtils.countAmount((Inventory) player.getInventory(), stack.getType(), stack.getDurability()));
                    if(sellQuantity <= 0) {
                        fakeLines[1] = ChatColor.RED + "Not enough";
                        fakeLines[3] = ChatColor.RED + "to sell.";
                    } else {
                        final int newPrice = price / quantity * sellQuantity;
                        fakeLines[0] =  "Sold " + ChatColor.GREEN + sellQuantity;
                        fakeLines[1] = BasePlugin.getPlugin().getItemDb().getName(stack);
                        fakeLines[2] = ChatColor.GREEN + "for " + EconomyManager.ECONOMY_SYMBOL + newPrice;
                        fakeLines[3] = "New Balance: " + ChatColor.GREEN + plugin.getEconomyManager().getBalance(player.getUniqueId());
                        this.plugin.getEconomyManager().addBalance(player.getUniqueId(), newPrice);
                        InventoryUtils.removeItem((Inventory) player.getInventory(), stack.getType(), (short) stack.getData().getData(), sellQuantity);
                        player.updateInventory();
                    }
                } else {
                    if(!lines[0].contains("- Buy -")) {
                        return;
                    }
                    if(price > this.plugin.getEconomyManager().getBalance(player.getUniqueId())) {
                        fakeLines[0] = ChatColor.RED + "Insufficient";
                        fakeLines[1] = ChatColor.RED + "Funds for";
                    } else {

                        fakeLines[0] = ChatColor.GREEN + "Bought";
                        fakeLines[3] = ChatColor.GREEN + "for " +  EconomyManager.ECONOMY_SYMBOL + price;
                        this.plugin.getEconomyManager().subtractBalance(player.getUniqueId(), price);
                        final World world = player.getWorld();
                        final Location location = player.getLocation();
                        final Map<Integer, ItemStack> excess = (Map<Integer, ItemStack>) player.getInventory().addItem(new ItemStack[]{stack});

                        for(final Map.Entry<Integer, ItemStack> excessItemStack : excess.entrySet()) {
                            world.dropItemNaturally(location, (ItemStack) excessItemStack.getValue());
                        }
                        player.setItemInHand(player.getItemInHand());
                    }
                }
                event.setCancelled(true);
                BasePlugin.getPlugin().getSignHandler().showLines(player, sign, fakeLines, SIGN_TEXT_REVERT_TICKS, true);
            }
        }
    }
}
