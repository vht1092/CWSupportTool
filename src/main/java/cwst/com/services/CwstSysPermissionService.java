/**
 * 
 */
package cwst.com.services;

import cwst.com.entities.CwstSysPermission;

import java.util.List;

/**
 * @author chautk
 */
public interface CwstSysPermissionService {
	void save(CwstSysPermission permission);

	List<CwstSysPermission> findByUserId(Long userid);

	void save(Long userid, Long viewid, String permission);

	void saveAllPermissionByViewIdAndUserId(Long userid, Long viewid, Boolean view, Boolean delete, Boolean edit, Boolean checker);

	void delete(Long userid);

	List<Object[]> findViewForPermission(Long userid);

}
