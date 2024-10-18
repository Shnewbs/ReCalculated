package sonar.core.client.renderers;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ISonarRendererProvider {

    @OnlyIn(Dist.CLIENT)
    ISonarCustomRenderer getRenderer();
}
