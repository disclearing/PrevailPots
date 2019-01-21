package com.prevailpots.kitmap.kothgame.argument.glowstone;

import com.customhcf.util.command.CommandArgument;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.FactionManager;
import com.prevailpots.kitmap.faction.claim.Claim;
import com.prevailpots.kitmap.faction.type.Faction;
import com.prevailpots.kitmap.kothgame.faction.GlowstoneFaction;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;

public class GlowstoneSetAreaArgument extends CommandArgument {
    private final HCF plugin;

    public GlowstoneSetAreaArgument(final HCF plugin) {
        super("setarea", "Sets the capture zone of an event");
        this.plugin = plugin;
        this.permission = "command.game." + this.getName();
    }

    public String getUsage(final String label) {
        return '/' + label + ' ' + this.getName() + "";
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can set KOTH arena capture points");
            return true;
        }
        final WorldEditPlugin worldEdit = this.plugin.getWorldEdit();
        if(worldEdit == null) {
            sender.sendMessage(ChatColor.RED + "WorldEdit must be installed to set KOTH capture points.");
            return true;
        }
        final Selection selection = worldEdit.getSelection((Player) sender);
        if(selection == null) {
            sender.sendMessage(ChatColor.RED + "You must make a WorldEdit selection to do this.");
            return true;
        }
        if(selection.getWidth() < 2 || selection.getLength() < 2) {
            sender.sendMessage(ChatColor.RED + "Capture zones must be at least " + 2 + 'x' + 2 + '.');
            return true;
        }
        final Faction faction = this.plugin.getFactionManager().getFaction("Glowstone");
        if(!(faction instanceof GlowstoneFaction)) {
            sender.sendMessage(ChatColor.RED + "There is not a capturable faction named '" + args[1] + "'.");
            return true;
        }
        final GlowstoneFaction capturableFaction = (GlowstoneFaction) faction;
        final Collection<Claim> claims = capturableFaction.getClaims();
        if(claims.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
            return true;
        }
        final Claim claim = new Claim(faction, selection.getMinimumPoint(), selection.getMaximumPoint());
        final World world = claim.getWorld();
        final int minimumX = claim.getMinimumX();
        final int maximumX = claim.getMaximumX();
        final int minimumZ = claim.getMinimumZ();
        final int maximumZ = claim.getMaximumZ();
        final FactionManager factionManager = this.plugin.getFactionManager();
        for(int x = minimumX; x <= maximumX; ++x) {
            for(int z = minimumZ; z <= maximumZ; ++z) {
                final Faction factionAt = factionManager.getFactionAt(world, x, z);
                if(!factionAt.equals(capturableFaction)) {
                    sender.sendMessage(ChatColor.RED + "Capture zones can only be inside the event claim.");
                    return true;
                }
            }
        }
        capturableFaction.setGlowstoneArea(claim);
        sender.sendMessage(ChatColor.YELLOW + "You set the glowstone area for " + ChatColor.GOLD + "glowstone palace");
        return true;
    }

}