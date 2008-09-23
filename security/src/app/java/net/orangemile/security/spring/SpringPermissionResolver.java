package net.orangemile.security.spring;

import net.orangemile.security.PermissionResolver;

import org.springframework.security.Authentication;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.context.SecurityContext;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.util.Assert;

/**
 * Permission Resolver for Spring Security.
 * <p>
 * @author Orange Mile, 
 */
public class SpringPermissionResolver implements PermissionResolver {

	/**
	 * @return the currently logged-in username
	 */
	public String getUserName() {
		SecurityContext ctx = SecurityContextHolder.getContext();
		return ctx.getAuthentication().getName();
	}

	/**
	 * @return true, if the current user has the given role, false, otherwise.
	 */
	public boolean isUserInRole(String roleName) {
		SecurityContext ctx = SecurityContextHolder.getContext();
		Assert.notNull( ctx, "SecurityContext is null");
		Authentication auth = ctx.getAuthentication();
		Assert.notNull( auth, "Authentication is null");
		GrantedAuthority [] authorities = auth.getAuthorities();
		if ( authorities != null ) {
			for ( GrantedAuthority ga : authorities ) {
				if ( ga.getAuthority().equalsIgnoreCase(roleName) ) {
					return true;
				}
			}
		}
		return false;
	}
	
}
