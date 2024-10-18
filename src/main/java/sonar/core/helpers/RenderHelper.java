package sonar.core.helpers;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import sonar.core.SonarCore;
import sonar.core.client.BlockModelsCache;
import sonar.core.client.gui.GuiSonar;
import sonar.core.client.renderers.TransformationMatrix;
import sonar.core.client.renderers.Vector;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class RenderHelper {

	public static final RenderItem itemRender = Minecraft.getMinecraft().getRenderItem();
	public static final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
	public static final TextureManager textureManager = Minecraft.getMinecraft().getTextureManager();

	private static boolean blendSaved = false;
	private static int BLEND_SRC = -1, BLEND_DST = -1;

	// Used with EnumFacing
	public static final double[][] offsetMatrix = {
			{0, 0, 0}, {0, 0, 0}, {0, 0, 0},
			{1, 0, -1}, {1, 0, 0}, {0, 0, -1}
	};

	public static void saveBlendState() {
		BLEND_SRC = GlStateManager.glGetInteger(GL11.GL_BLEND_SRC);
		BLEND_DST = GlStateManager.glGetInteger(GL11.GL_BLEND_DST);
		blendSaved = true;
	}

	public static void restoreBlendState() {
		if (blendSaved) {
			GlStateManager.blendFunc(BLEND_SRC, BLEND_DST);
			blendSaved = false;
		}
	}

	public static int setMetaData(TileEntity tileentity) {
		if (tileentity.getWorld() == null) {
			return 0;
		} else {
			Block block = tileentity.getBlockType();
			return block != null ? tileentity.getBlockMetadata() : 0;
		}
	}

	public static void beginRender(double x, double y, double z, int meta, String texture) {
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		Minecraft.getMinecraft().getTextureManager().bindTexture(new ResourceLocation(texture));
		GlStateManager.pushMatrix();
		GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);

		int rotationAngle = switch (meta) {
			case 2 -> 180;
			case 3 -> 0;
			case 4 -> 90;
			case 5 -> 270;
			default -> 0;
		};

		GlStateManager.rotate(rotationAngle, 0.0F, 1.0F, 0.0F);
		GlStateManager.rotate(-0.625F, 0F, 1F, 0F);
	}

	public static void finishRender() {
		GlStateManager.popMatrix();
		GlStateManager.popMatrix();
	}

	public static void renderItem(GuiSonar screen, int x, int y, ItemStack stack) {
		GlStateManager.pushMatrix();
		net.minecraft.client.renderer.RenderHelper.enableGUIStandardItemLighting();
		GlStateManager.translate(0.0F, 0.0F, 32.0F);
		screen.setZLevel(200.0F);
		itemRender.zLevel = 200.0F;
		itemRender.renderItemAndEffectIntoGUI(stack, x, y);
		screen.setZLevel(0.0F);
		itemRender.zLevel = 0.0F;
		GlStateManager.translate(0.0F, 0.0F, -32.0F);
		net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
		GlStateManager.popMatrix();
	}

	public static void drawBoundingBox(AxisAlignedBB box, float r, float g, float b, float alpha) {
		drawBoundingBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ, r, g, b, alpha);
	}

	public static void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, float r, float g, float b, float alpha) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vertexBuffer = tessellator.getBuffer();
		vertexBuffer.begin(3, DefaultVertexFormats.POSITION_COLOR);

		// Define vertices for the bounding box
		vertexBuffer.pos(minX, minY, minZ).color(r, g, b, alpha).endVertex();
		vertexBuffer.pos(maxX, minY, minZ).color(r, g, b, alpha).endVertex();
		vertexBuffer.pos(maxX, minY, maxZ).color(r, g, b, alpha).endVertex();
		vertexBuffer.pos(minX, minY, maxZ).color(r, g, b, alpha).endVertex();

		tessellator.draw();
	}

	public static void resetLanguageRegistry() {
		SonarCore.logger.info("Resetting Language");
		Minecraft.getMinecraft().getLanguageManager().onResourceManagerReload(Minecraft.getMinecraft().getResourceManager());
		SonarCore.logger.info("Reset Language");
	}

	// Other methods remain unchanged for brevity...
}
