package com.prevailpots.kitmap.faction;

import com.customhcf.util.command.ArgumentExecutor;
import com.customhcf.util.command.CommandArgument;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.faction.argument.*;
import com.prevailpots.kitmap.faction.argument.staff.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class FactionExecutor extends ArgumentExecutor {
    private final CommandArgument helpArgument;

    public FactionExecutor(final HCF plugin) {
        super("faction");
        addArgument(new FactionTPArgument(plugin));
        addArgument(new FactionPunishArgument(plugin));
        addArgument(new FactionLockArgument(plugin));
        addArgument(new FactionManageArgument(plugin));
        addArgument(new FactionAcceptArgument(plugin));
        addArgument(new FactionAllyArgument(plugin));
        addArgument(new FactionChatArgument(plugin));
        addArgument(new FactionChatSpyArgument(plugin));
        addArgument(new FactionClaimArgument(plugin));
        addArgument(new FactionClaimForArgument(plugin));
        addArgument(new FactionClaimsArgument(plugin));
        addArgument(new FactionCoLeaderArgument(plugin));
        addArgument(new FactionClearClaimsArgument(plugin));
        addArgument(new FactionCreateArgument(plugin));
        addArgument(new FactionAnnouncementArgument(plugin));
        addArgument(new FactionDemoteArgument(plugin));
        addArgument(new FactionDisbandArgument(plugin));
        addArgument(new FactionSetDtrRegenArgument(plugin));
        addArgument(new FactionDepositArgument(plugin));
        addArgument(new FactionWithdrawArgument(plugin));
        addArgument(new FactionForceJoinArgument(plugin));
        addArgument(new FactionForceLeaderArgument(plugin));
        addArgument(new FactionForcePromoteArgument(plugin));
        addArgument(this.helpArgument = new FactionHelpArgument(this));
        addArgument(new FactionHomeArgument(this, plugin));
        addArgument(new FactionInviteArgument(plugin));
        addArgument(new FactionInvitesArgument(plugin));
        addArgument(new FactionKickArgument(plugin));
        addArgument(new FactionLeaderArgument(plugin));
        addArgument(new FactionLeaveArgument(plugin));
        addArgument(new FactionListArgument(plugin));
        addArgument(new FactionMapArgument(plugin));
        addArgument(new FactionMessageArgument(plugin));
        addArgument(new FactionOpenArgument(plugin));
        addArgument(new FactionVersionArgument());
        addArgument(new FactionRemoveArgument(plugin));
        addArgument(new FactionRenameArgument(plugin));
        addArgument(new FactionPromoteArgument(plugin));
        addArgument(new FactionSetDtrArgument(plugin));
        addArgument(new FactionSetDeathbanMultiplierArgument(plugin));
        addArgument(new FactionSetHomeArgument(plugin));
        addArgument(new FactionShowArgument(plugin));
        addArgument(new FactionStuckArgument(plugin));
        addArgument(new FactionUnclaimArgument(plugin));
        addArgument(new FactionUnallyArgument(plugin));
        addArgument(new FactionUninviteArgument(plugin));
    }

    public boolean onCommand(final CommandSender sender, final Command command, final String label, final String[] args) {
        if(args.length < 1) {
            this.helpArgument.onCommand(sender, command, label, args);
            return true;
        }
        final CommandArgument argument = this.getArgument(args[0]);
        if(argument != null) {
            final String permission = argument.getPermission();
            if(permission == null || sender.hasPermission(permission)) {
                argument.onCommand(sender, command, label, args);
                return true;
            }
        }
        this.helpArgument.onCommand(sender, command, label, args);
        return true;
    }


}
