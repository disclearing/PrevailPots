package com.prevailpots.kitmap.faction.struct;

public enum Role {
    LEADER("Leader", "**"),
    COLEADER("Co-Leader", "*-"),
    CAPTAIN("Captain", "*"),
    MEMBER("Member", "");

    private final String name;
    private final String astrix;

    Role(final String name, final String astrix) {
        this.name = name;
        this.astrix = astrix;
    }

    public String getName() {
        return this.name;
    }

    public String getAstrix() {
        return this.astrix;
    }
}
