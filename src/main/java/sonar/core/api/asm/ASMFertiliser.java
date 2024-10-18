package sonar.core.api.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for handling custom fertiliser implementations in mods.
 * Ensure that this is compatible with NeoForge event or registry handling in 1.21.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ASMFertiliser {

	/**
	 * @return The mod ID associated with this fertiliser.
	 */
	String modid();

	/**
	 * @return Priority of this fertiliser event handling.
	 */
	int priority();
}
