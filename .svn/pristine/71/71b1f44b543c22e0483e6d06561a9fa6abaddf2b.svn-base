package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the CWST_SCHEDULE database table.
 * 
 */
@Entity
@Table(name="CWST_SCHEDULE")
@NamedQuery(name="CwstSchedule.findAll", query="SELECT c FROM CwstSchedule c")
public class CwstSchedule implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private BigDecimal id;

	private BigDecimal nextdate;

	private BigDecimal startdate;

	public CwstSchedule() {
	}

	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public BigDecimal getNextdate() {
		return this.nextdate;
	}

	public void setNextdate(BigDecimal nextdate) {
		this.nextdate = nextdate;
	}

	public BigDecimal getStartdate() {
		return this.startdate;
	}

	public void setStartdate(BigDecimal startdate) {
		this.startdate = startdate;
	}

}