package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The primary key class for the CWST_SYS_USRVIEW database table.
 * 
 */
@Embeddable
public class CwstSysUsrviewPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(unique=true, nullable=false)
	private long userid;

	@Column(unique=true, nullable=false)
	private long viewid;

	public CwstSysUsrviewPK() {
	}
	public long getUserid() {
		return this.userid;
	}
	public void setUserid(long userid) {
		this.userid = userid;
	}
	public long getViewid() {
		return this.viewid;
	}
	public void setViewid(long viewid) {
		this.viewid = viewid;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof CwstSysUsrviewPK)) {
			return false;
		}
		CwstSysUsrviewPK castOther = (CwstSysUsrviewPK)other;
		return 
			(this.userid == castOther.userid)
			&& (this.viewid == castOther.viewid);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.userid ^ (this.userid >>> 32)));
		hash = hash * prime + ((int) (this.viewid ^ (this.viewid >>> 32)));
		
		return hash;
	}
}