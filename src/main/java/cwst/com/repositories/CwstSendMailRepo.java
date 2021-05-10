package cwst.com.repositories;

import cwst.com.entities.CwstSendmail;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CwstSendMailRepo extends CrudRepository<CwstSendmail, Long> {
	List<CwstSendmail> findAllByMailtypeIn(Collection<String> mailtype);

	CwstSendmail findOneByCaseIdAndMailtype(String caseid, String mailtype);

	@Query(nativeQuery=true,value="select trim(fx_az006_usr_email_addr) as email from az006@im where trim(px_az006_uid) = (select substr(:username, instr(:username, '.') + 1, length(:username)) from dual)")
	String getUserEmail(@Param("username") String username);
}
