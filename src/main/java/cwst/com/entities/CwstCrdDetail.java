package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the ppt_crd_detail database table.
 */
@Entity
@Table(name = "ppt_crd_detail")
@NamedQuery(name = "CwstCrdDetail.findAll", query = "SELECT c FROM CwstCrdDetail c")
public class CwstCrdDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name = "TABLE_GEN_CRDDET", table = "PPT_HIBERNATE_SEQUENCES", valueColumnName = "SEQUENCE_NEXT_HI_VALUE", pkColumnName = "SEQUENCE_NAME", pkColumnValue = "ppt_crd_detail")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_CRDDET")
	@Column(unique = true, nullable = false)
	private long id;

	@Column(name = "CIF_NO", length = 100)
	private String cifNo;

	@Column(name = "CRD_PRIN_SUPP", length = 1)
	private String crdPrinSupp;

	@Column(name = "CRD_TYPE", length = 10)
	private String crdType;

	@Column(name = "CUST_NAME", length = 100)
	private String custName;

	@Column(name = "FILE_PIN", length = 50)
	private String filePin;

	@Column(name = "FW_BRN_CDE", length = 3)
	private String fwBrnCde;

	@Column(name = "ISSUE_DATE", length = 8)
	private String issueDate;

	@Column(name = "ISSUE_TYPE", length = 15)
	private String issueType;

	@Column(length = 20)
	private String pan;

	@Column(name = "BRCH_CDE", length = 3)
	private String brchCde;

	@Column(name = "REC_MK", precision = 1)
	private boolean recMk;

	@Column(name = "REC_MK_DATE", length = 17)
	private String recMkDate;

	@Column(name = "REC_MK_ISCHECK", precision = 1)
	private boolean recMkIscheck;

	@Column(name = "REC_MK_LOCK", precision = 1)
	private boolean recMkLock;

	@Column(name = "TRANS_BRANCH", precision = 1)
	private boolean transBranch;

	@Column(name = "TRANS_BRANCH_DATE", length = 17)
	private String transBranchDate;

	@Column(name = "TRANS_BRANCH_EMP_CODE", length = 6)
	private String transBranchEmpCode;

	@Column(name = "TRANS_BRANCH_EMP_NAME", length = 50)
	private String transBranchEmpName;

	@Column(name = "TRANS_BRANCH_ISCHECK", precision = 1)
	private boolean transBranchIscheck;

	@Column(name = "TRANS_BRANCH_LOCK", precision = 1)
	private boolean transBranchLock;

	@Column(name = "TRANS_BRANCH_NOTE", length = 250)
	private String transBranchNote;

	@Column(name = "TRANS_CUST", precision = 1)
	private boolean transCust;

	@Column(name = "TRANS_CUST_DATE", length = 17)
	private String transCustDate;

	@Column(name = "TRANS_CUST_ISCHECK", precision = 1)
	private boolean transCustIscheck;

	@Column(name = "TRANS_CUST_LOCK", precision = 1)
	private boolean transCustLock;

	@Column(name = "TRANS_EMP", precision = 1)
	private boolean transEmp;

	@Column(name = "TRANS_EMP_CODE", length = 6)
	private String transEmpCode;

	@Column(name = "TRANS_EMP_DATE", length = 17)
	private String transEmpDate;

	@Column(name = "TRANS_EMP_DATE1", length = 17)
	private String transEmpDate1;

	@Column(name = "TRANS_EMP_DATE2", length = 17)
	private String transEmpDate2;

	@Column(name = "TRANS_EMP_RENDER_DATE", length = 17)
	private String transEmpRenderDate;

	@Column(name = "TRANS_EMP_RENDER_DATE1", length = 17)
	private String transEmpRenderDate1;

	@Column(name = "TRANS_EMP_RENDER_DATE2", length = 17)
	private String transEmpRenderDate2;

	@Column(name = "TRANS_EMP_RENDER", length = 200)
	private String transEmpRender;

	@Column(name = "TRANS_EMP_RENDER1", length = 200)
	private String transEmpRender1;

	@Column(name = "TRANS_EMP_RENDER2", length = 200)
	private String transEmpRender2;

	@Column(name = "TRANS_MK_REAL_TIME_LOCK", length = 17)
	private String transMkRealTimeLock;

	@Column(name = "TRANS_MK_REAL_TIME_CHECK", length = 17)
	private String transMkRealTimeCheck;

	@Column(name = "TRANS_BRN_REC_REAL_TIME_LOCK", length = 17)
	private String transBrnRecRealTimeLock;

	@Column(name = "TRANS_BRN_REC_REAL_TIME_CHECK", length = 17)
	private String transBrnRecRealTimeCheck;

	@Column(name = "TRANS_CUST_REAL_TIME_LOCK", length = 17)
	private String transCustRealTimeLock;

	@Column(name = "TRANS_CUST_REAL_TIME_CHECK", length = 17)
	private String transCustRealTimeCheck;

	@Column(name = "TRANS_CUST_STATUS", length = 3)
	private String transCustStatus;

	@Column(name = "TRANS_CUST_NOTE", length = 100)
	private String transCustNote;

	@Column(name = "GTTN", length = 1)
	private String gttn;

	@Column(name = "TIME_GTTN", length = 17)
	private String timeGttn;

	@Column(name = "XACNHAN_GTTN", length = 1)
	private String xacNhanGttn;

	@Column(name = "TIME_XACNHAN_GTTN", length = 17)
	private String timeXacNhanGttn;

	public String getTransEmpDate1() {
		return transEmpDate1;
	}

	public void setTransEmpDate1(String transEmpDate1) {
		this.transEmpDate1 = transEmpDate1;
	}

	public String getTransEmpDate2() {
		return transEmpDate2;
	}

	public void setTransEmpDate2(String transEmpDate2) {
		this.transEmpDate2 = transEmpDate2;
	}

	public String getTransEmpRenderDate() {
		return transEmpRenderDate;
	}

	public void setTransEmpRenderDate(String transEmpRenderDate) {
		this.transEmpRenderDate = transEmpRenderDate;
	}

	public String getTransEmpRenderDate1() {
		return transEmpRenderDate1;
	}

	public void setTransEmpRenderDate1(String transEmpRenderDate1) {
		this.transEmpRenderDate1 = transEmpRenderDate1;
	}

	public String getTransEmpRenderDate2() {
		return transEmpRenderDate2;
	}

	public void setTransEmpRenderDate2(String transEmpRenderDate2) {
		this.transEmpRenderDate2 = transEmpRenderDate2;
	}

	public String getTransEmpRender() {
		return transEmpRender;
	}

	public void setTransEmpRender(String transEmpRender) {
		this.transEmpRender = transEmpRender;
	}

	public String getTransEmpRender1() {
		return transEmpRender1;
	}

	public void setTransEmpRender1(String transEmpRender1) {
		this.transEmpRender1 = transEmpRender1;
	}

	public String getTransEmpRender2() {
		return transEmpRender2;
	}

	public void setTransEmpRender2(String transEmpRender2) {
		this.transEmpRender2 = transEmpRender2;
	}

	public boolean isTransEmpIscheck1() {
		return transEmpIscheck1;
	}

	public void setTransEmpIscheck1(boolean transEmpIscheck1) {
		this.transEmpIscheck1 = transEmpIscheck1;
	}

	public boolean isTransEmpIscheck2() {
		return transEmpIscheck2;
	}

	public void setTransEmpIscheck2(boolean transEmpIscheck2) {
		this.transEmpIscheck2 = transEmpIscheck2;
	}

	public boolean isTransEmpIscheck3() {
		return transEmpIscheck3;
	}

	public void setTransEmpIscheck3(boolean transEmpIscheck3) {
		this.transEmpIscheck3 = transEmpIscheck3;
	}

	public boolean isTransEmpLock1() {
		return transEmpLock1;
	}

	public void setTransEmpLock1(boolean transEmpLock1) {
		this.transEmpLock1 = transEmpLock1;
	}

	public boolean isTransEmpLock2() {
		return transEmpLock2;
	}

	public void setTransEmpLock2(boolean transEmpLock2) {
		this.transEmpLock2 = transEmpLock2;
	}

	public boolean isTransEmpLock3() {
		return transEmpLock3;
	}

	public void setTransEmpLock3(boolean transEmpLock3) {
		this.transEmpLock3 = transEmpLock3;
	}

	@Column(name = "TRANS_EMP_ISCHECK", precision = 1)
	private boolean transEmpIscheck;

	@Column(name = "TRANS_EMP_ISCHECK1", precision = 1)
	private boolean transEmpIscheck1;

	@Column(name = "TRANS_EMP_ISCHECK2", precision = 1)
	private boolean transEmpIscheck2;

	@Column(name = "TRANS_EMP_ISCHECK3", precision = 1)
	private boolean transEmpIscheck3;

	@Column(name = "TRANS_EMP_ISCHECK4", precision = 1)
	private boolean transEmpIscheck4;

	public boolean isTransEmpIscheck4() {
		return transEmpIscheck4;
	}

	public void setTransEmpIscheck4(boolean transEmpIscheck4) {
		this.transEmpIscheck4 = transEmpIscheck4;
	}

	public boolean isTransEmpLock4() {
		return transEmpLock4;
	}

	public void setTransEmpLock4(boolean transEmpLock4) {
		this.transEmpLock4 = transEmpLock4;
	}

	@Column(name = "TRANS_EMP_LOCK", precision = 1)
	private boolean transEmpLock;

	@Column(name = "TRANS_EMP_LOCK1", precision = 1)
	private boolean transEmpLock1;

	@Column(name = "TRANS_EMP_LOCK2", precision = 1)
	private boolean transEmpLock2;

	@Column(name = "TRANS_EMP_LOCK3", precision = 1)
	private boolean transEmpLock3;

	@Column(name = "TRANS_EMP_LOCK4", precision = 1)
	private boolean transEmpLock4;

	@Column(name = "TRANS_EMP_ISRENDER", precision = 1)
	private boolean transEmpIsRender;

	@Column(name = "TRANS_EMP_ISRENDER1", precision = 1)
	private boolean transEmpIsRender1;

	@Column(name = "TRANS_EMP_ISRENDER2", precision = 1)
	private boolean transEmpIsRender2;

	public boolean isTransEmpIsRender() {
		return transEmpIsRender;
	}

	public void setTransEmpIsRender(boolean transEmpIsRender) {
		this.transEmpIsRender = transEmpIsRender;
	}

	public boolean isTransEmpIsRender1() {
		return transEmpIsRender1;
	}

	public void setTransEmpIsRender1(boolean transEmpIsRender1) {
		this.transEmpIsRender1 = transEmpIsRender1;
	}

	public boolean isTransEmpIsRender2() {
		return transEmpIsRender2;
	}

	public void setTransEmpIsRender2(boolean transEmpIsRender2) {
		this.transEmpIsRender2 = transEmpIsRender2;
	}

	@Column(name = "TRANS_EMP_NAME", length = 50)
	private String transEmpName;

	@Column(name = "TRANS_EMP_NOTE", length = 250)
	private String transEmpNote;

	@Column(name = "TRANS_MK", precision = 1)
	private boolean transMk;

	@Column(name = "TRANS_MK_DATE", length = 17)
	private String transMkDate;

	@Column(name = "TRANS_MK_ISCHECK", precision = 1)
	private boolean transMkIscheck;

	@Column(name = "TRANS_MK_LOCK", precision = 1)
	private boolean transMkLock;

	@Column(name = "TRANS_BRANCH_REC", precision = 1)
	private boolean transBranchRec;

	@Column(name = "TRANS_BRANCH_REC_DATE", length = 17)
	private String transBranchRecDate;

	@Column(name = "TRANS_BRANCH_REC_LOCK", precision = 1)
	private boolean transBranchRecLock;

	@Column(name = "TRANS_BRANCH_REC_CHECK", precision = 1)
	private boolean transBranchRecCheck;

	public CwstCrdDetail() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCifNo() {
		return this.cifNo;
	}

	public void setCifNo(String cifNo) {
		this.cifNo = cifNo;
	}

	public String getCrdPrinSupp() {
		return this.crdPrinSupp;
	}

	public void setCrdPrinSupp(String crdPrinSupp) {
		this.crdPrinSupp = crdPrinSupp;
	}

	public String getCrdType() {
		return this.crdType;
	}

	public void setCrdType(String crdType) {
		this.crdType = crdType;
	}

	public String getCustName() {
		return this.custName;
	}

	public void setCustName(String custName) {
		this.custName = custName;
	}

	public String getFilePin() {
		return this.filePin;
	}

	public void setFilePin(String filePin) {
		this.filePin = filePin;
	}

	public String getFwBrnCde() {
		return this.fwBrnCde;
	}

	public void setFwBrnCde(String fwBrnCde) {
		this.fwBrnCde = fwBrnCde;
	}

	public String getIssueDate() {
		return this.issueDate;
	}

	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}

	public String getIssueType() {
		return this.issueType;
	}

	public void setIssueType(String issueType) {
		this.issueType = issueType;
	}

	public String getPan() {
		return this.pan;
	}

	public void setPan(String pan) {
		this.pan = pan;
	}

	public boolean getRecMk() {
		return this.recMk;
	}

	public void setRecMk(boolean recMk) {
		this.recMk = recMk;
	}

	public String getRecMkDate() {
		return this.recMkDate;
	}

	public void setRecMkDate(String recMkDate) {
		this.recMkDate = recMkDate;
	}

	public boolean getRecMkIscheck() {
		return this.recMkIscheck;
	}

	public void setRecMkIscheck(boolean recMkIscheck) {
		this.recMkIscheck = recMkIscheck;
	}

	public boolean getRecMkLock() {
		return this.recMkLock;
	}

	public void setRecMkLock(boolean recMkLock) {
		this.recMkLock = recMkLock;
	}

	public boolean getTransBranch() {
		return this.transBranch;
	}

	public void setTransBranch(boolean transBranch) {
		this.transBranch = transBranch;
	}

	public String getTransBranchDate() {
		return this.transBranchDate;
	}

	public void setTransBranchDate(String transBranchDate) {
		this.transBranchDate = transBranchDate;
	}

	public String getTransBranchEmpCode() {
		return this.transBranchEmpCode;
	}

	public void setTransBranchEmpCode(String transBranchEmpCode) {
		this.transBranchEmpCode = transBranchEmpCode;
	}

	public String getTransBranchEmpName() {
		return this.transBranchEmpName;
	}

	public void setTransBranchEmpName(String transBranchEmpName) {
		this.transBranchEmpName = transBranchEmpName;
	}

	public boolean getTransBranchIscheck() {
		return this.transBranchIscheck;
	}

	public void setTransBranchIscheck(boolean transBranchIscheck) {
		this.transBranchIscheck = transBranchIscheck;
	}

	public boolean getTransBranchLock() {
		return this.transBranchLock;
	}

	public void setTransBranchLock(boolean transBranchLock) {
		this.transBranchLock = transBranchLock;
	}

	public String getTransBranchNote() {
		return this.transBranchNote;
	}

	public void setTransBranchNote(String transBranchNote) {
		this.transBranchNote = transBranchNote;
	}

	public boolean getTransCust() {
		return this.transCust;
	}

	public void setTransCust(boolean transCust) {
		this.transCust = transCust;
	}

	public String getTransCustDate() {
		return this.transCustDate;
	}

	public void setTransCustDate(String transCustDate) {
		this.transCustDate = transCustDate;
	}

	public boolean getTransCustIscheck() {
		return this.transCustIscheck;
	}

	public void setTransCustIscheck(boolean transCustIscheck) {
		this.transCustIscheck = transCustIscheck;
	}

	public boolean getTransCustLock() {
		return this.transCustLock;
	}

	public void setTransCustLock(boolean transCustLock) {
		this.transCustLock = transCustLock;
	}

	public boolean getTransEmp() {
		return this.transEmp;
	}

	public void setTransEmp(boolean transEmp) {
		this.transEmp = transEmp;
	}

	public String getTransEmpCode() {
		return this.transEmpCode;
	}

	public void setTransEmpCode(String transEmpCode) {
		this.transEmpCode = transEmpCode;
	}

	public String getTransEmpDate() {
		return this.transEmpDate;
	}

	public void setTransEmpDate(String transEmpDate) {
		this.transEmpDate = transEmpDate;
	}

	public boolean getTransEmpIscheck() {
		return this.transEmpIscheck;
	}

	public void setTransEmpIscheck(boolean transEmpIscheck) {
		this.transEmpIscheck = transEmpIscheck;
	}

	public boolean getTransEmpLock() {
		return this.transEmpLock;
	}

	public void setTransEmpLock(boolean transEmpLock) {
		this.transEmpLock = transEmpLock;
	}

	public String getTransEmpName() {
		return this.transEmpName;
	}

	public void setTransEmpName(String transEmpName) {
		this.transEmpName = transEmpName;
	}

	public String getTransEmpNote() {
		return this.transEmpNote;
	}

	public void setTransEmpNote(String transEmpNote) {
		this.transEmpNote = transEmpNote;
	}

	public boolean getTransMk() {
		return this.transMk;
	}

	public void setTransMk(boolean transMk) {
		this.transMk = transMk;
	}

	public String getTransMkDate() {
		return this.transMkDate;
	}

	public void setTransMkDate(String transMkDate) {
		this.transMkDate = transMkDate;
	}

	public boolean getTransMkIscheck() {
		return this.transMkIscheck;
	}

	public void setTransMkIscheck(boolean transMkIscheck) {
		this.transMkIscheck = transMkIscheck;
	}

	public boolean getTransMkLock() {
		return this.transMkLock;
	}

	public void setTransMkLock(boolean transMkLock) {
		this.transMkLock = transMkLock;
	}

	public boolean getTransBranchRec() {
		return transBranchRec;
	}

	public void setTransBranchRec(boolean transBranchRec) {
		this.transBranchRec = transBranchRec;
	}

	public String getTransBranchRecDate() {
		return transBranchRecDate;
	}

	public void setTransBranchRecDate(String transBranchRecDate) {
		this.transBranchRecDate = transBranchRecDate;
	}

	public boolean getTransBranchRecLock() {
		return transBranchRecLock;
	}

	public void setTransBranchRecLock(boolean transBranchRecLock) {
		this.transBranchRecLock = transBranchRecLock;
	}

	public boolean getTransBranchRecCheck() {
		return transBranchRecCheck;
	}

	public void setTransBranchRecCheck(boolean transBranchRecCheck) {
		this.transBranchRecCheck = transBranchRecCheck;
	}

	public String getBrchCde() {
		return brchCde;
	}

	public void setBrchCde(String brchCde) {
		this.brchCde = brchCde;
	}

	public String getTransMkRealTimeLock() {
		return transMkRealTimeLock;
	}

	public void setTransMkRealTimeLock(String transMkRealTimeLock) {
		this.transMkRealTimeLock = transMkRealTimeLock;
	}

	public String getTransMkRealTimeCheck() {
		return transMkRealTimeCheck;
	}

	public void setTransMkRealTimeCheck(String transMkRealTimeCheck) {
		this.transMkRealTimeCheck = transMkRealTimeCheck;
	}

	public String getTransBrnRecRealTimeLock() {
		return transBrnRecRealTimeLock;
	}

	public void setTransBrnRecRealTimeLock(String transBrnRecRealTimeLock) {
		this.transBrnRecRealTimeLock = transBrnRecRealTimeLock;
	}

	public String getTransBrnRecRealTimeCheck() {
		return transBrnRecRealTimeCheck;
	}

	public void setTransBrnRecRealTimeCheck(String transBrnRecRealTimeCheck) {
		this.transBrnRecRealTimeCheck = transBrnRecRealTimeCheck;
	}

	public String getTransCustRealTimeLock() {
		return transCustRealTimeLock;
	}

	public void setTransCustRealTimeLock(String transCustRealTimeLock) {
		this.transCustRealTimeLock = transCustRealTimeLock;
	}

	public String getTransCustRealTimeCheck() {
		return transCustRealTimeCheck;
	}

	public void setTransCustRealTimeCheck(String transCustRealTimeCheck) {
		this.transCustRealTimeCheck = transCustRealTimeCheck;
	}

	public String getTransCustStatus() {
		return transCustStatus;
	}

	public void setTransCustStatus(String transCustStatus) {
		this.transCustStatus = transCustStatus;
	}

	public String getTransCustNote() {
		return transCustNote;
	}

	public void setTransCustNote(String transCustNote) {
		this.transCustNote = transCustNote;
	}

	public String getGttn() {
		return gttn;
	}

	public void setGttn(String gttn) {
		this.gttn = gttn;
	}

	public String getTimeGttn() {
		return timeGttn;
	}

	public void setTimeGttn(String timeGttn) {
		this.timeGttn = timeGttn;
	}

	public String getXacNhanGttn() {
		return xacNhanGttn;
	}

	public void setXacNhanGttn(String xacNhanGttn) {
		this.xacNhanGttn = xacNhanGttn;
	}

	public String getTimeXacNhanGttn() {
		return timeXacNhanGttn;
	}

	public void setTimeXacNhanGttn(String timeXacNhanGttn) {
		this.timeXacNhanGttn = timeXacNhanGttn;
	}

}