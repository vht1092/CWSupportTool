package cwst.com.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the CWST_SYS_USER database table.
 * 
 */
@Entity
@Table(name="PPT_SYS_USER")
@NamedQuery(name="CwstSysUser.findAll", query="SELECT c FROM CwstSysUser c")
public class CwstSysUser implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@TableGenerator(name = "TABLE_GEN_USER", table = "PPT_HIBERNATE_SEQUENCES", valueColumnName = "SEQUENCE_NEXT_HI_VALUE", pkColumnName = "SEQUENCE_NAME", pkColumnValue = "cwst_sys_user")
	@GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN_USER")
	@Column(unique = true, nullable = false)
	private long id;

	@Column(name="CRE_TMS", nullable=false, precision=17)
	private BigDecimal creTms;

	@Column(length=70)
	private String desct;

	@Column(length=255)
	private String email;

	@Column(length=255)
	private String fullname;

	@Column(name="IS_LOCK", nullable=false, precision=1)
	private boolean isLock;

	@Column(name="UPD_TMS", precision=17)
	private BigDecimal updTms;

	@Column(nullable=false, length=50)
	private String username;

	@Column(name="USR_TYPE", length=10)
	private String usrType;

	//bi-directional many-to-one association to CwstSysPermission
	@OneToMany(mappedBy="cwstSysUser")
	private List<CwstSysPermission> cwstSysPermissions;

	public CwstSysUser() {
	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getCreTms() {
		return this.creTms;
	}

	public void setCreTms(BigDecimal creTms) {
		this.creTms = creTms;
	}

	public String getDesct() {
		return this.desct;
	}

	public void setDesct(String desct) {
		this.desct = desct;
	}

	public String getEmail() {
		return this.email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getFullname() {
		return this.fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public boolean getIsLock() {
		return this.isLock;
	}

	public void setIsLock(boolean isLock) {
		this.isLock = isLock;
	}

	public BigDecimal getUpdTms() {
		return this.updTms;
	}

	public void setUpdTms(BigDecimal updTms) {
		this.updTms = updTms;
	}

	public String getUsername() {
		return this.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUsrType() {
		return this.usrType;
	}

	public void setUsrType(String usrType) {
		this.usrType = usrType;
	}

	public List<CwstSysPermission> getCwstSysPermissions() {
		return this.cwstSysPermissions;
	}

	public void setCwstSysPermissions(List<CwstSysPermission> cwstSysPermissions) {
		this.cwstSysPermissions = cwstSysPermissions;
	}

	public CwstSysPermission addCwstSysPermission(CwstSysPermission cwstSysPermission) {
		getCwstSysPermissions().add(cwstSysPermission);
		cwstSysPermission.setCwstSysUser(this);

		return cwstSysPermission;
	}

	public CwstSysPermission removeCwstSysPermission(CwstSysPermission cwstSysPermission) {
		getCwstSysPermissions().remove(cwstSysPermission);
		cwstSysPermission.setCwstSysUser(null);

		return cwstSysPermission;
	}

}