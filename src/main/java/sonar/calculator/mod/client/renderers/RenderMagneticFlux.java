package sonar.calculator.mod.client.renderers;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import sonar.calculator.mod.client.models.ModelMagneticFlux;

public class RenderMagneticFlux extends TileEntitySpecialRenderer {
	public ModelMagneticFlux model;
	public String texture;

	public RenderMagneticFlux() {
		this.model = new ModelMagneticFlux();
		this.texture = "Calculator:textures/model/magnetic_flux.png";
	}

	@Override
    public void render(TileEntity entity, double x, double y, double z, float partialTicks, int destroyStage, float f) {
        //public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float partialTicks, int destroyStage) {
		/*
		RenderHelper.beginRender(x + 0.5F, y + 1.5F, z + 0.5F, RenderHelper.setMetaData(entity), texture);
		//model.render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);

		int r = 0;
		if (entity != null && entity.getWorld() != null) {
			r = (int) ((float) ((TileEntityMagneticFlux) entity).rotate * 360);
		}
		GL11.glRotated(r, 0, 1, 0);
		model.renderMagnet((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glRotated(-r, 0, 1, 0);
		RenderHelper.finishRender();
		*/
	}
}
