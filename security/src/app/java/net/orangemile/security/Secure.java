package net.orangemile.security;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This is a marker to programatically specify which methods should have their
 * return object/collection secured and which call stacks require security.
 * <p>
 * @author Orange Mile, Inc
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Secure {

	boolean secureCall() default false;
	boolean secureResult() default false;
	
}
