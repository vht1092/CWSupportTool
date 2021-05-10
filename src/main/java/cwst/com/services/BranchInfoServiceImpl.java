package cwst.com.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cwst.com.repositories.FullBranchRepo;

@Service
public class BranchInfoServiceImpl implements BranchInfoService {
	@Autowired
	private FullBranchRepo brnRepo;

	@Override
	public List<Object[]> findBranchByBrnCode(String brnCode) {
		// TODO Auto-generated method stub
		return brnRepo.findBranchByBrnCode(brnCode);
	}

	@Override
	public void updateBranchByBrnCode(String brnCode, String brnName, String empName, String empNewID, String empPhone,
			String brnAddress, String email) {
		brnRepo.branchByBrnCode(brnCode, brnName, empName, empNewID, empPhone, brnAddress, email);

	}

}
