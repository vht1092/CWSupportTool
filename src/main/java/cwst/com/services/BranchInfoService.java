package cwst.com.services;

import java.util.List;


public interface BranchInfoService {
	List<Object[]> findBranchByBrnCode(String brnCode);
	
	void updateBranchByBrnCode(String brnCode, String brnName, String empName, String empNewID, String empPhone, String brnAddress, String email);
}
