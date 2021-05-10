package cwst.com.repositories;

import cwst.com.entities.CwstSysPermission;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CwstSysPermissionRepo extends CrudRepository<CwstSysPermission, Long> {

	@Query(value = "select t from CwstSysPermission t where t.cwstSysUser.id=?1")
	List<CwstSysPermission> findAllByUserid(long userid);

	@Modifying
	@Transactional
	@Query(value = "delete from CwstSysPermission t where t.cwstSysUser.id=?1")
	void deleteByUserid(long userid);

	@Query(value = "select a.id,a.viewname,a.viewdesc,a.parent, nvl((select p.isview from {h-schema}ppt_sys_permission p where p.userid = :userid and p.viewid = a.id), 0) isview, nvl((select p.ischeker from {h-schema}ppt_sys_permission p where p.userid = :userid and p.viewid = a.id), 0) ischecker, nvl((select p.isdelete from {h-schema}ppt_sys_permission p where p.userid = :userid and p.viewid = a.id), 0) isdelete, nvl((select p.isedit from {h-schema}ppt_sys_permission p where p.userid = :userid and p.viewid = a.id), 0) isedit from {h-schema}ppt_sys_view a order by a.parent,a.id", nativeQuery = true)
	List<Object[]> findViewForPermission(@Param("userid") long userid);

}
