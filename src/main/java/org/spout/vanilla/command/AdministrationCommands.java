/*
 * This file is part of Vanilla (http://www.spout.org/).
 *
 * Vanilla is licensed under the SpoutDev License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the SpoutDev License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the SpoutDev license version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.command;

import java.util.Set;

import org.spout.api.Spout;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.entity.Entity;
import org.spout.api.exception.CommandException;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.player.Player;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.vanilla.VanillaPlugin;
import org.spout.vanilla.entity.living.player.CreativePlayer;
import org.spout.vanilla.entity.living.player.MinecraftPlayer;
import org.spout.vanilla.entity.living.player.SurvivalPlayer;
import org.spout.vanilla.entity.sky.Sky;
import org.spout.vanilla.entity.sky.NormalSky;
import org.spout.vanilla.protocol.msg.StateChangeMessage;
import org.spout.vanilla.world.Weather;

/**
 * Commands to emulate core Minecraft admin functions.
 */
public class AdministrationCommands {
	
	private final VanillaPlugin plugin = VanillaPlugin.getInstance();
	
	@Command(aliases = {"time"}, usage = "[add|set|day|night] [time]", desc = "Set the time of the server", min = 1, max = 2)
	@CommandPermissions("vanilla.command.time")
	public void time(CommandContext args, CommandSource source) throws CommandException {
		if (!(source instanceof Player)) {
			throw new CommandException("You must be a player to set the time!");
		}
		
		Player player = (Player) source;
		Sky sky = plugin.getSky(player.getEntity().getWorld());
		String arg1 = args.getString(0);
		if (args.length() == 1) {
			if (arg1.equalsIgnoreCase("day")) {
				sky.setTime(0);
			} else if (arg1.equalsIgnoreCase("night")) {
				sky.setTime(18000);
			} else {
				throw new CommandException(arg1 + " is not an option! Please use day or night.");
			}
		}
		
		if (args.length() == 2) {
			int time = args.getInteger(1);
			if (arg1.equalsIgnoreCase("set")) {
				sky.setTime(time);
			} else if (arg1.equalsIgnoreCase("add")) {
				time += sky.getTime();
				sky.setTime(time);
			} else {
				throw new CommandException("Options are 'set' or 'add'.");
			}
		}
	}
	
	@Command(aliases = {"gamemode", "gm"}, usage = "[player] <0|1|survival|creative> (0 = SURVIVAL, 1 = CREATIVE)", desc = "Change a player's game mode", min = 1, max = 2)
	@CommandPermissions("vanilla.command.gamemode")
	public void gamemode(CommandContext args, CommandSource source) throws CommandException {

		int index = 0;
		Player player;

		if (args.length() == 2) {
			player = Spout.getGame().getPlayer(args.getString(index++), true);

			if (player == null) {
				throw new CommandException(args.getString(0) + " is not online.");
			}
		} else {
			if (!(source instanceof Player)) {
				throw new CommandException("You must be a player to toggle your game mode.");
			}

			player = (Player)source;
		}

		int mode;

		if (args.isInteger(index)) {
			mode = args.getInteger(index);
		} else if (args.getString(index).equalsIgnoreCase("survival")) {
			mode = 0;
		} else if (args.getString(index).equalsIgnoreCase("creative")) {
			mode = 1;
		} else {
			throw new CommandException("A game mode must be either a number between 1 and 2, 'CREATIVE' or 'SURVIVAL'");
		}

		MinecraftPlayer controller;
		String message;

		switch (mode) {
			case 0:
				controller = new SurvivalPlayer(player);
				message = "SURVIVAL.";
				break;
			case 1:
				controller = new CreativePlayer(player);
				message = "CREATIVE.";
				break;
			default:
				throw new CommandException("A game mode must be either a number between 1 and 2, 'CREATIVE' or 'SURVIVAL'");
		}

		if (player.getEntity().is(controller.getClass())) {
			source.sendMessage(player.getName() + " is already in the choosen game mode.");
			return;
		}

		player.sendMessage("Your game mode has been changed to " + message);
		player.getEntity().setController(controller);
		player.getSession().send(new StateChangeMessage((byte)3, (byte)mode));

		if (!player.equals(source)) {
			source.sendMessage(player.getName() + "'s game mode has been changed to " + message);
		}
	}

	@Command(aliases = "xp", usage = "[player] <amount>", desc = "Give/take experience from a player", max = 2)
	@CommandPermissions("vanilla.command.xp")
	public void xp(CommandContext args, CommandSource source) throws CommandException {
		// If source is player
		if (args.length() == 1) {
			if (source instanceof Player) {
				Player sender = (Player) source;
				int amount = args.getInteger(0);
				source.sendMessage("You have been given " + amount + " xp.");
				// TODO: Give player 'amount' of xp.
			} else {
				throw new CommandException("You must be a player to give yourself xp.");
			}
		}

		// If source is not player
		if (args.length() == 2) {
			Player player = Spout.getGame().getPlayer(args.getString(0), true);
			if (player != null) {
				int amount = args.getInteger(1);
				player.sendMessage("You have been given " + amount + " xp.");
				// TODO: Give player 'amount' of xp.
			} else {
				throw new CommandException(args.getString(0) + " is not online.");
			}
		}
	}
	
	@Command(aliases = "weather", usage = "<0|1|2> (0 = CLEAR, 1 = RAIN, 2 = THUNDERSTORM)", desc = "Changes the weather", min = 1, max = 1)
	@CommandPermissions("vanilla.command.weather")
	public void weather(CommandContext args, CommandSource source) throws CommandException {
		if (!(source instanceof Player)) {
			throw new CommandException("You must be a player to set the time!");
		}
		
		Weather weather;
		
		if (args.isInteger(0)) {
			int mode = args.getInteger(0);
			switch (mode) {
				case 0:
					source.sendMessage("Weather set to CLEAR.");
					weather = Weather.CLEAR;
					break;
				case 1:
					source.sendMessage("Weather set to RAIN.");
					weather = Weather.RAIN;
					break;
				case 2:
					source.sendMessage("Weather set to THUNDERSTORM.");
					weather = Weather.THUNDERSTORM;
					break;
				default:
					throw new CommandException("Weather must be between 0 and 2.");
			}
		} else if (args.getString(0).equalsIgnoreCase("clear")) {
			source.sendMessage("Weather set to CLEAR.");
			weather = Weather.CLEAR;
		} else if (args.getString(0).equalsIgnoreCase("rain")) {
			source.sendMessage("Weather set to RAIN.");
			weather = Weather.RAIN;
		} else if (args.getString(0).equalsIgnoreCase("thunderstorm")) {
			source.sendMessage("Weather set to THUNDERSTORM.");
			weather = Weather.THUNDERSTORM;
		} else {
			throw new CommandException("Weather must be a mode between 0 and 2, 'CLEAR', 'RAIN', or 'THUNDERSTORM'");
		}
		
		Player player = (Player)source;
		
		Sky sky = plugin.getSky(player.getEntity().getWorld());
		sky.setWeather( weather );
	}

	@Command(aliases = "debug", usage = "[type] (/resend /resendall)", desc = "Debug commands", max = 1)
	//@CommandPermissions("vanilla.command.debug")
	public void debug(CommandContext args, CommandSource source) throws CommandException {
		Player player = null;
		if (source instanceof Player) {
			player = (Player)source;
		} else {
			player = Spout.getGame().getPlayer(args.getString(1, ""), true);
			if (player == null) {
				source.sendMessage("Must be a player or send player name in arguments");
				return;
			}
		}
		
		if (args.getString(0, "").contains("resendall")) {
			NetworkSynchronizer network = player.getNetworkSynchronizer();
			Set<Chunk> chunks = network.getActiveChunks();
			for (Chunk c : chunks) {
				network.sendChunk(c);
			}
			source.sendMessage("All chunks resent");
		} else if (args.getString(0, "").contains("resend")) {
			player.getNetworkSynchronizer().sendChunk(player.getEntity().getChunk());
			source.sendMessage("Chunk resent");
		}
	}
}
