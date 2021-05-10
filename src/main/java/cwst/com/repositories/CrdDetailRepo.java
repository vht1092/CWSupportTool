package cwst.com.repositories;

import cwst.com.entities.CwstCrdDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrdDetailRepo extends JpaRepository<CwstCrdDetail, Long> {

	// @Query(value = "select rownum, trim(fx_ir056_cif_no) as cif,
	// rtrim(regexp_substr(a.card_info, '[^|]*\\|', 1, 2), '|') as card_type,
	// trim(fx_ir056_name) hoten, rtrim(regexp_substr(a.card_info, '[^|]*\\|',
	// 1, 5), '|') as loai_the, substr(ccps.ded2(a.pan, ''), 1, 6) || 'xxxxxx'
	// || substr(ccps.ded2(a.pan, ''), -4, 4) as sothe_che, trim(crd_cat) as
	// crd_cat, emb_dt, trim(a.brch_cde), rtrim(regexp_substr(a.card_info,
	// '[^|]*\\|', 1, 1), '|') as loc, nvl(crddetail.trans_mk, 0) as trans_mk,
	// crddetail.trans_mk_date, nvl(crddetail.trans_mk_lock, 0) as
	// trans_mk_lock, nvl(crddetail.rec_mk, 0) as rec_mk, crddetail.rec_mk_date,
	// nvl(crddetail.rec_mk_lock, 0) as rec_mk_lock, nvl(crddetail.trans_branch,
	// 0) as trans_branch, crddetail.trans_branch_date,
	// nvl(crddetail.trans_branch_lock, 0) as trans_branch_lock,
	// crddetail.trans_branch_emp_code, nvl(crddetail.trans_emp, 0) as
	// trans_emp, crddetail.trans_emp_date, crddetail.trans_emp_name,
	// crddetail.trans_emp_code, nvl(crddetail.trans_emp_lock, 0) as
	// trans_emp_lock, nvl(crddetail.trans_cust, 0) as trans_cust,
	// crddetail.trans_cust_date, nvl(crddetail.trans_cust_lock, 0) as
	// trans_cust_lock, a.pan, rtrim(regexp_substr(a.card_info, '[^|]*\\|', 1,
	// 4), '|') s_brch_cde, crddetail.trans_branch_emp_name,
	// crddetail.trans_branch_note, crddetail.fw_brn_cde, crddetail.id,
	// nvl(crddetail.trans_mk_ischeck, 0) as trans_mk_ischeck,
	// nvl(crddetail.rec_mk_ischeck, 0) as rec_mk_ischeck,
	// nvl(crddetail.trans_branch_ischeck, 0) as trans_branch_ischeck,
	// nvl(crddetail.trans_emp_ischeck, 0) as trans_emp_ischeck,
	// nvl(crddetail.trans_cust_ischeck, 0) as trans_cust_ischeck,
	// trans_emp_note, crddetail.file_pin, nvl(crddetail.trans_branch_rec, 0) as
	// trans_branch_rec, crddetail.trans_branch_rec_date as
	// trans_branch_rec_date, nvl(crddetail.trans_branch_rec_lock, 0) as
	// trans_branch_rec_lock, nvl(crddetail.trans_branch_rec_check, 0) as
	// trans_branch_rec_check from (select fx_iw104_pan as pan, f9_iw104_emb_dt
	// as emb_dt, fx_iw104_brch_cde as brch_cde, fx_iw104_crd_cat as crd_cat,
	// (select trim(f9_ir025_loc_acct) || '|' || trim(fx_ir025_crd_pgm) || '|'
	// || trim(fx_ir025_crd_brn) || '|' || trim(fx_ir025_brch_cde) || '|' || 'P'
	// || '|' || f9_ir025_crn || '|' from ir025@im where
	// substr(fx_ir025_crd_pgm, 1, 2) in ('MC', 'MD', 'VS') and px_ir025_pan =
	// fx_iw104_pan union select trim(f9_ir275_loc_acct) || '|' ||
	// trim(fx_ir275_crd_pgm) || '|' || trim(fx_ir275_crd_brn) || '|' ||
	// trim(fx_ir275_brch_cde) || '|' || 'S' || '|' || f9_ir275_crn || '|' from
	// ir275@im where substr(fx_ir275_crd_pgm, 1, 2) in ('MC', 'MD', 'VS') and
	// px_ir275_own_pan = fx_iw104_pan) card_info from iw104@im where
	// (f9_iw104_emb_dt between :ifromdate and :itodate or :ifromdate is null or
	// :itodate is null) and (trim(fx_iw104_brch_cde) = trim(:ibrach_code) or
	// :ibrach_code is null) and (trim(fx_iw104_brch_cde) in (select
	// trim(t.branch_code) from ccps.cwst_sys_usr_branch t where t.username =
	// :username))) a left join ir056@im on p9_ir056_crn =
	// rtrim(regexp_substr(card_info, '[^|]*\\|', 1, 6), '|') left join
	// ccps.full_branch b on b.branch_code = brch_cde left join
	// ccps.cwst_crd_detail crddetail on crddetail.cif_no =
	// trim(fx_ir056_cif_no) and crddetail.crd_prin_supp =
	// rtrim(regexp_substr(card_info, '[^|]*\\|', 1, 5), '|') and
	// crddetail.cust_name = trim(fx_ir056_name) and crddetail.pan = trim(a.pan)
	// and crddetail.issue_type = trim(a.crd_cat) and crddetail.issue_date =
	// trim(a.emb_dt) where card_info is not null and (trim(fx_ir056_cif_no) =
	// trim(:icif) or :icif is null) and (:icrd_type is null or
	// rtrim(regexp_substr(a.card_info, '[^|]*\\|', 1, 3), '|') =
	// upper(:icrd_type)) or (crddetail.fw_brn_cde in ((select
	// trim(t.branch_code) from ccps.cwst_sys_usr_branch t where t.username =
	// :username)))", nativeQuery = true)
	@Query(value = "select * from table(ppt_get_carddetail(:ifromdate, :itodate, :icif, :ibrach_code, :iunit_code, :icrd_type, :username, :key, :keyStatus))", nativeQuery = true)
	public List<Object[]> findAll(@Param("ifromdate") final String fromdate, @Param("itodate") final String todate,
			@Param("ibrach_code") final String branchcode, @Param("iunit_code") final String unitcode,
			@Param("icrd_type") final String crdtypem, @Param("username") final String username,
			@Param("icif") final String cif, @Param("key") final String key,
			@Param("keyStatus") final String keyStatus);

	public CwstCrdDetail findOneById(long id);

	@Query(value = "select f from CwstCrdDetail f where f.cifNo=:cifno and f.custName=:custname and f.pan=:pan and f.crdType=:crdtype and f.crdPrinSupp=:crdprinsupp and f.issueType=:issuetype and f.issueDate=:issuedate")
	public CwstCrdDetail findOneByIdx(@Param("cifno") final String cifno, @Param("custname") final String custname,
			@Param("pan") final String pan, @Param("crdtype") final String crdtype,
			@Param("crdprinsupp") final String prinsupp, @Param("issuetype") final String issuetype,
			@Param("issuedate") final String issuedate);

	// @Query(value = "select t.cif_no, t.cust_name, t.crd_prin_supp,
	// t.crd_type, t.issue_date, substr(ccps.ded2(t.pan, ''), 1, 6) || 'xxxxxx'
	// || substr(ccps.ded2(t.pan, ''), -4, 4) as sothe_che,
	// t.brch_cde, t.issue_type, listagg(e.email, ',') within group(order by
	// e.email) as email from {h-schema}cwst_crd_detail t left join
	// {h-schema}cwst_email_list e on t.brch_cde = e.brch_cde where
	// t.trans_branch_date <= to_number(to_char(sysdate, 'yyyymmdd')) and
	// t.trans_branch_lock = 1 group by t.id, t.cif_no, t.crd_prin_supp,
	// t.crd_type, t.cust_name, t.fw_brn_cde, t.issue_date,
	// t.issue_type, t.pan, t.brch_cde", nativeQuery = true)
	@Query(nativeQuery = true, value = "select a.*,rownum from (select t.cif_no, t.crd_type, t.cust_name, t.crd_prin_supp, substr(ccps.ded2(t.pan, ''), 1, 6) || 'xxxxxx' || substr(ccps.ded2(t.pan, ''), -4, 4) as sothe_che, t.issue_type, t.issue_date, t.brch_cde, t.trans_branch_emp_name, t.trans_branch_emp_code, t.trans_branch_date, listagg(e.email, ',') within group(order by e.email) as email from ccps.cwst_crd_detail t left join ccps.cwst_email_list e on t.brch_cde = e.brch_cde where t.trans_branch_date <= to_number(to_char(sysdate-:num, 'yyyymmdd')) and t.trans_branch_lock = 1 and t.brch_cde = :brchcde group by t.id, t.cif_no, t.crd_prin_supp, t.crd_type, t.cust_name, t.fw_brn_cde, t.issue_date, t.issue_type, t.pan, t.brch_cde, t.trans_branch_emp_name, t.trans_branch_emp_code, t.trans_branch_date) a")
	public List<Object[]> findCaseDistributingCardBeforeNumberOfDay(@Param("brchcde") String brchcde,
			@Param("num") int num);

	// Dung cho viec gui mail ben quan ly phan phoi the
	@Query(nativeQuery = true, value = "select FX_IR423_DAYS from ir423@im where PX_IR423_YEAR=(select max(PX_IR423_YEAR) from ir423@im)")
	public String findWorkingDate();

	@Query(nativeQuery = true, value = "select t.brch_cde, (select listagg(e.email, ',') within group(order by e.email) as email from ccps.cwst_email_list e where e.brch_cde = t.brch_cde) from ccps.cwst_crd_detail t where t.trans_branch_date <= to_number(to_char(sysdate - :num, 'yyyymmdd')) and t.trans_branch_lock = 1 and t.brch_cde is not null group by t.brch_cde")
	public List<Object[]> findBrchBeforeNumberOfDay(@Param("num") int num);
}
