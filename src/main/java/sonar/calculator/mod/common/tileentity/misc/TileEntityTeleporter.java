package sonar.calculator.mod.common.tileentity.misc;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.calculator.mod.api.machines.ITeleport;
import sonar.calculator.mod.api.machines.TeleportLink;
import sonar.calculator.mod.client.gui.misc.GuiTeleporter;
import sonar.calculator.mod.utils.TeleporterRegistry;
import sonar.calculator.mod.utils.helpers.TeleporterHelper;
import sonar.core.SonarCore;
import sonar.core.api.IFlexibleGui;
import sonar.core.common.block.StableStone;
import sonar.core.common.tileentity.TileEntitySonar;
import sonar.core.handlers.inventories.containers.ContainerEmpty;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.network.sync.SyncTagType;
import sonar.core.network.sync.SyncTagType.STRING;
import sonar.core.network.utils.IByteBufTile;

import java.util.List;

public class TileEntityTeleporter extends TileEntitySonar implements ITeleport, IByteBufTile, IFlexibleGui {

	public int teleporterID;
	public SyncTagType.STRING name = (STRING) new SyncTagType.STRING(0).setDefault("LINK NAME");
	public SyncTagType.STRING destinationName = (STRING) new SyncTagType.STRING(1).setDefault("DESTINATION");
	public SyncTagType.STRING password = (STRING) new SyncTagType.STRING(2).setDefault("");
	public SyncTagType.STRING linkPassword = (STRING) new SyncTagType.STRING(3).setDefault("");

	public boolean coolDown, passwordMatch;
    public int coolDownTicks;

	public int linkID;

    /**
     * client only list
     */
	public List<TeleportLink> links;

	public TileEntityTeleporter(){
		syncList.addParts(name, destinationName, linkPassword, password);
	}
	
    @Override
	public void update() {
		super.update();
		if (this.world.isRemote) {
			return;
		}
		if (coolDownTicks != 0) {
			coolDownTicks--;
		} else {
			if (!coolDown) {
				if (this.teleporterID == 0) {
					return;
				}
				startTeleportation();
			} else {
				List<EntityPlayer> players = this.getPlayerList();
				if (players == null || players.size() == 0) {
					coolDown = false;
				}
			}
		}
	}

	public void startTeleportation() {
		List<ITeleport> links = TeleporterRegistry.getTeleporters();
		if (links != null && links.size() != 1) {
			for (ITeleport teleport : links) {
				TileEntityTeleporter tile = TeleporterRegistry.getTile(teleport);
				if (tile == null) {
					TeleporterRegistry.removeTeleporter(teleport);
					return;
				}
				if (tile.teleporterID != 0 && tile.teleporterID == this.linkID) {
					if (TeleporterHelper.canTeleport(tile, this) && canTeleportPlayer() && tile.canTeleportPlayer()) {
						this.passwordMatch = true;
						TeleporterHelper.travelToDimension(this.getPlayerList(), tile);
						updateDimensionName(tile.name.getObject());
					} else {
						this.passwordMatch = false;
					}
				} else if (tile.teleporterID == 0) {
					tile.resetFrequency();
				}
			}
		} else {
			this.passwordMatch = false;
		}
	}

	public void updateDimensionName(String name) {
		if (!destinationName.getObject().equals(name)) {
			destinationName.setObject(name);
			SonarCore.sendFullSyncAround(this, 64);
		}
	}

	public boolean canTeleportPlayer() {
		boolean flag = true;
		for (int i = 1; i < 3; i++) {
			Block block = world.getBlockState(pos.offset(EnumFacing.DOWN, i)).getBlock();
			if (!(block == Blocks.AIR)) {
				flag = false;
			}
		}
		EnumFacing[] dirs = new EnumFacing[] { EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.EAST, EnumFacing.WEST };
		int stable = 0;
        for (EnumFacing dir : dirs) {
			int blocks = 0;
			for (int j = 0; j < 3; j++) {
				Block block = world.getBlockState(pos.add(dir.getFrontOffsetX(), -j, dir.getFrontOffsetZ())).getBlock();
				if (block instanceof StableStone) {
					blocks++;
				}
			}
			if (blocks == 3) {
				blocks = 0;
				stable++;
			}
		}

		return stable >= 3 && flag && pos.getY() - 2 > 0;
	}

	public List<EntityPlayer> getPlayerList() {
		AxisAlignedBB aabb = new AxisAlignedBB(pos.getX() - 1, pos.getY() - 2, pos.getZ() - 1, pos.getX() + 1, pos.getY() - 1, pos.getZ() + 1);
        return this.world.getEntitiesWithinAABB(EntityPlayer.class, aabb, null);
	}

	public void resetFrequency() {
		if (!this.world.isRemote) {
			this.removeFromFrequency();
			this.addToFrequency();
		}
	}

	public void addToFrequency() {
		if (!this.world.isRemote) {
			if (this.teleporterID == 0) {
				teleporterID = TeleporterRegistry.nextID();
			}
			TeleporterRegistry.addTeleporter(this);
		}
	}

	public void removeFromFrequency() {
		if (!this.world.isRemote) {
			TeleporterRegistry.removeTeleporter(this);
		}
	}

	public void setFrequency(int freq) {
		removeFromFrequency();
		this.teleporterID = freq;
		addToFrequency();
	}

    @Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE)) {
			this.teleporterID = nbt.getInteger("freq");
			this.linkID = nbt.getInteger("linkID");
			this.coolDown = nbt.getBoolean("coolDown");
			this.passwordMatch = nbt.getBoolean("passwordMatch");
			this.coolDownTicks = nbt.getInteger("coolDownTicks");
		}
	}

    @Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE)) {
			nbt.setInteger("freq", this.teleporterID);
			nbt.setInteger("linkID", this.linkID);
			nbt.setBoolean("coolDown", this.coolDown);
			nbt.setBoolean("passwordMatch", this.passwordMatch);
			nbt.setInteger("coolDownTicks", this.coolDownTicks);
		}
		return nbt;
	}

    @Override
	public void onChunkUnload() {
		this.removeFromFrequency();
	}

	public void onLoaded() {
		if (!this.world.isRemote) {
			this.addToFrequency();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (!this.world.isRemote) {
			this.removeFromFrequency();
		}
	}

    @Override
	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip, IBlockState state) {
		currenttip.add("Link Name: " + name);
		if (!destinationName.getObject().equals("DESTINATION")) {
			currenttip.add("Destination: " + destinationName.getObject());
		}
		return currenttip;
	}

	@Override
	public int teleporterID() {
		return teleporterID;
	}

	@Override
	public String name() {
		return name.getObject();
	}

    @Override
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 65536.0D;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return INFINITE_EXTENT_AABB;
	}

	@Override
	public void writePacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			buf.writeInt(linkID);
			break;
		case 1:
			name.writeToBuf(buf);
			break;
		case 2:
			password.writeToBuf(buf);
			break;
		case 3:
			linkPassword.writeToBuf(buf);
			break;
		}
	}

	@Override
	public void readPacket(ByteBuf buf, int id) {
		switch (id) {
		case 0:
			this.removeFromFrequency();
			this.linkID = buf.readInt();
			this.addToFrequency();
			break;
		case 1:
			name.readFromBuf(buf);
			break;
		case 2:
			password.readFromBuf(buf);
			break;
		case 3:
			linkPassword.readFromBuf(buf);
			break;
		}
	}

	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return new ContainerEmpty(player.inventory, this);
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return new GuiTeleporter(player.inventory, this);
	}
}