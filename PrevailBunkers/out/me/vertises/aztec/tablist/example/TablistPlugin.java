package me.vertises.aztec.tablist.example;

import java.util.concurrent.TimeUnit;

import org.bukkit.plugin.java.JavaPlugin;

import me.vertises.aztec.tablist.TablistManager;

public class TablistPlugin extends JavaPlugin {
	
	@Override
	public void onEnable() {
		new TablistManager(this, new ExampleSupplier(), TimeUnit.SECONDS.toMillis(5l));
	}

}
