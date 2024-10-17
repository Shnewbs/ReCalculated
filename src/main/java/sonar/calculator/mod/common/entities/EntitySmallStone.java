package sonar.calculator.mod.common.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class EntitySmallStone extends EntityThrowable {
	public EntitySmallStone(World world) {
		super(world);
	}

	public EntitySmallStone(World world, EntityLivingBase player) {
		super(world, player);
	}

	public EntitySmallStone(World world, double x, double y, double z) {
		super(world, x, y, z);
	}

	@Override
	protected void onImpact(@Nonnull RayTraceResult result) {
		if (!this.world.isRemote) {
			if (result.entityHit != null) {
				result.entityHit.attackEntityFrom(CalculatorDamages.smallstone, 4.0F);
			}
			setDead();
		}
	}
}
