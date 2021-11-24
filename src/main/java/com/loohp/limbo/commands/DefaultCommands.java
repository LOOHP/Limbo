package com.loohp.limbo.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.loohp.limbo.Console;
import com.loohp.limbo.Limbo;
import com.loohp.limbo.player.Player;
import com.loohp.limbo.utils.GameMode;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.TranslatableComponent;

public class DefaultCommands implements CommandExecutor, TabCompletor {
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			return;
		}
		if (args[0].equalsIgnoreCase("spawn")) {
			if (sender.hasPermission("limboserver.spawn")) {
				if (args.length == 1 && sender instanceof Player) {
					Player player = (Player) sender;
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
				BaseComponent reason = new TranslatableComponent("multiplayer.disconnect.kicked");
				boolean customReason = false;
				if (args.length > 1) {
					Player player = Limbo.getInstance().getPlayer(args[1]);
					if (player != null) {
						if (args.length >= 2) {
							String reasonRaw = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
							if (reasonRaw.trim().length() > 0) {
								reason = new TextComponent(reasonRaw);
								customReason = true;
							}
						}
						player.disconnect(reason);
						if (customReason) {
							sender.sendMessage(ChatColor.RED + "Kicked the player " + player.getName() + " for the reason: " + reason.toLegacyText());
						} else {
							sender.sendMessage(ChatColor.RED + "Kicked the player " + player.getName());
						}
					} else {
						sender.sendMessage(ChatColor.RED + "Player is not online!");
					}
				} else {
					sender.sendMessage(ChatColor.RED + "You have to specifiy a player!");
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
			}
			return;
		}
		
		if (args[0].equalsIgnoreCase("gamemode")) {
			if (sender.hasPermission("limboserver.gamemode")) {
				if (args.length > 1) {
					Player player = args.length > 2 ? Limbo.getInstance().getPlayer(args[2]) : (sender instanceof Player ? (Player) sender : null);
					if (!(sender instanceof Player)) {
						sender.sendMessage(ChatColor.RED + "You have to specifiy a player!");
					} else if (player != null) {
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
				if (sender instanceof Console) {
					if (args.length > 1) {
						String message = "[Server] " + String.join(" ", Arrays.copyOfRange(args, 1, args.length));
						Limbo.getInstance().getConsole().sendMessage(message);
						for (Player each : Limbo.getInstance().getPlayers()) {
							each.sendMessage(message);
						}
					}
				} else {
					if (args.length > 1) {
						String message = "[" + sender.getName() + "] " + String.join(" ", Arrays.copyOfRange(args, 1, args.length));
						Limbo.getInstance().getConsole().sendMessage(message);
						for (Player each : Limbo.getInstance().getPlayers()) {
							each.sendMessage(message);
						}
					}
				}
			} else {
				sender.sendMessage(ChatColor.RED + "You do not have permission to use that command!");
			}
			return;
		}
	}
	
	@Override
	public List<String> tabComplete(CommandSender sender, String[] args) {
		List<String> tab = new ArrayList<>();
		switch (args.length) {
		case 0:
			if (sender.hasPermission("limboserver.spawn")) {
				tab.add("spawn");
			}
			if (sender.hasPermission("limboserver.kick")) {
				tab.add("kick");
			}
			if (sender.hasPermission("limboserver.stop")) {
				tab.add("stop");
			}
			if (sender.hasPermission("limboserver.say")) {
				tab.add("say");
			}
			if (sender.hasPermission("limboserver.gamemode")) {
				tab.add("gamemode");
			}
			break;
		case 1:
			if (sender.hasPermission("limboserver.spawn")) {
				if ("spawn".startsWith(args[0].toLowerCase())) {
					tab.add("spawn");
				}
			}
			if (sender.hasPermission("limboserver.kick")) {
				if ("kick".startsWith(args[0].toLowerCase())) {
					tab.add("kick");
				}
			}
			if (sender.hasPermission("limboserver.stop")) {
				if ("stop".startsWith(args[0].toLowerCase())) {
					tab.add("stop");
				}
			}
			if (sender.hasPermission("limboserver.say")) {
				if ("say".startsWith(args[0].toLowerCase())) {
					tab.add("say");
				}
			}
			if (sender.hasPermission("limboserver.gamemode")) {
				if ("gamemode".startsWith(args[0].toLowerCase())) {
					tab.add("gamemode");
				}
			}
			break;
		case 2:
			if (sender.hasPermission("limboserver.kick")) {
				if (args[0].equalsIgnoreCase("kick")) {
					for (Player player : Limbo.getInstance().getPlayers()) {
						if (player.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
							tab.add(player.getName());
						}
					}
				}
			}
			if (sender.hasPermission("limboserver.gamemode")) {
				if (args[0].equalsIgnoreCase("gamemode")) {
					for (GameMode mode : GameMode.values()) {
						if (mode.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
							tab.add(mode.getName());
						}
					}
				}
			}
			break;
		case 3:
			if (sender.hasPermission("limboserver.gamemode")) {
				if (args[0].equalsIgnoreCase("gamemode")) {
					for (Player player : Limbo.getInstance().getPlayers()) {
						if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
							tab.add(player.getName());
						}
					}
				}
			}
			break;
		}
		return tab;
	}

}
