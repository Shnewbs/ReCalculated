package sonar.core.integration;

import mcp.mobius.waila.api.*;
import mcp.mobius.waila.api.impl.ModuleRegistrar;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import sonar.core.common.tileentity.TileEntitySonar;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Integrations with WAILA - Registers all HUDs
 */
public class SonarWailaModule {

	public static List<String> FMPProviders = new ArrayList<>();

	public static void register() {
		// Registering WAILA Body Provider for TileEntitySonar
		ModuleRegistrar.instance().registerBodyProvider(new HUDSonar(), TileEntitySonar.class);

		// Registering WAILA Body Providers for FMP
		for (String fmpPart : FMPProviders) {
			if (fmpPart != null && !fmpPart.isEmpty()) {
				ModuleRegistrar.instance().registerBodyProvider(new HUDSonarFMP(), fmpPart);
			}
		}
	}

	public static void addFMPProvider(String string) {
		FMPProviders.add(string);
	}

	public static class HUDSonar implements IWailaDataProvider {

		@Nonnull
		@Override
		public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor, IWailaConfigHandler config) {
			TileEntity te = accessor.getTileEntity();
			if (te == null)
				return currenttip;
			if (te instanceof IWailaInfo) {
				IWailaInfo tile = (IWailaInfo) te;
				tile.getWailaInfo(currenttip, accessor.getBlockState());
			}

			return currenttip;
		}

		@Override
		public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
			return accessor.getStack();
		}

		@Nonnull
		@Override
		public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, BlockPos pos) {
			return tag;
		}

		@Nonnull
		@Override
		public List<String> getWailaHead(ItemStack arg0, List<String> currenttip, IWailaDataAccessor arg2, IWailaConfigHandler config) {
			return currenttip;
		}

		@Nonnull
		@Override
		public List<String> getWailaTail(ItemStack arg0, List<String> currenttip, IWailaDataAccessor arg2, IWailaConfigHandler config) {
			return currenttip;
		}
	}

	public static class HUDSonarFMP implements IWailaFMPProvider {

		@Override
		public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config) {
			Object handler = accessor.getTileEntity();
			if (handler instanceof IWailaInfo) {
				IWailaInfo tile = (IWailaInfo) handler;
				tile.getWailaInfo(currenttip, null);
			}

			return currenttip;
		}

		@Override
		public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config) {
			return currenttip;
		}

		@Override
		public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaFMPAccessor accessor, IWailaConfigHandler config) {
			return currenttip;
		}
	}
}
