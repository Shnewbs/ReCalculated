package sonar.core.api.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for handling custom fluid handler implementations in mods.
 * Ensure compatibility with NeoForge event or registry handling in 1.21.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FluidHandler {

	/**
	 * @return The mod ID required for the handler to load.
	 */
	String modid();

	/**
	 * @return Priority of the fluid handler event handling.
	 */
	int priority();
}
