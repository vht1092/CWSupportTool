package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the CWST_SENTMAIL database table.
 * 
 */
@Entity
@Table(name="CWST_SENTMAIL")
@NamedQuery(name="CwstSentmail.findAll", query="SELECT c FROM CwstSentmail c")
public class CwstSentmail implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@TableGenerator(name = "TABLE_GEN_SENTMAIL", table = "CWST_HIBERNATE_SEQUENCES", valueColumnName = "SEQUENCE_NEXT_HI_VALUE", pkColumnName = "SEQUENCE_NAME", pkColumnValue = "cwst_sentmail")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_SENTMAIL")
	@Column(unique = true, nullable = false)
	private long id;

	@Column(name="CASE_ID", nullable=false, length=50)
	private String caseId;

	@Column(name="CRE_TMS", nullable=false, precision=17)
	private BigDecimal creTms;

	@Column(length=500)
	private String mailcontent;

	@Column(nullable=false, length=14)
	private String mailtype;

	public CwstSentmail() {
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

	public BigDecimal getCreTms() {
		return this.creTms;
	}

	public void setCreTms(BigDecimal creTms) {
		this.creTms = creTms;
	}

	public String getMailcontent() {
		return this.mailcontent;
	}

	public void setMailcontent(String mailcontent) {
		this.mailcontent = mailcontent;
	}

	public String getMailtype() {
		return this.mailtype;
	}

	public void setMailtype(String mailtype) {
		this.mailtype = mailtype;
	}

}