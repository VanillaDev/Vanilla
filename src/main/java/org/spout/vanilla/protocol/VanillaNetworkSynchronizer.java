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
 * the MIT license and the SpoutDev License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */
package org.spout.vanilla.protocol;

import org.spout.api.entity.Controller;
import org.spout.api.entity.Entity;
import org.spout.api.generator.WorldGenerator;
import org.spout.api.generator.biome.BiomeGenerator;
import org.spout.api.generator.biome.BiomeType;
import org.spout.api.geo.World;
import org.spout.api.geo.cuboid.Chunk;
import org.spout.api.geo.cuboid.ChunkSnapshot;
import org.spout.api.geo.discrete.Point;
import org.spout.api.inventory.Inventory;
import org.spout.api.inventory.ItemStack;
import org.spout.api.material.BlockMaterial;
import org.spout.api.player.Player;
import org.spout.api.protocol.EntityProtocol;
import org.spout.api.protocol.Message;
import org.spout.api.protocol.NetworkSynchronizer;
import org.spout.api.protocol.Session.State;
import org.spout.api.util.map.TIntPairHashSet;
import org.spout.api.util.map.TIntPairObjectHashMap;
import org.spout.vanilla.util.VanillaMessageHandlerUtils;
import org.spout.vanilla.VanillaPlugin;
import org.spout.vanilla.entity.living.player.SurvivalPlayer;
import org.spout.vanilla.generator.VanillaBiomeType;
import org.spout.vanilla.generator.nether.NetherGenerator;
import org.spout.vanilla.generator.normal.NormalGenerator;
import org.spout.vanilla.protocol.msg.BlockChangeMessage;
import org.spout.vanilla.protocol.msg.CompressedChunkMessage;
import org.spout.vanilla.protocol.msg.EntityEquipmentMessage;
import org.spout.vanilla.protocol.msg.IdentificationMessage;
import org.spout.vanilla.protocol.msg.LoadChunkMessage;
import org.spout.vanilla.protocol.msg.PingMessage;
import org.spout.vanilla.protocol.msg.PositionRotationMessage;
import org.spout.vanilla.protocol.msg.RespawnMessage;
import org.spout.vanilla.protocol.msg.SetWindowSlotMessage;
import org.spout.vanilla.protocol.msg.SetWindowSlotsMessage;
import org.spout.vanilla.protocol.msg.SpawnPositionMessage;

import gnu.trove.iterator.TIntObjectIterator;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.hash.TIntHashSet;
import static org.spout.vanilla.util.VanillaMessageHandlerUtils.getInventoryId;

public class VanillaNetworkSynchronizer extends NetworkSynchronizer {
	@SuppressWarnings("unused")
	private final static int POSITION_UPDATE_TICKS = 20;
	private final static double STANCE = 1.6D;
	private final static int TIMEOUT = 15000;

	public VanillaNetworkSynchronizer(Player player) {
		this(player, null);
	}

	public VanillaNetworkSynchronizer(Player player, Entity entity) {
		super(player, entity);
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

		if (y < 0 || y > p.getWorld().getHeight() >> 4) {
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

	@Override
	protected void initChunk(Point p) {
		int x = (int) p.getX() >> Chunk.CHUNK_SIZE_BITS;
		int y = (int) p.getY() >> Chunk.CHUNK_SIZE_BITS;// + SEALEVEL_CHUNK;
		int z = (int) p.getZ() >> Chunk.CHUNK_SIZE_BITS;

		if (y < 0 || y > p.getWorld().getHeight() >> 4) {
			return;
		}

		TIntHashSet column = activeChunks.get(x, z);
		if (column == null) {
			column = new TIntHashSet();
			activeChunks.put(x, z, column);
			LoadChunkMessage loadChunk = new LoadChunkMessage(x, z, true);
			owner.getSession().send(loadChunk);
		}
		column.add(y);
	}

	@Override
	public void sendChunk(Chunk c) {
		int x = c.getX();
		int y = c.getY();// + SEALEVEL_CHUNK;
		int z = c.getZ();

		//System.out.println("Sending chunk (" + x + ", " + y + ", " + z + ") " + c);

		if (y < 0 || y > c.getWorld().getHeight() >> 4) {
			return;
		}

		ChunkSnapshot snapshot = c.getSnapshot(false);
		short[] rawBlockIdArray = snapshot.getBlockIds();
		short[] rawBlockData = snapshot.getBlockData();
		byte[] rawBlockLight = snapshot.getBlockLight();
		byte[] rawSkyLight = snapshot.getSkyLight();
		byte[] fullChunkData = new byte[16 * 16 * 16 * 5 / 2];

		boolean hasData = false;
		int arrIndex = 0;
		for (int i = 0; i < rawBlockIdArray.length; i++) {
			// TODO - conversion code
			if ((rawBlockIdArray[i] & 0xFF) != 0) {
				hasData = true;
			}
			fullChunkData[arrIndex++] = (byte) (rawBlockIdArray[i] & 0xFF);
		}
		if (!hasData) {
			return;
		}

		for (int i = 0; i < rawBlockData.length; i += 2) {
			fullChunkData[arrIndex++] = (byte) ((byte) rawBlockData[i] << 4 | (byte) rawBlockData[i + 1] & 0xF);
		}

		System.arraycopy(rawBlockLight, 0, fullChunkData, arrIndex, rawBlockLight.length);
		arrIndex += rawBlockLight.length;

		System.arraycopy(rawSkyLight, 0, fullChunkData, arrIndex, rawSkyLight.length);
		arrIndex += rawSkyLight.length;

		final boolean sendBiomes = !biomesSentChunks.contains(c.getX(), c.getZ());
		byte[] biomeData = sendBiomes ? new byte[Chunk.CHUNK_SIZE * Chunk.CHUNK_SIZE] : null;
		if (sendBiomes) {
			biomesSentChunks.add(c.getX(), c.getZ());
			WorldGenerator gen = c.getWorld().getGenerator();
			final long seed = c.getWorld().getSeed();
			if (gen instanceof BiomeGenerator) {
				for (int dx = x; dx < x + Chunk.CHUNK_SIZE; ++dx) {
					for (int dz = z; dz < z + Chunk.CHUNK_SIZE; ++dz) {
						BiomeType biome = ((BiomeGenerator) gen).getBiome(x, z, seed);
						if (biome instanceof VanillaBiomeType) {
							biomeData[(dz & Chunk.CHUNK_SIZE - 1) << 4 | (dx & Chunk.CHUNK_SIZE - 1)] = (byte) ((VanillaBiomeType) biome).getBiomeId();
						}
					}
				}
			}
		}

		byte[][] packetChunkData = new byte[16][];
		packetChunkData[y] = fullChunkData;
		CompressedChunkMessage CCMsg = new CompressedChunkMessage(x, z, sendBiomes, new boolean[16], 0, packetChunkData, biomeData);
		owner.getSession().send(CCMsg);
	}

	@Override
	protected void sendPosition(Point p, float yaw, float pitch) {
		//TODO: Implement Spout Protocol
		PositionRotationMessage PRMsg = new PositionRotationMessage(p.getX(), p.getY() + STANCE, p.getZ(), p.getY(), yaw, pitch, true);
		owner.getSession().send(PRMsg);
	}

	boolean first = true;

	@Override
	protected void worldChanged(World world) {
		if (first) {
			first = false;
			int entityId = owner.getEntity().getId();
			int dimensionBit;
			WorldGenerator worldGen = world.getGenerator();
			if (worldGen instanceof NormalGenerator) {
				dimensionBit = 0;
			} else if (worldGen instanceof NetherGenerator) {
				dimensionBit = -1;
			} else {
				dimensionBit = 1;
			}
			IdentificationMessage idMsg = new IdentificationMessage(entityId, owner.getName(), owner.getEntity().is(SurvivalPlayer.class) ? 0 : 1, dimensionBit, 0, world.getHeight(), session.getGame().getMaxPlayers(), "DEFAULT");
			owner.getSession().send(idMsg, true);
			//Normal messages may be sent
			owner.getSession().setState(State.GAME);
			for (int slot = 0; slot < 5; slot++) {
				ItemStack slotItem = owner.getEntity().getInventory().getItem(5 + slot);
				EntityEquipmentMessage EEMsg;
				if (slotItem == null) {
					EEMsg = new EntityEquipmentMessage(entityId, slot, -1, 0);
				} else {
					EEMsg = new EntityEquipmentMessage(entityId, slot, slotItem.getMaterial().getId(), slotItem.getData());
				}
				owner.getSession().send(EEMsg);
			}
			entity.getInventory().addViewer(this);
		} else {
			owner.getSession().send(new RespawnMessage(0, (byte) 0, (byte) (owner.getEntity().is(SurvivalPlayer.class) ? 0 : 1), world == null ? 0 : world.getHeight(), "DEFAULT"));
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
			PingMessage PingMsg = new PingMessage((int) currentTime);
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
		// TODO - proper translation
		int id = material.getId();
		if ((material.getId() & 0xFF) > 255) {
			id = 1;
		}
		if ((data & 0xF) > 15) {
			data = 0;
		}
		x = (chunk.getX() << Chunk.CHUNK_SIZE_BITS) + x;
		y = (chunk.getY() << Chunk.CHUNK_SIZE_BITS) + y;
		z = (chunk.getZ() << Chunk.CHUNK_SIZE_BITS) + z;
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
			EntityProtocol ep = c.getEntityProtocol(VanillaPlugin.vanillaProtocolId);
			if (ep != null) {
				Message spawn = ep.getSpawnMessage(e);
				if (spawn != null) {
					activeEntities.add(e.getId());
					session.send(spawn);
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
			EntityProtocol ep = c.getEntityProtocol(VanillaPlugin.vanillaProtocolId);
			if (ep != null) {
				Message death = ep.getDestroyMessage(e);
				if (death != null) {
					session.send(death);
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
			EntityProtocol ep = c.getEntityProtocol(VanillaPlugin.vanillaProtocolId);
			if (ep != null) {
				Message sync = ep.getUpdateMessage(e);
				if (sync != null) {
					session.send(sync);
				}
			}
		}
		super.syncEntity(e);
	}

	@Override
	public void onSlotSet(Inventory inventory, int slot, ItemStack item) {
		Message message;
		final int networkSlot = VanillaMessageHandlerUtils.playerInventorySlotToNetwork(slot);
		if (item == null) {
			message = new SetWindowSlotMessage(getInventoryId(inventory.getClass()), networkSlot);
		} else {
			message = new SetWindowSlotMessage(getInventoryId(inventory.getClass()), networkSlot, item.getMaterial().getId(), item.getAmount(), item.getData(), item.getAuxData());
		}
		queuedInventoryUpdates.put(slot, message);
	}

	@Override
	public void updateAll(Inventory inventory, ItemStack[] slots) {
		ItemStack[] newSlots = new ItemStack[slots.length];
		for (int i = 0; i < slots.length; ++i) {
			newSlots[VanillaMessageHandlerUtils.playerInventorySlotToNetwork(i)] = slots[i];
		}
		session.send(new SetWindowSlotsMessage(getInventoryId(inventory.getClass()), newSlots));
		queuedInventoryUpdates.clear();
	}
}
