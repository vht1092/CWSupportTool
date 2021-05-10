package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;


/**
 * The persistent class for the CWST_SYS_VIEW database table.
 * 
 */
@Entity
@Table(name="CWST_SYS_VIEW")
@NamedQuery(name="CwstSysView.findAll", query="SELECT c FROM CwstSysView c")
public class CwstSysView implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@Column(unique=true, nullable=false)
	private long id;

	@Column(length=50)
	private String viewdesc;

	@Column(length=50)
	private String viewname;

	//bi-directional many-to-one association to CwstSysPermission
	@OneToMany(mappedBy="cwstSysView")
	private List<CwstSysPermission> cwstSysPermissions;

	public CwstSysView() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getViewdesc() {
		return this.viewdesc;
	}

	public void setViewdesc(String viewdesc) {
		this.viewdesc = viewdesc;
	}

	public String getViewname() {
		return this.viewname;
	}

	public void setViewname(String viewname) {
		this.viewname = viewname;
	}

	public List<CwstSysPermission> getCwstSysPermissions() {
		return this.cwstSysPermissions;
	}

	public void setCwstSysPermissions(List<CwstSysPermission> cwstSysPermissions) {
		this.cwstSysPermissions = cwstSysPermissions;
	}

	public CwstSysPermission addCwstSysPermission(CwstSysPermission cwstSysPermission) {
		getCwstSysPermissions().add(cwstSysPermission);
		cwstSysPermission.setCwstSysView(this);

		return cwstSysPermission;
	}

	public CwstSysPermission removeCwstSysPermission(CwstSysPermission cwstSysPermission) {
		getCwstSysPermissions().remove(cwstSysPermission);
		cwstSysPermission.setCwstSysView(null);

		return cwstSysPermission;
	}

}