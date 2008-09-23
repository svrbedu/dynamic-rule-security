package net.orangemile.security.acl;

import java.io.Serializable;

/**
 * This implementation supports 31 unique permissions per entry. 31 is a limit
 * imposed by the 32 bit integer used to store the mask. 
 * <p> 
 * The mask is a bit mask. Each bit represents a seperate entitlement. 
 * <p>
 * This implementation allows for a very efficient entitlement checks. 
 * <p>
 * @author Orange Mile, Inc
 */
public class Permission implements Serializable {

	/**
	 * Serial Version UID
	 */
	private static final long serialVersionUID = 8727990600488652164L;

	public static final Permission NONE = new Permission(0);		// 00000000

    public static final Permission ADMINISTRATION = new Permission(1); // 00000001

	public static final Permission DELETE = new Permission(2);	    // 00000010
	            
	public static final Permission READ = new Permission(4);		// 00000100
	            
	public static final Permission WRITE = new Permission(8);		// 00001000

	public static final Permission EXECUTE = new Permission(16);	// 00010000

    public static final Permission CREATE = new Permission(32);	    // 00100000

	private int mask;
	
	public Permission( int mask ) {
		this.mask = mask;
	}
	
	public int getMask() {
		return mask;		
	}	
	
	/**
	 * Combines the given permissions into a single permission
	 * @param perm
	 * @return
	 */
	public static Permission join(Permission... perm) {
		int mask = 0; 
		for ( Permission p : perm ) {
			mask |= p.getMask();
		}
		return new Permission(mask);
	}
}
