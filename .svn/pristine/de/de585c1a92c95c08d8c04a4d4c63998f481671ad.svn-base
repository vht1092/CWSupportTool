package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the CWST_CRD_DETAIL_CHECKER database table.
 * 
 */
@Entity
@Table(name="CWST_CRD_DETAIL_CHECKER")
@NamedQuery(name="CwstCrdDetailChecker.findAll", query="SELECT c FROM CwstCrdDetailChecker c")
public class CwstCrdDetailChecker implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="CRD_ID")
	private long crdId;

	@Column(length=20)
	private String crdtype;

	@Column(name="CRE_TMS", precision=17)
	private long creTms;

	@Column(length=50)
	private String desct;

	private long userid;

	public CwstCrdDetailChecker() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getCrdId() {
		return this.crdId;
	}

	public void setCrdId(long crdId) {
		this.crdId = crdId;
	}

	public String getCrdtype() {
		return this.crdtype;
	}

	public void setCrdtype(String crdtype) {
		this.crdtype = crdtype;
	}

	public long getCreTms() {
		return this.creTms;
	}

	public void setCreTms(long creTms) {
		this.creTms = creTms;
	}

	public String getDesct() {
		return this.desct;
	}

	public void setDesct(String desct) {
		this.desct = desct;
	}

	public long getUserid() {
		return this.userid;
	}

	public void setUserid(long userid) {
		this.userid = userid;
	}

}