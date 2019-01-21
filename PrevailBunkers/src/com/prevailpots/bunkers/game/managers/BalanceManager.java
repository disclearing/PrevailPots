package com.prevailpots.bunkers.game.managers;

import java.util.*;
import org.bukkit.entity.*;

import com.prevailpots.bunkers.utils.*;

public class BalanceManager
{
    public HashMap<String, Double> balances;
    public static final String BALANCE_SYMBOL = "$";
    public static final int STARTING_BALANCE = 100;
    
    public BalanceManager() {
        this.balances = new HashMap<String, Double>();
    }
    
    public double getBalance(final Player p) {
        return this.balances.containsKey(p.getName()) ? MathUtils.round(this.balances.get(p.getName()), 2) : 100;
    }
    
    public boolean hasEnoughMoney(final Player p, final double price) {
        return this.getBalance(p) >= price;
    }
    
    public String getBalanceFormatted(final Player p) {
        return "$" + this.getBalance(p);
    }
    
    public void addBalance(final Player p, final double bal) {
        final double oldBal = this.getBalance(p);
        if (this.balances.containsKey(p.getName())) {
            this.balances.remove(p.getName());
        }
        this.balances.put(p.getName(), MathUtils.round(oldBal + bal, 2));
    }
    
    public void removeBalance(final Player p, final double bal) {
        final double oldBal = this.getBalance(p);
        if (oldBal - bal < 0.0) {
            return;
        }
        if (this.balances.containsKey(p.getName())) {
            this.balances.remove(p.getName());
        }
        this.balances.put(p.getName(), MathUtils.round(oldBal - bal, 2));
    }
    
    public void setBalance(final Player p, final double bal) {
        if (bal < 0.0) {
            return;
        }
        if (this.balances.containsKey(p.getName())) {
            this.balances.remove(p.getName());
        }
        this.balances.put(p.getName(), MathUtils.round(bal, 2));
    }
}
