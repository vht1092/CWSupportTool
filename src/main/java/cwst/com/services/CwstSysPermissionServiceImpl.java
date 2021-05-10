package cwst.com.services;

import cwst.com.entities.CwstSysPermission;
import cwst.com.entities.CwstSysUser;
import cwst.com.entities.CwstSysView;
import cwst.com.repositories.CwstSysPermissionRepo;
import cwst.com.repositories.SysUserRepo;
import cwst.com.views.UserManagerView;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CwstSysPermissionServiceImpl implements CwstSysPermissionService {
	private static final Logger LOGGER = LogManager.getLogger(CwstSysPermissionServiceImpl.class);
	@Autowired
	private CwstSysPermissionRepo cwstSysPermissionRepo;
	@Autowired
	private SysUserRepo sysUserRepo;

	@Override
	public void save(CwstSysPermission permission) {
		cwstSysPermissionRepo.save(permission);
	}

	@Override
	public List<CwstSysPermission> findByUserId(Long userid) {
		return cwstSysPermissionRepo.findAllByUserid(userid);
	}

	@Override
	public void save(Long userid, Long viewid, String permission) {
		final CwstSysUser e = sysUserRepo.findOne(userid);
		final CwstSysView v = new CwstSysView();
		v.setId(viewid);
		final CwstSysPermission p = new CwstSysPermission();
		p.setCwstSysUser(e);
		p.setCwstSysView(v);
		if (permission.equals("edit")) {
			p.setIsedit(true);
			p.setIsview(true);
		}
		if (permission.equals("view")) {
			p.setIsview(true);
		}
		if (permission.equals("checker")) {
			p.setIscheker(true);
			p.setIsview(true);
		}
		cwstSysPermissionRepo.save(p);

	}

	@Override
	public List<Object[]> findViewForPermission(Long userid) {
		return cwstSysPermissionRepo.findViewForPermission(userid);
	}

	@Override
	public void delete(Long userid) {
		cwstSysPermissionRepo.deleteByUserid(userid);
	}

	@Override
	public void saveAllPermissionByViewIdAndUserId(Long userid, Long viewid, Boolean view, Boolean delete, Boolean edit, Boolean checker) {
		final CwstSysUser e = sysUserRepo.findOne(userid);
		final CwstSysView v = new CwstSysView();
		v.setId(viewid);
		final CwstSysPermission p = new CwstSysPermission();
		p.setCwstSysUser(e);
		p.setCwstSysView(v);
		p.setIsview(view);
		p.setIsdelete(delete);
		p.setIsedit(edit);
		p.setIscheker(checker);
		cwstSysPermissionRepo.save(p);
	}

}
