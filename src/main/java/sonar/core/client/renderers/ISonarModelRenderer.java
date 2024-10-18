package sonar.core.client.renderers;

import net.minecraft.client.resources.model.ModelResourceLocation;

public interface ISonarModelRenderer extends ISonarCustomRenderer {

    ModelTechne getModel();
}
