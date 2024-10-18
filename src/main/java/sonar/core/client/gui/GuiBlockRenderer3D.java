package sonar.core.client.gui;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.vecmath.Vector3d;

import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.ForgeHooksClient;

public class GuiBlockRenderer3D implements IBlockAccess {

	protected static final Minecraft mc = Minecraft.getInstance();

	public final Vector3d origin = new Vector3d();
	public final Vector3d eye = new Vector3d();
	public List<GuiBlockRenderCache> blocks = new ArrayList<>();

	public int cubeSize;

	public GuiBlockRenderer3D(int cubeSize) {
		this.cubeSize = cubeSize;
	}

	public static class GuiBlockRenderCache {
		public BlockPos pos;
		public IBlockState state;
		public TileEntity tile;

		public GuiBlockRenderCache(BlockPos pos, IBlockState state) {
			this.pos = pos;
			this.state = state;
		}

		public GuiBlockRenderCache(BlockPos pos, IBlockState state, TileEntity tile) {
			this.pos = pos;
			this.state = state;
			this.tile = tile;
		}
	}

	public IBlockAccess world() {
		return this;
	}

	public boolean validPos(BlockPos pos) {
		int size = cubeSize / 2;
		return pos.getX() < size && pos.getX() > -size && pos.getY() < size && pos.getY() > -size && pos.getZ() < size && pos.getZ() > -size;
	}

	public void addBlock(BlockPos pos, IBlockState state) {
		if (validPos(pos)) {
			blocks.add(new GuiBlockRenderCache(pos, state));
		}
	}

	public void addBlock(BlockPos pos, IBlockState state, TileEntity tile) {
		if (validPos(pos)) {
			blocks.add(new GuiBlockRenderCache(pos, state, tile));
		}
	}

	public void renderInGui() {
		GlStateManager.enableCull();
		GlStateManager.enableRescaleNormal();

		RenderHelper.disableStandardItemLighting();
		mc.gameRenderer.disableLightmap();
		mc.getTextureManager().bind(TextureMap.LOCATION_BLOCKS_TEXTURE);

		GlStateManager.disableLighting();
		GlStateManager.enableTexture();
		GlStateManager.enableAlphaTest();

		Vector3d trans = new Vector3d(-origin.x + eye.x - 8 * 0.0625, -origin.y + eye.y, -origin.z + eye.z - 8 * 0.0625);

		for (BlockRenderLayer layer : BlockRenderLayer.values()) {
			ForgeHooksClient.setRenderLayer(layer);
			setGlStateForPass(layer);
			doWorldRenderPass(trans, layer);
		}

		RenderHelper.enableStandardItemLighting();
		GlStateManager.enableLighting();
		TileEntityRendererDispatcher.instance.xOff = origin.x - eye.x;
		TileEntityRendererDispatcher.instance.yOff = origin.y - eye.y;
		TileEntityRendererDispatcher.instance.zOff = origin.z - eye.z;

		for (int pass = 0; pass < 2; pass++) {
			setGlStateForPass(pass);
			doTileEntityRenderPass(pass);
		}
		setGlStateForPass(0);
	}

	private void doTileEntityRenderPass(int pass) {
		ForgeHooksClient.setRenderPass(pass);
		for (GuiBlockRenderCache cache : blocks) {
			if (cache.tile != null && cache.tile.shouldRenderInPass(pass)) {
				Vector3d at = new Vector3d(eye.x, eye.y, eye.z);
				BlockPos pos = cache.pos;
				at.x += pos.getX() - origin.x;
				at.y += pos.getY() - origin.y;
				at.z += pos.getZ() - origin.z;

				if (cache.tile.getClass() == TileEntityChest.class) {
					TileEntityChest chest = (TileEntityChest) cache.tile;
					at.x -= 0.5;
					at.z -= 0.5;
					GL11.glRotated(180, 0, 1, 0);
				}

				doSpecialRender(cache, at);

				if (cache.tile.getClass() == TileEntityChest.class) {
					GL11.glRotated(-180, 0, 1, 0);
				}
			}
		}
	}

	public void doSpecialRender(GuiBlockRenderCache cache, Vector3d at) {
		TileEntityRendererDispatcher.instance.render(cache.tile, at.x, at.y, at.z, 0);
	}

	private void doWorldRenderPass(Vector3d trans, BlockRenderLayer layer) {
		BufferBuilder wr = Tessellator.getInstance().getBuilder();
		wr.begin(7, DefaultVertexFormats.BLOCK);
		Tessellator.getInstance().getBuilder().setTranslation(trans.x, trans.y, trans.z);

		for (GuiBlockRenderCache cache : blocks) {
			IBlockState state = cache.state;
			BlockPos pos = cache.pos;
			Block block = state.getBlock();
			if (block.canRenderInLayer(state, layer)) {
				renderBlock(state, pos, this, Tessellator.getInstance().getBuilder());
			}
		}

		Tessellator.getInstance().end();
		Tessellator.getInstance().getBuilder().setTranslation(0, 0, 0);
	}

	public void renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder worldRendererIn) {
		try {
			BlockRendererDispatcher blockrendererdispatcher = mc.getBlockRenderer();
			EnumBlockRenderType type = state.getRenderShape();
			if (type != EnumBlockRenderType.MODEL) {
				blockrendererdispatcher.renderBlock(state, pos, blockAccess, worldRendererIn);
				return;
			}

			IBakedModel ibakedmodel = blockrendererdispatcher.getModelForState(state);
			state = state.getBlock().getExtendedState(state, this, pos);
			blockrendererdispatcher.getModelRenderer().tesselateBlock(blockAccess, ibakedmodel, state, pos, worldRendererIn, false);
		} catch (Throwable throwable) {
			throwable.printStackTrace();
		}
	}

	private void setGlStateForPass(BlockRenderLayer layer) {
		int pass = layer == BlockRenderLayer.TRANSLUCENT ? 1 : 0;
		setGlStateForPass(pass);
	}

	private void setGlStateForPass(int layer) {
		GlStateManager.color4f(1, 1, 1, 1);
		if (layer == 0) {
			GlStateManager.enableDepthTest();
			GlStateManager.disableBlend();
			GlStateManager.depthMask(true);
		} else {
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GlStateManager.depthMask(false);
		}
	}

	@Override
	public TileEntity getTileEntity(@Nonnull BlockPos pos) {
		for (GuiBlockRenderCache cache : blocks) {
			if (cache.pos.equals(pos) && cache.tile != null) {
				return cache.tile;
			}
		}
		return null;
	}

	@Override
	public int getLightEmission(@Nonnull BlockPos pos, int lightValue) {
		return 0;
	}

	@Nonnull
	@Override
	public IBlockState getBlockState(@Nonnull BlockPos pos) {
		for (GuiBlockRenderCache cache : blocks) {
			if (cache.pos.equals(pos) && cache.tile != null) {
				return cache.state;
			}
		}
		return Blocks.AIR.defaultBlockState();
	}

	@Override
	public boolean isEmptyBlock(@Nonnull BlockPos pos) {
		IBlockState state = getBlockState(pos);
		return state == null || state.getBlock() == Blocks.AIR;
	}

	@Nonnull
	@Override
	public Biome getBiome(@Nonnull BlockPos pos) {
		return Biome.getBiome(0);
	}

	@Override
	public int getDirectSignal(@Nonnull BlockPos pos, @Nonnull EnumFacing direction) {
		return 0;
	}

	@Nonnull
	@Override
	public WorldType getLevelType() {
		return WorldType.DEFAULT;
	}

	@Override
	public boolean isSolidBlockSide(@Nonnull BlockPos pos, @Nonnull EnumFacing side, boolean _default) {
		return false;
	}
}
