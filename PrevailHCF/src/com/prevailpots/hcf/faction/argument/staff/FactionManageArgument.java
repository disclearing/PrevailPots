package com.prevailpots.hcf.faction.argument.staff;

import com.customhcf.base.BasePlugin;
import com.customhcf.util.ItemBuilder;
import com.customhcf.util.Menu;
import com.customhcf.util.command.CommandArgument;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.type.Faction;
import com.prevailpots.hcf.faction.type.PlayerFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.conversations.*;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class FactionManageArgument extends CommandArgument {
    private final HCF plugin;

    public FactionManageArgument(final HCF plugin) {
        super("manage", "Gui Manager for Factions");
        this.plugin = plugin;
        this.permission = "command.faction." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + "  <factionName>";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: " + this.getUsage(label));
            return true;
        }
        final Player player = (Player) sender;
        final Faction faction = this.plugin.getFactionManager().getContainingFaction(args[1]);
        if(faction == null) {
            sender.sendMessage(ChatColor.RED + "Faction named or containing member with IGN or UUID " + args[1] + " not found.");
            return true;
        }
        if(!(faction instanceof PlayerFaction)) {
            sender.sendMessage(ChatColor.RED + "You can only punish player factions.");
            return true;
        }
        final PlayerFaction fac = (PlayerFaction) faction;
        Menu menu = new Menu(fac.getName(), 3);

        menu.fill(new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 15).displayName("").build());

        menu.setItem(10, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 14).displayName(ChatColor.RED + ChatColor.BOLD.toString() + "Punish Faction").build());

        menu.setItem(16, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 11).displayName(ChatColor.BLUE + ChatColor.BOLD.toString() + "Manage Faction").build());

        menu.runWhenEmpty(false);

        menu.setGlobalAction((player1, inventory, itemStack, slot, inventoryAction) -> {
            switch (slot) {
                case 10: {
                    player1.closeInventory();
                    final Conversable conversable = (Conversable) sender;
                    conversable.beginConversation(new ConversationFactory(plugin).withFirstPrompt(new FactionPrompt(player, fac)).withLocalEcho(true).buildConversation(conversable));
                    break;
                }
                case 16: {
                    Menu manage = new Menu(fac.getName(), 3);

                    manage.fill(new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 15).displayName("").build());

                    manage.setItem(9, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 13).displayName(ChatColor.GREEN + ChatColor.BOLD.toString() + "Join Faction").build());

                    manage.setItem(11, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 14).displayName(ChatColor.RED + ChatColor.BOLD.toString() + "Remove Faction").build());

                    manage.setItem(13, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 0).displayName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Spy on faction chat").build());

                    manage.setItem(15, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 6).displayName(ChatColor.LIGHT_PURPLE + ChatColor.BOLD.toString() + "Start DTR Regen").build());

                    manage.setItem(17, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 11).displayName(ChatColor.BLUE + ChatColor.BOLD.toString() + "Manage DTR").build());

                    manage.setItem(18, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 14).displayName(ChatColor.RED + ChatColor.BOLD.toString() + "Back").build());

                    manage.runWhenEmpty(false);

                    manage.setGlobalAction((player2, inventory1, itemStack1, s, inventoryAction1) -> {
                        switch (s) {
                            case 9: {
                                if (plugin.getFactionManager().getFaction(player2.getUniqueId()) != null) {
                                    if (plugin.getFactionManager().getPlayerFaction(player2.getUniqueId()).getLeader().equals(plugin.getFactionManager().getPlayerFaction(player2.getUniqueId()).getMember(player2))) {
                                        this.plugin.getFactionManager().removeFaction(plugin.getFactionManager().getPlayerFaction(player2.getUniqueId()), sender);
                                    }
                                    plugin.getFactionManager().getPlayerFaction(player2.getUniqueId()).setMember(player2, null);
                                }
                                Bukkit.dispatchCommand(player2, "faction forcejoin " + fac.getName());
                                break;
                            }
                            case 11: {
                                Bukkit.dispatchCommand(player2, "faction forcedisband " + fac.getName());
                                break;
                            }
                            case 13: {
                                Bukkit.dispatchCommand(player2, "faction chatspy add " + fac.getName());
                                break;
                            }
                            case 15: {
                                Bukkit.dispatchCommand(player2, "faction setdtrregen " + fac.getName() + " 0s");
                                break;
                            }
                            case 17: {
                                Menu dtr = new Menu("Manage DTR", 2);

                                dtr.fill(new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 15).displayName("").build());

                                manage.setItem(9, new ItemBuilder(Material.STAINED_GLASS_PANE).data((short) 14).displayName(ChatColor.RED + ChatColor.BOLD.toString() + "Back").build());



                                dtr.runWhenEmpty(false);

                                dtr.setGlobalAction((player3, inventory2, itemStack2, ss, inventoryAction2) -> {
                                    switch (ss) {
                                        case 1: {
                                            Bukkit.dispatchCommand(player3, "faction setdtr " +  (fac.getDeathsUntilRaidable() -3 ));
                                            break;
                                        }
                                        case 2: {
                                            Bukkit.dispatchCommand(player3, "faction setdtr " +  (fac.getDeathsUntilRaidable() -2 ));
                                            break;
                                        }
                                        case 3 : {
                                            Bukkit.dispatchCommand(player3, "faction setdtr " +  (fac.getDeathsUntilRaidable() -1 ));
                                            break;
                                        }
                                        case 5 : {
                                            Bukkit.dispatchCommand(player3, "faction setdtr " +  (fac.getDeathsUntilRaidable() +1 ));
                                            break;
                                        }
                                        case 6 : {
                                            Bukkit.dispatchCommand(player3, "faction setdtr " +  (fac.getDeathsUntilRaidable() +2 ));
                                            break;
                                        }
                                        case 7 : {
                                            Bukkit.dispatchCommand(player3, "faction setdtr " +  (fac.getDeathsUntilRaidable() +3 ));
                                            break;
                                        }
                                        case 9: {
                                            player2.closeInventory();
                                            manage.showMenu(player2);
                                            break;
                                        }
                                    }
                                });
                                player2.closeInventory();
                                dtr.showMenu(player2);
                                break;
                            }
                            case 18: {
                                player2.closeInventory();
                                menu.showMenu(player2);
                                break;
                            }
                        }
                    });
                    manage.showMenu(player);

                    break;
                }
            }
        });
        menu.showMenu(player);
        return true;
    }
    private static final class FactionPrompt extends StringPrompt {

        private Player player;
        private PlayerFaction faction;

        public FactionPrompt(Player player, PlayerFaction faction){
            this.player = player;
            this.faction = faction;
        }

        public String getPromptText(final ConversationContext context) {
            return ChatColor.YELLOW + "What reason would you like for the punishment to be?";
        }

        public Prompt acceptInput(final ConversationContext context, final String string) {
            Bukkit.dispatchCommand(player, "faction punish " + faction.getName() + " " + string);
            return Prompt.END_OF_CONVERSATION;
        }
    }

    public List<String> onTabComplete(final CommandSender sender, final Command command, final String label, final String[] args) {
        return (args.length == 2) ? null : Collections.emptyList();
    }
}
