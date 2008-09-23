package net.orangemile.security;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * @author Orange Mile, Inc
 */
@Aspect
public class SecurityAspect {

	// logging
	private static final Logger log = Logger.getLogger( SecurityAspect.class );
	
	private SecurityResolver securityResolver;
	
	public void setSecurityResolver(SecurityResolver securityResolver) {
		this.securityResolver = securityResolver;
	}

	/**
	 * 
	 * @param pjp
	 * @param secureAnnotation 
	 * @return
	 * @throws Throwable
	 */
	@Around(value="@annotation(secureAnnotation)")
    public Object secure(ProceedingJoinPoint pjp, net.orangemile.security.Secure secureAnnotation) throws Throwable {
		if ( secureAnnotation.secureCall() ) {
			log.debug("Checking security for SecureJoinPoint");
			SecureJoinPoint jp = new SecureJoinPoint();
			jp.setArgs(pjp.getArgs());
			jp.setKind(pjp.getKind());
			if ( pjp.getSignature() != null ) {
				jp.setSignatureDeclaringType(pjp.getSignature().getDeclaringType());
				jp.setSignatureModifier(pjp.getSignature().getModifiers());
				jp.setSignatureName(pjp.getSignature().getName());
			}
			jp.setTarget(pjp.getTarget());
			jp.setThisObject(pjp.getThis());
			Object result = securityResolver.secure(jp);
			if ( result == null ) {
				throw new SecurityException("You are not authorized for the requested operation.");				
			}
		}
		Object returnValue = pjp.proceed();
		if (secureAnnotation.secureResult()) {
			log.debug("Checking security for returned value");
			returnValue = securityResolver.secureGeneric(returnValue);
			if ( returnValue == null ) {
				throw new SecurityException("You are not authorized to view the requested dataset.");
			}
		}
		return returnValue;
	}
	
}
