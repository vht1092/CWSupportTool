package cwst.com.repositories;

import cwst.com.entities.CwstSysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUserRepo extends JpaRepository<CwstSysUser, Long> {
	@Query(value = "select f from CwstSysUser f")
	List<CwstSysUser> findAll();

	CwstSysUser findOneByUsername(String username);

	CwstSysUser findOneByUsernameAndIsLockFalse(String username);
	
	@Query(value = "select trim(BRANCH_CODE) from ppt_sys_usr_branch where username = :username and rownum = 1", nativeQuery = true)
	String getBrnOfUserDV(@Param("username") String username);

	@Query(value = "select upper(trim(px_az006_uid)) from az006@im where lower(trim(fx_az006_usr_email_addr)) =lower(:email)", nativeQuery = true)
	String findUserCardWorks(@Param("email") String email);
}
