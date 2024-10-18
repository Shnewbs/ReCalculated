package sonar.core.api.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for handling energy in tile entities.
 * Ensure compatibility with NeoForge event or registry handling in 1.21.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TileEnergyHandler {

	/**
	 * @return The mod ID required for the tile energy handler.
	 */
	String modid();

	/**
	 * @return Priority of the tile energy handler event handling.
	 */
	int priority();
}
