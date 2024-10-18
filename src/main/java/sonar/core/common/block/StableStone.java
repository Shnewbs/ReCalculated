package sonar.core.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import sonar.core.common.block.properties.IMetaVariant;

import javax.annotation.Nonnull;

public class StableStone extends ConnectedBlock {
	public static final PropertyEnum<Variants> VARIANT = PropertyEnum.create("variant", Variants.class);

	public enum Variants implements IStringSerializable, IMetaVariant {

		Normal(7), Black(0), Blue(4), Brown(3), Cyan(6), Green(2), LightBlue(12), LightGrey(8), Lime(10), Magenta(13), Orange(14), Pink(9), Plain(15), Purple(5), Red(1), Yellow(11);

		public int dyeMeta;

		Variants(int dyeMeta) {
			this.dyeMeta = dyeMeta;
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}

		@Override
		public int getMeta() {
			return ordinal();
		}

		public int getDyeMeta() {
			return dyeMeta;
		}
	}

	public StableStone(Material material, int target) {
		super(material, target);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Nonnull
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return super.getStateFromMeta(meta);
	}

	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, NORTH, EAST, SOUTH, WEST, DOWN, UP);
	}
}
