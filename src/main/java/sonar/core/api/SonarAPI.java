package sonar.core.api;

import net.minecraftforge.fml.loading.FMLLoader;
import sonar.core.api.wrappers.FluidWrapper;

/**
 * Use this for all your interaction with the mod. This will be initialized by Sonar Core if it is loaded.
 * Make sure you only register stuff once Sonar Core is loaded, in the FMLPostInitializationEvent.
 */
public final class SonarAPI {

	public static final String MODID = "sonarcore";
	public static final String NAME = "sonarapi";
	public static final String VERSION = "1.0.1";

	private static FluidWrapper fluids = new FluidWrapper();

	public static void init() {
		if (FMLLoader.getLoadingModList().isModLoaded("sonarcore")) {
			try {
				fluids = (FluidWrapper) Class.forName("sonar.core.handlers.fluids.FluidHelper").getDeclaredConstructor().newInstance();
			} catch (Exception exception) {
				System.err.println(NAME + " : FAILED TO INITIALIZE API: " + exception.getMessage());
			}
		}
	}

	public static FluidWrapper getFluidHelper() {
		return fluids;
	}
}
