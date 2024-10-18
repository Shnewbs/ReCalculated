package sonar.core.client.renderers;

import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.tileentity.TileEntity;

public interface ISonarTileRenderer extends ISonarCustomRenderer {

    Class<? extends TileEntity> getTileEntity();

    TileEntityRenderer<?> getTileRenderer();
}
