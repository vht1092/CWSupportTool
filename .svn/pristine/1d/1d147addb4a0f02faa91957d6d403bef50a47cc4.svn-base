package cwst.com.repositories;

import javax.persistence.EntityManager;
import javax.persistence.ParameterMode;
import javax.persistence.PersistenceContext;
import javax.persistence.StoredProcedureQuery;
import java.util.List;

public class FullBranchRepoImpl implements ProcedureRepoCustom {

	@PersistenceContext
	protected EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<Object[]> findAllBranchByUsername(String username) {
		final StoredProcedureQuery query = em.createStoredProcedureQuery("CCPS.CWST_PRO_GETUSERINFO");
		query.registerStoredProcedureParameter("P_USERNAME", String.class, ParameterMode.IN);
		query.registerStoredProcedureParameter("OUT_RS", void.class, ParameterMode.REF_CURSOR);

		query.setParameter("P_USERNAME", username);
		query.execute();

		return query.getResultList();
	}

}
