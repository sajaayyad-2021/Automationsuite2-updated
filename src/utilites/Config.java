package utilites;

public class Config {

	private String baseURL;

	private Auth auth = new Auth();
	private Defaults defaults = new Defaults();
	private LeaveSearch leaveSearch = new LeaveSearch();
	private Recruitment recruitment = new Recruitment();

	/* ==================== AUTH ==================== */
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

	/* ==================== DEFAULTS (PIM create) ==================== */
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

	/* ==================== LEAVE SEARCH ==================== */
	public static class LeaveSearch {
		private String fromDate;
		private String toDate;
		private String employeeName;
		private String status;
		private String leaveType;
		private String subUnit;
		private boolean resetFilters;

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
		 public boolean isResetFilters() {
		        return resetFilters;
		    }

		public void setLeaveSearch(String fromDate, String toDate, String employeeName, String status, String leaveType,
				String subUnit,boolean resetFilters) {
			this.fromDate = fromDate;
			this.toDate = toDate;
			this.employeeName = employeeName;
			this.status = status;
			this.leaveType = leaveType;
			this.subUnit = subUnit;
			this.resetFilters = resetFilters;
		}
	}

	/* ==================== RECRUITMENT (New) ==================== */
	public static class Recruitment {
		private String candidateFirstName;
		private String candidateMiddleName;
		private String candidateLastName;
		private String vacancy;
		private String email;
		private String contactNumber;
		private String resumePath;
		private String keywords;
		private String dateOfApplication; // yyyy-mm-dd
		private String notes;
		private boolean consent;

		public String getCandidateFirstName() {
			return candidateFirstName;
		}

		public String getCandidateMiddleName() {
			return candidateMiddleName;
		}

		public String getCandidateLastName() {
			return candidateLastName;
		}

		public String getVacancy() {
			return vacancy;
		}

		public String getEmail() {
			return email;
		}

		public String getContactNumber() {
			return contactNumber;
		}

		public String getResumePath() {
			return resumePath;
		}

		public String getKeywords() {
			return keywords;
		}

		public String getDateOfApplication() {
			return dateOfApplication;
		}

		public String getNotes() {
			return notes;
		}

		public boolean isConsent() {
			return consent;
		}

		public void setRecruitment(String first, String middle, String last, String vacancy, String email,
				String contact, String resumePath, String keywords, String dateOfApplication, String notes,
				boolean consent) {
			this.candidateFirstName = first;
			this.candidateMiddleName = middle;
			this.candidateLastName = last;
			this.vacancy = vacancy;
			this.email = email;
			this.contactNumber = contact;
			this.resumePath = resumePath;
			this.keywords = keywords;
			this.dateOfApplication = dateOfApplication;
			this.notes = notes;
			this.consent = consent;
		}
	}

	/* ==================== ROOT getters/setters ==================== */
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
	        String subUnit, boolean resetFilters) {  
	  
		this.leaveSearch.setLeaveSearch(fromDate, toDate, employeeName, status, leaveType, subUnit, resetFilters);
	}

	public Recruitment getRecruitment() {
		return recruitment;
	}

	public void setRecruitment(String first, String middle, String last, String vacancy, String email, String contact,
			String resume, String keywords, String date, String notes, boolean consent) {
		this.recruitment.setRecruitment(first, middle, last, vacancy, email, contact, resume, keywords, date, notes,
				consent);
	}
}
