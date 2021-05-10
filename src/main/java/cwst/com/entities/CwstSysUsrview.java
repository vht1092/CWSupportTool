package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;


/**
 * The persistent class for the CWST_SYS_USRVIEW database table.
 * 
 */
@Entity
@Table(name="CWST_SYS_USRVIEW")
@NamedQuery(name="CwstSysUsrview.findAll", query="SELECT c FROM CwstSysUsrview c")
public class CwstSysUsrview implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private CwstSysUsrviewPK id;

	public CwstSysUsrview() {
	}

	public CwstSysUsrviewPK getId() {
		return this.id;
	}

	public void setId(CwstSysUsrviewPK id) {
		this.id = id;
	}

}