package cwst.com.services;

import cwst.com.entities.FullBranch;
import cwst.com.repositories.FullBranchRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FullBranchServiceImpl implements FullBranchService {

	@Autowired
	FullBranchRepo fullBranch;

	@Override
	public List<FullBranch> findAll() {
		return fullBranch.findAll();
	}

	@Override
	public List<FullBranch> findByUserName(String username) {
		return fullBranch.findByUserName(username);
	}

	@Override
	public List<Object[]> getBrachPermission(String username) {
		return fullBranch.findAllBranchByUsername(username);
	}

	@Override
	public List<FullBranch> findByBranchCode(String code) {
		return fullBranch.findByBranchCode(code);
	}

}
