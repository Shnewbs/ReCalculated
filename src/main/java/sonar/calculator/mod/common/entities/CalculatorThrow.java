package sonar.calculator.mod.common.entities;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class CalculatorThrow extends BehaviorProjectileDispense {

	public int entity;

	public CalculatorThrow(int entity) {
		this.entity = entity;
	}

	@Nonnull
    @Override
	protected IProjectile getProjectileEntity(@Nonnull World world, @Nonnull IPosition pos, @Nonnull ItemStack stack) {
		switch (entity) {
		case 0:
			return new EntityBabyGrenade(world, pos.getX(), pos.getY(), pos.getZ());
		case 1:
			return new EntityGrenade(world, pos.getX(), pos.getY(), pos.getZ());
		case 2:
			return new EntitySmallStone(world, pos.getX(), pos.getY(), pos.getZ());
		case 3:
			return new EntitySoil(world, pos.getX(), pos.getY(), pos.getZ());
		}
		return null;
	}
}
