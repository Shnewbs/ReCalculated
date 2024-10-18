package sonar.core.client;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import sonar.core.helpers.RenderHelper;

public abstract class SonarTERender<T extends BlockEntity> implements BlockEntityRenderer<T> {
	protected final ModelPart model;
	protected final ResourceLocation texture;

	public SonarTERender(ModelPart model, ResourceLocation texture) {
		this.model = model;
		this.texture = texture;
	}

	@Override
	public void render(T blockEntity, float partialTicks, net.minecraft.client.renderer.MultiBufferSource bufferSource, int combinedLight, int combinedOverlay) {
		RenderHelper.beginRender(bufferSource, model, texture, blockEntity, partialTicks);
		model.renderToBuffer(bufferSource.getBuffer(RenderHelper.getRenderType(texture)), combinedLight, combinedOverlay);
		RenderHelper.finishRender();
		renderExtras(blockEntity);
	}

	/**
	 * For extra rotations and translations to be added, or rendering effects
	 */
	public void renderExtras(T blockEntity) {
		// Override to add extra rendering
	}

	@Override
	public boolean shouldRenderOffScreen(T blockEntity) {
		return true;
	}
}
