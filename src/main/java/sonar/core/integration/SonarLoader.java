package sonar.core.integration;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import java.lang.reflect.Method;

public class SonarLoader {

	public static boolean wailaLoaded;
	public static boolean teslaLoaded;
	public static boolean calculatorLoaded;
	public static boolean logisticsLoaded;
	public static boolean fluxedRedstone;
	public static boolean mcmultipartLoaded;
	public static boolean ic2Loaded;
	public static boolean jeiLoaded;
	public static boolean rfLoaded;

	public static void initLoader() {
		mcmultipartLoaded = Loader.isModLoaded("mcmultipart");
		wailaLoaded = Loader.isModLoaded("Waila") || Loader.isModLoaded("waila");
		teslaLoaded = Loader.isModLoaded("Tesla") || Loader.isModLoaded("tesla");
		calculatorLoaded = Loader.isModLoaded("calculator") || Loader.isModLoaded("Calculator");
		logisticsLoaded = Loader.isModLoaded("PracticalLogistics2") || Loader.isModLoaded("practicallogistics2");
		fluxedRedstone = Loader.isModLoaded("fluxedredstone");
		rfLoaded = Loader.isModLoaded("redstoneflux");
		ic2Loaded = Loader.isModLoaded("IC2") || Loader.isModLoaded("ic2");
		jeiLoaded = Loader.isModLoaded("jei") || Loader.isModLoaded("JEI");
	}

	public static boolean jeiLoaded() {
		return jeiLoaded;
	}

	/**
	 * @returns if Industrial Craft is installed
	 */
	public static boolean isIc2Loaded() {
		return ic2Loaded;
	}

	/**
	 * @returns if Waila is installed
	 */
	public static boolean isWailaLoaded() {
		return wailaLoaded;
	}

	/**
	 * @returns if Calculator is installed
	 */
	public static boolean isCalculatorLoaded() {
		return calculatorLoaded;
	}

	/**
	 * @returns if Optics is installed
	 */
	public static boolean isOpticsLoaded() {
		return Loader.isModLoaded("Optics");
	}

	/**
	 * @returns if Forge Multipart is installed
	 */
	public static boolean isForgeMultipartLoaded() {
		return Loader.isModLoaded("ForgeMultipart");
	}

	/**
	 * @returns if Practical Logistics is installed
	 */
	public static boolean isLogisticsLoaded() {
		return logisticsLoaded;
	}

	public static boolean isEnabled(ItemStack stack) {
		if (stack.isEmpty()) {
			return true;
		}

		if (isCalculatorLoaded()) {
			try {
				Class<?> recipeClass = Class.forName("sonar.calculator.mod.CalculatorConfig");
				Method method = recipeClass.getMethod("isEnabled", ItemStack.class);
				return (Boolean) method.invoke(null, stack);
			} catch (Exception exception) {
				return false;
				// Uncomment for debugging
				// System.err.println("SonarCore: Calculator couldn't check if ItemStack was enabled: " + exception.getMessage());
			}
		}

		if (isLogisticsLoaded()) {
			try {
				Class<?> recipeClass = Class.forName("sonar.logistics.LogisticsConfig");
				Method method = recipeClass.getMethod("isEnabled", ItemStack.class);
				return (Boolean) method.invoke(null, stack);
			} catch (Exception exception) {
				return false;
				// Uncomment for debugging
				// System.err.println("SonarCore: PracticalLogistics couldn't check if ItemStack was enabled: " + exception.getMessage());
			}
		}

		return true;
	}
}
