package com.loohp.limbo.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.loohp.limbo.Limbo;
import com.loohp.limbo.player.Player;
import com.loohp.limbo.utils.GameMode;

import net.md_5.bungee.api.ChatColor;

public class Defaults implements CommandExecutor, TabCompletor {
	public List<String> tabComplete(CommandSender sender, String[] args) {
	    List<String> tab = new ArrayList<>();
	    switch (args.length) {
	      case 0:
	        if (sender.hasPermission("limboserver.spawn"))
	          tab.add("spawn"); 
	        if (sender.hasPermission("limboserver.kick"))
	          tab.add("kick"); 
	        if (sender.hasPermission("limboserver.stop"))
	          tab.add("stop"); 
	        if (sender.hasPermission("limboserver.say"))
	          tab.add("say"); 
	        if (sender.hasPermission("limboserver.gamemode"))
	          tab.add("gamemode"); 
	        break;
	      case 1:
	        if (sender.hasPermission("limboserver.spawn") && 
	          "spawn".startsWith(args[0].toLowerCase()))
	          tab.add("spawn"); 
	        if (sender.hasPermission("limboserver.kick") && 
	          "kick".startsWith(args[0].toLowerCase()))
	          tab.add("kick"); 
	        if (sender.hasPermission("limboserver.stop") && 
	          "stop".startsWith(args[0].toLowerCase()))
	          tab.add("stop"); 
	        if (sender.hasPermission("limboserver.say") && 
	          "say".startsWith(args[0].toLowerCase()))
	          tab.add("say"); 
	        if (sender.hasPermission("limboserver.gamemode") && 
	          "gamemode".startsWith(args[0].toLowerCase()))
	          tab.add("gamemode"); 
	        break;
	      case 2:
	        if (sender.hasPermission("limboserver.kick") && 
	          args[0].equalsIgnoreCase("kick"))
	          for (Player player : Limbo.getInstance().getPlayers()) {
	            if (player.getName().toLowerCase().startsWith(args[1].toLowerCase()))
	              tab.add(player.getName()); 
	          }  
	        if (sender.hasPermission("limboserver.gamemode") && 
	          args[0].equalsIgnoreCase("gamemode")) {
	          byte b;
	          int i;
	          GameMode[] arrayOfGameMode;
	          for (i = (arrayOfGameMode = GameMode.values()).length, b = 0; b < i; ) {
	            GameMode mode = arrayOfGameMode[b];
	            if (mode.getName().toLowerCase().startsWith(args[1].toLowerCase()))
	              tab.add(mode.getName()); 
	            b++;
	          } 
	        } 
	        break;
	      case 3:
	        if (sender.hasPermission("limboserver.gamemode") && 
	          args[0].equalsIgnoreCase("gamemode"))
	          for (Player player : Limbo.getInstance().getPlayers()) {
	            if (player.getName().toLowerCase().startsWith(args[2].toLowerCase()))
	              tab.add(player.getName()); 
	          }  
	        break;
	    } 
	    return tab;
	  }
	  
	  public void execute(CommandSender sender, String[] args) {
	    if (args.length == 0)
	      return; 
	    if (args[0].equalsIgnoreCase("spawn")) {
	      if (sender.hasPermission("limboserver.spawn")) {
	        if (args.length == 1 && sender instanceof Player) {
	          Player player = (Player)sender;
	          player.teleport(Limbo.getInstance().getServerProperties().getWorldSpawn());
	          player.sendMessage(ChatColor.GOLD + "Teleporting you to spawn!");
	        } else if (args.length == 2) {
	          Player player = Limbo.getInstance().getPlayer(args[1]);
	          if (player != null) {
	            player.teleport(Limbo.getInstance().getServerProperties().getWorldSpawn());
	            sender.sendMessage(ChatColor.GOLD + "Teleporting " + player.getName() + " to spawn!");
	          } else {
	            sender.sendMessage(ChatColor.RED + "Player not found!");
	          } 
	        } else {
	          sender.sendMessage(ChatColor.RED + "Invalid command usage!");
	        } 
	      } else {
	        sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
	      } 
	      return;
	    } 
	    if (args[0].equalsIgnoreCase("stop")) {
	      if (sender.hasPermission("limboserver.stop")) {
	        Limbo.getInstance().stopServer();
	      } else {
	        sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
	      } 
	      return;
	    } 
	    if (args[0].equalsIgnoreCase("kick")) {
	      if (sender.hasPermission("limboserver.kick")) {
	        String reason = "Disconnected!";
	        Player player = (args.length > 1) ? Limbo.getInstance().getPlayer(args[1]) : null;
	        if (player != null) {
	          if (args.length < 2) {
	            player.disconnect();
	          } else {
	            reason = String.join(" ", Arrays.<CharSequence>copyOfRange((CharSequence[])args, 2, args.length));
	            player.disconnect(reason);
	          } 
	          sender.sendMessage(ChatColor.RED + "Kicked the player " + args[1] + " for the reason: " + reason);
	        } else {
	          sender.sendMessage(ChatColor.RED + "Player is not online!");
	        } 
	      } else {
	        sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
	      } 
	      return;
	    } 
	    if (args[0].equalsIgnoreCase("gamemode")) {
	      if (sender.hasPermission("limboserver.gamemode")) {
	        if (args.length > 1) {
	          Player player = (args.length > 2) ? Limbo.getInstance().getPlayer(args[2]) : ((sender instanceof Player) ? (Player)sender : null);
	          if (player != null) {
	            try {
	              player.setGamemode(GameMode.fromId(Integer.parseInt(args[1])));
	            } catch (Exception e) {
	              try {
	                player.setGamemode(GameMode.fromName(args[1]));
	              } catch (Exception e1) {
	                sender.sendMessage(ChatColor.RED + "Invalid usage!");
	                return;
	              } 
	            } 
	            sender.sendMessage(ChatColor.GOLD + "Updated gamemode to " + player.getGamemode().getName());
	          } else {
	            sender.sendMessage(ChatColor.RED + "Player is not online!");
	          } 
	        } else {
	          sender.sendMessage(ChatColor.RED + "Invalid usage!");
	        } 
	      } else {
	        sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
	      } 
	      return;
	    } 
	    if (args[0].equalsIgnoreCase("say")) {
	      if (sender.hasPermission("limboserver.say")) {
	        if (sender instanceof com.loohp.limbo.Console) {
	          if (args.length > 1) {
	            String message = "[Server] " + String.join(" ", Arrays.<CharSequence>copyOfRange((CharSequence[])args, 1, args.length));
	            Limbo.getInstance().getConsole().sendMessage(message);
	            for (Player each : Limbo.getInstance().getPlayers())
	              each.sendMessage(message); 
	          } 
	        } else if (args.length > 1) {
	          String message = "[" + sender.getName() + "] " + String.join(" ", Arrays.<CharSequence>copyOfRange((CharSequence[])args, 1, args.length));
	          Limbo.getInstance().getConsole().sendMessage(message);
	          for (Player each : Limbo.getInstance().getPlayers())
	            each.sendMessage(message); 
	        } 
	      } else {
	        sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
	      } 
	      return;
	    } 
	  }
}
