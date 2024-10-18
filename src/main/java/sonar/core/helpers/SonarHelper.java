package sonar.core.helpers;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.UsernameCache;
import net.minecraftforge.fml.common.FMLCommonHandler;
import sonar.core.api.utils.BlockCoords;
import sonar.core.utils.IWorldPosition;
import sonar.core.utils.SortingDirection;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Helps with getting tiles, adding energy, and checking stacks.
 */
public class SonarHelper {

	private static List<EnumFacing> faceValues;

	public static List<EnumFacing> getEnumFacingValues() {
		return faceValues == null ? faceValues = convertArray(EnumFacing.VALUES) : faceValues;
	}

	public static ChunkPos getChunkFromPos(int xPos, int zPos) {
		return new ChunkPos(xPos >> 4, zPos >> 4);
	}

	public static ChunkPos getChunkPos(int xChunk, int zChunk) {
		return new ChunkPos(xChunk, zChunk);
	}

	public static TileEntity getAdjacentTileEntity(TileEntity tile, EnumFacing side) {
		return tile.getWorld().getTileEntity(tile.getPos().offset(side));
	}

	public static Block getAdjacentBlock(World world, BlockPos pos, EnumFacing side) {
		return world.getBlockState(pos.offset(side)).getBlock();
	}

	public static <TILE> TILE getTile(World world, BlockPos pos, Class<TILE> type) {
		TileEntity tile = world.getTileEntity(pos);
		if (tile != null && type.isAssignableFrom(tile.getClass())) {
			return type.cast(tile);
		}
		return null;
	}

	public static <BLOCK> BLOCK getBlock(World world, BlockPos pos, Class<BLOCK> type) {
		Block block = world.getBlockState(pos).getBlock();
		if (type.isAssignableFrom(block.getClass())) {
			return type.cast(block);
		}
		return null;
	}

	public static Entity getEntity(Class<? extends Entity> entityClass, IWorldPosition tile, int range, boolean nearest) {
		BlockCoords coords = tile.getCoords();
		AxisAlignedBB aabb = new AxisAlignedBB(coords.getX() - range, coords.getY() - range, coords.getZ() - range,
				coords.getX() + range, coords.getY() + range, coords.getZ() + range);

		List<Entity> entities = coords.getWorld().getEntitiesWithinAABB(entityClass, aabb);
		Entity closestEntity = null;
		double entityDistance = nearest ? Double.MAX_VALUE : 0;

		for (Entity target : entities) {
			double dX = coords.getX() - target.posX;
			double dY = coords.getY() - target.posY;
			double dZ = coords.getZ() - target.posZ;
			double distance = dX * dX + dY * dY + dZ * dZ;

			if (nearest ? distance < entityDistance : distance > entityDistance) {
				closestEntity = target;
				entityDistance = distance;
			}
		}
		return closestEntity;
	}

	public static EntityPlayerMP getPlayerFromName(String playerName) {
		List<EntityPlayerMP> serverPlayers = FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayers();
		for (EntityPlayerMP player : serverPlayers) {
			if (player.getName().equals(playerName)) {
				return player;
			}
		}
		return null;
	}

	public static EntityPlayer getPlayerFromUUID(UUID playerUUID) {
		Entity entity = FMLCommonHandler.instance().getMinecraftServerInstance().getEntityFromUuid(playerUUID);
		return entity instanceof EntityPlayer ? (EntityPlayer) entity : null;
	}

	public static GameProfile getProfileByUUID(UUID playerUUID) {
		if (playerUUID == null) {
			return new GameProfile(UUID.randomUUID(), "ERROR: UUID IS INCORRECT");
		}
		if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
			return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getProfileByUUID(playerUUID);
		} else {
			return new GameProfile(playerUUID, UsernameCache.containsUUID(playerUUID) ? UsernameCache.getLastKnownUsername(playerUUID) : "PLAYER ERROR!");
		}
	}

	public static GameProfile getGameProfileForUsername(String playerName) {
		return FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerProfileCache().getGameProfileForUsername(playerName);
	}

	public static EnumFacing getForward(int meta) {
		return EnumFacing.getFront(meta).rotateYCCW();
	}

	public static int getAngleFromMeta(int meta) {
		switch (meta) {
			case 2: return 180;
			case 3: return 0;
			case 4: return 90;
			case 5: return 270;
			default: return 0;
		}
	}

	public static int invertMetadata(int meta) {
		switch (meta) {
			case 0: return 0;
			case 1: return 5;
			case 2: return 4;
			case 3: return 3;
			case 4: return 2;
			case 5: return 1;
			default: return -1;
		}
	}

	public static EnumFacing offsetFacing(EnumFacing facing, EnumFacing front) {
		if (facing.getAxis().getPlane() == EnumFacing.Plane.VERTICAL || front.getAxis().getPlane() == EnumFacing.Plane.VERTICAL) {
			return facing;
		} else {
			return EnumFacing.fromAngle(getRenderRotation(front) + getRenderRotation(facing)).getOpposite();
		}
	}

	public static int getRenderRotation(EnumFacing face) {
		switch (face) {
			case SOUTH: return 180;
			case WEST: return 270;
			case EAST: return 90;
			default: return 0;
		}
	}

	public static EnumFacing getHorizontal(EnumFacing dir) {
		switch (dir) {
			case NORTH: return EnumFacing.EAST;
			case EAST: return EnumFacing.SOUTH;
			case SOUTH: return EnumFacing.WEST;
			case WEST: return EnumFacing.NORTH;
			default: return dir; //DEFAULT
		}
	}

	public static ArrayList<BlockCoords> getConnectedBlocks(Block block, List<EnumFacing> directions, World world, BlockPos pos, int max) {
		ArrayList<BlockCoords> handlers = new ArrayList<>();
		addCoords(block, world, pos, max, handlers, directions);
		return handlers;
	}

	public static void addCoords(Block block, World world, BlockPos pos, int max, ArrayList<BlockCoords> handlers, List<EnumFacing> directions) {
		for (EnumFacing side : directions) {
			if (handlers.size() > max) {
				return;
			}
			BlockPos current = pos.offset(side);
			IBlockState state = world.getBlockState(current);
			Block tile = state.getBlock();
			if (tile == block) {
				BlockCoords coords = new BlockCoords(current);
				if (!handlers.contains(coords)) {
					handlers.add(coords);
					addCoords(block, world, current, max, handlers, SonarHelper.convertArray(EnumFacing.values()));
				}
			}
		}
	}

	public static <E extends Enum<E>> E incrementEnum(E enumObj, E[] values) {
		int ordinal = enumObj.ordinal() + 1;
		return ordinal < values.length ? values[ordinal] : values[0];
	}

	public static <T> List<T> convertArray(T[] array) {
		List<T> list = new ArrayList<>();
		Collections.addAll(list, array);
		return list;
	}

	public static <T> T[] convertArray(List<T> list) {
		return (T[]) list.toArray();
	}

	public static boolean intContains(int[] array, int num) {
		for (int i : array) {
			if (i == num) {
				return true;
			}
		}
		return false;
	}

	public static <T> boolean arrayContains(T[] array, T num) {
		for (T item : array) {
			if (item.equals(num)) {
				return true;
			}
		}
		return false;
	}

	public static int compareWithDirection(long value1, long value2, SortingDirection direction) {
		if (value1 < value2) {
			return direction == SortingDirection.DOWN ? 1 : -1;
		} else if (value1 == value2) {
			return 0;
		}
		return direction == SortingDirection.DOWN ? -1 : 1;
	}

	public static int compareStringsWithDirection(String string1, String string2, SortingDirection direction) {
		int result = String.CASE_INSENSITIVE_ORDER.compare(string1, string2);
		if (result == 0) {
			result = string1.compareTo(string2);
		}
		return direction == SortingDirection.DOWN ? result : -result;
	}

	public static List<EntityPlayerMP> getPlayersWatchingChunk(PlayerChunkMapEntry entry) {
		if (entry != null && entry.isSentToPlayers()) {
			try {
				Field field = PlayerChunkMapEntry.class.getDeclaredField("players");
				field.setAccessible(true);
				List<EntityPlayerMP> players = (List<EntityPlayerMP>) field.get(entry);
				return Lists.newArrayList(players);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		return new ArrayList<>();
	}

	@Nullable
	public static EnumFacing getBlockDirection(BlockPos main, BlockPos dirPos) {
		for (EnumFacing face : EnumFacing.values()) {
			if (main.offset(face).equals(dirPos)) {
				return face;
			}
		}
		return null;
	}
}
