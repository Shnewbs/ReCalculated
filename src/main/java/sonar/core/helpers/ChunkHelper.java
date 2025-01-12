package sonar.core.helpers;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;

public class ChunkHelper {

	public static List<EntityPlayerMP> getChunkPlayers(World world, BlockPos pos) {
		return getChunkPlayers(world, new ChunkPos(pos));
	}

	public static List<EntityPlayerMP> getChunkPlayers(World world, ChunkPos pos) {
		if (world instanceof WorldServer) {
			WorldServer server = (WorldServer) world;
			PlayerChunkMap map = server.getPlayerChunkMap();
			PlayerChunkMapEntry entry = map.getEntry(pos.x, pos.z);
			if (entry != null) {
				return getChunkPlayers(entry);
			}
		}
		return new ArrayList<>();
	}

	public static List<EntityPlayerMP> getChunkPlayers(World world, List<ChunkPos> chunks) {
		List<EntityPlayerMP> allPlayers = new ArrayList<>();
		if (world instanceof WorldServer) {
			WorldServer server = (WorldServer) world;
			PlayerChunkMap map = server.getPlayerChunkMap();
			for (ChunkPos pos : chunks) {
				if (server.isChunkGeneratedAt(pos.x, pos.z)) {
					PlayerChunkMapEntry entry = map.getEntry(pos.x, pos.z);
					if (entry != null) {
						List<EntityPlayerMP> players = getChunkPlayers(entry);
						for (EntityPlayerMP player : players) {
							if (!allPlayers.contains(player)) {
								allPlayers.add(player);
							}
						}
					}
				}
			}
		}
		return allPlayers;
	}

	public static List<ChunkPos> getChunksInRadius(BlockPos pos, double radius) {
		List<ChunkPos> chunks = new ArrayList<>();

		int smallX = MathHelper.floor((pos.getX() - radius) / 16.0D);
		int bigX = MathHelper.floor((pos.getX() + radius) / 16.0D);
		int smallZ = MathHelper.floor((pos.getZ() - radius) / 16.0D);
		int bigZ = MathHelper.floor((pos.getZ() + radius) / 16.0D);

		for (int x = smallX; x <= bigX; x++) {
			for (int z = smallZ; z <= bigZ; z++) {
				chunks.add(new ChunkPos(x, z));
			}
		}
		return chunks;
	}

	public static List<EntityPlayerMP> getChunkPlayers(PlayerChunkMapEntry entry) {
		return entry.getWatchingPlayers();
	}
}
