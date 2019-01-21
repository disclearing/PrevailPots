package com.prevailpots.kitmap.key.type;

import com.customhcf.util.Config;
import com.google.common.base.MoreObjects;
import com.prevailpots.kitmap.HCF;
import com.prevailpots.kitmap.key.EnderChestKey;
import com.prevailpots.kitmap.key.RewardableItemStack;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class CustomKey extends EnderChestKey {

    private Config file;

    public CustomKey(String name) {
        super(name, 3);
        getFile();
        load();
    }



    public Config getFile() {
        return MoreObjects.firstNonNull(file, file = new Config(HCF.getPlugin(), "key_" + this.getName()));
    }



    public void load(){
        List<RewardableItemStack> fakeList = (List<RewardableItemStack>) getFile().getList("items");
        List<RewardableItemStack> itemStacks = MoreObjects.firstNonNull(fakeList, new ArrayList<>());
        if(itemStacks.size() == 0) return;
        for(RewardableItemStack itemStack : itemStacks){
            setupRarity(itemStack.getItemStack(), itemStack.getPercetage());
        }
    }

    public boolean addItem(RewardableItemStack itemStack){
        setupRarity(itemStack.getItemStack(), itemStack.getPercetage());
        ArrayList<RewardableItemStack> fakeList = (ArrayList<RewardableItemStack>) getFile().getList("items");
        List<RewardableItemStack> itemStacks = MoreObjects.firstNonNull(fakeList, new ArrayList<>());
        itemStacks.add(itemStack);
        file.set("items", itemStacks);
        file.save();
        return false;
    }
    @Override
    public ChatColor getColour() {
        return ChatColor.DARK_RED;
    }
    @Override
    public boolean getBroadcastItems() {
        return false;
    }
}
