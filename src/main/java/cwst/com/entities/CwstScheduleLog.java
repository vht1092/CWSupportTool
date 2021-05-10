package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;


/**
 * The persistent class for the CWST_SCHEDULE_LOG database table.
 * 
 */
@Entity
@Table(name="CWST_SCHEDULE_LOG")
@NamedQuery(name="CwstScheduleLog.findAll", query="SELECT c FROM CwstScheduleLog c")
public class CwstScheduleLog implements Serializable {
	private static final long serialVersionUID = 1L;

	private String content;

	private BigDecimal crdate;

	@Id
	private BigDecimal id;

	public CwstScheduleLog() {
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public BigDecimal getCrdate() {
		return this.crdate;
	}

	public void setCrdate(BigDecimal crdate) {
		this.crdate = crdate;
	}

	public BigDecimal getId() {
		return this.id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

}