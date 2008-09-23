package net.orangemile.security;

/**
 * This class allows for externaling the permission management away
 * from the security rule implementation. Who manges the users and roles
 * is of no concern to this library. 
 * <p>
 * @author Orange Mile, Inc
 */
public interface PermissionResolver {

	/**
	 * Retrieves the username of the currently logged in user.
	 * @return
	 */
	public String getUserName();
	
	/**
	 * Checks whether the currently logged in user has the given role. 
	 * @param roleName
	 * @return
	 */
	public boolean isUserInRole( String roleName );
	
}
