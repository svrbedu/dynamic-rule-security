package net.orangemile.security;

/**
 * @author Orange Mile, Inc 
 */
public interface SecurityResolver {

	public Object secureGeneric( Object secure );

	public Object secure(SecureJoinPoint pjp);
	
	public Object [] secure( Object [] original );
	
	public void secure(Iterable result);
	
	public Object secureObject(Object value);
}
