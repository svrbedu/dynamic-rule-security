package net.orangemile.security;

public class TestPermissionResolver implements PermissionResolver {

	public String getUserName() {
		return "jack";
	}

	public boolean isUserInRole(String roleName) {
		return true;
	}	
}
