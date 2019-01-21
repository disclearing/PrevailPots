package com.prevailpots.hcf.kothgame.tracker;

import com.customhcf.util.chat.ClickAction;
import com.customhcf.util.chat.Text;
import com.prevailpots.hcf.DateTimeFormats;
import com.prevailpots.hcf.HCF;
import com.prevailpots.hcf.faction.type.PlayerFaction;
import com.prevailpots.hcf.kothgame.CaptureZone;
import com.prevailpots.hcf.kothgame.EventTimer;
import com.prevailpots.hcf.kothgame.EventType;
import com.prevailpots.hcf.kothgame.faction.EventFaction;
import com.prevailpots.hcf.kothgame.faction.KothFaction;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@Deprecated
public class KothTracker implements EventTracker {
    public static final long DEFAULT_CAP_MILLIS;
    private static final long MINIMUM_CONTROL_TIME_ANNOUNCE;

    static {
        MINIMUM_CONTROL_TIME_ANNOUNCE = TimeUnit.SECONDS.toMillis(25L);
        DEFAULT_CAP_MILLIS = TimeUnit.MINUTES.toMillis(15L);
    }

    private final HCF plugin;

    public KothTracker(final HCF plugin) {
        this.plugin = plugin;
    }

    @Override
    public EventType getEventType() {
        return EventType.KOTH;
    }

    public String correctColor(CaptureZone captureZone){
        if(captureZone.getName().contains("Palace")){
           return ChatColor.GOLD + captureZone.getDisplayName();
        }
        return ChatColor.GOLD + captureZone.getDisplayName();
    }

    @Override
    public void tick(final EventTimer eventTimer, final EventFaction eventFaction) {
        final CaptureZone captureZone = ((KothFaction) eventFaction).getCaptureZone();
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if(remainingMillis <= 0L) {
            this.plugin.getTimerManager().eventTimer.handleWinner(captureZone.getCappingPlayer());
            eventTimer.clearCooldown();
            return;
        }
        if(remainingMillis == captureZone.getDefaultCaptureMillis()) {
            return;
        }
        final int remainingSeconds = (int) (remainingMillis / 1000L);
        if(remainingSeconds > 0 && remainingSeconds % 30 == 0) {
            Bukkit.broadcastMessage(ChatColor.GOLD + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.YELLOW + "Someone is controlling " + correctColor(captureZone) + ChatColor.YELLOW + ". " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(remainingMillis) + ')');
        }
    }

    @Override
    public void onContest(final EventFaction eventFaction, final EventTimer eventTimer) {
        Bukkit.broadcastMessage(ChatColor.GOLD + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.RED + eventFaction.getName() + ChatColor.YELLOW + " can now be contested. " + ChatColor.RED + '(' + DateTimeFormats.KOTH_FORMAT.format(eventTimer.getRemaining()) + ')');
    }

    @Override
    public boolean onControlTake(final Player player, final CaptureZone captureZone) {
        if(this.plugin.getFactionManager().getPlayerFaction(player.getUniqueId()) == null) {
            player.sendMessage(ChatColor.RED + "You must be in a faction to capture a KoTH.");
            return false;
        }
        player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() +  "You are now in control of " +correctColor(captureZone)+ ChatColor.YELLOW.toString() + ChatColor.BOLD.toString() + '.');
        return true;
    }

    @Override
    public boolean onControlLoss(final Player player, final CaptureZone captureZone, final EventFaction eventFaction) {
        player.sendMessage(ChatColor.RED+ ChatColor.BOLD.toString() +  "You no longer in control of " +correctColor(captureZone)+ ChatColor.RED.toString() + ChatColor.BOLD.toString() + '.');
        final long remainingMillis = captureZone.getRemainingCaptureMillis();
        if(remainingMillis > 0L && captureZone.getDefaultCaptureMillis() - remainingMillis > KothTracker.MINIMUM_CONTROL_TIME_ANNOUNCE) {
            PlayerFaction playerFaction = HCF.getPlugin().getFactionManager().getPlayerFaction(player.getUniqueId());
            //// TODO: 12/21/2016 add hovercompent
            for (Player bcall : Bukkit.getOnlinePlayers()) {
                Text text = new Text(ChatColor.GOLD + "[" + eventFaction.getEventType().getDisplayName() + "] ");
                text.append(new Text(((playerFaction == null) ? player.getName() : "A member in " + playerFaction.getName())).setClick(ClickAction.RUN_COMMAND, "/faction who " + playerFaction.getName()).setHoverText(ChatColor.GRAY + "Click to view information.")).setColor(ChatColor.YELLOW);
                text.append(ChatColor.YELLOW + " has lost control of " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.YELLOW + '.' + ChatColor.RED + " (" + DateTimeFormats.KOTH_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')').send(bcall);
                //Bukkit.broadcastMessage(ChatColor.GOLD + "[" + eventFaction.getEventType().getDisplayName() + "] " + ChatColor.RED + ((playerFaction == null) ?  player.getName() :  "A member in " + playerFaction.getName()) + ChatColor.YELLOW + " has lost control of " + ChatColor.RED + captureZone.getDisplayName() + ChatColor.YELLOW + '.' + ChatColor.RED + " (" + DateTimeFormats.KOTH_FORMAT.format(captureZone.getRemainingCaptureMillis()) + ')');
                /**
                 * Wolfy wanted the faction name. Not the player as the player could be singled out.
                 * NOTE: I added a check to the message to see if the player has a faction if not their name will be displayed.
                 * Ex: [Koth] lolitsalex was knocked off Car. (12:45) That would make the player get focused cause they are already low.
                 */
            }
        }
        return true;
    }

    @Override
    public void stopTiming() {
    }
}
