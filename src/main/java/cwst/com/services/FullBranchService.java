package cwst.com.services;

import cwst.com.entities.FullBranch;

import java.util.List;

public interface FullBranchService {
	public List<FullBranch> findAll();
	public List<FullBranch> findByUserName(String username);
	public List<Object[]> getBrachPermission(String username);
	public List<FullBranch> findByBranchCode(String code);
}
