package com.prevailpots.bunkers.world;

import org.bukkit.*;
import java.util.*;
import java.io.*;

public class WorldManager
{
    public void unloadWorld(final World world) {
        if (!world.equals(null)) {
            Bukkit.getServer().unloadWorld(world, true);
        }
    }
    
    public boolean deleteWorld(final File path) {
        if (path.exists()) {
            final File[] files = path.listFiles();
            for (int i = 0; i < files.length; ++i) {
                if (files[i].isDirectory()) {
                    this.deleteWorld(files[i]);
                }
                else {
                    files[i].delete();
                }
            }
        }
        return path.delete();
    }
    
    public void copyWorld(final File source, final File target) {
        try {
            final ArrayList<String> ignore = new ArrayList<String>(Arrays.asList("uid.dat", "session.lock"));
            if (!ignore.contains(source.getName())) {
                if (source.isDirectory()) {
                    if (!target.exists()) {
                        target.mkdirs();
                    }
                    final String[] files = source.list();
                    String[] array;
                    for (int length2 = (array = files).length, i = 0; i < length2; ++i) {
                        final String file = array[i];
                        final File srcFile = new File(source, file);
                        final File destFile = new File(target, file);
                        this.copyWorld(srcFile, destFile);
                    }
                }
                else {
                    final InputStream in = new FileInputStream(source);
                    final OutputStream out = new FileOutputStream(target);
                    final byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                    in.close();
                    out.close();
                }
            }
        }
        catch (IOException ex) {}
    }
}
