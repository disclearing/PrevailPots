package com.prevailpots.bunkers.utils;

import org.bukkit.*;
import org.bukkit.entity.*;
import ru.tehkode.permissions.bukkit.*;
import java.util.*;

public class BroadcastUtils
{
    public static void broadcastToPerm(final String msg, final String permission) {
        for (final Player p : Bukkit.getOnlinePlayers() {
            if (PermissionsEx.getUser(p).has(permission) || p.isOp()) {
                p.sendMessage(msg);
    }
}
    }
}