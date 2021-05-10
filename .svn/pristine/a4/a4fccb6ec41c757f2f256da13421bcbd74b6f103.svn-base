package cwst.com.repositories;

import cwst.com.entities.CwstEmailList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CwstEmailListRepo extends JpaRepository<CwstEmailList, Long> {

	public List<CwstEmailList> findAllByEmail(String email);

	public List<CwstEmailList> findAllByBrchCde(String brchcde);

}
