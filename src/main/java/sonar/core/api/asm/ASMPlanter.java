package sonar.core.api.asm;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for handling custom planter implementations in mods.
 * Ensure compatibility with NeoForge event or registry handling in 1.21.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ASMPlanter {

	/**
	 * @return The mod ID associated with this planter.
	 */
	String modid();

	/**
	 * @return Priority of this planter event handling.
	 */
	int priority();
}
