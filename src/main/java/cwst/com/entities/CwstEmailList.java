package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the CWST_EMAIL_LIST database table.
 * 
 */
@Entity
@Table(name="CWST_EMAIL_LIST")
@NamedQuery(name="CwstEmailList.findAll", query="SELECT c FROM CwstEmailList c")
public class CwstEmailList implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(name="BRCH_CDE", nullable=false, length=3)
	private String brchCde;

	@Column(nullable=false, length=50)
	private String email;

	@Column(name="\"TYPE\"", length=3)
	private String type;

	public CwstEmailList() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBrchCde() {
		return this.brchCde;
	}

	public void setBrchCde(String brchCde) {
		this.brchCde = brchCde;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}

}