package net.orangemile.security.drools;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.orangemile.security.PermissionResolver;
import net.orangemile.security.SecureJoinPoint;
import net.orangemile.security.SecurityResolver;
import net.orangemile.security.SecuritySupport;
import net.orangemile.security.acl.AclContext;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.springframework.core.io.Resource;

/**
 * @author Orange Mile, Inc
 */
public class DroolsSecurityResolver implements SecurityResolver {
	
	protected RuleBase ruleBase;
	protected PermissionResolver permissionResolver;
	protected AclContext aclContext;
	
	/**
	 * Constructor - DRL is the path to the DRL Rule File
	 * @param drl
	 */
	public DroolsSecurityResolver(Resource drl) {
		try {
			InputStreamReader reader = new InputStreamReader(drl.getInputStream());
			ruleBase = DroolsUtil.compileDrl( reader );
		} catch ( Exception e ) {
			throw new RuntimeException(e);
		}
	}
			
	public void setPermissionResolver(PermissionResolver permissionResolver) {
		this.permissionResolver = permissionResolver;
	}
		
	public void setAclContext(AclContext aclContext) {
		this.aclContext = aclContext;
	}
	
	/**
	 * Secures the given object. The object maybe an iteratable, an array, a map, or a single object. 
	 * @param result
	 * @return
	 */
	public Object secureGeneric(Object result) {
		if ( result instanceof Iterable ) {
			secure( (Iterable) result );
		}
		else if ( result instanceof Object [] ) {
			result = secure( (Object []) result );
		} 
		else if ( result instanceof Map ) {
			secure( ((Map) result).values() );
		} 
		else {
			result = secureObject(result);
		}
		return result;
	}
		
	/**
	 * Runs the rules for the join-point. This is useful for restricting
	 * method call access. 
	 * @param pjp
	 * @return
	 */
	public Object secure(SecureJoinPoint sjp) {
		return secureObject(sjp);
	}
	
	/**
	 * Applies security to the array
	 * @param original
	 * @return
	 */
	public Object [] secure( Object [] original ) {
		List<Object> values = new ArrayList<Object>();
		for ( Object obj : original ) {
			obj = secureObject(obj);
			if ( obj != null ) {
				values.add(obj);
			}
		}
		return values.toArray();
	}
	
	/**
	 * Applies security to the collection
	 * @param result
	 */
	public void secure(Iterable result) {
		Iterator it = ((Iterable) result).iterator();
		while ( it.hasNext() ) {
			Object value = it.next();
			value = secureObject(value);
			if ( value == null ) {
				it.remove();
			}
		}
	}

	/**
	 * Executes the security rules
	 * @param value
	 * @return
	 */
	public Object secureObject(Object value) {
		StatefulSession session = null; 
		try {
			session = ruleBase.newStatefulSession();
			SecuritySupport securitySupport = new SecuritySupport( value, permissionResolver, aclContext );
			session.setGlobal("security", securitySupport );
			FactHandle valueFact = session.insert(value);
			session.fireAllRules();
			//	if the object was retracted, value will be null
			value = session.getObject(valueFact); 
		} finally {
			if ( session != null ) { 
				session.dispose();
			}
		}
		return value;
	}
}
