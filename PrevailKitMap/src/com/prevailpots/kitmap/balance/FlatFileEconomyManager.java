package com.prevailpots.kitmap.balance;

import com.customhcf.util.Config;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.mongo.MongoUser;

import net.minecraft.util.gnu.trove.map.TObjectIntMap;
import net.minecraft.util.gnu.trove.map.hash.TObjectIntHashMap;

import java.util.*;

public class FlatFileEconomyManager implements EconomyManager {
    private final HCF plugin;
    private TObjectIntMap<UUID> balanceMap;
    private Config balanceConfig;

    public FlatFileEconomyManager(final HCF plugin) {
        this.balanceMap = new TObjectIntHashMap(10, 0.5f, 0);
        this.plugin = plugin;
        this.reloadEconomyData();
    }

    @Override
    public TObjectIntMap<UUID> getBalanceMap() {
        return this.balanceMap;
    }

    @Override
    public Integer getBalance(final UUID uuid) {
        balanceMap.putIfAbsent(uuid, 0);
        return this.balanceMap.get(uuid);
    }

    @Override
    public Integer setBalance(final UUID uuid, final int amount) {
        this.balanceMap.put(uuid, amount);
        return amount;
    }

    @Override
    public Integer addBalance(final UUID uuid, final int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) + amount);
    }

    @Override
    public Integer subtractBalance(final UUID uuid, final int amount) {
        return this.setBalance(uuid, this.getBalance(uuid) - amount);
    }

    @Override
    public void save(){
        for(UUID entry : balanceMap.keySet()){
            if(entry == null) continue;
            MongoUser.updatePlayer(entry, "balance", balanceMap.get(entry));
        }
    }

    @Override
    public void reloadEconomyData() {
        this.balanceMap = new TObjectIntHashMap(10, 0.5f, 0);
        DBCursor cursor = plugin.getMongoManager().getPlayers().find();
        while (cursor.hasNext()){
            DBObject next = cursor.next();
            if(next.get("uuid") == null || next.get("balance") == null){
                plugin.getMongoManager().getPlayers().remove(next);
                continue;
            }
            balanceMap.put(((UUID)next.get("uuid")), ((Integer) next.get("balance")));
        }
    }

}
