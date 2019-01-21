package com.prevailpots.hcf.listener;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import com.customhcf.util.chat.ClickAction;
import com.customhcf.util.chat.Text;
import com.google.common.base.Optional;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.claim.Claim;
import com.prevailpots.hcf.faction.event.FactionCreateEvent;
import com.prevailpots.hcf.faction.event.FactionRenameEvent;
import com.prevailpots.hcf.faction.event.PlayerClaimEnterEvent;
import com.prevailpots.hcf.faction.event.PlayerJoinFactionEvent;
import com.prevailpots.hcf.faction.event.PlayerLeaveFactionEvent;
import com.prevailpots.hcf.faction.event.PlayerLeftFactionEvent;
import com.prevailpots.hcf.faction.struct.RegenStatus;
import com.prevailpots.hcf.faction.type.Faction;
import com.prevailpots.hcf.faction.type.PlayerFaction;
import com.prevailpots.hcf.kothgame.faction.KothFaction;

public class FactionListener implements Listener {
    private static final long FACTION_JOIN_WAIT_MILLIS;
    private static final String FACTION_JOIN_WAIT_WORDS;
    private static final String LAND_CHANGED_META_KEY = "landChangedMessage";
    private static final long LAND_CHANGE_MSG_THRESHOLD = 225L;

    static {
        FACTION_JOIN_WAIT_MILLIS = TimeUnit.SECONDS.toMillis(30L);
        FACTION_JOIN_WAIT_WORDS = DurationFormatUtils.formatDurationWords(FACTION_JOIN_WAIT_MILLIS, true, true);
    }

    private final HCF plugin;

    public FactionListener(HCF plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onSignChange(SignChangeEvent e){
        if(e.getLine(0).equalsIgnoreCase("[KOTH]")){
            if(plugin.getFactionManager().getFaction(e.getLine(1)) instanceof KothFaction){
                KothFaction kothFaction = ((KothFaction) plugin.getFactionManager().getFaction(e.getLine(1)));
                e.setLine(0, org.bukkit.ChatColor.LIGHT_PURPLE + "KOTH");
                e.setLine(1, org.bukkit.ChatColor.GOLD + kothFaction.getName());
                for(final Claim claim : kothFaction.getClaims()) {
                    final Location location = claim.getCenter();
                    e.setLine(2, org.bukkit.ChatColor.RED.toString()  + location.getBlockX() + " | " + location.getBlockZ() );
                }
                e.setLine(3, org.bukkit.ChatColor.RED + kothFaction.getCaptureZone().getDefaultCaptureWords() );
            }
        }
    }
    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onFactionCreate(FactionCreateEvent event) {
        final Faction faction = event.getFaction();
        if(faction instanceof PlayerFaction) {
            final CommandSender sender = event.getSender();
            String name = sender.getName();
            if(sender instanceof Player){
                name = ((Player) sender).getDisplayName();
            }
            Bukkit.broadcastMessage(ChatColor.YELLOW + "Faction " + event.getFaction().getDisplayName(sender) + ChatColor.YELLOW +  " has been " + ChatColor.GREEN + "created" + ChatColor.YELLOW + " by " + ChatColor.YELLOW + name);
        }

    }



    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onFactionRename(final FactionRenameEvent event) {
        final Faction faction = event.getFaction();
        if(faction instanceof PlayerFaction) {
            ((PlayerFaction) faction).broadcast(ChatColor.YELLOW + "Your faction has been renamed to"+ChatColor.GRAY+": " + ChatColor.GREEN + event.getNewName());
        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onFactionRenameMonitor(FactionRenameEvent event) {
        Faction faction = event.getFaction();
        if(faction instanceof KothFaction) {
            ((KothFaction) faction).getCaptureZone().setName(event.getNewName());
        }

    }

    private long getLastLandChangedMeta(Player player) {
        long millis = System.currentTimeMillis();
        long remaining = 0L;
        if (remaining <= 0L) { // update the metadata.
            player.setMetadata(LAND_CHANGED_META_KEY, new FixedMetadataValue(plugin, millis + LAND_CHANGE_MSG_THRESHOLD));
        }

        return remaining;
    }


    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    private void onPlayerClaimEnter(PlayerClaimEnterEvent event) {
        Faction toFaction = event.getToFaction();
        Player player;
        if(toFaction.isSafezone()) {
            player = event.getPlayer();
            player.setHealth(((Damageable)player).getMaxHealth());
            player.setFoodLevel(20);
            player.setFireTicks(0);
            player.setSaturation(4.0F);
        }

        player = event.getPlayer();
        if(this.getLastLandChangedMeta(player) <= 0L) {
            Faction fromFaction = event.getFromFaction();
            Text text1 = new Text("Now leaving: ").setColor(ChatColor.YELLOW);
            text1.append(new Text(fromFaction.getDisplayName(player)).setClick(ClickAction.RUN_COMMAND, "/faction who " + fromFaction.getName()).setHoverText(ChatColor.GRAY + "Click to view information.")).setColor(ChatColor.YELLOW);
            text1.append(new Text(" (" + (fromFaction.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban") + ChatColor.YELLOW + ')')).setColor(ChatColor.YELLOW).send(player);
            Text text = new Text("Now entering: ").setColor(ChatColor.YELLOW);
            text.append(new Text(toFaction.getDisplayName(player)).setClick(ClickAction.RUN_COMMAND, "/faction who " + toFaction.getName()).setHoverText(ChatColor.GRAY + "Click to view information.")).setColor(ChatColor.YELLOW);
            text.append(" (" + (toFaction.isDeathban() ? ChatColor.RED + "Deathban" : ChatColor.GREEN + "Non-Deathban") + ChatColor.YELLOW + ')').setColor(ChatColor.YELLOW).send(player);
        }
    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onPlayerLeftFaction(PlayerLeftFactionEvent event) {
       Optional<Player> optionalPlayer = event.getPlayer();
        if(optionalPlayer.isPresent()) {
            this.plugin.getUserManager().getUser((optionalPlayer.get()).getUniqueId()).setLastFactionLeaveMillis(System.currentTimeMillis());
        }

    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGH
    )
    public void onPlayerPreFactionJoin(PlayerJoinFactionEvent event) {
        Faction faction = event.getFaction();
        Optional optionalPlayer = event.getPlayer();
        if(faction instanceof PlayerFaction && optionalPlayer.isPresent()) {
            Player player = (Player) optionalPlayer.get();
            PlayerFaction playerFaction = (PlayerFaction) faction;
            if(!this.plugin.getEotwHandler().isEndOfTheWorld() && playerFaction.getRegenStatus() == RegenStatus.PAUSED) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions that are not regenerating DTR.");
                return;
            }

            long difference = this.plugin.getUserManager().getUser(player.getUniqueId()).getLastFactionLeaveMillis() - System.currentTimeMillis() + FACTION_JOIN_WAIT_MILLIS;
            if(difference > 0L && !player.hasPermission("command.faction.argument.staff.forcejoin")) {
                event.setCancelled(true);
                player.sendMessage(ChatColor.RED + "You cannot join factions after just leaving within " + FACTION_JOIN_WAIT_WORDS + ". " + "You gotta wait another " + DurationFormatUtils.formatDurationWords(difference, true, true) + '.');
            }
        }

    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.HIGH
    )
    public void onFactionLeave(PlayerLeaveFactionEvent event) {
        Faction faction = event.getFaction();
        if(faction instanceof PlayerFaction) {
            Optional optional = event.getPlayer();
            if(optional.isPresent()) {
                Player player = (Player) optional.get();
                if(this.plugin.getFactionManager().getFactionAt(player.getLocation()).equals(faction)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "You cannot leave your faction whilst you remain in its\' territory.");
                }
            }
        }

    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if(playerFaction != null) {
            playerFaction.printDetails(player);
            playerFaction.broadcast(ChatColor.GREEN + "Member Online: " + ChatColor.GREEN + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.', new UUID[]{player.getUniqueId()});
        }

    }

    @EventHandler(
            ignoreCancelled = true,
            priority = EventPriority.MONITOR
    )
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerFaction playerFaction = this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId());
        if(playerFaction != null) {
            playerFaction.broadcast(ChatColor.RED + "Member Offline: " + ChatColor.RED + playerFaction.getMember(player).getRole().getAstrix() + player.getName() + ChatColor.GOLD + '.');
        }

    }
}
