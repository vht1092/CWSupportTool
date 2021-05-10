package cwst.com.services;

import cwst.com.entities.CwstSysUser;

import java.util.List;

public interface SysUserService {

	public List<CwstSysUser> findAll();

	public CwstSysUser findUserByUsername(String username);

	public CwstSysUser findOneByUsernameAndIsLockFalse(String username);

	public String findUserCardWorks(String email);

	public CwstSysUser saveUser(String username, String fullname, String desc, List<String> branchcode, Boolean islock,
			String typeuser);

	public String getBrnOfUserDV(String username);

}
