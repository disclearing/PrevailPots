package me.vertises.aztec.tablist;

import org.bukkit.Bukkit;

public class TablistUpdateTask implements Runnable {

	@Override
	public void run() {
		TablistManager manager = TablistManager.INSTANCE;
		if (manager == null) return;
		Bukkit.getOnlinePlayers().forEach(player -> {
			Tablist tablist = manager.getTablist(player);
			if (tablist != null) {
				tablist.hideRealPlayers().update();
			}
		});
	}
	

}
