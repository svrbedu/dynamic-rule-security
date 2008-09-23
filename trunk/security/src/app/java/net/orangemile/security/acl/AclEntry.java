package net.orangemile.security.acl;

import org.springframework.util.Assert;

import java.io.Serializable;

/**
 * @author Orange Mile, Inc
 */
public class AclEntry implements Serializable {

	/**
	 * Serial Version UID 
	 */
	private static final long serialVersionUID = -2360829292375350454L;
	
	private Object sid;
	private Permission permission;
	
	public AclEntry( Object sid, Permission permission ) {
		Assert.notNull(sid, "Sid is a required field!");
		Assert.notNull(permission, "Permission is a required field!");
		this.sid = sid;
		this.permission = permission;
	}

	public Permission getPermission() {
		return permission;
	}

	public Object getSid() {
		return sid;
	}
}
