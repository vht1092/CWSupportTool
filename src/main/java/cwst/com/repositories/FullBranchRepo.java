package cwst.com.repositories;

import cwst.com.entities.FullBranch;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FullBranchRepo extends CrudRepository<FullBranch, String>, ProcedureRepoCustom {

	// @Query(value = "select distinct f from FullBranch f where f.branchCode in
	// (select t.id.branchCode from CwstSysUsrBranch t where t.id.username =
	// ?1)")
	@Query(value = "select a.* from full_branch a where a.branch_code in ( select b.parent_branch  from full_branch b, ppt_sys_usr_branch c where b.branch_code = c.branch_code and c.username = ?1) order by a.branch_code", nativeQuery = true)
	List<FullBranch> findByUserName(String username);

	@Query(value = "select f from FullBranch f order by branchCode ")
	List<FullBranch> findAll();

	@Query(value = "select distinct f from FullBranch f where  trunc(f.parentBranch)  = ?1 ")
	List<FullBranch> findByBranchCode(String code);

	@Query(value = "select TEN_DV, ADDRESS, NHAN_SU_NHAN_THE, CMND, PHONE,EMAIL from ccps.distribution_card_mk where MA_DV=:brnCode", nativeQuery = true)
	List<Object[]> findBranchByBrnCode(@Param("brnCode") String brnCode);

	@Query(value = "UPDATE ccps.distribution_card_mk set TEN_DV=:brnName, ADDRESS=:brnAddress, NHAN_SU_NHAN_THE=:empName, CMND=:empNewID, PHONE=:empPhone, EMAIL=:email where MA_DV=:brnCode", nativeQuery = true)
	void branchByBrnCode(@Param("brnCode") String brnCode, @Param("brnName") String brnName,
			@Param("empName") String empName, @Param("empNewID") String empNewID, @Param("empPhone") String empPhone,
			@Param("brnAddress") String brnAddress, @Param("email") String email);

}
