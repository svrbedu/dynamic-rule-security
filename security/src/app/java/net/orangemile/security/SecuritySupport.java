package net.orangemile.security;

import org.springframework.util.Assert;

import net.orangemile.security.acl.Acl;
import net.orangemile.security.acl.AclContext;
import net.orangemile.security.acl.AclEntry;
import net.orangemile.security.acl.Permission;

/**
 * @author Orange Mile, Inc
 */
public class SecuritySupport implements PermissionResolver {

	private PermissionResolver permissionResolver;
	private AclContext aclContext;
	private Object object;
	
	public SecuritySupport( Object object, PermissionResolver permissionResolver, AclContext aclContext ) {
		Assert.notNull(permissionResolver, "PermissionResolver is required");
		Assert.notNull(aclContext, "AclContext is required");
		
		this.object = object;
		this.permissionResolver = permissionResolver;
		this.aclContext = aclContext;
	}

	public String getUserName() {
		return permissionResolver.getUserName();
	}
	
	public boolean isUserInRole(String roleName) {
		return permissionResolver.isUserInRole(roleName);
	}
	
	public void addAcl(Object sid, Permission... permissions) {
		try {
			Acl acl = aclContext.get(object);
			if (acl == null) {
				acl = new Acl();
				aclContext.put(object, acl);
			}			
			AclEntry aclEntry = new AclEntry(sid, Permission.join(permissions));
			acl.join(aclEntry);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public void setAcl(Object sid, Permission... permissions) {
		try {
			Acl acl = aclContext.get(object);
			if (acl == null) {
				acl = new Acl();
				aclContext.put(object, acl);
			}			
			AclEntry aclEntry = new AclEntry(sid, Permission.join(permissions));
			acl.replace(aclEntry);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Sets a NONE permission for the given sid
	 * @param sid
	 */
	public void restrict( Object sid ) {
		setAcl(sid, Permission.NONE);
	}
	
}
