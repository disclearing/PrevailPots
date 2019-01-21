package com.prevailpots.bunkers.scoreboard;

import net.minecraft.util.gnu.trove.map.hash.*;
import org.apache.commons.lang3.*;
import net.minecraft.util.gnu.trove.procedure.*;
import org.bukkit.scoreboard.*;
import java.util.*;

public class BufferedObjective
{
    private static final int MAX_SIDEBAR_ENTRIES = 32;
    private static final int MAX_NAME_LENGTH = 32;
    private static final int MAX_PREFIX_LENGTH = 32;
    private static final int MAX_SUFFIX_LENGTH = 32;
    private final Scoreboard scoreboard;
    private Set<String> previousLines;
    private TIntObjectHashMap<SidebarEntry> contents;
    private boolean requiresUpdate;
    private String title;
    private Objective current;
    private DisplaySlot displaySlot;
    
    public BufferedObjective(final Scoreboard scoreboard) {
        this.previousLines = new HashSet<String>();
        this.contents = (TIntObjectHashMap<SidebarEntry>)new TIntObjectHashMap();
        this.requiresUpdate = false;
        this.scoreboard = scoreboard;
        this.title = RandomStringUtils.randomAlphabetic(4);
        this.current = scoreboard.registerNewObjective("buffered", "dummy");
    }
    
    public void setTitle(final String title) {
        if (this.title == null || !this.title.equals(title)) {
            this.title = title;
            this.requiresUpdate = true;
        }
    }
    
    public void setDisplaySlot(final DisplaySlot slot) {
        this.displaySlot = slot;
        this.current.setDisplaySlot(slot);
    }
    
    public void setAllLines(final List<SidebarEntry> lines) {
        if (lines.size() != this.contents.size()) {
            this.contents.clear();
            if (lines.isEmpty()) {
                this.requiresUpdate = true;
                return;
            }
        }
        final int size = Math.min(32, lines.size());
        int count = 0;
        for (final SidebarEntry sidebarEntry : lines) {
            this.setLine(size - count++, sidebarEntry);
        }
    }
    
    public void setLine(final int lineNumber, final SidebarEntry sidebarEntry) {
        final SidebarEntry value = (SidebarEntry)this.contents.get(lineNumber);
        if (value == null || value != sidebarEntry) {
            this.contents.put(lineNumber, (SidebarEntry)sidebarEntry);
            this.requiresUpdate = true;
        }
    }
    
    public void flip() {
        if (!this.requiresUpdate) {
            return;
        }
        final Set<String> adding = new HashSet<String>();
        this.contents.forEachEntry((TIntObjectProcedure)new TIntObjectProcedure<SidebarEntry>() {
            public boolean execute(final int i, final SidebarEntry sidebarEntry) {
                String name = sidebarEntry.name;
                if (name.length() > 32) {
                    name = name.substring(0, 32);
                }
                Team team = BufferedObjective.this.scoreboard.getTeam(name);
                if (team == null) {
                    team = BufferedObjective.this.scoreboard.registerNewTeam(name);
                }
                if (sidebarEntry.prefix != null) {
                    team.setPrefix((sidebarEntry.prefix.length() > 32) ? sidebarEntry.prefix.substring(0, 32) : sidebarEntry.prefix);
                }
                if (sidebarEntry.suffix != null) {
                    team.setSuffix((sidebarEntry.suffix.length() > 32) ? sidebarEntry.suffix.substring(0, 32) : sidebarEntry.suffix);
                }
                adding.add(name);
                if (!team.hasEntry(name)) {
                    team.addEntry(name);
                }
                BufferedObjective.this.current.getScore(name).setScore(i);
                return true;
            }
        });
        this.previousLines.removeAll(adding);
        final Iterator<String> iterator = this.previousLines.iterator();
        while (iterator.hasNext()) {
            final String last = iterator.next();
            final Team team = this.scoreboard.getTeam(last);
            if (team != null) {
                team.removeEntry(last);
            }
            this.scoreboard.resetScores(last);
            iterator.remove();
        }
        this.previousLines = adding;
        this.current.setDisplayName(this.title);
        this.requiresUpdate = false;
    }
    
    public void setVisible(final boolean value) {
        if (this.displaySlot != null && !value) {
            this.scoreboard.clearSlot(this.displaySlot);
            this.displaySlot = null;
        }
        else if (this.displaySlot == null && value) {
            this.current.setDisplaySlot(this.displaySlot = DisplaySlot.SIDEBAR);
        }
    }
}
