package utilites;

public class Config {

	private String baseURL;

	private Auth auth = new Auth();
	private Defaults defaults = new Defaults();
	private LeaveSearch leaveSearch = new LeaveSearch();


	public static class Auth {
		private String userName;
		private String passWord;

		public String getUserName() {
			return userName;
		}

		public String getPassWord() {
			return passWord;
		}

		public void setAuth(String user, String pass) {
			this.userName = user;
			this.passWord = pass;
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

		public void setDefaults(String first, String middle, String last) {
			this.firstName = first;
			this.middleName = middle;
			this.lastName = last;
		}
	}

	public static class LeaveSearch {
		private String fromDate;
		private String toDate;
		private String employeeName;
		private String status;
		private String leaveType;
		private String subUnit;

		public String getFromDate() {
			return fromDate;
		}

		public String getToDate() {
			return toDate;
		}

		public String getEmployeeName() {
			return employeeName;
		}

		public String getStatus() {
			return status;
		}

		public String getLeaveType() {
			return leaveType;
		}

		public String getSubUnit() {
			return subUnit;
		}

		public void setLeaveSearch(String fromDate, String toDate, String employeeName, String status, String leaveType,
				String subUnit) {
			this.fromDate = fromDate;
			this.toDate = toDate;
			this.employeeName = employeeName;
			this.status = status;
			this.leaveType = leaveType;
			this.subUnit = subUnit;
		}
	}


	public String getBaseURL() {
		return baseURL;
	}

	public void setBaseURL(String baseURL) {
		this.baseURL = baseURL;
	}

	public Auth getAuth() {
		return auth;
	}

	public void setAuth(String userName, String passWord) {
		this.auth.setAuth(userName, passWord);
	}

	public Defaults getDefaults() {
		return defaults;
	}

	public void setDefaults(String first, String middle, String last) {
		this.defaults.setDefaults(first, middle, last);
	}

	public LeaveSearch getLeaveSearch() {
		return leaveSearch;
	}

	public void setLeaveSearch(String fromDate, String toDate, String employeeName, String status, String leaveType,
			String subUnit) {
		this.leaveSearch.setLeaveSearch(fromDate, toDate, employeeName, status, leaveType, subUnit);
	}
}
