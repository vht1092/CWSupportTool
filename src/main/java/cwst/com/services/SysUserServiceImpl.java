package cwst.com.services;

import cwst.com.TimeConverter;
import cwst.com.entities.CwstSysUser;
import cwst.com.entities.CwstSysUsrBranch;
import cwst.com.entities.CwstSysUsrBranchPK;
import cwst.com.repositories.SysUserRepo;
import cwst.com.repositories.SysUsrBranchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SysUserServiceImpl implements SysUserService {

	@Autowired
	private SysUserRepo userRepo;
	@Autowired
	private SysUsrBranchRepo sysUsrBranchRepo;
	private final TimeConverter timeConverter = new TimeConverter();

	@Override
	public CwstSysUser findUserByUsername(final String username) {
		return userRepo.findOneByUsername(username);
	}

	@Override
	public CwstSysUser saveUser(String username, String fullname, String desc, List<String> branchcode, Boolean islock, String typeuser) {

		CwstSysUser usr = userRepo.findOneByUsername(username);
		if (usr == null) {
			usr = new CwstSysUser();
			usr.setCreTms(new BigDecimal(timeConverter.getCurrentTime("FULL")));
			usr.setUsername(username);
			usr.setIsLock(false);
			usr.setUsrType(typeuser);

		} else {
			if (StringUtils.hasText(typeuser)) {
				usr.setUsrType(typeuser);
			}

			usr.setUpdTms(new BigDecimal(timeConverter.getCurrentTime("FULL")));
			usr.setIsLock(islock);
		}
		// Update branch code
		if (branchcode.size() > 0) {
			final List<CwstSysUsrBranch> lsBranch = sysUsrBranchRepo.findAllByUsername(username);
			sysUsrBranchRepo.delete(lsBranch);
			branchcode.forEach(s -> {
				final CwstSysUsrBranch branch = new CwstSysUsrBranch(new CwstSysUsrBranchPK(username, s));
				sysUsrBranchRepo.save(branch);
			});

		}
		usr.setFullname(fullname);
		usr.setDesct(desc);
		return userRepo.save(usr);

	}

	@Override
	public List<CwstSysUser> findAll() {
		return userRepo.findAll();
	}

	@Override
	public String findUserCardWorks(String email) {
		return userRepo.findUserCardWorks(email + "@scb.com.vn");
	}

	@Override
	public CwstSysUser findOneByUsernameAndIsLockFalse(String username) {
		return userRepo.findOneByUsernameAndIsLockFalse(username);
	}
	
	@Override
	public String getBrnOfUserDV(String username) {
		return userRepo.getBrnOfUserDV(username);
	}

}
