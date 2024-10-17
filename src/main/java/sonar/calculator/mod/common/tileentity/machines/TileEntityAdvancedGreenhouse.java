package sonar.calculator.mod.common.tileentity.machines;

import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.calculator.mod.Calculator;
import sonar.calculator.mod.CalculatorConfig;
import sonar.calculator.mod.client.gui.machines.GuiAdvancedGreenhouse;
import sonar.calculator.mod.common.containers.ContainerAdvancedGreenhouse;
import sonar.calculator.mod.common.tileentity.TileEntityBuildingGreenhouse;
import sonar.calculator.mod.utils.helpers.GreenhouseHelper;
import sonar.core.api.IFlexibleGui;
import sonar.core.api.energy.EnergyMode;
import sonar.core.handlers.inventories.handling.EnumFilterType;
import sonar.core.handlers.inventories.handling.filters.IExtractFilter;
import sonar.core.handlers.inventories.handling.filters.SlotFilter;
import sonar.core.handlers.inventories.handling.filters.SlotHelper;
import sonar.core.helpers.SonarHelper;

import java.util.ArrayList;

public class TileEntityAdvancedGreenhouse extends TileEntityBuildingGreenhouse implements IFlexibleGui {

	public static final SlotFilter resource_slots = new SlotFilter(null, new int[] { 0, 1, 2, 3, 4, 5, 6 }, new int[]{1});
	public static final SlotFilter plant_slots = new SlotFilter(null, new int[] { 8, 9, 10, 11, 12, 13, 14, 15, 16 }, new int[]{0,2,3,4,5});
	public int plants, lanterns, checkTicks, growTicks, growTick;

	public int[] logStack = new int[] { 0 };
	public int[] glassStack = new int[] { 4, 5 };
	public int[] plankStack = new int[] { 6 };
	public int[] stairStack = new int[] { 1, 2, 3 };

	public TileEntityAdvancedGreenhouse() {
		super(183, 30, 42, 94);
		super.storage.setCapacity(CalculatorConfig.ADVANCED_GREENHOUSE_STORAGE);
		super.storage.setMaxTransfer(CalculatorConfig.ADVANCED_GREENHOUSE_TRANSFER_RATE);
		super.inv.setSize(17);
		super.inv.getInsertFilters().put((SLOT,STACK,FACE) -> resource_slots.checkFilter(SLOT, FACE) ? checkInsert(SLOT,STACK,FACE) : null, EnumFilterType.EXTERNAL_INTERNAL);
		super.inv.getInsertFilters().put((SLOT,STACK,FACE) -> plant_slots.checkFilter(SLOT, FACE) ? isSeed(STACK) : null, EnumFilterType.EXTERNAL_INTERNAL);
		super.inv.getInsertFilters().put(SlotHelper.dischargeSlot(7), EnumFilterType.INTERNAL);
		super.inv.getExtractFilters().put(IExtractFilter.BLOCK_EXTRACT, EnumFilterType.EXTERNAL);
		super.energyMode = EnergyMode.RECIEVE;
		super.type = 2;
		super.maxLevel = 100000;
		super.plantTick = 10;
	}

	@Override
	public void update() {
		super.update();
		discharge(7);
	}

    @Override
	public ArrayList<BlockPos> getPlantArea() {
        ArrayList<BlockPos> coords = new ArrayList<>();
		for (int Z = -3; Z <= 3; Z++) {
			for (int X = -3; X <= 3; X++) {
                coords.add(this.pos.add(forward.getFrontOffsetX() * 4 + X, 0, forward.getFrontOffsetZ() * 4 + Z));
			}
		}
		return coords;
	}
	
    /**
     * adds gas, depends on day and night
     **/
    @Override
	public void gasLevels() {
		boolean day = this.world.isDaytime();
		if (day) {
            int add = this.plants / 5 * 8 - this.lanterns * 50;
			this.addGas(-add);
		}
		if (!day) {
            int add = this.plants / 5 * 2 + this.lanterns * 50;
			this.addGas(add);
		}
	}

    /**
     * gets plants inside greenhouse and sets it to this.plants
     **/
    @Override
	public int getPlants() {
		this.plants = 0;
		for (int Z = -3; Z <= 3; Z++) {
			for (int X = -3; X <= 3; X++) {
                BlockPos pos = this.pos.add(forward.getFrontOffsetX() * 4 + X, 0, forward.getFrontOffsetZ() * 4 + Z);
				if (this.world.getBlockState(pos).getBlock() instanceof IGrowable) {
					this.plants++;
				}
			}
		}
		return plants;
	}

    /**
     * gets lanterns inside greenhouse and sets it to this.lanterns
     **/
    @Override
	public int getLanterns() {
		this.lanterns = 0;
		for (int Z = -3; Z <= 3; Z++) {
			for (int X = -3; X <= 3; X++) {
				for (int Y = 0; Y <= 5; Y++) {
                    BlockPos pos = this.pos.add(forward.getFrontOffsetX() * 4 + X, Y, forward.getFrontOffsetZ() * 4 + Z);
					if (this.world.getBlockState(pos).getBlock() == Calculator.gas_lantern_on) {
						this.lanterns++;
					}
				}
			}
		}
		return lanterns;
	}

    /**
     * Hoes the ground
     **/
    @Override
	public void addFarmland() {
		for (int Z = -3; Z <= 3; Z++) {
			for (int X = -3; X <= 3; X++) {
                BlockPos pos = this.pos.add(4 * forward.getFrontOffsetX() + X, 0, 4 * forward.getFrontOffsetZ() + Z);
                if (X == 3 && Z == 3 || X == -3 && Z == -3 || X == 3 && Z == -3 || X == -3 && Z == 3) {
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
					IBlockState state = world.getBlockState(pos);
					Block block = state.getBlock();
					if (!block.isAir(state, world, pos) && GreenhouseHelper.r(world, pos)) {
						world.setBlockToAir(pos);
					}
				}
			}
		}
	}

    @Override
	public ArrayList<BlockPlace> getStructure() {
        ArrayList<BlockPlace> blocks = new ArrayList<>();

		int hX = SonarHelper.getHorizontal(forward).getFrontOffsetX();
		int hZ = SonarHelper.getHorizontal(forward).getFrontOffsetZ();

		int hoX = SonarHelper.getHorizontal(forward).getOpposite().getFrontOffsetX();
		int hoZ = SonarHelper.getHorizontal(forward).getOpposite().getFrontOffsetZ();

		int fX = forward.getFrontOffsetX();
		int fZ = forward.getFrontOffsetZ();

		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		//end
		for (int i = 1; i <= 6; i++) {
			blocks.add(new BlockPlace(BlockType.LOG, x, y + i, z, -1));
		}

		for (int i = 0; i <= 3; i++) {
            blocks.add(new BlockPlace(BlockType.LOG, x + hX * 4, y + i, z + hZ * 4, -1));
            blocks.add(new BlockPlace(BlockType.LOG, x + hoX * 4, y + i, z + hoZ * 4, -1));
		}

		for (int i = 1; i <= 3; i++) {
            blocks.add(new BlockPlace(BlockType.PLANKS, x + hX * i, y - 1, z + hZ * i, -1));
            blocks.add(new BlockPlace(BlockType.PLANKS, x + hoX * i, y - 1, z + hoZ * i, -1));
		}

		for (int j = 1; j <= 3; j++) {
			if (j != 3) {
				for (int i = 0; i <= 5; i++) {
                    blocks.add(new BlockPlace(BlockType.GLASS, x + hX * j, y + i, z + hZ * j, -1));
                    blocks.add(new BlockPlace(BlockType.GLASS, x + hoX * j, y + i, z + hoZ * j, -1));
				}
			} else {
				for (int i = 0; i <= 4; i++) {
                    blocks.add(new BlockPlace(BlockType.GLASS, x + hX * j, y + i, z + hZ * j, -1));
                    blocks.add(new BlockPlace(BlockType.GLASS, x + hoX * j, y + i, z + hoZ * j, -1));
				}
			}
		}

        x = pos.getX() + forward.getFrontOffsetX() * 8;
		y = pos.getY();
        z = pos.getZ() + forward.getFrontOffsetZ() * 8;
		for (int i = 0; i <= 3; i++) {
            blocks.add(new BlockPlace(BlockType.LOG, x + hX * 4, y + i, z + hZ * 4, -1));
            blocks.add(new BlockPlace(BlockType.LOG, x + hoX * 4, y + i, z + hoZ * 4, -1));
		}

		for (int i = 0; i <= 5; i++) {
			if (i <= 4) {
                blocks.add(new BlockPlace(BlockType.GLASS, x + hX * 3, y + i, z + hZ * 3, -1));
                blocks.add(new BlockPlace(BlockType.GLASS, x + hoX * 3, y + i, z + hoZ * 3, -1));
			}
            blocks.add(new BlockPlace(BlockType.GLASS, x + hX * 2, y + i, z + hZ * 2, -1));
            blocks.add(new BlockPlace(BlockType.GLASS, x + hoX * 2, y + i, z + hoZ * 2, -1));
		}

		for (int i = 0; i <= 6; i++) {
			if (i > 2) {
                blocks.add(new BlockPlace(BlockType.GLASS, x + hX, y + i, z + hZ, -1));
                blocks.add(new BlockPlace(BlockType.GLASS, x + hoX, y + i, z + hoZ, -1));
				blocks.add(new BlockPlace(BlockType.GLASS, x, y + i, z, -1));
			}
			if (i <= 2) {
                blocks.add(new BlockPlace(BlockType.PLANKS, x + hX, y + i, z + hZ, -1));
                blocks.add(new BlockPlace(BlockType.PLANKS, x + hoX, y + i, z + hoZ, -1));
			}
			if (i == 2) {
				blocks.add(new BlockPlace(BlockType.PLANKS, x, y + i, z, -1));
			}
		}

		for (int i = 2; i <= 3; i++) {
            blocks.add(new BlockPlace(BlockType.PLANKS, x + hX * i, y - 1, z + hZ * i, -1));
            blocks.add(new BlockPlace(BlockType.PLANKS, x + hoX * i, y - 1, z + hoZ * i, -1));
		}

		x = pos.getX();
		y = pos.getY();
		z = pos.getZ();
		//sides
		for (int i = 1; i <= 7; i++) {
			if (i != 4) {
				for (int s = 0; s <= 2; s++) {
					if (s == 0) {
                        blocks.add(new BlockPlace(BlockType.PLANKS, x + hX * 4 + fX * i, y + s, z + hZ * 4 + fZ * i, -1));
                        blocks.add(new BlockPlace(BlockType.PLANKS, x + hoX * 4 + fX * i, y + s, z + hoZ * 4 + fZ * i, -1));
					} else {
                        blocks.add(new BlockPlace(BlockType.GLASS, x + hX * 4 + fX * i, y + s, z + hZ * 4 + fZ * i, -1));
                        blocks.add(new BlockPlace(BlockType.GLASS, x + hoX * 4 + fX * i, y + s, z + hoZ * 4 + fZ * i, -1));
					}
				}
			}
		}

		for (int Y = 0; Y <= 3; Y++) {
            blocks.add(new BlockPlace(BlockType.LOG, x + hX * 4 + fX * 4, y + Y, z + hZ * 4 + fZ * 4, -1));
            blocks.add(new BlockPlace(BlockType.LOG, x + hoX * 4 + fX * 4, y + Y, z + hoZ * 4 + fZ * 4, -1));
		}

		//roof		
        blocks.add(new BlockPlace(BlockType.PLANKS, x + hX, y + 6, z + hZ, -1));
        blocks.add(new BlockPlace(BlockType.PLANKS, x + hoX, y + 6, z + hoZ, -1));

		for (int i = -1; i <= 9; i++) {
			for (int s = 3; s <= 7; s++) {
                blocks.add(new BlockPlace(BlockType.STAIRS, x + hX * intValues(s, BlockType.STAIRS) + fX * i, y + s, z + hZ * intValues(s, BlockType.STAIRS) + fZ * i, type("r")));
                blocks.add(new BlockPlace(BlockType.STAIRS, x + hoX * intValues(s, BlockType.STAIRS) + fX * i, y + s, z + hoZ * intValues(s, BlockType.STAIRS) + fZ * i, type("l")));
                blocks.add(new BlockPlace(BlockType.PLANKS, x + fX * i, y + 7, z + fZ * i, -1));
			}
		}

		//under-roof
		for (int i = -1; i <= 9; i++) {
			if (i != 0) {
				if (i != 4 && i != 8) {
                    blocks.add(new BlockPlace(BlockType.STAIRS, x + hX * 4 + fX * i, y + 3, z + hZ * 4 + fZ * i, type("d")));
                    blocks.add(new BlockPlace(BlockType.STAIRS, x + hoX * 4 + fX * i, y + 3, z + hoZ * 4 + fZ * i, type("d2")));
				}
				if (i != 8) {
                    blocks.add(new BlockPlace(BlockType.STAIRS, x + hX * 3 + fX * i, y + 4, z + hZ * 3 + fZ * i, type("d")));
                    blocks.add(new BlockPlace(BlockType.STAIRS, x + hX * 2 + fX * i, y + 5, z + hZ * 2 + fZ * i, type("d")));
                    blocks.add(new BlockPlace(BlockType.STAIRS, x + hX + fX * i, y + 6, z + hZ + fZ * i, type("d")));

                    blocks.add(new BlockPlace(BlockType.STAIRS, x + hoX * 3 + fX * i, y + 4, z + hoZ * 3 + fZ * i, type("d2")));
                    blocks.add(new BlockPlace(BlockType.STAIRS, x + hoX * 2 + fX * i, y + 5, z + hoZ * 2 + fZ * i, type("d2")));
                    blocks.add(new BlockPlace(BlockType.STAIRS, x + hoX + fX * i, y + 6, z + hoZ + fZ * i, type("d2")));
				}
			}
		}
		return blocks;
	}

    @Override
	public int[] getSlotsForType(BlockType type) {
		switch (type) {
		case LOG:
			return logStack;
		case STAIRS:
			return stairStack;
		case PLANKS:
			return plankStack;
		case GLASS:
			return glassStack;
		default:
			break;
		}
		return new int[0];
	}


	@Override
	public Object getServerElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return new ContainerAdvancedGreenhouse(player.inventory, this);
	}

	@Override
	public Object getClientElement(Object obj, int id, World world, EntityPlayer player, NBTTagCompound tag) {
		return new GuiAdvancedGreenhouse(player.inventory, this);
	}
}