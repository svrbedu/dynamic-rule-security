package net.orangemile.security.acl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Orange Mile, Inc
 */
public class ThreadLocalAclContext implements AclContext {

	private ThreadLocal<Map<Object, Acl>> threadLocal;

	public ThreadLocalAclContext() {
		 threadLocal = new ThreadLocal<Map<Object, Acl>>();
	}
	
	/**
	 * 
	 */
	public void put(Object object, Acl acl) {
		Map<Object, Acl> map = threadLocal.get();
		if ( map == null ) {
			map = new HashMap<Object, Acl>();
			threadLocal.set(map);
		}
		map.put(object, acl);
	}
	
	/**
	 * 
	 */
	public Acl get( Object object ) {
		Map<Object, Acl> map = threadLocal.get();
		if ( map == null ) {
			return null;
		}	
		return map.get(object);
	}
	
	public Map<Object, Acl> getAll() {
		return threadLocal.get();
	}
}
