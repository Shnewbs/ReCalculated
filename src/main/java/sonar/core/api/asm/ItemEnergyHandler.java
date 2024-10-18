package sonar.core.api.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for handling custom energy-based item implementations in mods.
 * Ensure compatibility with NeoForge event or registry handling in 1.21.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ItemEnergyHandler {

	/**
	 * @return The mod ID required for the handler to load.
	 */
	String modid();

	/**
	 * @return Priority of the item energy handler event handling.
	 */
	int priority();
}
