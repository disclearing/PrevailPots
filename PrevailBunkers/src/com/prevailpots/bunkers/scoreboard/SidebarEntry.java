package com.prevailpots.bunkers.scoreboard;

public class SidebarEntry
{
    public final String name;
    public String prefix;
    public String suffix;
    
    public SidebarEntry(final String name) {
        this.name = name;
    }
    
    public SidebarEntry(final Object name) {
        this.name = String.valueOf(name);
    }
    
    public SidebarEntry(final String prefix, final String name, final String suffix) {
        this.name = name;
        this.prefix = prefix;
        this.suffix = suffix;
    }
    
    public SidebarEntry(final Object prefix, final Object name, final Object suffix) {
        this(name);
        this.prefix = String.valueOf(prefix);
        this.suffix = String.valueOf(suffix);
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SidebarEntry)) {
            return false;
        }
        final SidebarEntry that = (SidebarEntry)o;
        Label_0054: {
            if (this.name != null) {
                if (this.name.equals(that.name)) {
                    break Label_0054;
                }
            }
            else if (that.name == null) {
                break Label_0054;
            }
            return false;
        }
        if (this.prefix != null) {
            if (this.prefix.equals(that.prefix)) {
                return !((this.suffix != null) ? (!this.suffix.equals(that.suffix)) : (that.suffix != null));
            }
        }
        else if (that.prefix == null) {
            return !((this.suffix != null) ? (!this.suffix.equals(that.suffix)) : (that.suffix != null));
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        int result = (this.name != null) ? this.name.hashCode() : 0;
        result = 31 * result + ((this.prefix != null) ? this.prefix.hashCode() : 0);
        result = 31 * result + ((this.suffix != null) ? this.suffix.hashCode() : 0);
        return result;
    }
}
