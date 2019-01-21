package com.prevailpots.bunkers.game;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.util.*;
import org.bukkit.event.*;
import org.bukkit.inventory.*;

import java.util.*;
import org.bukkit.scheduler.*;
import org.bukkit.plugin.*;
import org.bukkit.entity.*;
import java.util.concurrent.*;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.entity.*;

import com.prevailpots.bunkers.*;
import com.prevailpots.bunkers.config.*;
import com.prevailpots.bunkers.task.*;
import com.prevailpots.bunkers.utils.*;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.selections.*;
import org.bukkit.event.player.*;
import org.bukkit.event.block.*;

public class GameHandler implements Listener, Runnable
{
    public Map<String, Team> players;
    private List<String> playersDeadOrRespawning;
    private long actualTime;
    private GameState gameState;
    private int lastTeam;
    public long gameStartCountdown;
    private ArrayList<Vector> bloq;
	private Object bloq1;
    
    public GameHandler() {
        this.players = new HashMap<String, Team>();
        this.playersDeadOrRespawning = new ArrayList<String>();
        this.actualTime = 0L;
        this.gameState = GameState.LOBBY;
        this.lastTeam = 0;
        this.gameStartCountdown = 10L;
        this.bloq = new ArrayList<Vector>();
    }
    
    public int teamSize(final Team t) {
        int siz = 0;
        for (final String s : this.players.keySet()) {
            if (this.players.get(s).equals(t)) {
                ++siz;
            }
        }
        return siz;
    }
    
    public Team autoAssign() {
        ++this.lastTeam;
        if (this.lastTeam == 5) {
            this.lastTeam = 1;
        }
        return Team.values()[this.lastTeam - 1];
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void asyncJoin(final AsyncPlayerPreLoginEvent e) {
        if (this.gameState != GameState.LOBBY) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§7[§cUHCBunkers§7] §cThe game has already started!");
        }
        else if (this.players.size() > 16) {
            e.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "§7[§cUHCBunkers§7] §cThe game is full.");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void join(final PlayerJoinEvent e) {
        e.setJoinMessage(null);
        if (this.gameState == GameState.LOBBY) {
            e.getPlayer().teleport(new Location(e.getPlayer().getWorld(), ConfigurationService.getLobbySpawn().getX(), ConfigurationService.getLobbySpawn().getY(), ConfigurationService.getLobbySpawn().getZ()));
            e.getPlayer().setHealth(20.0);
            e.getPlayer().setFoodLevel(20);
            e.getPlayer().getInventory().clear();
            e.getPlayer().getInventory().setHelmet((ItemStack)null);
            e.getPlayer().getInventory().setChestplate((ItemStack)null);
            e.getPlayer().getActivePotionEffects().clear();
            e.getPlayer().getInventory().setLeggings((ItemStack)null);
            e.getPlayer().getInventory().setBoots((ItemStack)null);
            e.getPlayer().setFireTicks(0);
            try {
                e.getPlayer().giveExpLevels(-e.getPlayer().getExpToLevel());
            }
            catch (Exception ex) {}
            players.put(e.getPlayer().getName(), autoAssign());
            Core.getInstance().getItemManager().giveTeamItems(e.getPlayer());
            Bukkit.broadcastMessage("§6§l" + e.getPlayer().getName() + " §ehas joined the game. §c§l(" + Bukkit.getOnlinePlayers().size() + "/16)");
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void leave(final PlayerQuitEvent e) {
        e.setQuitMessage((String)null);
        if (this.gameState == GameState .LOBBY) {
            if (this.players.containsKey(e.getPlayer().getName())) {
                this.players.remove(e.getPlayer().getName());
            }
            if (this.playersDeadOrRespawning.contains(e.getPlayer().getName())) {
                this.playersDeadOrRespawning.remove(e.getPlayer().getName());
            }
            Bukkit.broadcastMessage("§6§l" + e.getPlayer().getName() + " §ehas left the game. §c§l(" + Bukkit.getOnlinePlayers().size() + "/16)");
        }
        else {
            Bukkit.broadcastMessage(String.valueOf(this.players.get(e.getPlayer().getName()).getColor().toString()) + e.getPlayer().getName() + " §7has quit!");
            if (this.players.containsKey(e.getPlayer().getName())) {
                this.players.remove(e.getPlayer().getName());
            }
            if (this.playersDeadOrRespawning.contains(e.getPlayer().getName())) {
                this.playersDeadOrRespawning.remove(e.getPlayer().getName());
            }
            Core.getInstance().getPointManager().removePoints(e.getPlayer(), 5);
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void kill(final PlayerDeathEvent e) {
        e.setDeathMessage(null);
        if (this.gameState != GameState.GAME) {
            return;
        }
        new ItemDropTask(new ArrayList<>(e.getDrops()), e.getEntity().getLocation().clone()).execute();
        e.setDroppedExp(0);
        e.getDrops().clear();
        e.getEntity().setHealth(20.0);
        e.getEntity().setFoodLevel(20);
        e.getEntity().setFireTicks(0);
        final Team t = this.players.get(e.getEntity().getName());
        if (t != null) {
            if (Core.getInstance().getDTRManager().isRaidable(t)) {
                e.getEntity().kickPlayer("§aYour team went §4RAIDABLE§a. §aThanks for playing.");
                return;
            }
            e.getEntity().teleport(new Location(e.getEntity().getWorld(), ConfigurationService.getTeamSpawn(t).getX(), ConfigurationService.getTeamSpawn(t).getY(), ConfigurationService.getTeamSpawn(t).getZ()));
        }
        Core.getInstance().getTeamManager().setDeaths(e.getEntity(), Core.getInstance().getTeamManager().getDeaths(e.getEntity()) + 1);
        ConfigurationService.setStatDeaths(e.getEntity(), ConfigurationService.getStatDeaths(e.getEntity()) + 1);
        this.playersDeadOrRespawning.add(e.getEntity().getName());
        Core.getInstance().getDTRManager().removeDTR(this.players.get(e.getEntity().getName()), 1.0);
        Core.getInstance().getPointManager().removePoints(e.getEntity(), 5);
        new BukkitRunnable() {
            public void run() {
                if (GameHandler.this.playersDeadOrRespawning.contains(e.getEntity().getName())) {
                    GameHandler.this.playersDeadOrRespawning.remove(e.getEntity().getName());
                }
            }
        }.runTaskLater((Plugin)Core.getInstance(), 100L);
        Core.getInstance().getFreezeManager().addFrozen(e.getEntity(), 5, new PlayerItemTask(e.getEntity()));
        if (e.getEntity().getKiller() == null || !(e.getEntity().getKiller() instanceof Player)) {
            e.setDeathMessage(String.valueOf(this.players.get(e.getEntity().getName()).getColor().toString()) + e.getEntity().getName() + "§7[" + Core.getInstance().getDTRManager().getDTRFormatted(this.players.get(e.getEntity().getName())) + "§7]" + " §edied" + "§e, " + this.players.get(e.getEntity().getName()).getColor().toString() + StringUtils.capitalize(this.players.get(e.getEntity().getName()).toString().toLowerCase()) + "§e now has §6" + Core.getInstance().getTeamManager().getTeamPoints(this.players.get(e.getEntity().getName())) + " §epoints.");
            return;
        }
        if (e.getEntity().getKiller() != null) {
            Core.getInstance().getTeamManager().setKills(e.getEntity(), Core.getInstance().getTeamManager().getKills(e.getEntity().getKiller()) + 1);
            ConfigurationService.setStatKills(e.getEntity().getKiller(), ConfigurationService.getStatKills(e.getEntity().getKiller()) + 1);
        }
        Core.getInstance().getPointManager().addPoints(e.getEntity().getKiller(), 5);
        if (e.getEntity().getKiller() != null) {
            e.setDeathMessage(String.valueOf(this.players.get(e.getEntity().getName()).getColor().toString()) + e.getEntity().getName() + "§7[" + Core.getInstance().getDTRManager().getDTRFormatted(this.players.get(e.getEntity().getName())) + "§7]" + " §ewas slain by " + this.players.get(e.getEntity().getKiller().getName()).getColor().toString() + e.getEntity().getKiller().getName() + "§7[" + Core.getInstance().getDTRManager().getDTRFormatted(this.players.get(e.getEntity().getKiller().getName())) + "§7]" + "§e, " + this.players.get(e.getEntity().getName()).getColor().toString() + StringUtils.capitalize(this.players.get(e.getEntity().getName()).toString().toLowerCase()) + "§e now has §6" + Core.getInstance().getTeamManager().getTeamPoints(this.players.get(e.getEntity().getName())) + " §epoints.");
        }
    }
    
    public String getTime() {
        final int minutes = (int)(this.actualTime % 3600L / 60L);
        final int seconds = (int)(this.actualTime % 60L);
        return (this.gameState == GameState.LOBBY) ? "Pre Game" : ((this.gameState == GameState.ENDING) ? "Ending" : String.valueOf(String.valueOf(minutes) + "m " + seconds + "s"));
    }
    
    public Map<String, Team> getPlayers() {
        return this.players;
    }
    
    public List<String> getPlayersDeadOrRespawning() {
        return this.playersDeadOrRespawning;
    }
    
    public void endGame(final Team winningTeam) {
        if (winningTeam == null) {
            this.resetGame();
            return;
        }
        Bukkit.broadcastMessage("§7[§cUHCBunkers§7] §aCongratiulations to the " + winningTeam.getColor().toString() + winningTeam.name() + " §ateam for winning!");
        new BukkitRunnable() {
            public void run() {
                Bukkit.broadcastMessage("§7[§cUHCBunkers§7] §aThanks for playing, you will be kicked in §e10 §aseconds.");
            }
        }.runTaskLater((Plugin)Core.getInstance(), 60L);
        new BukkitRunnable() {
            public void run() {
                GameHandler.this.resetGame();
            }
        }.runTaskLater((Plugin)Core.getInstance(), 200L);
    }
    
    private void resetGame() {
        this.gameState = GameState.LOBBY;
        for (final Player p : Bukkit.getOnlinePlayers()) {
            p.kickPlayer("§aThank you for playing!");
        }
        Bukkit.getServer().reload();
    }
    
    public void startGame() {
        this.gameState = GameState.GAME;
        for (final String s : this.players.keySet()) {
            final Player p = Bukkit.getPlayer(s);
            if (p != null) {
                p.setHealth(20.0);
                p.setFoodLevel(20);
                p.getInventory().clear();
                p.getInventory().setHelmet((ItemStack)null);
                p.setFireTicks(0);
                p.getInventory().setChestplate((ItemStack)null);
                p.getInventory().setLeggings((ItemStack)null);
                p.getActivePotionEffects().clear();
                try {
                    p.giveExpLevels(-p.getExpToLevel());
                }
                catch (Exception ex) {}
                p.getInventory().setBoots((ItemStack)null);
                p.closeInventory();
                p.teleport(new Location(p.getWorld(), ThreadLocalRandom.current().nextDouble(ConfigurationService.getTeamSpawn(this.players.get(s)).getX(), ConfigurationService.getTeamSpawn(this.players.get(s)).getX() + 6.0), ConfigurationService.getTeamSpawn(this.players.get(s)).getY(), ThreadLocalRandom.current().nextDouble(ConfigurationService.getTeamSpawn(this.players.get(s)).getZ(), ConfigurationService.getTeamSpawn(this.players.get(s)).getZ() + 6.0)));
                Core.getInstance().getFreezeManager().addFrozen(p, 5, new PlayerItemTask(p));
            }
        }
    }
    
    @EventHandler
    public void onAttack(final EntityDamageByEntityEvent e) {
        if (this.gameState == GameState.LOBBY) {
            if (e.getDamager() != null && e.getDamager() instanceof Player) {
                e.setCancelled(true);
            }
            if (e.getEntity() != null && e.getEntity() instanceof Player) {
                e.setCancelled(true);
            }
        }
        
        if(this.gameState == GameState.GAME) {
        	if(e.getDamager() instanceof Player && e.getEntity() instanceof Player) {
        		Player damager = (Player)e.getDamager();
        		Player damaged = (Player)e.getEntity();
        		
        		if(players.get(damager.getName()) == players.get(damaged.getName())) {
        			e.setCancelled(true);
        		}
        	}
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.CREATIVE && player.hasPermission("bunkers.mine")) {
            final ItemStack stack = player.getItemInHand();
            if (stack != null && stack.getType() != Material.AIR && !stack.containsEnchantment(Enchantment.SILK_TOUCH)) {
                final Block block = event.getBlock();
                Material dropType = null;
                switch (block.getType()) {
                    case IRON_ORE: {
                        dropType = Material.IRON_INGOT;
                        break;
                    }
                    case GOLD_ORE: {
                        dropType = Material.GOLD_INGOT;
                        break;
                    }
                    case DIAMOND_ORE: {
                        dropType = Material.DIAMOND;
                        break;
                    }
                    default: {
                        return;
                    }
                }
            }
        }
    }
        
    

    
    @EventHandler
    public void ores(final BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }
        if (e.getBlock().getType().equals((Object)Material.GOLD_ORE) && !e.getPlayer().getItemInHand().getType().equals((Object)Material.IRON_PICKAXE)) {
            e.setCancelled(true);
            e.getBlock().setType(Material.COBBLESTONE);
            final org.bukkit.util.Vector v = e.getBlock().getLocation().toVector();
            if (!this.bloq.contains(v)) {
                this.bloq.addAll((Collection<? extends Vector>) v);
            }
            new BukkitRunnable() {
                public void run() {
                    e.getBlock().setType(Material.GOLD_ORE);
                    if (GameHandler.this.bloq.contains(e.getBlock().getLocation().toVector())) {
                        GameHandler.this.bloq.remove(e.getBlock().getLocation().toVector());
                    }
                    ParticleEffect.PORTAL.display(0.0f, 0.0f, 0.0f, 1.0f, 10, e.getBlock().getLocation(), 10.0);
                }
            }.runTaskLater((Plugin)Core.getInstance(), 100L);
            e.getPlayer().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.GOLD_INGOT));
            e.getPlayer().giveExp(2);
        }
        else if (e.getBlock().getType().equals((Object)Material.IRON_ORE) && !e.getPlayer().getItemInHand().getType().equals((Object)Material.IRON_PICKAXE)) {
            e.setCancelled(true);
            e.getBlock().setType(Material.IRON_ORE);
            final org.bukkit.util.Vector v = e.getBlock().getLocation().toVector();
            if (!this.bloq.contains(v)) {
                this.bloq.addAll((Collection<? extends Vector>) v);
            }
            new BukkitRunnable() {
                public void run() {
                    e.getBlock().setType(Material.IRON_ORE);
                    if (GameHandler.this.bloq.contains(e.getBlock().getLocation().toVector())) {
                        GameHandler.this.bloq.remove(e.getBlock().getLocation().toVector());
                    }
                    ParticleEffect.PORTAL.display(0.0f, 0.0f, 0.0f, 1.0f, 10, e.getBlock().getLocation(), 10.0);
                }
            }.runTaskLater((Plugin)Core.getInstance(), 100L);
            e.getPlayer().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.IRON_INGOT));
            e.getPlayer().giveExp(2);
        }
        else if (e.getBlock().getType().equals((Object)Material.DIAMOND_ORE) &&!e.getPlayer().getItemInHand().getType().equals((Object)Material.IRON_PICKAXE)) {
            e.setCancelled(true);
            e.getBlock().setType(Material.GOLD_ORE);
            final org.bukkit.util.Vector v = e.getBlock().getLocation().toVector();
            if (!this.bloq.contains(v)) {
                this.bloq.addAll((Collection<? extends Vector>) v);
            }
            new BukkitRunnable() {
                public void run() {
                    e.getBlock().setType(Material.DIAMOND_ORE);
                    if (GameHandler.this.bloq.contains(e.getBlock().getLocation().toVector())) {
                        GameHandler.this.bloq.remove(e.getBlock().getLocation().toVector());
                    }
                    ParticleEffect.PORTAL.display(0.0f, 0.0f, 0.0f, 1.0f, 10, e.getBlock().getLocation(), 10.0);
                }
            }.runTaskLater((Plugin)Core.getInstance(), 100L);
            e.getPlayer().getWorld().dropItem(e.getBlock().getLocation(), new ItemStack(Material.DIAMOND));
            e.getPlayer().giveExp(2);
        }
    }
    
    @EventHandler
    public void hunger(final FoodLevelChangeEvent e) {
        if (this.gameState == GameState.LOBBY) {
            e.setCancelled(true);
            e.setFoodLevel(20);
        }
    }
    
    @EventHandler
    public void move(final PlayerMoveEvent e) {
        if (this.gameState == GameState.GAME) {
            Team[] values;
            for (int length = (values = Team.values()).length, i = 0; i < length; ++i) {
                final Team t = values[i];
                final CuboidSelection sel = RegionUtils.getSelectionFromRegion(e.getPlayer().getWorld(), t);
                if (sel.contains(e.getTo()) && !sel.contains(e.getFrom())) {
                    //e.getPlayer().sendMessage("§eNow entering the territory of " + t.getColor().toString() + t.name() + "§e.");
                    e.getPlayer().sendMessage("§eNow entering the territory of " + t.getColor().toString() + t.name() + "§e.");
                    return;
                }
                if (!sel.contains(e.getTo()) && sel.contains(e.getFrom())) {
                    e.getPlayer().sendMessage("§eNow leaving the territory of " + t.getColor().toString() + t.name() + "§e.");
                    return;
                }
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void place(final BlockPlaceEvent e) {
        if (this.gameState != GameState.GAME) {
            if (e.getPlayer() != null) {
                e.setCancelled(true);
            }
        }
        else {
            final Player placer = e.getPlayer();
            final Team team = this.players.get(placer.getName());
            if (team == null) {
                return;
            }
            final CuboidSelection sel = RegionUtils.getSelectionFromRegion(e.getBlockPlaced().getWorld(), team);
            if (!sel.contains(e.getBlockPlaced().getLocation())) {
            	if(!Core.getInstance().getDTRManager().isRaidable(team)) {
	                e.setCancelled(true);
	                Team[] values; 
	                for (int length = (values = Team.values()).length, i = 0; i < length; ++i) {
	                    final Team t = values[i];
	                    final CuboidSelection sels = RegionUtils.getSelectionFromRegion(e.getBlockPlaced().getWorld(), t);
	                    if (sels.contains(e.getBlockPlaced().getLocation())) {
	                        e.getPlayer().sendMessage("§eYou cannot place this in the territory of " + t.getColor().toString() + t.name() + "§e.");
	                        return;
	                    }
	                }
	                e.getPlayer().sendMessage("§eYou cannot place this here!");
            	}
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void breaak(final BlockBreakEvent e) {
        if (e.getBlock() != null && this.bloq.contains(e.getBlock().getLocation().toVector())) {
            e.setCancelled(true);
            return;
        }
        if (this.gameState != GameState.GAME) {
            if (e.getPlayer() != null) {
                e.setCancelled(true);
            }
        }
        else {
            final Player breaker = e.getPlayer();
            final Team team = this.players.get(breaker.getName());
            if (team == null) {
                return;
            }
            final CuboidSelection sel = RegionUtils.getSelectionFromRegion(e.getBlock().getWorld(), team);
            if (!sel.contains(e.getBlock().getLocation())) {
            	if(!Core.getInstance().getDTRManager().isRaidable(team)) {
	                e.setCancelled(true);
	                Team[] values;
	                for (int length = (values = Team.values()).length, i = 0; i < length; ++i) {
	                    final Team t = values[i];
	                    final CuboidSelection sels = RegionUtils.getSelectionFromRegion(e.getBlock().getWorld(), t);
	                    if (sels.contains(e.getBlock().getLocation())) {
	                        e.getPlayer().sendMessage("§eYou cannot break this in the territory of " + t.getColor().toString() + t.name() + "§e.");
	                        return;
	                    }
	                }
	                e.getPlayer().sendMessage("§eYou cannot break this here!");
            	}
            }
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void bloks(final PlayerInteractEvent e) {
        if (this.gameState == GameState.GAME) {
        	   final Team t = this.players.get(e.getPlayer().getName());
        	if (t != null) {
                if (Core.getInstance().getDTRManager().isRaidable(t)) {
                	e.setCancelled(true);
                }
        	if (!e.getAction().equals((Object)Action.RIGHT_CLICK_BLOCK)) {
                return;
            }
            if (e.getClickedBlock() != null && e.getClickedBlock().getType() != null && ItemUtil.isInteractable(e.getClickedBlock().getType())) {
                final Player abuser = e.getPlayer();
                final Team team = this.players.get(abuser.getName());
                if (team == null) {
                    return;
                }
                final CuboidSelection sel = RegionUtils.getSelectionFromRegion(e.getClickedBlock().getWorld(), team);
                if (!Core.getInstance().getDTRManager().isRaidable(team) && !sel.contains(e.getClickedBlock().getLocation())) {
                    e.setCancelled(true);
                    Team[] values;
                    for (int length = (values = Team.values()).length, i = 0; i < length; ++i) {
                        final Team t1 = values[i];
                        final CuboidSelection sels = RegionUtils.getSelectionFromRegion(e.getClickedBlock().getWorld(), t1);
                        if (sels.contains(e.getClickedBlock().getLocation())) {
                            e.getPlayer().sendMessage("§eYou cannot use this in the territory of " + t1.getColor().toString() + t1.name() + "§e.");
                            return;
                        }
                    }
                    e.getPlayer().sendMessage("§eYou cannot use this here!");
                }
            }
        }
    }
    }
    
    public void run() {
        this.update();
    }
    
    public GameState getGameState() {
        return this.gameState;
    }
    
    private Team getBestTeamToWin() {
        final double[] points = new double[4];
        int i = 0;
        Team[] values;
        for (int length = (values = Team.values()).length, j = 0; j < length; ++j) {
            final Team t = values[j];
            points[i] = Core.getInstance().getTeamManager().getTeamPoints(t);
            ++i;
        }
        return Team.values()[MathUtils.indexOfMax(points)];
    }
    
    private void checkEndGame() {
        if (this.players.size() == 0) {
            this.endGame(null);
            return;
        }
        final int minutes = (int)(this.actualTime % 3600L / 60L);
        if (minutes >= 35) {
            this.endGame(this.getBestTeamToWin());
            return;
        }
        final ArrayList<Team> raidable = new ArrayList<Team>();
        Team[] values;
        for (int length = (values = Team.values()).length, j = 0; j < length; ++j) {
            final Team t = values[j];
            if (Core.getInstance().getDTRManager().isRaidable(t)) {
                raidable.add(t);
            }
        }
        if (raidable.size() != 0 && raidable.size() >= 3) {
            Team[] values2;
            for (int length2 = (values2 = Team.values()).length, k = 0; k < length2; ++k) {
                final Team t = values2[k];
                if (!raidable.contains(t)) {
                    this.endGame(t);
                    return;
                }
            }
        }
        final ArrayList<Team> tied = new ArrayList<Team>();
        Team[] values3;
        for (int length3 = (values3 = Team.values()).length, l = 0; l < length3; ++l) {
            final Team t2 = values3[l];
            if (Core.getInstance().getTeamManager().getTeamPoints(t2) >= 125.0) {
                tied.add(t2);
            }
        }
        if (tied.size() != 0) {
            if (tied.size() == 1) {
                this.endGame(tied.get(0));
            }
            else {
                final double[] leastDeaths = new double[tied.size()];
                int i = 0;
                for (final Team t3 : tied) {
                    leastDeaths[i] = Core.getInstance().getTeamManager().getTotalDeaths(t3);
                    ++i;
                }
                Team winningTeam = tied.get(MathUtils.indexOfMin(leastDeaths));
                if (winningTeam == null) {
                    winningTeam = tied.get(Core.RANDOM.nextInt(tied.size()));
                }
                this.endGame(winningTeam);
            }
        }
    }
    
    private void update() {
        if (this.gameState == GameState.LOBBY && this.players.size() >= 4) {
            if (this.gameStartCountdown <= 0L) {
                this.startGame();
            }
            else {
                --this.gameStartCountdown;
            }
        }
        if (this.gameState == GameState.GAME) {
            this.checkEndGame();
            ++this.actualTime;
            if (this.actualTime % 2L == 0L) {
                for (final String s : this.players.keySet()) {
                    if (!this.playersDeadOrRespawning.contains(s)) {
                        final Player p = Bukkit.getPlayer(s);
                        if (p == null) {
                            continue;
                        }
                        Core.getInstance().getBalanceManager().addBalance(p, 0.55);
                    }
                }
            }
        }
    }
}
