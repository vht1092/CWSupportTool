package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the FULL_BRANCH database table.
 * 
 */
@Entity
@Table(name="FULL_BRANCH")
@NamedQuery(name="FullBranch.findAll", query="SELECT f FROM FullBranch f")
public class FullBranch implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="BRANCH_ADDR1", nullable=false, length=120)
	private String branchAddr1;

	@Id
	@Column(name="BRANCH_CODE", nullable=false, length=5)
	private String branchCode;

	@Column(name="BRANCH_NAME", nullable=false, length=120)
	private String branchName;

	@Column(name="PARENT_BRANCH", nullable=false, length=5)
	private String parentBranch;

	public FullBranch() {
	}

	public String getBranchAddr1() {
		return this.branchAddr1;
	}

	public void setBranchAddr1(String branchAddr1) {
		this.branchAddr1 = branchAddr1;
	}

	public String getBranchCode() {
		return this.branchCode;
	}

	public void setBranchCode(String branchCode) {
		this.branchCode = branchCode;
	}

	public String getBranchName() {
		return this.branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getParentBranch() {
		return this.parentBranch;
	}

	public void setParentBranch(String parentBranch) {
		this.parentBranch = parentBranch;
	}

}