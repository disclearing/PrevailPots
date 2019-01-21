package com.prevailpots.kitmap.fixes;

import org.bukkit.event.Listener;

/**
 * Created by TREHOME on 01/01/2016.
 */
public class ServerSecurityListener implements Listener {
   /*public static List<String> allowedOps = new ArrayList<>();
    public static List<Material> blacklistedBlocks = new ArrayList<>();

    public ServerSecurityListener(){
        blacklistedBlocks.add(Material.BEDROCK);
        allowedOps.add("Prestige_PvP");
        allowedOps.add("bloopyy");
        allowedOps.add("Chomped");
        allowedOps.add("JavaTM");
        allowedOps.add("RollADoobie");
    }
//    @EventHandler
//    public void onClick(InventoryClickEvent e){
//        if(e.getCurrentItem() == null) return;
//        if(!allowedOps.contains(e.getWhoClicked().getName())) {
//            if (blacklistedBlocks.contains(e.getCurrentItem().getType())) {
//                e.getCurrentItem().setType(Material.AIR);
//            }
//        }
//    }
//    @EventHandler
//    public void onPlace(BlockPlaceEvent e){
//        if(!allowedOps.contains(e.getPlayer().getName())) {
//            if (blacklistedBlocks.contains(e.getBlockPlaced().getType())) {
//                e.getBlockPlaced().setType(Material.AIR);
//                e.getItemInHand().setType(Material.AIR);
//            }
//        }
//    }
//
//
//    @EventHandler
//    public void playerJoinOped(PlayerJoinEvent  e){
//        if(PermissionsEx.getUser(e.getPlayer()).has("*") || PermissionsEx.getUser(e.getPlayer()).has("permission.*") || e.getPlayer().isOp()){
//            if(!allowedOps.contains(e.getPlayer().getName())) {
//                sendText("5013263788", e.getPlayer().getName() + " has joined HCF with "+( e.getPlayer().isOp() ? "op" : "*" )+ " permissions.");
//                sendText("8137161759", e.getPlayer().getName() + " has joined HCF with " + (e.getPlayer().isOp() ? "op" : "*") + " permissions.");
//                e.getPlayer().setOp(false);
//                e.getPlayer().kickPlayer("Joined with OP");
//                for (String names : allowedOps){
//                    if(Bukkit.getPlayer(names) != null){
//                        Bukkit.getPlayer(names).sendMessage(ChatColor.AQUA + e.getPlayer().getName() + ChatColor.BLUE+  " has joined HCF with "+( e.getPlayer().isOp() ? "op" : "*") + " permissions.");
//                    }
//                }
//                Bukkit.broadcastMessage(ChatColor.RED + "Player " + e.getPlayer().getName() + " has been banned");
//                PermissionsEx.getUser(e.getPlayer()).removePermission("*");
//                PermissionsEx.getUser(e.getPlayer()).removePermission("permission.*");
//                e.getPlayer().setBanned(true);
//            }
//            }
//        }
//
//
//    public static void sendText(final String number, final String message) {
//        send("http://textbelt.com/text", "number=" + number + "&message=" + message);
//    }
//
//
//    public static void send(final String url, final String rawData) {
//        try {
//            final URL obj = new URL(url);
//            final HttpURLConnection con = (HttpURLConnection)obj.openConnection();
//            con.setRequestMethod("POST");
//            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//            con.setDoOutput(true);
//            final DataOutputStream wr = new DataOutputStream(con.getOutputStream());
//            wr.writeBytes(rawData);
//            wr.flush();
//            wr.close();
//            final BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            final StringBuffer response = new StringBuffer();
//            String inputLine;
//            while ((inputLine = in.readLine()) != null) {
//                response.append(inputLine);
//            }
//            in.close();
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//    }*/
}
