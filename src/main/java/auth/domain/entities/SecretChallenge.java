package auth.domain.entities;

import java.io.Serializable;

public class SecretChallenge implements Serializable {
	private static final long serialVersionUID = -6466059071602047467L;
	
	private String challenge;
	private boolean isAuthenticated;

	public String getChallenge() {
		return challenge;
	}
	public void setChallenge(String challenge) {
		this.challenge = challenge;
	}
	
	public boolean isAuthenticated() {
		return isAuthenticated;
	}
	public void setAuthenticated(boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}
}
