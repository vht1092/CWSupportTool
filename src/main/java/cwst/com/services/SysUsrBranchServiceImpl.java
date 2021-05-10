package cwst.com.services;

import cwst.com.entities.CwstSysUsrBranch;
import cwst.com.repositories.SysUsrBranchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysUsrBranchServiceImpl implements SysUsrBranchService {

	@Autowired
	SysUsrBranchRepo sysUsrBranchRepo;

	@Override
	public List<CwstSysUsrBranch> findAllByUsername(String usrname) {
		return sysUsrBranchRepo.findAllByUsername(usrname);
	}

}
