package com.prevailpots.hcf.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.prevailpots.hcf.HCF;

import java.util.UUID;

public class MongoUser  {

    private static HCF plugin = HCF.getPlugin();


    public static void updatePlayer(UUID uuid, String key, Object toUpdate) {
        //Lets build a minimal object to get all objects in the
        //collection "players" containing the field
        //"uuid" with the value uuid (what we are
        //searching for)
        DBObject r = new BasicDBObject("uuid", uuid);
        //Use findOne to only get one object!
        DBObject found = plugin.getMongoManager().getPlayers().findOne(r);
        if (found == null){
            storePlayer(uuid);
            return;
        }
        //The user was found! Lets create a new replacement object!
        DBObject obj = new BasicDBObject("uuid", uuid);
        obj.put(key, toUpdate);

        //And update it! This simply replace our found object!
        plugin.getMongoManager().getPlayers().update(found, obj);
    }


    public static DBObject storePlayer(UUID uuid){
        //Lets store our first player!
        //This player has never played before and we just want to create a object for him
        DBObject obj = new BasicDBObject("uuid", uuid);
        if(plugin.getMongoManager().getPlayers().findOne(obj) != null){
            return null;
        }

        plugin.getMongoManager().getPlayers().insert(obj);
        return obj;
    }
}
