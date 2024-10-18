package sonar.core.client.renderers;

import java.util.ArrayList;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.Block;

public interface ISonarCustomRenderer {

    ModelResourceLocation getBlockModelResourceLocation();

    Block getBlock();

    boolean hasStaticRendering();

    TextureAtlasSprite getIcon();

    boolean doInventoryRendering();

    ArrayList<ResourceLocation> getAllTextures();

    // Add new render-related methods as per NeoForge standards if needed
}
