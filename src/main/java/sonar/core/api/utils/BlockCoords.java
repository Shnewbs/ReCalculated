package sonar.core.api.utils;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import sonar.core.SonarCore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Object representing a block's x, y, and z coordinates along with its dimension.
 */
public class BlockCoords {

	public static final String X = "x", Y = "y", Z = "z", HAS_DIMENSION = "hasDimension", DIMENSION = "dimension";
	public static final BlockCoords EMPTY = new BlockCoords(0, 0, 0);
	private BlockPos pos;
	private int dimension;
	private boolean hasDimension;

	public BlockCoords(int x, int y, int z) {
		this.pos = new BlockPos(x, y, z);
		this.hasDimension = false;
	}

	@Deprecated
	public BlockCoords(int x, int y, int z, World world) {
		this.pos = new BlockPos(x, y, z);
		this.hasDimension = true;
		this.dimension = world.dimension();
	}

	public BlockCoords(int x, int y, int z, int dimension) {
		this.pos = new BlockPos(x, y, z);
		this.hasDimension = true;
		this.dimension = dimension;
	}

	public BlockCoords(BlockPos pos) {
		this.pos = pos;
		this.hasDimension = false;
	}

	public BlockCoords(BlockPos pos, World world) {
		this.pos = pos;
		this.hasDimension = true;
		this.dimension = world.dimension();
	}

	public BlockCoords(BlockPos pos, int dimension) {
		this.pos = pos;
		this.hasDimension = true;
		this.dimension = dimension;
	}

	public BlockCoords(TileEntity tile) {
		this.pos = tile.getBlockPos();
		this.hasDimension = true;
		this.dimension = tile.getLevel().dimension();
	}

	public BlockCoords(TileEntity tile, int dimension) {
		this.pos = tile.getBlockPos();
		this.hasDimension = true;
		this.dimension = dimension;
	}

	public BlockPos getBlockPos() {
		return pos;
	}

	public int getX() {
		return pos.getX();
	}

	public int getY() {
		return pos.getY();
	}

	public int getZ() {
		return pos.getZ();
	}

	public void setX(int x) {
		this.pos = new BlockPos(x, pos.getY(), pos.getZ());
	}

	public void setY(int y) {
		this.pos = new BlockPos(pos.getX(), y, pos.getZ());
	}

	public void setZ(int z) {
		this.pos = new BlockPos(pos.getX(), pos.getY(), z);
	}

	public int getDimension() {
		return this.dimension;
	}

	public boolean hasDimension() {
		return this.hasDimension;
	}

	public Block getBlock(World world) {
		return world.getBlockState(pos).getBlock();
	}

	public TileEntity getTileEntity(World world) {
		return world.getBlockEntity(pos);
	}

	public Block getBlock() {
		if (this.hasDimension()) {
			return getWorld().getBlockState(pos).getBlock();
		} else {
			return null;
		}
	}

	public IBlockState getBlockState(World world) {
		return world.getBlockState(pos);
	}

	public IBlockState getBlockState() {
		if (this.hasDimension()) {
			return getWorld().getBlockState(pos);
		} else {
			return null;
		}
	}

	public TileEntity getTileEntity() {
		if (this.hasDimension()) {
			return getWorld().getBlockEntity(pos);
		} else {
			return null;
		}
	}

	public World getWorld() {
		return SonarCore.proxy.getDimension(getDimension());
	}

	public boolean insideChunk(ChunkPos pos) {
		return pos.x >> 4 == getX() >> 4 && pos.z >> 4 == getZ() >> 4;
	}

	public boolean insideChunk(int chunkX, int chunkZ) {
		return chunkX == getX() >> 4 && chunkZ == getZ() >> 4;
	}

	public boolean isChunkLoaded(World world) {
		return world.hasChunkAt(pos);
	}

	public boolean isChunkLoaded() {
		return getWorld().hasChunkAt(pos);
	}

	public static void writeToBuf(ByteBuf tag, BlockCoords coords) {
		tag.writeInt(coords.pos.getX());
		tag.writeInt(coords.pos.getY());
		tag.writeInt(coords.pos.getZ());
		tag.writeInt(coords.dimension);
	}

	public static BlockCoords readFromBuf(ByteBuf tag) {
		return new BlockCoords(tag.readInt(), tag.readInt(), tag.readInt(), tag.readInt());
	}

	public static CompoundNBT writeToNBT(CompoundNBT tag, BlockCoords coords) {
		tag.putInt(X, coords.getX());
		tag.putInt(Y, coords.getY());
		tag.putInt(Z, coords.getZ());
		tag.putBoolean(HAS_DIMENSION, coords.hasDimension);
		tag.putInt(DIMENSION, coords.dimension);
		return tag;
	}

	public static boolean hasCoords(CompoundNBT tag) {
		return tag.contains(X) && tag.contains(Y) && tag.contains(Z);
	}

	public static BlockCoords readFromNBT(CompoundNBT tag) {
		if (tag.getBoolean(HAS_DIMENSION)) {
			return new BlockCoords(tag.getInt(X), tag.getInt(Y), tag.getInt(Z), tag.getInt(DIMENSION));
		}
		return new BlockCoords(tag.getInt(X), tag.getInt(Y), tag.getInt(Z));
	}

	public static CompoundNBT writeBlockCoords(CompoundNBT tag, List<BlockCoords> coords, String tagName) {
		ListNBT list = new ListNBT();
		if (coords != null) {
			for (BlockCoords coord : coords) {
				if (coord != null) {
					CompoundNBT compound = new CompoundNBT();
					writeToNBT(compound, coord);
					list.add(compound);
				}
			}
		}
		tag.put(tagName, list);
		return tag;
	}

	public static CompoundNBT writeBlockCoords(CompoundNBT tag, BlockCoords[] coords) {
		ListNBT list = new ListNBT();
		if (coords != null) {
			for (int i = 0; i < coords.length; i++) {
				if (coords[i] != null) {
					CompoundNBT compound = new CompoundNBT();
					compound.putByte("Slot", (byte) i);
					writeToNBT(compound, coords[i]);
					list.add(compound);
				}
			}
		}
		tag.put("BlockCoords", list);
		return tag;
	}

	public static ArrayList<BlockCoords> readBlockCoords(CompoundNBT tag, String tagName) {
		ArrayList<BlockCoords> coords = new ArrayList<>();
		if (tag.contains(tagName)) {
			ListNBT list = tag.getList(tagName, 10);
			for (int i = 0; i < list.size(); i++) {
				CompoundNBT compound = list.getCompound(i);
				coords.add(readFromNBT(compound));
			}
		}
		return coords;
	}

	public static BlockCoords[] readBlockCoords(CompoundNBT tag, int listSize) {
		ListNBT list = tag.getList("BlockCoords", 10);
		BlockCoords[] coords = new BlockCoords[listSize];
		for (int i = 0; i < list.size(); i++) {
			CompoundNBT compound = list.getCompound(i);
			byte b = compound.getByte("Slot");
			if (b >= 0 && b < listSize) {
				coords[b] = readFromNBT(compound);
			}
		}
		return coords;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof BlockCoords)) {
			return false;
		}
		BlockCoords coords = (BlockCoords) obj;
		return pos.getX() == coords.pos.getX() && pos.getY() == coords.pos.getY() && pos.getZ() == coords.pos.getZ() && this.dimension == coords.dimension;
	}

	@Override
	public int hashCode() {
		int result = 1;
		result = 37 * result + (hasDimension ? 0 : 1);
		result = 37 * result + pos.getX();
		result = 37 * result + pos.getY();
		result = 37 * result + pos.getZ();
		result = 37 * result + dimension;
		return result;
	}

	public static boolean equalCoords(BlockCoords coords1, BlockCoords coords2) {
		return coords1 == null && coords2 == null || coords1 == null || coords2 != null && (coords1.pos.getX() == coords2.pos.getX() && coords1.pos.getY() == coords2.pos.getY() && coords1.pos.getZ() == coords2.pos.getZ() && coords1.dimension == coords2.dimension);
	}

	public static boolean equalCoordArrays(BlockCoords[] coords1, BlockCoords[] coords2) {
		if (coords1.length != coords2.length) {
			return false;
		}
		for (int i = 0; i < coords1.length; i++) {
			if (!equalCoords(coords1[i], coords2[i])) {
				return false;
			}
		}
		return true;
	}

	public static BlockCoords translateCoords(BlockCoords coords, Direction dir) {
		return new BlockCoords(coords.getX() + dir.getStepX(), coords.getY() + dir.getStepY(), coords.getZ() + dir.getStepZ(), coords.dimension);
	}

	@Override
	public String toString() {
		return "X: " + getX() + " Y: " + getY() + " Z: " + getZ() + " D: " + dimension;
	}

	public BlockCoords fromString(String string) {
		String[] split = string.split(": ");
		int x = Integer.parseInt(split[1]);
		int y = Integer.parseInt(split[3]);
		int z = Integer.parseInt(split[5]);
		int d = Integer.parseInt(split[7]);

		return new BlockCoords(x, y, z, d);
	}

	public boolean contains(Map<BlockCoords, ?> map) {
		for (Entry<BlockCoords, ?> set : map.entrySet()) {
			if (set.getKey().equals(this)) {
				return true;
			}
		}
		return false;
	}

	public boolean contains(List<BlockCoords> list) {
		for (BlockCoords coords : list) {
			if (coords.equals(this)) {
				return true;
			}
		}
		return false;
	}
}
