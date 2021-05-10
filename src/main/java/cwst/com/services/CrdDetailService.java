package cwst.com.services;

import cwst.com.entities.CardDetail;
import cwst.com.entities.CardTransStatus;
import cwst.com.entities.CwstCrdDetail;

import java.util.List;

public interface CrdDetailService {

	List<Object[]> findAll(String fromDate, String toDate, String branchCode, String unitcode, String crdtype,
			String username, String cif, String key, String keyStatus);

	CwstCrdDetail findOneById(long id);

	void saveDataFromFormContextMenu(String cifno, String custname, String pan, String crdtype, String crdprinsupp,
			String issueType, String issuedate, String type, String brchcode, String empCode, String empName,
			String brnchCode, String note, String dateTransFileMK);

	void saveDataFromFormContextMenu_TransCust(String cifno, String custname, String pan, String crdtype,
			String crdprinsupp, String issueType, String issuedate, String type, String brchcode, String empCode,
			String empName, String brnchCode, String note, String dateTransFileMK, String keyStatus, String NoteStatus);

	void saveDataFromFormContextMenu_GTTN(String cifno, String custname, String pan, String crdtype, String crdprinsupp,
			String issueType, String issuedate, String type, String brchcode, String empCode, String empName,
			String brnchCode, String note, String dateTransFileMK, String keyStatus);

	void saveDataFromForm(String cifno, String custname, String pan, String crdtype, String crdprinsupp,
			String issueType, String issuedate, boolean tranmk, String tranmkdate, boolean recmk, String recmkdate,
			boolean transbranch, String transbranchdate, String transbranchempcode, boolean stransemp,
			String transempcode, String transEmpname,

			String transEmpDate, boolean bolTransEmpRender, String transEmpRenderDate, String transEmpRender,
			boolean Renderlock1,

			String transEmpDate1, boolean bolTransEmpRender1, String transEmpRenderDate1, String transEmpRender1,
			boolean Renderlock2,

			String transEmpDate2, boolean bolTransEmpRender2, String transEmpRenderDate2, String transEmpRender2,
			boolean Renderlock3,

			boolean transcust, String trancustdate, String transbranchempname, String transbranchfwcde,
			String transbranchnote, boolean transbranchrec, String transbranchrecdate, String transempnote,
			String filepin);

	void updateDate(long id, String cifno, String custname, String pan, String crdtype, String crdprinsupp,
			String issueType, String issuedate, String date, String type);

	boolean lockData(long id);

	boolean unlockData(long id);

	boolean checkData(long id);

	// void checkData(long id);

	void update(CwstCrdDetail cwstCrdDetail);

	CwstCrdDetail findOneByIdx(final String cifno, final String custname, final String pan, final String crdtype,
			final String prinsupp, final String issuetype, final String issuedate);

	public List<Object[]> findBrchBeforeNumberOfDay(int num);

	public List<Object[]> findCaseDistributingCardBeforeNumberOfDay(String brchcde, int num);

	public String findWorkingDate();
	
	public List<CardDetail> getListChuaDuyetChuaKichHoat(int tungay, int denngay);
	
	public List<CardDetail> getListChuaDuyetDaKichHoat(int tungay, int denngay);
	
	public List<CardDetail> getListChuaNhapChuaKichHoat(int tungay, int denngay);
	
	public List<CardDetail> getListChuaNhapDaKichHoat(int tungay, int denngay);
	
	public List<CardDetail> getListDaDuyetChuaKichHoat(int tungay, int denngay);

	public List<CardTransStatus> getTongHopTrangThaiTheVung(int fromdate, int todate);
	
}
