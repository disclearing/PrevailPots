package com.prevailpots.bunkers.scoreboard;

import org.bukkit.entity.*;
import java.util.*;

public interface SidebarProvider
{
    String getTitle();
    
    List<SidebarEntry> getLines(final Player p0);
}
