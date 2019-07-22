package caceresenzo.apps.barcutoptimizer.logic.algorithms.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface AlgorithmSetting {
	
	/** i18n main key, a .name and a .description will be used with it to determine the name and the usage of this setting. */
	String key();
	
}