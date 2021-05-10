package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;

/**
 * The persistent class for the CWST_SYS_PERMISSION database table.
 */
@Entity
@Table(name = "PPT_SYS_PERMISSION")
@NamedQuery(name = "CwstSysPermission.findAll", query = "SELECT c FROM CwstSysPermission c")
public class CwstSysPermission implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@TableGenerator(name = "TABLE_GEN_PER", table = "PPT_HIBERNATE_SEQUENCES", valueColumnName = "SEQUENCE_NEXT_HI_VALUE", pkColumnName = "SEQUENCE_NAME", pkColumnValue = "cwst_sys_permission")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_PER")
	private long id;

	@Column(nullable = false, precision = 1)
	private boolean isdelete;

	@Column(nullable = false, precision = 1)
	private boolean isedit;

	@Column(nullable = false, precision = 1)
	private boolean isview;
	
	@Column(nullable = false, precision = 1)
	private boolean ischeker;

	// bi-directional many-to-one association to CwstSysUser
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "USERID", nullable = false)
	private CwstSysUser cwstSysUser;

	// bi-directional many-to-one association to CwstSysView
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "VIEWID")
	private CwstSysView cwstSysView;

	public CwstSysPermission() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public boolean getIsdelete() {
		return this.isdelete;
	}

	public void setIsdelete(boolean isdelete) {
		this.isdelete = isdelete;
	}

	public boolean getIsedit() {
		return this.isedit;
	}

	public void setIsedit(boolean isedit) {
		this.isedit = isedit;
	}

	public boolean getIsview() {
		return this.isview;
	}

	public void setIsview(boolean isview) {
		this.isview = isview;
	}

	public boolean getIscheker() {
		return ischeker;
	}

	public void setIscheker(boolean ischeker) {
		this.ischeker = ischeker;
	}

	public CwstSysUser getCwstSysUser() {
		return this.cwstSysUser;
	}

	public void setCwstSysUser(CwstSysUser cwstSysUser) {
		this.cwstSysUser = cwstSysUser;
	}

	public CwstSysView getCwstSysView() {
		return this.cwstSysView;
	}

	public void setCwstSysView(CwstSysView cwstSysView) {
		this.cwstSysView = cwstSysView;
	}

}