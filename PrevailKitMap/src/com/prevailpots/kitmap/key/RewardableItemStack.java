package com.prevailpots.kitmap.key;

import com.customhcf.util.GenericUtils;
import com.customhcf.util.cuboid.Cuboid;
import com.google.common.collect.Maps;
import net.minecraft.server.v1_7_R4.Item;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Map;

public class RewardableItemStack implements ConfigurationSerializable {

    private int percetage;
    private ItemStack itemStack;

    public RewardableItemStack(ItemStack itemStack, Integer percetage, Integer amount){
        this.percetage = percetage;
        this.itemStack = itemStack;
        this.itemStack.setAmount(amount);
    }

    public RewardableItemStack(final Map<String, Object> map) {
        this.percetage = (int) map.get("percent");
        this.itemStack = (ItemStack) map.get("itemStack");
    }

    public Map<String, Object> serialize() {
        final Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("percent", percetage);
        map.put("itemStack", itemStack);
        return map;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public int getPercetage() {
        return percetage;
    }

    public void setPercetage(int percetage) {
        this.percetage = percetage;
    }
}
