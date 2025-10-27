package utilites;

public class Config {

	private String baseURL;
	private Auth auth;
	private Defaults defaults;

	public static class Auth {
		private String userName;
		private String passWord;

		public String getUserName() {
			return userName;
		}

		public String getPassWord() {
			return passWord;
		}
	}

	public static class Defaults {
		private String firstName;
		private String middleName;
		private String lastName;

		public String getFirstName() {
			return firstName;
		}

		public String getMiddleName() {
			return middleName;
		}

		public String getLastName() {
			return lastName;
		}
	}

	public String getBaseURL() {
		return baseURL;
	}

	public Auth getAuth() {
		return auth;
	}

	public Defaults getDefaults() {
		return defaults;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public void setAuth(String userName, String passWord) {
		this.auth = new Auth();
		this.auth.userName = userName;
		this.auth.passWord = passWord;
	}

	public void setDefaults(String firstName, String middleName, String lastName) {
		this.defaults = new Defaults();
		this.defaults.firstName = firstName;
		this.defaults.middleName = middleName;
		this.defaults.lastName = lastName;
	}
}
