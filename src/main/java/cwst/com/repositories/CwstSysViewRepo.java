package cwst.com.repositories;

import cwst.com.entities.CwstSysView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CwstSysViewRepo extends CrudRepository<CwstSysView, Long> {
	@Query(value = "select a.id, a.viewname, a.viewdesc from ccps.cwst_sys_view a",nativeQuery=true)
	List<CwstSysView> findAll();
}
