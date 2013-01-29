/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, Spout LLC <http://www.spout.org/>
 * Vanilla is licensed under the Spout License Version 1.
 *
 * Vanilla is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option)
 * any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Vanilla is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for
 * more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://spout.in/licensev1> for the full license, including
 * the MIT license.
 */
package org.spout.vanilla.plugin.command;

import gnu.trove.iterator.TLongIterator;
import gnu.trove.list.linked.TLongLinkedList;

import org.spout.api.Client;
import org.spout.api.Server;
import org.spout.api.Spout;
import org.spout.api.chat.ChatArguments;
import org.spout.api.chat.style.ChatStyle;
import org.spout.api.command.CommandContext;
import org.spout.api.command.CommandSource;
import org.spout.api.command.annotated.Command;
import org.spout.api.command.annotated.CommandPermissions;
import org.spout.api.entity.Player;
import org.spout.api.exception.CommandException;
import org.spout.api.generator.biome.Biome;
import org.spout.api.generator.biome.BiomeGenerator;
import org.spout.api.geo.World;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.Material;
import org.spout.api.scheduler.TaskPriority;
import org.spout.api.util.concurrent.AtomicFloat;

import org.spout.vanilla.api.data.GameMode;
import org.spout.vanilla.api.data.Time;
import org.spout.vanilla.api.data.Weather;
import org.spout.vanilla.api.event.cause.HealthChangeCause;

import org.spout.vanilla.plugin.VanillaPlugin;
import org.spout.vanilla.plugin.component.inventory.PlayerInventory;
import org.spout.vanilla.plugin.component.living.neutral.Human;
import org.spout.vanilla.plugin.component.misc.HealthComponent;
import org.spout.vanilla.plugin.component.misc.LevelComponent;
import org.spout.vanilla.plugin.component.substance.material.CommandBlockComponent;
import org.spout.vanilla.plugin.component.world.VanillaSky;
import org.spout.vanilla.plugin.configuration.OpConfiguration;
import org.spout.vanilla.plugin.configuration.VanillaConfiguration;
import org.spout.vanilla.plugin.material.VanillaMaterials;

public class AdministrationCommands {
	private final VanillaPlugin plugin;
	private final TicksPerSecondMonitor tpsMonitor;

	public AdministrationCommands(VanillaPlugin plugin) {
		this.plugin = plugin;
		tpsMonitor = new TicksPerSecondMonitor();
		plugin.getEngine().getScheduler().scheduleSyncRepeatingTask(plugin, tpsMonitor, 0, 50, TaskPriority.CRITICAL);
	}

	@Command(aliases = "clear", usage = "[player] [id] [data]", desc = "Clears your inventory", min = 0, max = 3)
	@CommandPermissions("vanilla.command.clear")
	public void clear(CommandContext args, CommandSource source) throws CommandException {
		
		Player player;
		Material material = null;
		Integer data = null;
		
		if (args.length() == 0) {
			if (!(source instanceof Player)) {
				throw new CommandException("You must be a player to clear your own inventory.");
			}
			player = (Player) source;
		} else if (args.length() <= 2) {
			if (args.isInteger(0)) {
				if (!(source instanceof Player)) {
					throw new CommandException("You must be a player to clear your own inventory.");
				}
				player = (Player) source;
				material = VanillaMaterials.getMaterial((short) args.getInteger(0));
				if (material == null) {
					throw new CommandException(args.getInteger(0) + " is not a valid item ID.");
				}
				if (args.length() == 2) {
					data = args.getInteger(1);
				}
			} else {
				player = plugin.getEngine().getPlayer(args.getString(0), false);
				if (player == null) {
					if (!(source instanceof Player)) {
						throw new CommandException("You must be a player to clear your own inventory.");
					}
					material = Material.get(args.getString(0));
					if (material == null) {
						throw new CommandException(args.getString(0) + " is not a valid item ID.");
					}
					if (args.length() == 2) {
						data = args.getInteger(1);
					}
				} else if (args.length() == 2) {
					if (args.isInteger(0)) {
						material = VanillaMaterials.getMaterial((short) args.getInteger(0));
					} else {
						material = Material.get(args.getString(0));
					}
					if (material == null) {
						throw new CommandException(args.getString(0) + " is not a valid item ID.");
					}
				}
			}
		} else {
			player = plugin.getEngine().getPlayer(args.getString(0), false);
			if (player == null) {
				throw new CommandException(args.getString(0) + " is not online.");
			}
			
			if (args.isInteger(1)) {
				material = VanillaMaterials.getMaterial((short) args.getInteger(0));
			} else {
				material = Material.get(args.getString(0));
			}
			
			if (material == null) {
				throw new CommandException(args.getString(0) + " is not a valid item ID.");
			}
			
			data = args.getInteger(2);
		}
		
		PlayerInventory inv = ((Player) source).get(PlayerInventory.class);
		if (inv == null) {
			throw new CommandException("Player has no inventory!");
		}
		
		// clearing the inventory and counting the removed items
		int cleared = 0;
		if (material == null) {
			for (int i = 0; i < inv.getMain().size(); i++) {
				ItemStack item = inv.getMain().get(i);
				if (item != null) {
					cleared += item.getAmount();
					inv.getMain().set(i, null);
				}
			}
			
			for (int i = 0; i < inv.getQuickbar().size(); i++) {
				ItemStack item = inv.getQuickbar().get(i);
				if (item != null) {
					cleared += item.getAmount();
					inv.getQuickbar().set(i, null);
				}
			}
		} else if (data == null) {
			for (int i = 0; i < inv.getMain().size(); i++) {
				ItemStack item = inv.getMain().get(i);
				if (item != null && item.isMaterial(material)) {
					cleared += item.getAmount();
					inv.getMain().set(i, null);
				}
			}
			
			for (int i = 0; i < inv.getQuickbar().size(); i++) {
				ItemStack item = inv.getQuickbar().get(i);
				if (item != null && item.isMaterial(material)) {
					cleared += item.getAmount();
					inv.getQuickbar().set(i, null);
				}
			}
		} else {			
			for (int i = 0; i < inv.getMain().size(); i++) {
				ItemStack item = inv.getMain().get(i);
				if (item != null && item.isMaterial(material) && item.getData() == data) {
					cleared += item.getAmount();
					inv.getMain().set(i, null);
				}
			}
			
			for (int i = 0; i < inv.getQuickbar().size(); i++) {
				ItemStack item = inv.getQuickbar().get(i);
				if (item != null && item.isMaterial(material) && item.getData() == data) {
					cleared += item.getAmount();
					inv.getQuickbar().set(i, null);
				}
			}
		}
		
		source.sendMessage(plugin.getPrefix(), ChatStyle.BRIGHT_GREEN, "Cleared the inventory of ", ChatStyle.WHITE, player.getName(), ChatStyle.BRIGHT_GREEN, 
				", removing ", ChatStyle.WHITE, cleared, ChatStyle.BRIGHT_GREEN, " items.");
	}

	@Command(aliases = {"give"}, usage = "[player] <block> [amount] [data]", desc = "Lets a player spawn items", min = 1, max = 4)
	@CommandPermissions("vanilla.command.give")
	public void give(CommandContext args, CommandSource source) throws CommandException {
		
		int index = 0;
		Player player = Spout.getEngine().getPlayer(args.getString(index++), false);		
		if (player == null) {
			if (args.length() == 4) {
				throw new CommandException(args.getString(0) + " is not online.");
			} else if (args.length() > 0) {
				index--;
			}
			
			if (!(source instanceof Player)) {
				throw new CommandException("You must be a player to give yourself materials!");
			}

			player = (Player) source;
		}
		
		Material material;
		if (args.isInteger(index)) {
			material = VanillaMaterials.getMaterial((short) args.getInteger(index++));
		} else {
			material = Material.get(args.getString(index++));
		}
		
		int quantity = args.getInteger(index++, 1);
		int data = args.getInteger(index, 0);
		
		player.get(PlayerInventory.class).add(new ItemStack(material, data, quantity));
		
		source.sendMessage(plugin.getPrefix(), ChatStyle.BRIGHT_GREEN, "Gave ", ChatStyle.WHITE, player.getName() + " ", quantity, ChatStyle.BRIGHT_GREEN, " of ", ChatStyle.WHITE,
				material.getDisplayName());
	}

	@Command(aliases = {"deop"}, usage = "<player>", desc = "Revoke a players operator status", min = 1, max = 1)
	@CommandPermissions("vanilla.command.deop")
	public void deop(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getEngine() instanceof Client) {
			throw new CommandException("You cannot search for players unless you are in server mode.");
		}

		String playerName = args.getString(0);
		OpConfiguration ops = VanillaConfiguration.OPS;
		ops.setOp(playerName, false);
		source.sendMessage(plugin.getPrefix(), playerName, ChatStyle.RED, " had their operator status revoked!");
		Player player = Spout.getEngine().getPlayer(playerName, true);
		if (player != null && !source.equals(player)) {
			player.sendMessage(plugin.getPrefix(), ChatStyle.RED, "You had your operator status revoked!");
		}
	}

	@Command(aliases = {"op"}, usage = "<player>", desc = "Make a player an operator", min = 1, max = 1)
	@CommandPermissions("vanilla.command.op")
	public void op(CommandContext args, CommandSource source) throws CommandException {
		if (Spout.getEngine() instanceof Client) {
			throw new CommandException("You cannot search for players unless you are in server mode.");
		}

		String playerName = args.getString(0);
		OpConfiguration ops = VanillaConfiguration.OPS;
		ops.setOp(playerName, true);
		source.sendMessage(plugin.getPrefix(), ChatStyle.RED, playerName, " is now an operator!");
		Player player = Spout.getEngine().getPlayer(playerName, true);
		if (player != null && !source.equals(player)) {
			player.sendMessage(plugin.getPrefix(), ChatStyle.YELLOW, "You are now an operator!");
		}
	}

	@Command(aliases = {"time"}, usage = "<add|set> <0-24000|day|night|dawn|dusk> [world]", desc = "Set the time of the server", min = 2, max = 3)
	@CommandPermissions("vanilla.command.time")
	public void time(CommandContext args, CommandSource source) throws CommandException {
		long time = 0;
		boolean relative = false;
		if (args.getString(0).equalsIgnoreCase("set")) {
			if (args.isInteger(1)) {
				time = args.getInteger(1);
			} else {
				try {
					time = Time.get(args.getString(1)).getTime();
				} catch (Exception e) {
					throw new CommandException("'" + args.getString(1) + "' is not a valid time.");
				}
			}
		} else if (args.getString(0).equalsIgnoreCase("add")) {
			relative = true;
			if (args.isInteger(1)) {
				time = args.getInteger(1);
			} else {
				throw new CommandException("Argument to 'add' must be an integer.");
			}
		}

		World world;
		if (args.length() == 3) {
			world = plugin.getEngine().getWorld(args.getString(2));
			if (world == null) {
				throw new CommandException("'" + args.getString(2) + "' is not a valid world.");
			}
		} else if (source instanceof Player) {
			Player player = (Player) source;
			world = player.getWorld();
		} else if (source instanceof CommandBlockComponent) {
			CommandBlockComponent commandBlock = (CommandBlockComponent) source;
			world = commandBlock.getBlock().getWorld();
		} else {
			throw new CommandException("You must specify a world.");
		}

		VanillaSky sky = VanillaSky.getSky(world);
		if (sky == null) {
			throw new CommandException("The sky for " + world.getName() + " is not available.");
		}

		sky.setTime(relative ? (sky.getTime() + time) : time);
		if (Spout.getEngine() instanceof Client) {
			source.sendMessage(plugin.getPrefix(), ChatStyle.BRIGHT_GREEN, "You set ", ChatStyle.WHITE, world.getName(), ChatStyle.BRIGHT_GREEN, " to time: ", ChatStyle.WHITE,
					sky.getTime());
		} else {
			((Server) Spout.getEngine()).broadcastMessage(plugin.getPrefix(), ChatStyle.WHITE, world.getName(), ChatStyle.BRIGHT_GREEN, " set to: ", ChatStyle.WHITE, sky.getTime());
		}
	}

	@Command(aliases = {"gamemode", "gm"}, usage = "<0|1|2|survival|creative|adventure|s|c|a> [player] (0 = SURVIVAL, 1 = CREATIVE, 2 = ADVENTURE)", desc = "Change a player's game mode", min = 1, max = 2)
	@CommandPermissions("vanilla.command.gamemode")
	public void gamemode(CommandContext args, CommandSource source) throws CommandException {
		GameMode mode;
		try {
			if (args.isInteger(0)) {
				mode = GameMode.get(args.getInteger(0));
			} else {
				mode = GameMode.get(args.getString(0).replace("c", "creative").replace("s", "survival").replace("a", "adventure"));
			}
		} catch (Exception e) {
			throw new CommandException("A game mode must be either a number between 0 and 2, 'CREATIVE', 'SURVIVAL', 'ADVENTURE', 'c', 's', or 'a'");
		}
		
		Player player;
		if (args.length() == 2) {
			if (Spout.getEngine() instanceof Client) {
				throw new CommandException("You cannot search for players unless you are in server mode.");
			}
			player = Spout.getEngine().getPlayer(args.getString(1), true);
			if (player == null) {
				throw new CommandException(args.getString(1) + " is not online.");
			}
		} else {
			if (!(source instanceof Player)) {
				throw new CommandException("You must be a player to toggle your game mode.");
			}

			player = (Player) source;
		}

		if (!player.has(Human.class)) {
			throw new CommandException("Invalid player!");
		}
		
		player.get(Human.class).setGamemode(mode);

		if (!player.equals(source)) {
			source.sendMessage(plugin.getPrefix(), ChatStyle.WHITE, player.getName(), "'s ", ChatStyle.BRIGHT_GREEN, "gamemode has been changed to ", ChatStyle.WHITE, mode.name(), ChatStyle.BRIGHT_GREEN, ".");
		}
	}

	@Command(aliases = "xp", usage = "[player] <amount>", desc = "Give/take experience from a player", min = 1, max = 2)
	@CommandPermissions("vanilla.command.xp")
	public void xp(CommandContext args, CommandSource source) throws CommandException {
		// If source is player
		if (args.length() == 1) {
			if (source instanceof Player) {
				@SuppressWarnings("unused")
				Player sender = (Player) source;
				int amount = args.getInteger(0);
				source.sendMessage(plugin.getPrefix(), ChatStyle.BRIGHT_GREEN, "You have been given ", ChatStyle.WHITE, amount, ChatStyle.BRIGHT_GREEN, " xp.");
				// TODO: Give player 'amount' of xp.
			} else {
				throw new CommandException("You must be a player to give yourself xp.");
			}
		} else {
			if (Spout.getEngine() instanceof Client) {
				throw new CommandException("You cannot search for players unless you are in server mode.");
			}
			Player player = ((Server) Spout.getEngine()).getPlayer(args.getString(0), true);
			if (player != null) {
				short amount = (short) args.getInteger(1);
				LevelComponent level = player.get(LevelComponent.class);

				if (level == null) {
					return;
				}

				if (amount > 0) {
					level.addExperience(amount);
				} else {
					level.removeExperience(amount);
				}
				player.sendMessage(plugin.getPrefix(), ChatStyle.BRIGHT_GREEN, "Your experience has been set to ", ChatStyle.WHITE, amount, ChatStyle.BRIGHT_GREEN, ".");
			} else {
				throw new CommandException(args.getString(0) + " is not online.");
			}
		}
	}

	@Command(aliases = "weather", usage = "<0|1|2|clear|rain|thunder> (0 = CLEAR, 1 = RAIN/SNOW, 2 = THUNDERSTORM) [world]", desc = "Changes the weather", min = 1, max = 2)
	@CommandPermissions("vanilla.command.weather")
	public void weather(CommandContext args, CommandSource source) throws CommandException {
		World world;
		if (source instanceof Player && args.length() == 1) {
			world = ((Player) source).getWorld();
		} else if (args.length() == 2) {
			world = plugin.getEngine().getWorld(args.getString(1));

			if (world == null) {
				throw new CommandException("Invalid world '" + args.getString(1) + "'.");
			}
		} else {
			throw new CommandException("You need to specify a world.");
		}

		Weather weather;
		try {
			if (args.isInteger(0)) {
				weather = Weather.get(args.getInteger(0));
			} else {
				weather = Weather.get(args.getString(0).replace("snow", "rain").replace("thunder", "thunderstorm"));
			}
		} catch (Exception e) {
			throw new CommandException("Weather must be a mode between 0 and 2, 'CLEAR', 'RAIN', 'SNOW', or 'THUNDERSTORM'");
		}

		VanillaSky sky = VanillaSky.getSky(world);
		if (sky == null) {
			throw new CommandException("The sky of world '" + world.getName() + "' is not availible.");
		}

		sky.setWeather(weather);
		ChatArguments message;

		switch (weather) {
			case RAIN:
				message = new ChatArguments(plugin.getPrefix(), ChatStyle.BRIGHT_GREEN, "Weather set to ", ChatStyle.WHITE, "RAIN/SNOW", ChatStyle.BRIGHT_GREEN, ".");
				break;
			default:
				message = new ChatArguments(plugin.getPrefix(), ChatStyle.BRIGHT_GREEN, "Weather set to ", ChatStyle.WHITE, weather.name(), ChatStyle.BRIGHT_GREEN, ".");
				break;
		}
		if (Spout.getEngine() instanceof Client) {
			source.sendMessage(message);
		} else {
			for (Player player : ((Server) Spout.getEngine()).getOnlinePlayers()) {
				if (player.getWorld().equals(world)) {
					player.sendMessage(message);
				}
			}
		}
	}

	@Command(aliases = {"kill"}, usage = "[player]", desc = "Kill yourself or another player", min = 0, max = 1)
	@CommandPermissions("vanilla.command.kill")
	public void kill(CommandContext args, CommandSource source) throws CommandException {
		if (args.length() == 0) {
			if (!(source instanceof Player)) {
				throw new CommandException("Don't be silly...you cannot kill yourself as the console.");
			}
			((Player) source).get(HealthComponent.class).kill(HealthChangeCause.COMMAND);
		} else {
			if (Spout.getEngine() instanceof Client) {
				throw new CommandException("You cannot search for players unless you are in server mode.");
			}
			Player victim = ((Server) Spout.getEngine()).getPlayer(args.getString(0), true);
			if (victim != null) {
				victim.get(HealthComponent.class).kill(HealthChangeCause.COMMAND);
			}
		}
	}

	@Command(aliases = {"version", "vr"}, usage = "", desc = "Print out the version information for Vanilla", min = 0, max = 0)
	@CommandPermissions("vanilla.command.version")
	public void getVersion(CommandContext args, CommandSource source) {
		source.sendMessage("Vanilla ", plugin.getDescription().getVersion(), " (Implementing Minecraft protocol v", plugin.getDescription().getData("protocol"), ")");
		source.sendMessage("Powered by Spout " + Spout.getEngine().getVersion(), " (Implementing SpoutAPI ", Spout.getAPIVersion(), ")");
	}

	@Command(aliases = {"biome"}, usage = "", desc = "Print out the name of the biome at the current location", min = 0, max = 0)
	@CommandPermissions("vanilla.command.biome")
	public void getBiomeName(CommandContext args, CommandSource source) throws CommandException {
		if (!(source instanceof Player)) {
			throw new CommandException("Only a player may call this command.");
		}
		Player player = (Player) source;
		if (!(player.getTransform().getPosition().getWorld().getGenerator() instanceof BiomeGenerator)) {
			throw new CommandException("This map does not appear to have any biome data.");
		}
		Point pos = player.getTransform().getPosition();
		Biome biome = pos.getWorld().getBiome(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());
		source.sendMessage(plugin.getPrefix(), ChatStyle.BRIGHT_GREEN, "Current biome: ", ChatStyle.WHITE, (biome != null ? biome.getName() : "none"));
	}

	@Command(aliases = {"tps"}, usage = "", desc = "Print out the current engine ticks per second", min = 0, max = 0)
	@CommandPermissions("vanilla.command.tps")
	public void getTPS(CommandContext args, CommandSource source) throws CommandException {
		source.sendMessage("TPS: " + tpsMonitor.getTPS());
		source.sendMessage("Average TPS: " + tpsMonitor.getAvgTPS());
	}

	private static class TicksPerSecondMonitor implements Runnable {
		private static final int MAX_MEASUREMENTS = 20 * 60;
		private TLongLinkedList timings = new TLongLinkedList();
		private long lastTime = System.currentTimeMillis();
		private final AtomicFloat ticksPerSecond = new AtomicFloat(20);
		private final AtomicFloat avgTicksPerSecond = new AtomicFloat(20);

		@Override
		public void run() {
			long time = System.currentTimeMillis();
			timings.add(time - lastTime);
			lastTime = time;
			if (timings.size() > MAX_MEASUREMENTS) {
				timings.removeAt(0);
			}
			final int size = timings.size();
			if (size > 20) {
				TLongIterator i = timings.iterator();
				int count = 0;
				long last20 = 0;
				long total = 0;
				while (i.hasNext()) {
					long next = i.next();
					if (count > size - 20) {
						last20 += next;
					}
					total += next;
					count++;
				}
				ticksPerSecond.set(1000F / (last20 / 20F));
				avgTicksPerSecond.set(1000F / (total / ((float) size)));
			}
		}

		public float getTPS() {
			return ticksPerSecond.get();
		}

		public float getAvgTPS() {
			return avgTicksPerSecond.get();
		}
	}
}
