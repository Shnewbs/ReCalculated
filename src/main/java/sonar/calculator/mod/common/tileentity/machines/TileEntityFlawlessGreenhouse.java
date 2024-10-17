package sonar.calculator.mod.common.tileentity.machines;

import com.google.common.collect.Lists;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import sonar.calculator.mod.CalculatorConfig;
import sonar.calculator.mod.api.machines.IFlawlessGreenhouse;
import sonar.calculator.mod.client.gui.machines.GuiFlawlessGreenhouse;
import sonar.calculator.mod.common.containers.ContainerFlawlessGreenhouse;
import sonar.calculator.mod.common.tileentity.TileEntityGreenhouse;
import sonar.calculator.mod.common.tileentity.misc.TileEntityCO2Generator;
import sonar.calculator.mod.utils.helpers.GreenhouseHelper;
import sonar.core.api.IFlexibleGui;
import sonar.core.api.energy.EnergyMode;
import sonar.core.api.utils.BlockCoords;
import sonar.core.handlers.inventories.handling.EnumFilterType;
import sonar.core.handlers.inventories.handling.ItemTransferHelper;
import sonar.core.handlers.inventories.handling.filters.IExtractFilter;
import sonar.core.handlers.inventories.handling.filters.SlotFilter;
import sonar.core.handlers.inventories.handling.filters.SlotHelper;
import sonar.core.helpers.FontHelper;
import sonar.core.helpers.NBTHelper.SyncType;
import sonar.core.helpers.SonarHelper;
import sonar.core.utils.FailedCoords;

import java.util.ArrayList;
import java.util.List;

public class TileEntityFlawlessGreenhouse extends TileEntityGreenhouse implements IFlawlessGreenhouse, IFlexibleGui {

	public static final SlotFilter plant_slots = new SlotFilter(true, new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9 });
	public int plants, levelTicks, checkTicks, houseSize, growTicks, growTick;
	public int plantsGrown;

	public TileEntityFlawlessGreenhouse() {
		super.storage.setCapacity(CalculatorConfig.FLAWLESS_GREENHOUSE_STORAGE);
		super.storage.setMaxTransfer(CalculatorConfig.FLAWLESS_GREENHOUSE_TRANSFER_RATE);
		super.inv.setSize(10);
		super.inv.getInsertFilters().put(plant_slots, EnumFilterType.EXTERNAL);
		super.inv.getInsertFilters().put((SLOT,STACK,FACE) -> TileEntityGreenhouse.isSeed(STACK), EnumFilterType.EXTERNAL_INTERNAL);
		super.inv.getInsertFilters().put(SlotHelper.dischargeSlot(0), EnumFilterType.INTERNAL);
		super.inv.getExtractFilters().put(IExtractFilter.BLOCK_EXTRACT, EnumFilterType.EXTERNAL);
		super.energyMode = EnergyMode.RECIEVE;
		super.type = 3;
		super.maxLevel = 100000;
		super.plantTick = 2;
	}

	@Override
	public void update() {
		super.update();
		if (!(houseState.getObject() == State.BUILDING)) {
			checkTile();
		}
		if (houseState.getObject() == State.COMPLETED) {
			if (!this.world.isRemote) {
				extraTicks();
				if (isActive()) {
					plantCrops();
					grow();
					harvestCrops();
				}
			}
		}
		discharge(0);
	}

	public void grow() {
		if (this.growTicks == 0) {
			this.growTick = GreenhouseHelper.getGrowTicks(this.getOxygen(), 3);
			this.growTicks++;
		}
		if (this.growTick != 0) {
			if (this.growTicks >= 0 && this.growTicks != growTick) {
				growTicks++;
			}
		}
		if (this.growTicks == growTick) {
			growCrops(houseSize);
			this.growTicks = 0;
		}
	}

    @Override
	public ArrayList<BlockPos> getPlantArea() {
        ArrayList<BlockPos> coords = new ArrayList<>();

		int hX = horizontal.getFrontOffsetX();
		int hZ = horizontal.getFrontOffsetZ();

		int fX = forward.getFrontOffsetX();
		int fZ = forward.getFrontOffsetZ();

		for (int i = 0; i <= this.houseSize; i++) {
			for (int XZ = 1; XZ <= 2; XZ++) {
                coords.add(pos.add(hX * XZ + fX * (1 + i), 0, hZ * XZ + fZ * (1 + i)));
			}
		}
		return coords;
	}

	public void extraTicks() {
		if (levelTicks == 15) {
			this.getPlants();
		}
		if (this.levelTicks >= 0 && this.levelTicks != 20) {
			levelTicks++;
		}
		if (this.levelTicks == 20) {
			this.levelTicks = 0;
			ItemTransferHelper.doSimpleTransfer(Lists.newArrayList(getAdjacentChestHandler()), Lists.newArrayList(inv().getItemHandler(forward)), TileEntityGreenhouse::isSeed, 32);
			gasLevels();
		}
	}

    @Override
	public FailedCoords checkStructure(GreenhouseAction action) {
		if (SonarHelper.getHorizontal(forward) != null) {
			int hX = SonarHelper.getHorizontal(forward).getFrontOffsetX();
			int hZ = SonarHelper.getHorizontal(forward).getFrontOffsetZ();

			int hoX = SonarHelper.getHorizontal(forward).getOpposite().getFrontOffsetX();
			int hoZ = SonarHelper.getHorizontal(forward).getOpposite().getFrontOffsetZ();

			int fX = forward.getFrontOffsetX();
			int fZ = forward.getFrontOffsetZ();

			FailedCoords size = checkSize(true, this.world, hX, hZ, hoX, hoZ, fX, fZ, pos.getX(), pos.getY(), pos.getZ());

			if (!size.getBoolean()) {
				return size;
			}

			return new FailedCoords(true, BlockCoords.EMPTY, FontHelper.translate("locator.none"));
		}
		return new FailedCoords(false, BlockCoords.EMPTY, "Something went wrong...");
	}

    /**
     * adds gas, depends on day and night
     **/
	public void gasLevels() {
		boolean day = this.world.isDaytime();
		int gasAdd = this.getGasAdd();
		if (day) {
            int add = this.plants * 8 - gasAdd * 2;
			this.addGas(-add);
		}
		if (!day) {

            int add = this.plants * 2 + gasAdd * 2;
			this.addGas(add);
		}
	}

	private int getGasAdd() {
        TileEntity tile = this.world.getTileEntity(pos.add(SonarHelper.getHorizontal(forward).getFrontOffsetX() * 3, 0, SonarHelper.getHorizontal(forward).getFrontOffsetZ() * 3));
		if (tile instanceof TileEntityCO2Generator) {
			TileEntityCO2Generator generator = (TileEntityCO2Generator) tile;
			return generator.gasAdd;
		}
		return 0;
	}

    /**
     * gets plants inside greenhouse and sets it to this.plants
     **/
	private void getPlants() {
		int hX = SonarHelper.getHorizontal(forward).getFrontOffsetX();
		int hZ = SonarHelper.getHorizontal(forward).getFrontOffsetZ();

		int hoX = SonarHelper.getHorizontal(forward).getOpposite().getFrontOffsetX();
		int hoZ = SonarHelper.getHorizontal(forward).getOpposite().getFrontOffsetZ();

		int fX = forward.getFrontOffsetX();
		int fZ = forward.getFrontOffsetZ();

		this.plants = 0;

		for (int i = 0; i <= this.houseSize; i++) {
			for (int XZ = 1; XZ <= 2; XZ++) {
                BlockPos pos = this.pos.add(hX * XZ + fX * (1 + i), 0, hZ * XZ + fZ * (1 + i));
				if (this.world.getBlockState(pos).getBlock() instanceof IGrowable) {
					this.plants++;
				}
			}
		}
	}

    /**
     * Hoes the ground
     **/
    @Override
	public void addFarmland() {
		int hX = SonarHelper.getHorizontal(forward).getFrontOffsetX();
		int hZ = SonarHelper.getHorizontal(forward).getFrontOffsetZ();

		int hoX = SonarHelper.getHorizontal(forward).getOpposite().getFrontOffsetX();
		int hoZ = SonarHelper.getHorizontal(forward).getOpposite().getFrontOffsetZ();

		int fX = forward.getFrontOffsetX();
		int fZ = forward.getFrontOffsetZ();

		for (int i = 0; i <= this.houseSize; i++) {
			for (int XZ = 0; XZ <= 3; XZ++) {
                BlockPos pos = this.pos.add(hX * XZ + fX * (1 + i), 0, hZ * XZ + fZ * (1 + i));
				if (XZ != 1 && XZ != 2) {
					if (this.storage.getEnergyLevel() >= waterRF) {
						if (GreenhouseHelper.applyWater(world, pos)) {
							this.storage.modifyEnergyStored(-waterRF);
						}
					}
				} else {
					if (this.storage.getEnergyLevel() >= farmlandRF) {
						if (GreenhouseHelper.applyFarmland(world, pos)) {
							this.storage.modifyEnergyStored(-farmlandRF);
						}
					}
				}
			}
		}
	}

    @Override
	public void readData(NBTTagCompound nbt, SyncType type) {
		super.readData(nbt, type);
		if (type.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE)) {
			this.plantsHarvested = nbt.getInteger("plantsHarvested");
			this.plantsGrown = nbt.getInteger("plantsGrown");
			this.houseSize = nbt.getInteger("houseSize");

			if (type == SyncType.SAVE) {
				this.planting = nbt.getInteger("planting");
				this.plants = nbt.getInteger("Plants");
				this.levelTicks = nbt.getInteger("Level");
				this.plantTicks = nbt.getInteger("Plant");
				this.checkTicks = nbt.getInteger("Check");
				this.growTick = nbt.getInteger("growTick");
				this.growTicks = nbt.getInteger("growTicks");
			}
		}
	}

    @Override
	public NBTTagCompound writeData(NBTTagCompound nbt, SyncType type) {
		super.writeData(nbt, type);
		if (type.isType(SyncType.DEFAULT_SYNC, SyncType.SAVE)) {
			nbt.setInteger("plantsHarvested", this.plantsHarvested);
			nbt.setInteger("plantsGrown", this.plantsGrown);
			nbt.setInteger("houseSize", this.houseSize);

			if (type == SyncType.SAVE) {
				nbt.setInteger("planting", this.planting);
				nbt.setInteger("Plants", this.plants);
				nbt.setInteger("Level", this.levelTicks);
				nbt.setInteger("Check", this.checkTicks);
				nbt.setInteger("Plant", this.plantTicks);
				nbt.setInteger("growTicks", this.growTicks);
				nbt.setInteger("growTick", this.growTick);
			}
		}
		return nbt;
	}

	public boolean stableStone(int x, int y, int z) {
		Block block = this.world.getBlockState(new BlockPos(x, y, z)).getBlock();
        return !GreenhouseHelper.stableStone(block);
	}

	public boolean flawlessGlass(int x, int y, int z) {
		Block block = this.world.getBlockState(new BlockPos(x, y, z)).getBlock();
        return !GreenhouseHelper.flawlessGlass(block);
	}

	public boolean slabQuartz(int x, int y, int z) {
		return !GreenhouseHelper.slabQuartz(this.world, new BlockPos(x, y, z));
	}

	public FailedCoords checkSize(boolean check, World w, int hX, int hZ, int hoX, int hoZ, int fX, int fZ, int x, int y, int z) {
		this.houseSize = 0;
		FailedCoords start = end(check, w, hX, hZ, hoX, hoZ, fX, fZ, x, y, z);
		if (start.getBoolean()) {
			for (int i = 1; i <= 65; i++) {
				if (i == 65) {
                    return end(check, w, hX, hZ, hoX, hoZ, fX, fZ, x + fX * i, y, z + fZ * i);
				} else {
                    FailedCoords middle = middle(check, w, hX, hZ, hoX, hoZ, fX, fZ, x + fX * i, y, z + fZ * i);
					if (!middle.getBoolean()) {
						if (this.houseSize > 0) {
                            return end(check, w, hX, hZ, hoX, hoZ, fX, fZ, x + fX * i, y, z + fZ * i);
						} else {
							return middle;
						}
					}
					if (middle.getBoolean()) {
						this.houseSize++;
					}
				}
			}
		} else {
			return start;
		}

		return new FailedCoords(true, BlockCoords.EMPTY, FontHelper.translate("locator.none"));
	}

	public FailedCoords middle(boolean check, World w, int hX, int hZ, int hoX, int hoZ, int fX, int fZ, int x, int y, int z) {

		for (int i = 0; i <= 3; i++) {
            if (slabQuartz(x + hX * i, y + 2, z + hZ * i)) {
                return new FailedCoords(false, x + hX * i, y + 2, z + hZ * i, FontHelper.translate("greenhouse.quartz"));
			}
		}
		for (int i = 0; i <= 1; i++) {
            if (flawlessGlass(x + hX * 3, y + i, z + hZ * 3)) {
                return new FailedCoords(false, x + hX * 3, y + i, z + hZ * 3, FontHelper.translate("greenhouse.glass"));
			}
			if (flawlessGlass(x, y + i, z)) {
				return new FailedCoords(false, x, y + i, z, FontHelper.translate("greenhouse.glass"));
			}
		}

		return new FailedCoords(true, BlockCoords.EMPTY, FontHelper.translate("locator.none"));
	}

	public FailedCoords end(boolean check, World w, int hX, int hZ, int hoX, int hoZ, int fX, int fZ, int x, int y, int z) {

		for (int i = 0; i <= 3; i++) {
            if (stableStone(x + hX * i, y - 1, z + hZ * i)) {
                return new FailedCoords(false, x + hX * i, y - 1, z + hZ * i, FontHelper.translate("greenhouse.stable"));
			}
            if (slabQuartz(x + hX * i, y + 2, z + hZ * i)) {
                return new FailedCoords(false, x + hX * i, y + 2, z + hZ * i, FontHelper.translate("greenhouse.quartz"));
			}
		}
		for (int i = 0; i <= 1; i++) {
            if (stableStone(x + hX * 3, y + i, z + hZ * 3)) {
                return new FailedCoords(false, x + hX * 3, y + i, z + hZ * 3, FontHelper.translate("greenhouse.stable"));
			}
			if (stableStone(x, y + i, z)) {
				return new FailedCoords(false, x, y + i, z, FontHelper.translate("greenhouse.stable"));
			}
		}
		return new FailedCoords(true, BlockCoords.EMPTY, FontHelper.translate("locator.none"));
	}

    @Override
	@SideOnly(Side.CLIENT)
	public List<String> getWailaInfo(List<String> currenttip, IBlockState state) {
		currenttip.add(FontHelper.translate("greenhouse.size") + ": " + this.houseSize);
		return super.getWailaInfo(currenttip, state);
	}

	@Override
	public int getPlantsHarvested() {
		return this.plantsHarvested;
	}

	@Override
	public int getPlantsGrown() {
		return this.plantsGrown;
	}

	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return new ContainerFlawlessGreenhouse(player.inventory, this);
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return new GuiFlawlessGreenhouse(player.inventory, this);
	}
}