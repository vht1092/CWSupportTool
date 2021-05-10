package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The primary key class for the CWST_SYS_USR_BRANCH database table.
 */
@Embeddable
public class CwstSysUsrBranchPK implements Serializable {
	// default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(unique = true, nullable = false, length = 50)
	private String username;

	@Column(name = "BRANCH_CODE", unique = true, nullable = false, length = 5)
	private String branchCode;

	public CwstSysUsrBranchPK() {
	}

	public CwstSysUsrBranchPK(String username, String branchCode) {
		this.username = username;
		this.branchCode = branchCode;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getBranchCode() {
		return this.branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CwstSysUsrBranchPK)) {
			return false;
		}
		CwstSysUsrBranchPK castOther = (CwstSysUsrBranchPK) other;
		return this.username.equals(castOther.username) && this.branchCode.equals(castOther.branchCode);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.username.hashCode();
		hash = hash * prime + this.branchCode.hashCode();

		return hash;
	}
}