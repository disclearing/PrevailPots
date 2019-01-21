package com.prevailpots.hcf.kothgame;

import com.google.common.collect.ImmutableList;
import com.prevailpots.hcf.DateTimeFormats;
import com.prevailpots.hcf.HCF;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public class EventScheduler {
	private final ImmutableList<String> kothNames = ImmutableList.of("Temple", "Ruins", "Driveout");
	private final Map<LocalDateTime, String> scheduleMap = new LinkedHashMap<>();
	private final HCF plugin;

	public EventScheduler(HCF plugin) {
		this.plugin = plugin;
		reloadSchedules();
	}

	private void reloadSchedules() {
		scheduleMap.clear();

		if (kothNames.size() < 2) {
			plugin.getLogger().warning("Less than 2 koths defined");
		} else {
			plugin.getLogger().info("Defining hardcoded schedules");
			LocalDateTime now = LocalDateTime.now(DateTimeFormats.SERVER_ZONE_ID);

			int assigned = 0;
			String lastPicked = null;
			while (scheduleMap.size() < 3) {
				String kothName = kothNames.get(plugin.getRandom().nextInt(kothNames.size()));
				if (lastPicked == null || !kothName.equals(lastPicked)) {
					assigned++;
					lastPicked = kothName;

					int assignedHour;
					if (assigned == 1) {
						assignedHour = 6;
					} else if (assigned == 2) {
						assignedHour = 12;
					} else if (assigned == 3) {
						assignedHour = 18;
					} else {
						// should be impossible
						continue;
					}
					int assignedDay = now.getDayOfMonth();
					if (now.getHour() > assignedHour) {
						assignedDay++;
					}

					if(assignedDay >= 32)
						assignedDay = 1;

					LocalDateTime time = LocalDateTime.of(now.getYear(), now.getMonth(), assignedDay, assignedHour, 0);
					scheduleMap.put(time, kothName);
					System.out.println("Assigning " + kothName + " for " + time.toString());
				}
			}
		}
	}

	public Map<LocalDateTime, String> getScheduleMap() {
		return scheduleMap;
	}
}
