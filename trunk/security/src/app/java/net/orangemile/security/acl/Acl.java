package net.orangemile.security.acl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.io.Serializable;

import org.apache.log4j.Logger;
import org.springframework.util.Assert;

/**
 * An ACL is an object level permission, with each AclEntry representing field level
 * permissions within the object. Each AclEntry has a sid that maps to the field name, and a 
 * permission which is a bit mask representing unique permissions for that field within that object.  
 * <p>
 * @author Orange Mile, Inc
 */
public class Acl implements Serializable {

	/**
	 * Serial version uid
	 */
	private static final long serialVersionUID = 8054949525926412507L;

	// logging
    private static final Logger log = Logger.getLogger( Acl.class );

	private Map<Object, AclEntry> objectAclEntryMap = new HashMap<Object, AclEntry>();

	
    /**
	 * Removes the given AclEntry from this Acl
	 * @param sid
	 */
	public void remove( Object sid ) {
		objectAclEntryMap.remove(sid);
	}
	
	/**
	 * Replaces the existing permission set with the given
	 * @param ace
	 */
	public void replace( AclEntry ace ) {
		Assert.notNull( ace, "AclEntry is a required field");
		objectAclEntryMap.put(ace.getSid(), ace);
	}

	/**
	 * Combines the current permission set with a new one
	 * @param ace
	 */
	public void join( AclEntry ace ) {
		Object sid = ace.getSid();
		AclEntry old = objectAclEntryMap.get(sid);
		if ( old == null ) {
			objectAclEntryMap.put(sid, ace);
		}
		else {
			int mask = old.getPermission().getMask() | ace.getPermission().getMask();
			AclEntry newer = new AclEntry(ace.getSid(), new Permission(mask));
			objectAclEntryMap.put(newer.getSid(), newer);
		}
	}
	
	/**
	 * @return a list of all Acl Entries
	 */
	public Collection<AclEntry> getEntries() {
		return objectAclEntryMap.values();
	}

	/**
	 * Checks whether the given Acl contains all the requested permissions
	 * @param sid
	 * @param permissions
	 * @return
	 */
	public boolean isGranted(Object sid, Permission... permissions) {
		AclEntry acle = objectAclEntryMap.get(sid);
		if ( acle == null ) {
			log.debug("Acl entry for " + sid + " is null, allowing access");
			return true;
		}
		int mask = acle.getPermission().getMask();
		for( Permission p : permissions ) {
			log.debug("Checking: " + mask + " against " + p.getMask() );
			if ( !((mask & p.getMask()) == p.getMask()) ) {				
				return false;
			}
		}
		return true;
	}
	
}
