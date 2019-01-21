package com.prevailpots.hcf;

import com.customhcf.util.chat.Text;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

/**
 * Created by TREHOME on 01/28/2016.
 */
public final class Message {
    HCF plugin;
    private final HashMap<UUID, Long> messageDelay = new HashMap<>();


    public Message(HCF plugin){
        this.plugin = plugin;
    }



    public void sendMessage(Player player, String message){
        if(messageDelay.containsKey(player.getUniqueId())){
            if(messageDelay.get(player.getUniqueId()) - System.currentTimeMillis() > 0){
                return;
            }else{
                messageDelay.remove(player.getUniqueId());
            }
        }
        messageDelay.putIfAbsent(player.getUniqueId(), System.currentTimeMillis() + 3000);
        player.sendMessage(message);
    }

    public void sendMessage(Player player, Text text){
        if(messageDelay.containsKey(player.getUniqueId())){
            if(messageDelay.get(player.getUniqueId()) - System.currentTimeMillis() > 0){
                return;
            }else{
                messageDelay.remove(player.getUniqueId());
            }
        }
        messageDelay.putIfAbsent(player.getUniqueId(), System.currentTimeMillis() + 3000);
        text.send(player);
    }

}
