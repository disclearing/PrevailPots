package com.prevailpots.hcf.mongo;

import com.customhcf.util.Error;
import com.google.common.base.MoreObjects;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.prevailpots.hcf.HCF;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MongoManager {


    private DBCollection players;
    private MongoClient client;
    private DB db;


    public boolean connect(){
        //Connect to the specified ip and port
        //Default is localhost, 27017
        try {
            client = new MongoClient();
        } catch (Exception e) {
            //When you end up here, the server the db is running on could not be found!
            System.out.println("Could not connect to database!");
            HCF.getPlugin().getLogger().log(Level.WARNING, "Database for mongo WILL NOT connect!");
            return false;
        }


        db = client.getDB("HCF");

        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);

        return true;
    }

    public MongoClient getClient() {
        return client;
    }


    public DBCollection getPlayers() {
        return MoreObjects.firstNonNull(players, (players = db.getCollection("players")));
    }

    public DB getDb() {
        return db;
    }
}
