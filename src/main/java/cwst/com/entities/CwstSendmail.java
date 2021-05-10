package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the CWST_SENDMAIL database table.
 * 
 */
@Entity
@Table(name="CWST_SENDMAIL")
@NamedQuery(name="CwstSendmail.findAll", query="SELECT c FROM CwstSendmail c")
public class CwstSendmail implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@TableGenerator(name = "TABLE_GEN_SENDMAIL", table = "CWST_HIBERNATE_SEQUENCES", valueColumnName = "SEQUENCE_NEXT_HI_VALUE", pkColumnName = "SEQUENCE_NAME", pkColumnValue = "cwst_sendmail")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_SENDMAIL")
	@Column(unique = true, nullable = false)
	private long id;

	@Column(name="CASE_ID", nullable=false, length=50)
	private String caseId;

	@Column(name="CRE_TMS", nullable=false, precision=17)
	private long creTms;

	@Column(precision=1)
	private boolean issend;

	@Column(nullable=false, length=500)
	private String mailcontent;

	@Column(length=50)
	private String mailtitle;

	@Column(length=500)
	private String mailto;

	@Column(nullable=false, length=14)
	private String mailtype;

	public CwstSendmail() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getCaseId() {
		return this.caseId;
	}

	public void setCaseId(String caseId) {
		this.caseId = caseId;
	}

	public long getCreTms() {
		return this.creTms;
	}

	public void setCreTms(long creTms) {
		this.creTms = creTms;
	}

	public boolean getIssend() {
		return this.issend;
	}

	public void setIssend(boolean issend) {
		this.issend = issend;
	}

	public String getMailcontent() {
		return this.mailcontent;
	}

	public void setMailcontent(String mailcontent) {
		this.mailcontent = mailcontent;
	}

	public String getMailtitle() {
		return this.mailtitle;
	}

	public void setMailtitle(String mailtitle) {
		this.mailtitle = mailtitle;
	}

	public String getMailto() {
		return this.mailto;
	}

	public void setMailto(String mailto) {
		this.mailto = mailto;
	}

	public String getMailtype() {
		return this.mailtype;
	}

	public void setMailtype(String mailtype) {
		this.mailtype = mailtype;
	}

}