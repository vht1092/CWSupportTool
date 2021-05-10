package cwst.com;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import cwst.com.views.LoginView;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	// DEVELOPMENT
	// ===============================================================
	public static boolean isLoggedIn() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.isAuthenticated();
	}

	public static boolean hasRole(String role) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null && authentication.getAuthorities().contains(new SimpleGrantedAuthority(role));
	}

	public static String getFullname() {
		return "Tran Khanh Chau";
	}

	public static String getUserCardWord() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication.getName().equals("phuongntd2")) {
			return "PHUONGNTD";
		} else if (authentication.getName().equals("nguyenha")) {
			return "TDPD1";
		} else if (authentication.getName().equals("vanph1")) {
			return "AGIANG1";
		} else if (authentication.getName().equals("luyennth")) {
			return "20101";
		} else if (authentication.getName().equals("linhntd1")) {
			return "LINHNTD4";
		} else {
			return "";
		}

	}

	public static String getLastLogin() {
		TimeConverter timeConverter = new TimeConverter();
		return timeConverter.convertStrToDateTime("20161004092231025");
	}

	public static long getUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		switch (authentication.getName()) {
		case "ho":
			return new Long("1");
		case "dvkh":
			return new Long("2");
		case "donvi":
			return new Long("3");
		case "cwst":
			return new Long("4");
		case "hochecker":
			return new Long("400");
		case "donvichecker":
			return new Long("450");
		default:
			break;
		}
		return new Long("0");
	}

	public static String getUserName() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		return authentication != null ? authentication.getName() : "";
	}

	public static void makeLogout() {
		SecurityContextHolder.clearContext();
	}
	// ===============================================================

	// ============================= PRODUCTION =============================
	// public static boolean isLoggedIn() {
	// final Authentication authentication =
	// SecurityContextHolder.getContext().getAuthentication();
	// return authentication != null && authentication.isAuthenticated();
	// }
	//
	// public static void makeLogout() {
	// SecurityContextHolder.clearContext();
	// }
	//
	// public static boolean hasRole(String role) {
	// final Authentication authentication =
	// SecurityContextHolder.getContext().getAuthentication();
	// return authentication != null &&
	// authentication.getAuthorities().contains(new
	// SimpleGrantedAuthority(role));
	// }
	//
	// public static String getFullname() {
	// final Authentication authentication =
	// SecurityContextHolder.getContext().getAuthentication();
	// if (authentication != null) {
	// final CwstLdapUserDetails myUserDetails = (CwstLdapUserDetails)
	// authentication.getPrincipal();
	// return String.valueOf(myUserDetails.getFullname());
	// }
	// return "";
	// }
	//
	// public static long getUserId() {
	// final Authentication authentication =
	// SecurityContextHolder.getContext().getAuthentication();
	// if (authentication != null) {
	// final CwstLdapUserDetails myUserDetails = (CwstLdapUserDetails)
	// authentication.getPrincipal();
	// return myUserDetails.getId();
	// }
	// return 0;
	// }
	//
	// public static String getUserName() {
	// final Authentication authentication =
	// SecurityContextHolder.getContext().getAuthentication();
	// if (authentication != null) {
	// final CwstLdapUserDetails myUserDetails = (CwstLdapUserDetails)
	// authentication.getPrincipal();
	// return myUserDetails.getUsername();
	// }
	// return "";
	// }
	//
	// public static String getUserCardWord() {
	// Authentication authentication =
	// SecurityContextHolder.getContext().getAuthentication();
	// if (authentication != null) {
	// final CwstLdapUserDetails myUserDetails = (CwstLdapUserDetails)
	// authentication.getPrincipal();
	// return myUserDetails.getUsernamecardwork();
	// }
	// return "";
	// }
	// ===============================================================

}