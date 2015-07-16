package gov.healthit.chpl.auth.permission;


public class JWTAuthenticatedPermission implements UserPermission {
	
	private static final long serialVersionUID = 1L;
	
	private String authority;
	
	public JWTAuthenticatedPermission(String authority){
		this.authority = authority;
	}
	
	@Override
	public String getAuthority() {
		return authority;
	}

	@Override
	public void setAuthority(String authority) {
		this.authority = authority;
	}
	
	@Override
	public String toString(){
		return authority;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof UserPermission))
			return false;

		UserPermission claim = (UserPermission) obj;
		return claim.getAuthority() == this.getAuthority() || claim.getAuthority().equals(this.getAuthority());
	}

	@Override
	public int hashCode() {
		return getAuthority() == null ? 0 : getAuthority().hashCode();
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

}
