/*
 * This file is part of Vanilla.
 *
 * Copyright (c) 2011-2012, SpoutDev <http://www.spout.org/>
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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.protocol;

import java.util.Set;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;

import org.spout.api.Spout;
import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.Biome;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Block;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.InventoryBase;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.BlockMaterial;
import org.spout.api.math.Quaternion;
import org.spout.api.player.Player;
import org.spout.api.protocol.EntityProtocol;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.protocol.Session.State;
import org.spout.api.protocol.event.ProtocolEventListener;
import org.spout.api.util.map.TIntPairObjectHashMap;
import org.spout.api.util.set.TIntPairHashSet;

import org.spout.vanilla.VanillaPlugin;
import org.spout.vanilla.controller.living.player.VanillaPlayer;
import org.spout.vanilla.protocol.msg.BlockActionMessage;
import org.spout.vanilla.protocol.msg.BlockChangeMessage;
import org.spout.vanilla.protocol.msg.CompressedChunkMessage;
import org.spout.vanilla.protocol.msg.EntityEquipmentMessage;
import org.spout.vanilla.protocol.msg.KeepAliveMessage;
import org.spout.vanilla.protocol.msg.LoadChunkMessage;
import org.spout.vanilla.protocol.msg.LoginRequestMessage;
import org.spout.vanilla.protocol.msg.PlayEffectMessage;
import org.spout.vanilla.protocol.msg.PlayerPositionLookMessage;
import org.spout.vanilla.protocol.msg.RespawnMessage;
import org.spout.vanilla.protocol.msg.SetWindowSlotMessage;
import org.spout.vanilla.protocol.msg.SetWindowSlotsMessage;
import org.spout.vanilla.protocol.msg.SpawnPositionMessage;
import org.spout.vanilla.world.generator.VanillaBiome;
import org.spout.vanilla.world.generator.flat.FlatGenerator;
import org.spout.vanilla.world.generator.nether.NetherGenerator;
import org.spout.vanilla.world.generator.normal.NormalGenerator;

import static org.spout.vanilla.material.VanillaMaterials.getMinecraftId;

public class VanillaNetworkSynchronizer extends NetworkSynchronizer implements ProtocolEventListener {
	@SuppressWarnings("unused")
	private final static int POSITION_UPDATE_TICKS = 20;
	private final static double STANCE = 1.6D;
	private final static int TIMEOUT = 15000;

	public VanillaNetworkSynchronizer(Player player, Entity entity) {
		super(player, entity);
		registerProtocolEvents(this);
		initChunk(player.getEntity().getPosition());
	}

	private TIntPairObjectHashMap<TIntHashSet> activeChunks = new TIntPairObjectHashMap<TIntHashSet>();
	private TIntPairHashSet biomesSentChunks = new TIntPairHashSet();
	//TODO: track entities as they come into range and untrack entities as they move out of range
	private TIntHashSet activeEntities = new TIntHashSet();
	private final TIntObjectHashMap<Message> queuedInventoryUpdates = new TIntObjectHashMap<Message>();

	@Override
	protected void freeChunk(Point p) {
		int x = (int) p.getX() >> Chunk.CHUNK_SIZE_BITS;
		int y = (int) p.getY() >> Chunk.CHUNK_SIZE_BITS;// + SEALEVEL_CHUNK;
		int z = (int) p.getZ() >> Chunk.CHUNK_SIZE_BITS;

		if (y < 0 || y > p.getWorld().getHeight() >> Chunk.CHUNK_SIZE_BITS) {
			return;
		}

		TIntHashSet column = activeChunks.get(x, z);
		if (column != null) {
			column.remove(y);
			if (column.isEmpty()) {
				biomesSentChunks.remove(x, z);
				activeChunks.remove(x, z);
				LoadChunkMessage unLoadChunk = new LoadChunkMessage(x, z, false);
				owner.getSession().send(unLoadChunk);
			}
		}
	}

	private byte[] getBiomeData(Chunk chunk, int x, int z) {
		byte[] biomeData = new byte[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE];
		final int chunkMask = (Chunk.CHUNK_SIZE - 1);
		for (int dx = x; dx < x + Chunk.CHUNK_SIZE; ++dx) {
			for (int dz = z; dz < z + Chunk.CHUNK_SIZE; ++dz) {
				Biome biome = chunk.getBiomeType(dx & chunkMask, 0, dz & chunkMask);
				if (biome instanceof VanillaBiome) {
					biomeData[(dz & chunkMask) << 4 | (dx & chunkMask)] = (byte) ((VanillaBiome) biome).getBiomeId();
				}
			}
		}
		return biomeData;
	}

	@Override
	protected void initChunk(Point p) {
		int x = (int) p.getX() >> Chunk.CHUNK_SIZE_BITS;
		int y = (int) p.getY() >> Chunk.CHUNK_SIZE_BITS;// + SEALEVEL_CHUNK;
		int z = (int) p.getZ() >> Chunk.CHUNK_SIZE_BITS;

		if (y < 0 || y > p.getWorld().getHeight() >> Chunk.CHUNK_SIZE_BITS) {
			return;
		}

		TIntHashSet column = activeChunks.get(x, z);
		if (column == null) {
			int[][] heights = new int[Chunk.CHUNK_SIZE][Chunk.CHUNK_SIZE];

			World w = p.getWorld();

			for (int xx = 0; xx < Chunk.CHUNK_SIZE; xx++) {
				for (int zz = 0; zz < Chunk.CHUNK_SIZE; zz++) {
					heights[xx][zz] = w.getSurfaceHeight(p.getBlockX() + xx, p.getBlockZ() + zz, true);
				}
			}

			byte[][] packetChunkData = new byte[16][Chunk.CHUNK_VOLUME * 5 / 2];

			for (int xx = 0; xx < Chunk.CHUNK_SIZE; xx++) {
				for (int zz = 0; zz < Chunk.CHUNK_SIZE; zz++) {
					for (int yy = 0; yy < Chunk.CHUNK_SIZE * 16; yy++) {
						int cube = yy >> Chunk.CHUNK_SIZE_BITS;
						int yOffset = yy & 0xF;
						int dataOffset = xx | (yOffset << 8) | (zz << 4);
						if (heights[xx][zz] < yy && yy > 0) {
							byte mask = (dataOffset & 0x1) == 0 ? (byte) 0x0F : (byte) 0xF0;
							packetChunkData[cube][(Chunk.CHUNK_VOLUME << 1) + (dataOffset >> 1)] |= mask;
						} else {
							packetChunkData[cube][dataOffset] = 1;
						}
					}
				}
			}

			column = new TIntHashSet();
			activeChunks.put(x, z, column);
			LoadChunkMessage loadChunk = new LoadChunkMessage(x, z, true);
			owner.getSession().send(loadChunk);

			final boolean sendBiomes = !biomesSentChunks.contains(x, z);
			final byte[] biomeData;
			if (sendBiomes) {
				biomesSentChunks.add(x, z);
				biomeData = getBiomeData(p.getWorld().getChunkFromBlock(p), x, z);
			} else {
				biomeData = null;
			}

			CompressedChunkMessage CCMsg = new CompressedChunkMessage(x, z, sendBiomes, new boolean[16], 0, packetChunkData, biomeData);
			owner.getSession().send(CCMsg);
		}
		column.add(y);
	}

	@Override
	public void sendChunk(Chunk c) {
		int x = c.getX();
		int y = c.getY();// + SEALEVEL_CHUNK;
		int z = c.getZ();

		if (y < 0 || y >= c.getWorld().getHeight() >> Chunk.CHUNK_SIZE_BITS) {
			return;
		}

		ChunkSnapshot snapshot = c.getSnapshot(false);
		short[] rawBlockIdArray = snapshot.getBlockIds();
		short[] rawBlockData = snapshot.getBlockData();
		byte[] rawBlockLight = snapshot.getBlockLight();
		byte[] rawSkyLight = snapshot.getSkyLight();
		byte[] fullChunkData = new byte[Chunk.CHUNK_VOLUME * 5 / 2];

		boolean hasData = false;
		int arrIndex = 0;
		for (int i = 0; i < rawBlockIdArray.length; i++) {
			short convert = getMinecraftId(rawBlockIdArray[i]);
			fullChunkData[arrIndex++] = (byte) (convert & 0xFF);
			if ((rawBlockIdArray[i] & 0xFF) != 0) {
				hasData = true;
			}
		}
		if (!hasData) {
			return;
		}

		for (int i = 0; i < rawBlockData.length; i += 2) {
			fullChunkData[arrIndex++] = (byte) ((rawBlockData[i + 1] << 4) | (rawBlockData[i] & 0xF));
		}

		System.arraycopy(rawBlockLight, 0, fullChunkData, arrIndex, rawBlockLight.length);
		arrIndex += rawBlockLight.length;

		System.arraycopy(rawSkyLight, 0, fullChunkData, arrIndex, rawSkyLight.length);
		arrIndex += rawSkyLight.length;

		final boolean sendBiomes = !biomesSentChunks.contains(x, z);
		final byte[] biomeData;
		if (sendBiomes) {
			biomeData = getBiomeData(c, x, z);
			biomesSentChunks.add(x, z);
		} else {
			biomeData = null;
		}

		byte[][] packetChunkData = new byte[16][];
		packetChunkData[y] = fullChunkData;
		CompressedChunkMessage CCMsg = new CompressedChunkMessage(x, z, sendBiomes, new boolean[16], 0, packetChunkData, biomeData);
		owner.getSession().send(CCMsg);
	}

	@Override
	protected void sendPosition(Point p, Quaternion rot) {
		//TODO: Implement Spout Protocol
		PlayerPositionLookMessage PRMsg = new PlayerPositionLookMessage(p.getX(), p.getY() + STANCE, p.getZ(), p.getY(), rot.getYaw(), rot.getPitch(), true);
		owner.getSession().send(PRMsg);
	}

	boolean first = true;

	@Override
	protected void worldChanged(World world) {
		int dimensionBit;
		WorldGenerator worldGen = world.getGenerator();
		if (worldGen instanceof NormalGenerator || worldGen instanceof FlatGenerator) {
			dimensionBit = 0;
		} else if (worldGen instanceof NetherGenerator) {
			dimensionBit = -1;
		} else {
			dimensionBit = 1;
		}
		if (first) {
			first = false;
			int entityId = owner.getEntity().getId();
			VanillaPlayer vc = (VanillaPlayer) owner.getEntity().getController();
			LoginRequestMessage idMsg = new LoginRequestMessage(entityId, owner.getName(), vc.isSurvival() ? 0 : 1, dimensionBit, 0, world.getHeight(), session.getGame().getMaxPlayers(), "DEFAULT");
			owner.getSession().send(idMsg, true);
			//Normal messages may be sent
			owner.getSession().setState(State.GAME);
			for (int slot = 0; slot < 4; slot++) {
				ItemStack slotItem = vc.getInventory().getArmor().getItem(slot);
				EntityEquipmentMessage EEMsg;
				if (slotItem == null) {
					EEMsg = new EntityEquipmentMessage(entityId, slot, -1, 0);
				} else {
					EEMsg = new EntityEquipmentMessage(entityId, slot, getMinecraftId(slotItem.getMaterial().getId()), slotItem.getData());
				}
				owner.getSession().send(EEMsg);
			}
		} else {
			VanillaPlayer vc = (VanillaPlayer) owner.getEntity().getController();
			owner.getSession().send(new RespawnMessage(dimensionBit, (byte) 0, (byte) (vc.isSurvival() ? 0 : 1), world.getHeight(), "DEFAULT"));
		}

		if (world != null) {
			Point spawn = world.getSpawnPoint().getPosition();
			SpawnPositionMessage SPMsg = new SpawnPositionMessage((int) spawn.getX(), (int) spawn.getY(), (int) spawn.getZ());
			owner.getSession().send(SPMsg);
		}
	}

	long lastKeepAlive = System.currentTimeMillis();

	@Override
	public void preSnapshot() {
		long currentTime = System.currentTimeMillis();
		if (currentTime > lastKeepAlive + TIMEOUT) {
			KeepAliveMessage PingMsg = new KeepAliveMessage((int) currentTime);
			lastKeepAlive = currentTime;
			owner.getSession().send(PingMsg, true);
		}

		for (TIntObjectIterator<Message> i = queuedInventoryUpdates.iterator(); i.hasNext(); ) {
			i.advance();
			session.send(i.value());
		}
		super.preSnapshot();
	}

	@Override
	public void updateBlock(Chunk chunk, int x, int y, int z, BlockMaterial material, short data) {
		int id = getMinecraftId(material);
		if ((data & 0xF) > 15) {
			data = 0;
		}
		x += chunk.getBlockX();
		y += chunk.getBlockY();
		z += chunk.getBlockZ();
		if (y >= 0 && y < chunk.getWorld().getHeight()) {
			BlockChangeMessage BCM = new BlockChangeMessage(x, y, z, id & 0xFF, data & 0xF);
			session.send(BCM);
		}
	}

	@Override
	public void spawnEntity(Entity e) {
		if (e == null) {
			return;
		}

		Controller c = e.getController();
		if (c != null) {
			EntityProtocol ep = c.getType().getEntityProtocol(VanillaPlugin.VANILLA_PROTOCOL_ID);
			if (ep != null) {
				Message[] spawn = ep.getSpawnMessage(e);
				if (spawn != null) {
					activeEntities.add(e.getId());
					for (Message msg : spawn) {
						session.send(msg);
					}
				}
			}
		}
		super.spawnEntity(e);
	}

	@Override
	public void destroyEntity(Entity e) {
		if (e == null) {
			return;
		}
		if (!activeEntities.contains(e.getId())) {
			return;
		}

		Controller c = e.getController();
		if (c != null) {
			EntityProtocol ep = c.getType().getEntityProtocol(VanillaPlugin.VANILLA_PROTOCOL_ID);
			if (ep != null) {
				Message[] death = ep.getDestroyMessage(e);
				if (death != null) {
					for (Message msg : death) {
						session.send(msg);
					}
					activeEntities.remove(e.getId());
				}
			}
		}
		super.destroyEntity(e);
	}

	@Override
	public void syncEntity(Entity e) {
		if (e == null) {
			return;
		}

		// TODO - is this really worth checking?
		if (!activeEntities.contains(e.getId())) {
			return;
		}
		Controller c = e.getController();
		if (c != null) {
			EntityProtocol ep = c.getType().getEntityProtocol(VanillaPlugin.VANILLA_PROTOCOL_ID);
			if (ep != null) {
				Message[] sync = ep.getUpdateMessage(e);
				if (sync != null) {
					for (Message msg : sync) {
						session.send(msg);
					}
				}
			}
		}
		super.syncEntity(e);
	}

	@Override
	public void onSlotSet(InventoryBase inventory, int slot, ItemStack item) {
		Controller c = owner.getEntity().getController();
		if (!(c instanceof VanillaPlayer)) {
			return;
		}
		VanillaPlayer controller = (VanillaPlayer) c;
		slot = controller.getActiveWindow().getMCSlotIndex(slot);
		if (slot == -1) {
			return;
		}

		Message message;
		int id = controller.getActiveWindow().getInstanceId();
		if (item == null) {
			message = new SetWindowSlotMessage(id, slot);
		} else {
			message = new SetWindowSlotMessage(id, slot, getMinecraftId(item.getMaterial().getId()), item.getAmount(), item.getData(), item.getNBTData());
		}
		queuedInventoryUpdates.put(slot, message);
	}

	@Override
	public void updateAll(InventoryBase inventory, ItemStack[] slots) {
		Controller c = owner.getEntity().getController();
		if (!(c instanceof VanillaPlayer)) {
			return;
		}
		VanillaPlayer controller = (VanillaPlayer) c;
		ItemStack[] newItems = new ItemStack[slots.length];
		for (int i = 0; i < newItems.length; i++) {
			newItems[controller.getActiveWindow().getMCSlotIndex(i)] = slots[i];
		}

		int id = controller.getActiveWindow().getInstanceId();
		session.send(new SetWindowSlotsMessage((byte) id, newItems));
		queuedInventoryUpdates.clear();
	}

	/**
	 * This method takes any amount of messages and sends them to every online player on the server.
	 * @param messages
	 */
	public static void broadcastPacket(Message... messages) {
		sendPacket(Spout.getEngine().getOnlinePlayers(), messages);
	}

	/**
	 * This method takes in any amount of messages and sends them to any amount of
	 * players.
	 * @param players specific players to send a message to.
	 * @param messages the message(s) to send
	 */
	public static void sendPacket(Player[] players, Message... messages) {
		for (Player player : players) {
			for (Message message : messages) {
				sendPacket(player, message);
			}
		}
	}

	/**
	 * This method takes in a message and sends it to a specific player
	 * @param player specific player to relieve message
	 * @param messages specific message to send.
	 */
	public static void sendPacket(Player player, Message... messages) {
		for (Message message : messages) {
			player.getSession().send(message);
		}
	}

	/**
	 * This method sends an effect play message for a block to all nearby players in a 16-block radius
	 * @param block The block that the effect comes from.
	 * @param effect The effect to play
	 */
	public static void playBlockEffect(Block block, Entity ignore, PlayEffectMessage.Messages effect) {
		playBlockEffect(block, ignore, 16, effect, 0);
	}

	/**
	 * This method sends an effect play message for a block to all nearby players
	 * @param block The block that the effect comes from.
	 * @param range The range (circular) from the entity in-which the nearest player should be searched for.
	 * @param effect The effect to play
	 */
	public static void playBlockEffect(Block block, Entity ignore, int range, PlayEffectMessage.Messages effect) {
		playBlockEffect(block, ignore, range, effect, 0);
	}

	/**
	 * This method sends an effect play message for a block to all nearby players
	 * @param block The block that the effect comes from.
	 * @param range The range (circular) from the entity in-which the nearest player should be searched for.
	 * @param effect The effect to play
	 * @param data The data to use for the effect
	 */
	public static void playBlockEffect(Block block, Entity ignore, int range, PlayEffectMessage.Messages effect, int data) {
		sendPacketsToNearbyPlayers(block.getPosition(), ignore, range, new PlayEffectMessage(effect.getId(), block, data));
	}

	/**
	 * Sends a block action message to all nearby players in a 48-block radius
	 */
	public static void playBlockAction(Block block, byte arg1, byte arg2) {
		sendPacketsToNearbyPlayers(block.getPosition(), 48, new BlockActionMessage(block, arg1, arg2));
	}

	/**
	 * Sends a block action message to all nearby players
	 */
	public static void playBlockAction(Block block, int range, byte arg1, byte arg2) {
		sendPacketsToNearbyPlayers(block.getPosition(), range, new BlockActionMessage(block, arg1, arg2));
	}

	/**
	 * This method sends any amount of packets to all nearby players of a position (within a specified range).
	 * @param position The position that the packet relates to. It will be used as the central point to send packets in a range from.
	 * @param range The range (circular) from the entity in-which the nearest player should be searched for.
	 * @param messages The messages that should be sent to the discovered nearest player.
	 */
	public static void sendPacketsToNearbyPlayers(Point position, Entity ignore, int range, Message... messages) {
		Set<Player> players = position.getWorld().getNearbyPlayers(position, ignore, range);
		for (Player plr : players) {
			plr.getSession().sendAll(messages);
		}
	}

	/**
	 * This method sends any amount of packets to all nearby players of a position (within a specified range).
	 * @param position The position that the packet relates to. It will be used as the central point to send packets in a range from.
	 * @param range The range (circular) from the entity in-which the nearest player should be searched for.
	 * @param messages The messages that should be sent to the discovered nearest player.
	 */
	public static void sendPacketsToNearbyPlayers(Point position, int range, Message... messages) {
		Set<Player> players = position.getWorld().getNearbyPlayers(position, range);
		for (Player plr : players) {
			plr.getSession().sendAll(messages);
		}
	}

	/**
	 * This method sends any amount of packets to all nearby players of an entity (within a specified range).
	 * @param entity The entity that the packet relates to. It will be used as the central point to send packets in a range from.
	 * @param range The range (circular) from the entity in-which the nearest player should be searched for.
	 * @param messages The messages that should be sent to the discovered nearest player.
	 */
	public static void sendPacketsToNearbyPlayers(Entity entity, int range, Message... messages) {
		if (entity == null || entity.getRegion() == null) {
			return;
		}
		Set<Player> players = entity.getWorld().getNearbyPlayers(entity, range);
		for (Player plr : players) {
			plr.getSession().sendAll(messages);
		}
	}

	/**
	 * This method sends any amount of packets and sends them to the nearest player from the entity specified.
	 * @param entity The entity that the packet relates to. It will be used as the central point to send packets in a range from.
	 * @param range The range (circular) from the entity in-which the nearest player should be searched for.
	 * @param messages The messages that should be sent to the discovered nearest player.
	 */
	public static void sendPacketsToNearestPlayer(Entity entity, int range, Message... messages) {
		if (entity == null || entity.getRegion() == null) {
			return;
		}

		Player plr = entity.getWorld().getNearestPlayer(entity, range);
		//Only send if we have a player nearby.
		if (plr != null) {
			plr.getSession().sendAll(messages);
		}
	}
}
