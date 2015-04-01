package auth.domain.entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("unused")
public class FacebookResponse implements Serializable {
	private static final long serialVersionUID = -3930296634079937670L;
	
	public FacebookResponse() {
	}
	
	private Data data;

	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
	
	public String getAppId() {
		return data.appId;
	}
	
	public boolean isValid() {
		return data.isValid;
	}
	
	public String getUserId() {
		return data.userId;
	}
	
	public Timestamp getExpiresAt() {
		return data.expiresAt;
	}

	private class Data {
		private String appId;
		private String application;
		private Error error;
		private Timestamp expiresAt;
		private Timestamp issuedAt;
		private boolean isValid;
		private List<String> scopes;
		private String userId;
		
		public Data() {			
		}
		
		@JsonProperty("app_id")
		public String getAppId() {
			return appId;
		}
		public void setAppId(String appId) {
			this.appId = appId;
		}
		
		public String getApplication() {
			return application;
		}
		public void setApplication(String application) {
			this.application = application;
		}
		
		public Error getError() {
			return error;
		}
		public void setError(Error error) {
			this.error = error;
		}
		
		@JsonProperty("expires_at")
		public Timestamp getExpiresAt() {
			return expiresAt;
		}
		public void setExpiresAt(Timestamp expiresAt) {
			this.expiresAt = expiresAt;
		}

		@JsonProperty("issued_at")
		public Timestamp getIssuedAt() {
			return issuedAt;
		}
		public void setIssuedAt(Timestamp issuedAt) {
			this.issuedAt = issuedAt;
		}

		@JsonProperty("is_valid")
		public boolean isValid() {
			return isValid;
		}
		public void setValid(boolean isValid) {
			this.isValid = isValid;
		}

		public List<String> getScopes() {
			return scopes;
		}
		public void setScopes(List<String> scopes) {
			this.scopes = scopes;
		}

		@JsonProperty("user_id")
		public String getUserId() {
			return userId;
		}
		public void setUserId(String userId) {
			this.userId = userId;
		}
		
		private class Error {
			private int code;
			private String message;
			private int subcode;
			
			public Error() {
			}
			
			public int getCode() {
				return code;
			}
			public void setCode(int code) {
				this.code = code;
			}
			
			public String getMessage() {
				return message;
			}
			public void setMessage(String message) {
				this.message = message;
			}
			
			public int getSubcode() {
				return subcode;
			}
			public void setSubcode(int subcode) {
				this.subcode = subcode;
			}
		}
	}
}
