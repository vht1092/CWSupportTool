package cwst.com.repositories;

import cwst.com.entities.CwstSysUsrBranch;
import cwst.com.entities.CwstSysUsrBranchPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SysUsrBranchRepo extends JpaRepository<CwstSysUsrBranch, CwstSysUsrBranchPK> {

	@Query("select f from CwstSysUsrBranch f where f.id.username=?1")
	List<CwstSysUsrBranch> findAllByUsername(String username);
}
