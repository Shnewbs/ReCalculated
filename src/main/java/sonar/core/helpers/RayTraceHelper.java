package sonar.core.helpers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.ForgeHooks;
import sonar.core.utils.Pair;

import javax.annotation.Nonnull;
import java.util.List;

public class RayTraceHelper {

	@Nonnull
	public static Pair<RayTraceResult, AxisAlignedBB> rayTraceBoxes(BlockPos pos, Vec3d start, Vec3d end, List<AxisAlignedBB> boxes) {
		Vec3d vecStart = start.subtract(pos.getX(), pos.getY(), pos.getZ());
		Vec3d vecEnd = end.subtract(pos.getX(), pos.getY(), pos.getZ());
		RayTraceResult rayTraceResult = null;
		AxisAlignedBB currentBB = null;

		for (AxisAlignedBB box : boxes) {
			rayTraceResult = box.calculateIntercept(vecStart, vecEnd);
			if (rayTraceResult != null) {
				currentBB = box;
				break;
			}
		}

		if (rayTraceResult == null) {
			return new Pair<>(null, null);
		}

		Vec3d hitVec = rayTraceResult.hitVec.addVector(pos.getX(), pos.getY(), pos.getZ());
		return new Pair<>(new RayTraceResult(hitVec, rayTraceResult.sideHit, pos), currentBB);
	}

	public static double getBlockReach(EntityPlayer player) {
		return player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() + 1;
	}

	public static Pair<Vec3d, Vec3d> getPlayerLookVec(EntityPlayer player) {
		double reachDistance = getBlockReach(player);
		Vec3d startPos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
		Vec3d endPos = startPos.add(player.getLookVec().scale(reachDistance));
		return new Pair<>(startPos, endPos);
	}

	public static Pair<Vec3d, Vec3d> getStandardLookVec(Entity entity, double reach) {
		Vec3d startPos = new Vec3d(entity.posX, entity.posY + entity.getEyeHeight(), entity.posZ);
		Vec3d endPos = startPos.add(entity.getLookVec().scale(reach));
		return new Pair<>(startPos, endPos);
	}

	public static RayTraceResult getRayTraceEyes(EntityPlayer player) {
		return ForgeHooks.rayTraceEyes(player, getBlockReach(player));
	}
}
