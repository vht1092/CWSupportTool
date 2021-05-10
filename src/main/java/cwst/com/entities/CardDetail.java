package cwst.com.entities;

import java.io.Serializable;

public class CardDetail implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String cif;

	private String loc;
	
	private String custName;
	
	private String phoneNo;
	
	private String panMask;
	
	private String cardType;
	
	private String issueDate;
	
	private String brchCde;
	
	private String brchName;
	
	private String statusCheck;
	
	private String transMkDate;
	
	private String transCustDate;
	
	private String transCustStatus;
	

	public CardDetail() {
	}

	

	public String getCif() {
		return cif;
	}



	public void setCif(String cif) {
		this.cif = cif;
	}



	public String getLoc() {
		return loc;
	}


	public void setLoc(String loc) {
		this.loc = loc;
	}


	public String getCustName() {
		return custName;
	}


	public void setCustName(String custName) {
		this.custName = custName;
	}


	public String getPhoneNo() {
		return phoneNo;
	}


	public void setPhoneNo(String phoneNo) {
		this.phoneNo = phoneNo;
	}


	public String getPanMask() {
		return panMask;
	}


	public void setPanMask(String panMask) {
		this.panMask = panMask;
	}


	public String getCardType() {
		return cardType;
	}


	public void setCardType(String cardType) {
		this.cardType = cardType;
	}


	public String getIssueDate() {
		return issueDate;
	}


	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}


	public String getBrchCde() {
		return brchCde;
	}


	public void setBrchCde(String brchCde) {
		this.brchCde = brchCde;
	}


	public String getBrchName() {
		return brchName;
	}


	public void setBrchName(String brchName) {
		this.brchName = brchName;
	}


	public String getStatusCheck() {
		return statusCheck;
	}


	public void setStatusCheck(String statusCheck) {
		this.statusCheck = statusCheck;
	}


	public String getTransMkDate() {
		return transMkDate;
	}


	public void setTransMkDate(String transMkDate) {
		this.transMkDate = transMkDate;
	}


	public String getTransCustDate() {
		return transCustDate;
	}


	public void setTransCustDate(String transCustDate) {
		this.transCustDate = transCustDate;
	}


	public String getTransCustStatus() {
		return transCustStatus;
	}


	public void setTransCustStatus(String transCustStatus) {
		this.transCustStatus = transCustStatus;
	}

	

}