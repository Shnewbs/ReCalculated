package sonar.calculator.mod.common.item.modules;
/*
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import sonar.calculator.mod.CalculatorConfig;
import sonar.core.helpers.FontHelper;

public class WIPAtomicTerrainModule extends BaseTerrainModule {

	public WIPAtomicTerrainModule() {
		super.replacable = new Block[2];
		capacity = CalculatorConfig.getInteger("Advanced Terrain Module");
		maxReceive = CalculatorConfig.getInteger("Advanced Terrain Module");
		maxExtract = CalculatorConfig.getInteger("Advanced Terrain Module");
		maxTransfer = CalculatorConfig.getInteger("Advanced Terrain Module");

	}

	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitx, float hity, float hitz) {
		if (this.getEnergyLevel(stack) > 0) {
			if (!player.canPlayerEdit(pos, side, stack)) {
				return false;
			}
			Block block = world.getBlock(pos);
			if (player.isSneaking()) {
				int mode = this.getCurrentMode(stack);
				if (!(block instanceof ITileEntityProvider)) {
					this.setBlock(stack, block, mode);
				}
				FontHelper.sendMessage("Block " + mode + " = " + new ItemStack(world.getBlock(x, y, z), 1).getDisplayName(), world, player);
				incrementMode(stack);
			} else {
				if (this.getBlock(stack, 0) != null && this.getBlock(stack, 1) != null && block == this.getBlock(stack, 0)) {
					for (int s = 0; s < player.inventories.getSizeInventory(); s++) {
						ItemStack target = player.inventories.getStackInSlot(s);
						if (target != null && Block.getBlockFromItem(target.getItem()) != null && Block.getBlockFromItem(target.getItem()) == this.getBlock(stack, 1) && target.getItemDamage() == Item.getItemFromBlock(getBlock(stack, 1)).getDamage(new ItemStack(getBlock(stack, 1)))) {
							player.inventories.getStackInSlot(s).stackSize--;
							world.setBlock(x, y, z, block.getBlockFromItem(target.getItem()));
							int energy = this.getEnergyLevel(stack);
							stack.getTagCompound().setInteger("Energy", energy - 1);
						}
					}
				}
			}
		}

		if (this.getEnergyLevel(stack) == 0) {
			FontHelper.sendMessage("No Energy Stored", world, player);
		}
		return true;
	}

	public void setBlock(ItemStack stack, Block block, int i) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		ItemStack blockStack = new ItemStack(block, 1);
		NBTTagCompound tag = new NBTTagCompound();
		blockStack.writeToNBT(tag);
		stack.getTagCompound().setTag("" + i, tag);
	}

	public void incrementMode(ItemStack stack) {
		int current = this.getCurrentMode(stack);
		if (current + 1 != 2) {
			stack.getTagCompound().setInteger("Mode", current + 1);
		} else {
			stack.getTagCompound().setInteger("Mode", 0);
		}
	}

	public Block getBlock(ItemStack stack, int i) {
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		NBTTagCompound tag = (NBTTagCompound) stack.getTagCompound().getTag("" + i);
		if (tag != null) {
			ItemStack blockStack = ItemStack.loadItemStackFromNBT(tag);
			if (blockStack != null) {
				return Block.getBlockFromItem(blockStack.getItem());
			}
		}
		return null;
	}
}
*/