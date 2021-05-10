package cwst.com.entities;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * The persistent class for the CWST_SYS_USR_BRANCH database table.
 */
@Entity
@Table(name = "PPT_SYS_USR_BRANCH")
@NamedQuery(name = "CwstSysUsrBranch.findAll", query = "SELECT c FROM CwstSysUsrBranch c")
public class CwstSysUsrBranch implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CwstSysUsrBranchPK id;

	public CwstSysUsrBranch() {
	}

	public CwstSysUsrBranch(CwstSysUsrBranchPK id) {
		this.id = id;
	}

	public CwstSysUsrBranchPK getId() {
		return this.id;
	}

	public void setId(CwstSysUsrBranchPK id) {
		this.id = id;
	}

}