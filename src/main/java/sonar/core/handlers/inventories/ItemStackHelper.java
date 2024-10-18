package sonar.core.handlers.inventories;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class ItemStackHelper {

	/** Checks if two itemstacks are the same ignoring the size */
	public static boolean equalStacksRegular(ItemStack stack1, ItemStack stack2) {
		return !stack1.isEmpty() && !stack2.isEmpty()
				&& stack1.getItem() == stack2.getItem()
				&& stack1.getDamage() == stack2.getDamage()
				&& ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	/** Turns blocks/items into ItemStacks */
	public static ItemStack getOrCreateStack(Object obj) {
		if (obj instanceof ItemStack) {
			ItemStack stack = ((ItemStack) obj).copy();
			if (stack.getCount() == 0) {
				stack.setCount(1);
			}
			return stack;
		} else if (obj instanceof Item) {
			return new ItemStack((Item) obj, 1);
		} else if (obj instanceof Block) {
			return new ItemStack((Block) obj, 1);
		}
		throw new RuntimeException(String.format("Invalid ItemStack: %s", obj));
	}

	/** Checks if the two input itemstacks come from the same mod */
	public static boolean matchingModid(ItemStack target, ItemStack stack) {
		String targetID = target.getItem().getRegistryName().getNamespace();
		String stackID = stack.getItem().getRegistryName().getNamespace();
		return targetID != null && !targetID.isEmpty() && !stackID.isEmpty() && targetID.equals(stackID);
	}

	/** Checks if stacks have matching ore dictionary entries */
	public static boolean matchingOreDictID(ItemStack target, ItemStack stack) {
		int[] stackIDs = OreDictionary.getOreIDs(stack);
		int[] filterIDs = OreDictionary.getOreIDs(target);
		for (int sID : stackIDs) {
			for (int fID : filterIDs) {
				if (sID == fID) {
					return true;
				}
			}
		}
		return false;
	}

	/** Gets the ItemStack to represent a block in the world */
	public static ItemStack getBlockItem(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		ItemStack stack = state.getBlock().getItem(world, pos, state);
		if (stack.isEmpty()) {
			stack = new ItemStack(Item.getItemFromBlock(state.getBlock()));
		}
		return stack;
	}
}
