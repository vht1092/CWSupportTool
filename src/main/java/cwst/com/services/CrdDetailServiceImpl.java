package cwst.com.services;

import cwst.com.SecurityUtils;
import cwst.com.SpringContextHelper;
import cwst.com.TimeConverter;
import cwst.com.entities.CardDetail;
import cwst.com.entities.CardTransStatus;
import cwst.com.entities.CwstCrdDetail;
import cwst.com.repositories.CrdDetailRepo;
import oracle.jdbc.OracleTypes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vaadin.server.VaadinServlet;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

@Service
public class CrdDetailServiceImpl implements CrdDetailService {

	@Autowired
	private CrdDetailRepo crdDetailRepo;

	private final TimeConverter timeConverter = new TimeConverter();
	
	protected DataSource localDataSource;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CrdDetailServiceImpl.class);

	@Override
	public List<Object[]> findAll(String fromdate, String todate, String branchcode, String unitcode, String crdtype,
			String username, String cif, String key, String keyStatus) {
		System.out.println("fromdate: " + fromdate + " todate: " + todate + " branchcode: " + branchcode + " unitcode: "
				+ unitcode + " crdtype: " + crdtype + " username: " + username + " cif:" + cif);
		return crdDetailRepo.findAll(fromdate, todate, branchcode, unitcode, crdtype, username, cif, key, keyStatus);
	}

	/**
	 * Luu data tu form khi click chuot phai
	 */
	@Override
	public void saveDataFromFormContextMenu(String cifno, String custname, String pan, String crdtype,
			String crdprinsupp, String issuetype, String issueDate, String type, String brchcode, String empCode,
			String empName, String fwbrnchCode, String note, String selectDate) {
		CwstCrdDetail cwstCrdDetail = crdDetailRepo.findOneByIdx(cifno, custname, pan, crdtype, crdprinsupp, issuetype,
				issueDate);

		if (cwstCrdDetail == null) {
			cwstCrdDetail = new CwstCrdDetail();
			cwstCrdDetail.setCifNo(cifno);
			cwstCrdDetail.setCustName(custname);
			cwstCrdDetail.setCrdType(crdtype);
			cwstCrdDetail.setCrdPrinSupp(crdprinsupp);
			cwstCrdDetail.setIssueType(issuetype);
			cwstCrdDetail.setIssueDate(issueDate);
			cwstCrdDetail.setPan(pan);
			cwstCrdDetail.setBrchCde(brchcode);
		}
		if ("TOMK".equals(type)) {
			if (!cwstCrdDetail.getTransMkLock()) {
				cwstCrdDetail.setTransMk(true);
				if (selectDate == null || selectDate.equals(""))
					cwstCrdDetail.setTransMkDate(timeConverter.getCurrentTime("SHORT"));
				else
					cwstCrdDetail.setTransMkDate(selectDate);
			}
		}

		if ("BRANCHREC".equals(type)) {
			if (cwstCrdDetail.getTransMkLock() && !cwstCrdDetail.getTransBranchRecLock()) {
				cwstCrdDetail.setTransBranchRec(true);
				cwstCrdDetail.setTransBranchRecLock(true);
				cwstCrdDetail.setTransBrnRecRealTimeLock(timeConverter.getCurrentTime("SHORT"));
				if (selectDate == null || selectDate.equals(""))
					cwstCrdDetail.setTransBranchRecDate(timeConverter.getCurrentTime("SHORT"));
				else
					cwstCrdDetail.setTransBranchRecDate(selectDate);
			}
		}

		if ("TOCUST".equals(type)) {
			if (!cwstCrdDetail.getTransCustLock() && cwstCrdDetail.getTransBranchRecCheck()) {
				cwstCrdDetail.setTransCust(true);
				cwstCrdDetail.setTransCustLock(true);
				if (selectDate == null || selectDate.equals(""))

					cwstCrdDetail.setTransCustDate(timeConverter.getCurrentTime("SHORT"));
				else
					cwstCrdDetail.setTransCustDate(selectDate);
			}
		}
		crdDetailRepo.save(cwstCrdDetail);
	}

	/**
	 * XU LY CAP NHAT CHO POPUP DON VI GIAO THE CHO KHACH HANG
	 */
	@Override
	public void saveDataFromFormContextMenu_TransCust(String cifno, String custname, String pan, String crdtype,
			String crdprinsupp, String issuetype, String issueDate, String type, String brchcode, String empCode,
			String empName, String fwbrnchCode, String note, String selectDate, String keyStatus, String NoteStatus) {

		CwstCrdDetail cwstCrdDetail = crdDetailRepo.findOneByIdx(cifno, custname, pan, crdtype, crdprinsupp, issuetype,
				issueDate);

		if (cwstCrdDetail == null) {
			cwstCrdDetail = new CwstCrdDetail();
			cwstCrdDetail.setCifNo(cifno);
			cwstCrdDetail.setCustName(custname);
			cwstCrdDetail.setCrdType(crdtype);
			cwstCrdDetail.setCrdPrinSupp(crdprinsupp);
			cwstCrdDetail.setIssueType(issuetype);
			cwstCrdDetail.setIssueDate(issueDate);
			cwstCrdDetail.setPan(pan);
			cwstCrdDetail.setBrchCde(brchcode);
			cwstCrdDetail.setTransCustStatus(keyStatus);
			cwstCrdDetail.setTransCustNote(NoteStatus);

		}

		// Lấy thời gian trên form:
		if (selectDate == null || selectDate.equals(""))
			cwstCrdDetail.setTransCustDate(timeConverter.getCurrentTime("SHORT"));
		else
			cwstCrdDetail.setTransCustDate(selectDate);

		if ("TOCUST".equals(type)) {
			// Neu chua DUyet, thi user cap 1 duoc chuyen trang thai thoai mai
			if (!cwstCrdDetail.getTransCustIscheck() && cwstCrdDetail.getTransBranchRecCheck()) {
				cwstCrdDetail.setTransCust(true);
				cwstCrdDetail.setTransCustLock(true);
				cwstCrdDetail.setTransCustStatus(keyStatus);
				cwstCrdDetail.setTransCustNote(NoteStatus);
				crdDetailRepo.save(cwstCrdDetail);
			}

			/*
			 * Neu da duyet giao thẻ KH, va status hien tai ở DB la 01 02 thi
			 * KHONG CHAP NHAN chỉnh sửa
			 */
			if (cwstCrdDetail.getTransCustIscheck() && cwstCrdDetail.getTransBranchRecCheck()
					&& (cwstCrdDetail.getTransCustStatus().equals("01")
							|| cwstCrdDetail.getTransCustStatus().equals("02"))) {
				return;
			}

			/*
			 * Neu da duyet giao thẻ KH, va status đã chọn là 05 06: thi user
			 * cap 1 van dc phep doi trang thai và CẦN Duyệt lại lần nữa
			 */
			if (cwstCrdDetail.getTransCustIscheck() && cwstCrdDetail.getTransBranchRecCheck()
					&& (keyStatus.equals("05") || keyStatus.equals("06"))) {
				cwstCrdDetail.setTransCust(true);
				cwstCrdDetail.setTransCustLock(true);
				cwstCrdDetail.setTransCustStatus(keyStatus);
				cwstCrdDetail.setTransCustNote(NoteStatus);
				cwstCrdDetail.setTransCustIscheck(false);
				crdDetailRepo.save(cwstCrdDetail);
				return;
			}

			/*
			 * Neu status đã chọn la 03, 04: thi được check duyệt auto
			 */
			if (cwstCrdDetail.getTransBranchRecCheck() && (keyStatus.equals("03") || keyStatus.equals("04"))) {
				cwstCrdDetail.setTransCustStatus(keyStatus);
				cwstCrdDetail.setTransCustNote(NoteStatus);
				cwstCrdDetail.setTransCust(true);
				cwstCrdDetail.setTransCustLock(true);
				cwstCrdDetail.setTransCustIscheck(true);
				crdDetailRepo.save(cwstCrdDetail);
				return;
			}

			/*
			 * Neu status dưới DB la 03, 04: thi được thoải mái chuyển qua trạng
			 * thái khác và cần duyệt lại
			 */
			if (cwstCrdDetail.getTransBranchRecCheck() && (cwstCrdDetail.getTransCustStatus().equals("03")
					|| cwstCrdDetail.getTransCustStatus().equals("04"))) {
				cwstCrdDetail.setTransCustStatus(keyStatus);
				cwstCrdDetail.setTransCustNote(NoteStatus);
				cwstCrdDetail.setTransCust(true);
				cwstCrdDetail.setTransCustLock(true);
				cwstCrdDetail.setTransCustIscheck(false);
				crdDetailRepo.save(cwstCrdDetail);
				return;
			}
		}
		return;
	}

	/**
	 * XU LY CAP NHAT CHO POPUP GIAO THE TAN NOI
	 */
	@Override
	public void saveDataFromFormContextMenu_GTTN(String cifno, String custname, String pan, String crdtype,
			String crdprinsupp, String issuetype, String issueDate, String type, String brchcode, String empCode,
			String empName, String fwbrnchCode, String note, String selectDate, String keyStatus) {

		CwstCrdDetail cwstCrdDetail = crdDetailRepo.findOneByIdx(cifno, custname, pan, crdtype, crdprinsupp, issuetype,
				issueDate);

		if (cwstCrdDetail == null) {
			cwstCrdDetail = new CwstCrdDetail();
			cwstCrdDetail.setCifNo(cifno);
			cwstCrdDetail.setCustName(custname);
			cwstCrdDetail.setCrdType(crdtype);
			cwstCrdDetail.setCrdPrinSupp(crdprinsupp);
			cwstCrdDetail.setIssueType(issuetype);
			cwstCrdDetail.setIssueDate(issueDate);
			cwstCrdDetail.setPan(pan);
			cwstCrdDetail.setBrchCde(brchcode);
			cwstCrdDetail.setTransCustStatus(keyStatus);
		}

		if ("TOCUST_GTTN".equals(type)) {
			if (keyStatus.equals("01")) {
				cwstCrdDetail.setXacNhanGttn("1");
				cwstCrdDetail.setTimeXacNhanGttn(selectDate);
			}

		}
		crdDetailRepo.save(cwstCrdDetail);
	}

	@Override
	public void updateDate(long id, String cifno, String custname, String pan, String crdtype, String crdprinsupp,
			String issuetype, String issueDate, String date, String type) {
		// saveData(cifno, cust_name,pan, crd_type, crd_prin_supp, issue_type,
		// issue_date,type);
		CwstCrdDetail cwstCrdDetail = crdDetailRepo.findOneById(id);
		if ("TOMK".equals(type)) {
			cwstCrdDetail.setTransMkDate(date);
		}
		if ("RECMK".equals(type)) {
			cwstCrdDetail.setRecMkDate(date);
		}
		if ("TOBRANCH".equals(type)) {
			cwstCrdDetail.setTransBranchDate(date);

		}
		if ("TOEMP".equals(type)) {
			cwstCrdDetail.setTransEmpDate(date);

		}
		if ("TOCUST".equals(type)) {
			cwstCrdDetail.setTransBranchDate(date);

		}
		crdDetailRepo.save(cwstCrdDetail);

	}

	@Override
	public CwstCrdDetail findOneById(long id) {
		CwstCrdDetail cwstCrdDetail = null;
		cwstCrdDetail = crdDetailRepo.findOne(id);
		if (cwstCrdDetail != null) {
			return cwstCrdDetail;
		} else {
			cwstCrdDetail = new CwstCrdDetail();
			cwstCrdDetail.setId(id);
			return cwstCrdDetail;
		}
	}

	/**
	 * Luu du lieu tu form
	 */
	@Override
	public void saveDataFromForm(String cifno, String custname, String pan, String crdtype, String crdprinsupp,
			String issuetype, String issuedate, boolean transmk, String tranmkdate, boolean recmk, String recmkdate,
			boolean transbranch, String transbranchdate, String tranbranchempcode, boolean tranemp, String tranempcode,
			String tranempname,

			String tranempDate, boolean bolTransEmpRender, // boolean
															// bolTransEmpRender1,
															// boolean
															// bolTransEmpRender2,
			String transEmpRenderDate, String transEmpRender, // String
																// transEmpRender1,
																// //String
																// transEmpRender2,
			boolean Renderlock1,

			String transEmpDate1, boolean bolTransEmpRender1, String transEmpRenderDate1, String transEmpRender1,
			boolean Renderlock2,

			String transEmpDate2, boolean bolTransEmpRender2, String transEmpRenderDate2, String transEmpRender2,
			boolean Renderlock3,

			boolean transcust, String transcustdate, String tranbranchempname, String transbranchfwcde,
			String transbranchnote, boolean transbranchrec, String transbranchrecdate, String transempnote,
			String filepin) {

		CwstCrdDetail cwstCrdDetail = null;
		cwstCrdDetail = crdDetailRepo.findOneByIdx(cifno, custname, pan, crdtype, crdprinsupp, issuetype, issuedate);
		if (cwstCrdDetail == null) {
			cwstCrdDetail = new CwstCrdDetail();
			cwstCrdDetail.setCifNo(cifno);
			cwstCrdDetail.setCustName(custname);
			cwstCrdDetail.setPan(pan);
			cwstCrdDetail.setCrdType(crdtype);
			cwstCrdDetail.setCrdPrinSupp(crdprinsupp);
			cwstCrdDetail.setIssueType(issuetype);
			cwstCrdDetail.setIssueDate(issuedate);
		}
		cwstCrdDetail.setTransMk(transmk);
		cwstCrdDetail.setTransMkDate(tranmkdate);
		if (!transmk) {
			cwstCrdDetail.setTransMkLock(false);
		}

		cwstCrdDetail.setRecMk(recmk);
		cwstCrdDetail.setRecMkDate(recmkdate);
		if (!recmk) {
			cwstCrdDetail.setRecMk(false);
		}

		cwstCrdDetail.setTransBranch(transbranch);
		cwstCrdDetail.setTransBranchDate(transbranchdate);
		cwstCrdDetail.setTransBranchEmpName(tranbranchempname);
		cwstCrdDetail.setTransBranchEmpCode(tranbranchempcode);
		cwstCrdDetail.setTransBranchNote(transbranchnote);
		cwstCrdDetail.setFwBrnCde(transbranchfwcde);
		if (!transbranch) {
			cwstCrdDetail.setTransBranch(false);
		}

		cwstCrdDetail.setTransBranchRec(transbranchrec);
		cwstCrdDetail.setTransBranchRecDate(transbranchrecdate);
		if (!transbranchrec) {
			cwstCrdDetail.setTransBranchRecLock(false);
		}

		cwstCrdDetail.setTransEmp(tranemp);
		cwstCrdDetail.setTransEmpDate(tranempDate);
		cwstCrdDetail.setTransEmpDate1(transEmpDate1);
		cwstCrdDetail.setTransEmpDate2(transEmpDate2);
		cwstCrdDetail.setTransEmpCode(tranempcode);
		cwstCrdDetail.setTransEmpNote(transempnote);
		cwstCrdDetail.setTransEmpName(tranempname);
		if (!tranemp) {
			cwstCrdDetail.setTransEmpLock(false);
		}

		cwstCrdDetail.setTransEmpIsRender(bolTransEmpRender);
		cwstCrdDetail.setTransEmpRenderDate(transEmpRenderDate);
		cwstCrdDetail.setTransEmpIsRender1(bolTransEmpRender1);
		cwstCrdDetail.setTransEmpIsRender2(bolTransEmpRender2);
		cwstCrdDetail.setTransEmpRenderDate1(transEmpRenderDate1);
		cwstCrdDetail.setTransEmpRenderDate2(transEmpRenderDate2);
		cwstCrdDetail.setTransEmpRender(transEmpRender);
		cwstCrdDetail.setTransEmpRender1(transEmpRender1);
		cwstCrdDetail.setTransEmpRender2(transEmpRender2);

		cwstCrdDetail.setTransCust(transcust);
		cwstCrdDetail.setTransCustDate(transcustdate);
		cwstCrdDetail.setFilePin(filepin);
		if (!transcust) {
			cwstCrdDetail.setTransCustLock(false);
		}
		crdDetailRepo.save(cwstCrdDetail);
	}

	@Override
	public CwstCrdDetail findOneByIdx(String cifno, String custname, String pan, String crdtype, String prinsupp,
			String issuetype, String issuedate) {
		CwstCrdDetail cwstCrdDetail = crdDetailRepo.findOneByIdx(cifno, custname, pan, crdtype, prinsupp, issuetype,
				issuedate);
		if (cwstCrdDetail == null) {
			cwstCrdDetail = new CwstCrdDetail();
		}
		return cwstCrdDetail;
	}

	/**
	 * Khoa du lieu
	 */
	@Override
	public boolean lockData(long id) {
		CwstCrdDetail cwstCrdDetail = crdDetailRepo.findOneById(id);
		if (cwstCrdDetail != null) {
			if (SecurityUtils.hasRole("ROLE_HO")) {
				// Khoa du lieu chuyen file cho MK
				if (cwstCrdDetail.getTransMk() && cwstCrdDetail.getTransMkDate() != null
						&& !cwstCrdDetail.getTransMkLock()) {
					cwstCrdDetail.setTransMkLock(true);
					cwstCrdDetail.setTransMkRealTimeLock(timeConverter.getCurrentTime("SHORT"));
				}
			}
			if (SecurityUtils.hasRole("ROLE_DONVI")) {
				// Khoa du don vi da nhan the
				if (cwstCrdDetail.getTransMkIscheck() && cwstCrdDetail.getTransBranchRec()
						&& cwstCrdDetail.getTransBranchRecDate() != null && !cwstCrdDetail.getTransBranchRecLock()) {
					cwstCrdDetail.setTransBranchRecLock(true);
					cwstCrdDetail.setTransBrnRecRealTimeLock(timeConverter.getCurrentTime("SHORT"));
				}

				// Khoa du lieu giao the cho KH
				if (cwstCrdDetail.getTransCust() && cwstCrdDetail.getTransCustDate() != null
						&& cwstCrdDetail.getTransCustLock() == false) {
					cwstCrdDetail.setTransCustLock(true);
					cwstCrdDetail.setTransCustRealTimeLock(timeConverter.getCurrentTime("SHORT"));
				}
				if (cwstCrdDetail.getTransCust() && cwstCrdDetail.getTransCustDate() != null
						&& cwstCrdDetail.getTransCustLock() == false) {
					return false;
				}
			}
			crdDetailRepo.save(cwstCrdDetail);

		}
		return true;
	}

	@Override
	public boolean unlockData(long id) {
		CwstCrdDetail cwstCrdDetail = crdDetailRepo.findOneById(id);
		if (cwstCrdDetail != null) {
			if (SecurityUtils.hasRole("ROLE_HOCHECKER")) {
				// Mo Khoa du lieu chuyen file cho MK
				if (cwstCrdDetail.getTransMkLock() && !cwstCrdDetail.getTransMkIscheck()
						&& !cwstCrdDetail.getRecMkLock()) {
					cwstCrdDetail.setTransMkLock(false);
				}
				// Mo Khoa du lieu nhan file tu MK
				if (cwstCrdDetail.getRecMkLock() && !cwstCrdDetail.getRecMkIscheck()
						&& !cwstCrdDetail.getTransBranchLock()) {
					cwstCrdDetail.setRecMkLock(false);
				}
				// Mo Khoa du lieu chuyen the cho don vi
				if (cwstCrdDetail.getTransBranchLock() && !cwstCrdDetail.getTransBranchIscheck()
						&& !cwstCrdDetail.getTransBranchRecLock()) {
					cwstCrdDetail.setTransBranchLock(false);
				}
			}
			if (SecurityUtils.hasRole("ROLE_DONVICHECKER")) {

				// Mo Khoa du don vi da nhan the
				if (cwstCrdDetail.getTransBranchRecLock() && !cwstCrdDetail.getTransBranchRecCheck()
						&& (cwstCrdDetail.getTransCustDate() == null || cwstCrdDetail.getTransCustDate().equals("0"))) {
					cwstCrdDetail.setTransBranchRecLock(false);
					cwstCrdDetail.setTransBranchRecCheck(false);
				}
				// KHONG XAIMo Khoa du lieu giao the cho nhan vien
				if (cwstCrdDetail.isTransEmpLock1() && !cwstCrdDetail.isTransEmpIscheck1()
						&& !cwstCrdDetail.getTransCustLock()) {
					cwstCrdDetail.setTransEmpLock1(false);
				}

				// Mo Khoa du lieu giao the cho nhan vien
				if (cwstCrdDetail.isTransEmpLock2() && !cwstCrdDetail.isTransEmpIscheck2()
						&& !cwstCrdDetail.getTransCustLock()) {
					cwstCrdDetail.setTransEmpLock2(false);
				}

				// Mo Khoa du lieu giao the cho nhan vien
				if (cwstCrdDetail.isTransEmpLock3() && !cwstCrdDetail.isTransEmpIscheck3()
						&& !cwstCrdDetail.getTransCustLock()) {
					cwstCrdDetail.setTransEmpLock3(false);
				}

				// Mo Khoa du lieu giao the cho nhan vien
				if (cwstCrdDetail.getTransEmpLock() && !cwstCrdDetail.getTransEmpIscheck()
						&& !cwstCrdDetail.getTransCustLock()) {
					cwstCrdDetail.setTransEmpLock(false);
				}

				// Mo Khoa du lieu giao the cho KH
				if (cwstCrdDetail.getTransCustLock() && cwstCrdDetail.getTransCustIscheck()
						&& !cwstCrdDetail.getTransCustStatus().equals("01")
						&& !cwstCrdDetail.getTransCustStatus().equals("02")) {
					// cwstCrdDetail.setTransCustStatus("");
					// cwstCrdDetail.setTransCustNote("");
					cwstCrdDetail.setTransCustIscheck(false);
					cwstCrdDetail.setTransCustLock(false);
				}
			}
			crdDetailRepo.save(cwstCrdDetail);

		}
		return true;
	}

	/**
	 * Thuc hien kiem soat
	 */
	@Override
	public boolean checkData(long id) {

		final CwstCrdDetail cwstCrdDetail = crdDetailRepo.findOne(id);
		if (cwstCrdDetail != null) {
			if (SecurityUtils.hasRole("ROLE_HOCHECKER")) {
				/*
				 * << KIEM SOAT CHUYEN FILE CTY MKS >>
				 * ---------------------------------------------------------
				 */
				if (cwstCrdDetail.getTransMkLock() && !cwstCrdDetail.getTransMkIscheck()) {
					cwstCrdDetail.setTransMkIscheck(true);
					cwstCrdDetail.setTransMkRealTimeCheck(timeConverter.getCurrentTime("SHORT"));
				} /*
					 * ---------------------------------------------------------
					 * -- --------------------------------
					 */

			}

			if (SecurityUtils.hasRole("ROLE_DONVICHECKER")) {
				/*
				 * << KIEM SOAT DON VI DA NHAN THE >>
				 * -----------------------------------------------------------
				 */

				if (cwstCrdDetail.getTransBranchRecLock() && !cwstCrdDetail.getTransBranchRecCheck()
						&& cwstCrdDetail.getTransBranchRec()) {
					cwstCrdDetail.setTransBranchRecCheck(true);
					cwstCrdDetail.setTransBrnRecRealTimeCheck(timeConverter.getCurrentTime("SHORT"));
				}

				/*
				 * -------------------------------------------------------------
				 * -------------------------------
				 */

				/*
				 * << KIEM SOAT GIAO THE CHO KHACH HANG >>
				 * ------------------------------------------------------
				 */
				if (cwstCrdDetail.getTransCustLock() && !cwstCrdDetail.getTransCustIscheck()
						&& cwstCrdDetail.getTransBranchRecCheck()) {
					cwstCrdDetail.setTransCustIscheck(true);
					cwstCrdDetail.setTransCustRealTimeCheck(timeConverter.getCurrentTime("SHORT"));
				}
			}
			crdDetailRepo.save(cwstCrdDetail);
		}
		return true;

	}

	@Override
	public void update(final CwstCrdDetail cwstCrdDetail) {
		crdDetailRepo.save(cwstCrdDetail);
	}

	public List<Object[]> findCaseDistributingCardBeforeNumberOfDay(String brchcde, int num) {
		return crdDetailRepo.findCaseDistributingCardBeforeNumberOfDay(brchcde, num);
	}

	@Override
	public String findWorkingDate() {
		return crdDetailRepo.findWorkingDate();
	}

	@Override
	public List<Object[]> findBrchBeforeNumberOfDay(int num) {
		return crdDetailRepo.findBrchBeforeNumberOfDay(num);
	}

	@Override
	public List<CardDetail> getListChuaDuyetChuaKichHoat(int tungay, int denngay) {
		// TODO Auto-generated method stub
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		localDataSource = (DataSource) helper.getBean("dataSource");
		
		StringBuilder sqlString = new StringBuilder();

		sqlString 	.append("select CIF_NO CIF,\r\n" + 
				"    B.LOC,\r\n" + 
				"    TRIM(CUST_NAME) HOTEN,\r\n" + 
				"    trim(FX_IR056_HP) SDT,\r\n" + 
				"    PX_IRPANMAP_PANMASK so_the_che,\r\n" + 
				"    CRD_TYPE LOAI_THE,\r\n" + 
				"    ISSUE_DATE NGAY_PHAT_HANH,\r\n" + 
				"    trim(A.brch_cde) DON_VI,\r\n" + 
				"    trim(D.BRANCH_NAME) TEN_DON_VI,\r\n" + 
				"    'CHUA DUYET' TINH_TRANG_DUYET,\r\n" + 
				"    TRANS_BRANCH_REC_DATE NGAY_DV_NHANTHE_TU_MKS,\r\n" + 
				"    TRANS_CUST_DATE NGAYNHAP_GIAOTHE_KH,\r\n" + 
				"    (case when TRANS_CUST_STATUS = '01' then 'GIAO THE THANH CONG'\r\n" + 
				"               when TRANS_CUST_STATUS = '02' then 'KH TU CHOI NHAN THE'\r\n" + 
				"               when TRANS_CUST_STATUS = '03' then 'KH HEN DEN NHAN'\r\n" + 
				"               when TRANS_CUST_STATUS = '04' then 'KHONG LIEN LAC DUOC KH'\r\n" + 
				"               when TRANS_CUST_STATUS = '05' then 'DA CHUYEN DOI TAC PHT'\r\n" + 
				"               when TRANS_CUST_STATUS = '06' then 'LY DO KHAC'\r\n" + 
				"               when TRANS_CUST_STATUS = '07' then 'THE CHON SAI THONG SO'\r\n" + 
				"               else '' end) as TINH_TRANG_TREN_PPT\r\n" + 
				"FROM ppt_crd_detail A\r\n" + 
				"INNER JOIN ( \r\n" + 
				"    SELECT F9_IR025_LOC_ACCT LOC,PX_IR025_PAN PAN,F9_IR025_CRN CRN,fx_ir025_brch_cde brch_cde\r\n" + 
				"    FROM  ir025@im\r\n" + 
				"    where F9_IR025_CRD_ATV_DT = 0--CHUA KICH HOAT\r\n" + 
				"    UNION ALL\r\n" + 
				"    SELECT F9_IR275_LOC_ACCT LOC,PX_IR275_OWN_PAN PAN,F9_IR275_OWN_CRN CRN,FX_IR275_BRCH_CDE brch_cde\r\n" + 
				"    FROM ir275@im\r\n" + 
				"    where f9_ir275_crd_atv_dt = 0\r\n" + 
				") B ON A.PAN=B.PAN\r\n" + 
				"LEFT JOIN ir056@IM C ON B.CRN=C.P9_IR056_CRN\r\n" + 
				"LEFT JOIN full_branch D ON D.BRANCH_CODE=A.brch_cde\r\n" + 
				"LEFT JOIN ir_pan_map@im E ON B.PAN=PX_IRPANMAP_PAN\r\n" + 
				"WHERE  TRANS_CUST_DATE is not null\r\n" + 
				"and trans_cust_lock = 1 \r\n" + 
				"AND TRANS_CUST_ISCHECK = 0  \r\n" + 
				"AND ISSUE_DATE between ? and ?");
		
		Connection connect;
		List<CardDetail> crdDetailList = new ArrayList<CardDetail>();
		try {
			connect = localDataSource.getConnection();
			
			PreparedStatement preStmt = connect.prepareStatement(sqlString.toString());
			preStmt.setInt(1, tungay);
			preStmt.setInt(2, denngay);
			
			ResultSet rs = preStmt.executeQuery();
			
			while(rs.next()) {
				CardDetail crdDetail = new CardDetail();
				crdDetail.setCif(rs.getString(1));
				crdDetail.setLoc(rs.getString(2));
				crdDetail.setCustName(rs.getString(3));
				crdDetail.setPhoneNo(rs.getString(4));
				crdDetail.setPanMask(rs.getString(5));
				crdDetail.setCardType(rs.getString(6));
				crdDetail.setIssueDate(rs.getString(7));
				crdDetail.setBrchCde(rs.getString(8));
				crdDetail.setBrchName(rs.getString(9));
				crdDetail.setStatusCheck(rs.getString(10));
				crdDetail.setTransMkDate(rs.getString(11));
				crdDetail.setTransCustDate(rs.getString(12));
				crdDetail.setTransCustStatus(rs.getString(13));
				
				crdDetailList.add(crdDetail);
			}
			
			preStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return crdDetailList;
		
	}

	@Override
	public List<CardDetail> getListChuaDuyetDaKichHoat(int tungay, int denngay) {
		// TODO Auto-generated method stub
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		localDataSource = (DataSource) helper.getBean("dataSource");
		
		StringBuilder sqlString = new StringBuilder();

		sqlString 	.append("select CIF_NO CIF,\r\n" + 
				"    B.LOC,\r\n" + 
				"    CUST_NAME HOTEN,\r\n" + 
				"    trim(FX_IR056_HP) SDT,\r\n" + 
				"    PX_IRPANMAP_PANMASK so_the_che,\r\n" + 
				"    CRD_TYPE LOAI_THE,\r\n" + 
				"    ISSUE_DATE NGAY_PHAT_HANH,\r\n" + 
				"    trim(A.brch_cde) DON_VI,\r\n" + 
				"    trim(D.BRANCH_NAME) TEN_DON_VI,\r\n" + 
				"    'CHUA DUYET' TINH_TRANG_DUYET,\r\n" + 
				"    TRANS_BRANCH_REC_DATE NGAY_DV_NHANTHE_TU_MKS,\r\n" + 
				"    TRANS_CUST_DATE NGAYNHAP_GIAOTHE_KH,\r\n" + 
				"    (case when TRANS_CUST_STATUS = '01' then 'GIAO THE THANH CONG'\r\n" + 
				"               when TRANS_CUST_STATUS = '02' then 'KH TU CHOI NHAN THE'\r\n" + 
				"               when TRANS_CUST_STATUS = '03' then 'KH HEN DEN NHAN'\r\n" + 
				"               when TRANS_CUST_STATUS = '04' then 'KHONG LIEN LAC DUOC KH'\r\n" + 
				"               when TRANS_CUST_STATUS = '05' then 'DA CHUYEN DOI TAC PHT'\r\n" + 
				"               when TRANS_CUST_STATUS = '06' then 'LY DO KHAC'\r\n" + 
				"               when TRANS_CUST_STATUS = '07' then 'THE CHON SAI THONG SO'\r\n" + 
				"               else '' end) as TINH_TRANG_TREN_PPT\r\n" + 
				"FROM ppt_crd_detail A\r\n" + 
				"INNER JOIN ( \r\n" + 
				"    SELECT F9_IR025_LOC_ACCT LOC,PX_IR025_PAN PAN,F9_IR025_CRN CRN,fx_ir025_brch_cde brch_cde\r\n" + 
				"    FROM  ir025@im\r\n" + 
				"    where F9_IR025_CRD_ATV_DT != 0--DA KICH HOAT\r\n" + 
				"    UNION ALL\r\n" + 
				"    SELECT F9_IR275_LOC_ACCT LOC,PX_IR275_OWN_PAN PAN,F9_IR275_OWN_CRN CRN,FX_IR275_BRCH_CDE brch_cde\r\n" + 
				"    FROM ir275@im\r\n" + 
				"    where f9_ir275_crd_atv_dt != 0\r\n" + 
				") B ON A.PAN=B.PAN\r\n" + 
				"LEFT JOIN ir056@IM C ON B.CRN=C.P9_IR056_CRN\r\n" + 
				"LEFT JOIN full_branch D ON D.BRANCH_CODE=A.brch_cde\r\n" + 
				"LEFT JOIN ir_pan_map@im E ON B.PAN=PX_IRPANMAP_PAN\r\n" + 
				"WHERE  TRANS_CUST_DATE is not null\r\n" + 
				"and trans_cust_lock = 1 \r\n" + 
				"AND TRANS_CUST_ISCHECK = 0  \r\n" + 
				"AND ISSUE_DATE between ? and ?");
		
		Connection connect;
		List<CardDetail> crdDetailList = new ArrayList<CardDetail>();
		try {
			connect = localDataSource.getConnection();
			
			PreparedStatement preStmt = connect.prepareStatement(sqlString.toString());
			preStmt.setInt(1, tungay);
			preStmt.setInt(2, denngay);
			
			ResultSet rs = preStmt.executeQuery();
			
			while(rs.next()) {
				CardDetail crdDetail = new CardDetail();
				crdDetail.setCif(rs.getString(1));
				crdDetail.setLoc(rs.getString(2));
				crdDetail.setCustName(rs.getString(3));
				crdDetail.setPhoneNo(rs.getString(4));
				crdDetail.setPanMask(rs.getString(5));
				crdDetail.setCardType(rs.getString(6));
				crdDetail.setIssueDate(rs.getString(7));
				crdDetail.setBrchCde(rs.getString(8));
				crdDetail.setBrchName(rs.getString(9));
				crdDetail.setStatusCheck(rs.getString(10));
				crdDetail.setTransMkDate(rs.getString(11));
				crdDetail.setTransCustDate(rs.getString(12));
				crdDetail.setTransCustStatus(rs.getString(13));
				
				crdDetailList.add(crdDetail);
			}
			
			preStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return crdDetailList;
		
	}

	@Override
	public List<CardDetail> getListChuaNhapChuaKichHoat(int tungay, int denngay) {
		// TODO Auto-generated method stub
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		localDataSource = (DataSource) helper.getBean("dataSource");
		
		StringBuilder sqlString = new StringBuilder();

		sqlString 	.append("select CIF_NO CIF,\r\n" + 
				"    B.LOC,\r\n" + 
				"    TRIM(CUST_NAME) HOTEN,\r\n" + 
				"    trim(FX_IR056_HP) SDT,\r\n" + 
				"    PX_IRPANMAP_PANMASK so_the_che,\r\n" + 
				"    CRD_TYPE LOAI_THE,\r\n" + 
				"    ISSUE_DATE NGAY_PHAT_HANH,\r\n" + 
				"    trim(A.brch_cde) DON_VI,\r\n" + 
				"    trim(D.BRANCH_NAME) TEN_DON_VI,\r\n" + 
				"    'CHUA NHAP' TINH_TRANG_DUYET,\r\n" + 
				"    TRANS_BRANCH_REC_DATE NGAY_DV_NHANTHE_TU_MKS,\r\n" + 
				"    TRANS_CUST_DATE NGAYNHAP_GIAOTHE_KH,\r\n" + 
				"    (case when TRANS_CUST_STATUS = '01' then 'GIAO THE THANH CONG'\r\n" + 
				"               when TRANS_CUST_STATUS = '02' then 'KH TU CHOI NHAN THE'\r\n" + 
				"               when TRANS_CUST_STATUS = '03' then 'KH HEN DEN NHAN'\r\n" + 
				"               when TRANS_CUST_STATUS = '04' then 'KHONG LIEN LAC DUOC KH'\r\n" + 
				"               when TRANS_CUST_STATUS = '05' then 'DA CHUYEN DOI TAC PHT'\r\n" + 
				"               when TRANS_CUST_STATUS = '06' then 'LY DO KHAC'\r\n" + 
				"               when TRANS_CUST_STATUS = '07' then 'THE CHON SAI THONG SO'\r\n" + 
				"               else '' end) as TINH_TRANG_TREN_PPT\r\n" + 
				"FROM ppt_crd_detail A\r\n" + 
				"INNER JOIN ( \r\n" + 
				"    SELECT F9_IR025_LOC_ACCT LOC,PX_IR025_PAN PAN,F9_IR025_CRN CRN,fx_ir025_brch_cde brch_cde\r\n" + 
				"    FROM  ir025@im\r\n" + 
				"    where F9_IR025_CRD_ATV_DT = 0--CHUA KICH HOAT\r\n" + 
				"    UNION ALL\r\n" + 
				"    SELECT F9_IR275_LOC_ACCT LOC,PX_IR275_OWN_PAN PAN,F9_IR275_OWN_CRN CRN,FX_IR275_BRCH_CDE brch_cde\r\n" + 
				"    FROM ir275@im\r\n" + 
				"    where f9_ir275_crd_atv_dt = 0\r\n" + 
				") B ON A.PAN=B.PAN\r\n" + 
				"LEFT JOIN ir056@IM C ON B.CRN=C.P9_IR056_CRN\r\n" + 
				"LEFT JOIN full_branch D ON D.BRANCH_CODE=A.brch_cde\r\n" + 
				"LEFT JOIN ir_pan_map@im E ON B.PAN=PX_IRPANMAP_PAN\r\n" + 
				"WHERE TRANS_CUST_DATE is null\r\n" + 
				"and trans_cust_lock = 0 \r\n" + 
				"AND TRANS_CUST_ISCHECK = 0  \r\n" + 
				"AND ISSUE_DATE between ? and ?");
		
		Connection connect;
		List<CardDetail> crdDetailList = new ArrayList<CardDetail>();
		try {
			connect = localDataSource.getConnection();
			
			PreparedStatement preStmt = connect.prepareStatement(sqlString.toString());
			preStmt.setInt(1, tungay);
			preStmt.setInt(2, denngay);
			
			ResultSet rs = preStmt.executeQuery();
			
			while(rs.next()) {
				CardDetail crdDetail = new CardDetail();
				crdDetail.setCif(rs.getString(1));
				crdDetail.setLoc(rs.getString(2));
				crdDetail.setCustName(rs.getString(3));
				crdDetail.setPhoneNo(rs.getString(4));
				crdDetail.setPanMask(rs.getString(5));
				crdDetail.setCardType(rs.getString(6));
				crdDetail.setIssueDate(rs.getString(7));
				crdDetail.setBrchCde(rs.getString(8));
				crdDetail.setBrchName(rs.getString(9));
				crdDetail.setStatusCheck(rs.getString(10));
				crdDetail.setTransMkDate(rs.getString(11));
				crdDetail.setTransCustDate(rs.getString(12));
				crdDetail.setTransCustStatus(rs.getString(13));
				
				crdDetailList.add(crdDetail);
			}
			
			preStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return crdDetailList;
	}

	@Override
	public List<CardDetail> getListChuaNhapDaKichHoat(int tungay, int denngay) {
		// TODO Auto-generated method stub
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		localDataSource = (DataSource) helper.getBean("dataSource");
		
		StringBuilder sqlString = new StringBuilder();

		sqlString 	.append("select CIF_NO CIF,\r\n" + 
				"    B.LOC,\r\n" + 
				"    CUST_NAME HOTEN,\r\n" + 
				"    trim(FX_IR056_HP) SDT,\r\n" + 
				"    PX_IRPANMAP_PANMASK so_the_che,\r\n" + 
				"    CRD_TYPE LOAI_THE,\r\n" + 
				"    ISSUE_DATE NGAY_PHAT_HANH,\r\n" + 
				"    trim(A.brch_cde) DON_VI,\r\n" + 
				"    trim(D.BRANCH_NAME) TEN_DON_VI,\r\n" + 
				"    'CHUA NHAP' TINH_TRANG_DUYET,\r\n" + 
				"    TRANS_BRANCH_REC_DATE NGAY_DV_NHANTHE_TU_MKS,\r\n" + 
				"    TRANS_CUST_DATE NGAYNHAP_GIAOTHE_KH,\r\n" + 
				"    (case when TRANS_CUST_STATUS = '01' then 'GIAO THE THANH CONG'\r\n" + 
				"               when TRANS_CUST_STATUS = '02' then 'KH TU CHOI NHAN THE'\r\n" + 
				"               when TRANS_CUST_STATUS = '03' then 'KH HEN DEN NHAN'\r\n" + 
				"               when TRANS_CUST_STATUS = '04' then 'KHONG LIEN LAC DUOC KH'\r\n" + 
				"               when TRANS_CUST_STATUS = '05' then 'DA CHUYEN DOI TAC PHT'\r\n" + 
				"               when TRANS_CUST_STATUS = '06' then 'LY DO KHAC'\r\n" + 
				"               when TRANS_CUST_STATUS = '07' then 'THE CHON SAI THONG SO'\r\n" + 
				"               else '' end) as TINH_TRANG_TREN_PPT,A.PAN\r\n" + 
				"FROM ppt_crd_detail A\r\n" + 
				"INNER JOIN ( \r\n" + 
				"    SELECT F9_IR025_LOC_ACCT LOC,PX_IR025_PAN PAN,F9_IR025_CRN CRN,fx_ir025_brch_cde brch_cde\r\n" + 
				"    FROM  ir025@im\r\n" + 
				"    where F9_IR025_CRD_ATV_DT != 0--DA KICH HOAT\r\n" + 
				"    UNION ALL\r\n" + 
				"    SELECT F9_IR275_LOC_ACCT LOC,PX_IR275_OWN_PAN PAN,F9_IR275_OWN_CRN CRN,FX_IR275_BRCH_CDE brch_cde\r\n" + 
				"    FROM ir275@im\r\n" + 
				"    where f9_ir275_crd_atv_dt != 0\r\n" + 
				") B ON A.PAN=B.PAN\r\n" + 
				"LEFT JOIN ir056@IM C ON B.CRN=C.P9_IR056_CRN\r\n" + 
				"LEFT JOIN full_branch D ON D.BRANCH_CODE=A.brch_cde\r\n" + 
				"LEFT JOIN ir_pan_map@im E ON B.PAN=PX_IRPANMAP_PAN\r\n" + 
				"WHERE TRANS_CUST_DATE is null\r\n" + 
				"    and trans_cust_lock = 0 \r\n" + 
				"    AND TRANS_CUST_ISCHECK = 0 \r\n" + 
				"AND ISSUE_DATE between ? and ?");
		
		Connection connect;
		List<CardDetail> crdDetailList = new ArrayList<CardDetail>();
		try {
			connect = localDataSource.getConnection();
			
			PreparedStatement preStmt = connect.prepareStatement(sqlString.toString());
			preStmt.setInt(1, tungay);
			preStmt.setInt(2, denngay);
			
			ResultSet rs = preStmt.executeQuery();
			
			while(rs.next()) {
				CardDetail crdDetail = new CardDetail();
				crdDetail.setCif(rs.getString(1));
				crdDetail.setLoc(rs.getString(2));
				crdDetail.setCustName(rs.getString(3));
				crdDetail.setPhoneNo(rs.getString(4));
				crdDetail.setPanMask(rs.getString(5));
				crdDetail.setCardType(rs.getString(6));
				crdDetail.setIssueDate(rs.getString(7));
				crdDetail.setBrchCde(rs.getString(8));
				crdDetail.setBrchName(rs.getString(9));
				crdDetail.setStatusCheck(rs.getString(10));
				crdDetail.setTransMkDate(rs.getString(11));
				crdDetail.setTransCustDate(rs.getString(12));
				crdDetail.setTransCustStatus(rs.getString(13));
				
				crdDetailList.add(crdDetail);
			}
			
			preStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return crdDetailList;
	}

	@Override
	public List<CardDetail> getListDaDuyetChuaKichHoat(int tungay, int denngay) {
		// TODO Auto-generated method stub
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		localDataSource = (DataSource) helper.getBean("dataSource");
		
		StringBuilder sqlString = new StringBuilder();

		sqlString 	.append("select CIF_NO CIF,\r\n" + 
				"    B.LOC,\r\n" + 
				"    CUST_NAME HOTEN,\r\n" + 
				"    trim(FX_IR056_HP) SDT,\r\n" + 
				"    PX_IRPANMAP_PANMASK so_the_che,\r\n" + 
				"    CRD_TYPE LOAI_THE,\r\n" + 
				"    ISSUE_DATE NGAY_PHAT_HANH,\r\n" + 
				"    trim(A.brch_cde) DON_VI,\r\n" + 
				"    trim(D.BRANCH_NAME) TEN_DON_VI,\r\n" + 
				"    'DA DUYET' TINH_TRANG_DUYET,\r\n" + 
				"    TRANS_BRANCH_REC_DATE NGAY_DV_NHANTHE_TU_MKS,\r\n" + 
				"    TRANS_CUST_DATE NGAYNHAP_GIAOTHE_KH,\r\n" + 
				"    (case when TRANS_CUST_STATUS = '01' then 'GIAO THE THANH CONG'\r\n" + 
				"               when TRANS_CUST_STATUS = '02' then 'KH TU CHOI NHAN THE'\r\n" + 
				"               when TRANS_CUST_STATUS = '03' then 'KH HEN DEN NHAN'\r\n" + 
				"               when TRANS_CUST_STATUS = '04' then 'KHONG LIEN LAC DUOC KH'\r\n" + 
				"               when TRANS_CUST_STATUS = '05' then 'DA CHUYEN DOI TAC PHT'\r\n" + 
				"               when TRANS_CUST_STATUS = '06' then 'LY DO KHAC'\r\n" + 
				"               when TRANS_CUST_STATUS = '07' then 'THE CHON SAI THONG SO'\r\n" + 
				"               else '' end) as TINH_TRANG_TREN_PPT,A.PAN\r\n" + 
				"FROM ppt_crd_detail A\r\n" + 
				"INNER JOIN ( \r\n" + 
				"    SELECT F9_IR025_LOC_ACCT LOC,PX_IR025_PAN PAN,F9_IR025_CRN CRN,fx_ir025_brch_cde brch_cde\r\n" + 
				"    FROM  ir025@im\r\n" + 
				"    where F9_IR025_CRD_ATV_DT = 0--CHUA KICH HOAT\r\n" + 
				"    UNION ALL\r\n" + 
				"    SELECT F9_IR275_LOC_ACCT LOC,PX_IR275_OWN_PAN PAN,F9_IR275_OWN_CRN CRN,FX_IR275_BRCH_CDE brch_cde\r\n" + 
				"    FROM ir275@im\r\n" + 
				"    where f9_ir275_crd_atv_dt = 0\r\n" + 
				") B ON A.PAN=B.PAN\r\n" + 
				"LEFT JOIN ir056@IM C ON B.CRN=C.P9_IR056_CRN\r\n" + 
				"LEFT JOIN full_branch D ON D.BRANCH_CODE=A.brch_cde\r\n" + 
				"LEFT JOIN ir_pan_map@im E ON B.PAN=PX_IRPANMAP_PAN\r\n" + 
				"WHERE  trans_cust_lock = 1 \r\n" + 
				"    and TRANS_CUST_DATE is not null\r\n" + 
				"    AND TRANS_CUST_ISCHECK = 1      \r\n" + 
				"AND ISSUE_DATE between ? and ?");
		
		Connection connect;
		List<CardDetail> crdDetailList = new ArrayList<CardDetail>();
		try {
			connect = localDataSource.getConnection();
			
			PreparedStatement preStmt = connect.prepareStatement(sqlString.toString());
			preStmt.setInt(1, tungay);
			preStmt.setInt(2, denngay);
			
			ResultSet rs = preStmt.executeQuery();
			
			while(rs.next()) {
				CardDetail crdDetail = new CardDetail();
				crdDetail.setCif(rs.getString(1));
				crdDetail.setLoc(rs.getString(2));
				crdDetail.setCustName(rs.getString(3));
				crdDetail.setPhoneNo(rs.getString(4));
				crdDetail.setPanMask(rs.getString(5));
				crdDetail.setCardType(rs.getString(6));
				crdDetail.setIssueDate(rs.getString(7));
				crdDetail.setBrchCde(rs.getString(8));
				crdDetail.setBrchName(rs.getString(9));
				crdDetail.setStatusCheck(rs.getString(10));
				crdDetail.setTransMkDate(rs.getString(11));
				crdDetail.setTransCustDate(rs.getString(12));
				crdDetail.setTransCustStatus(rs.getString(13));
				
				crdDetailList.add(crdDetail);
			}
			
			preStmt.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return crdDetailList;
	}
	
	
	@Override
	public List<CardTransStatus> getTongHopTrangThaiTheVung(int fromdate,int todate) {
		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		localDataSource = (DataSource) helper.getBean("dataSource");
		Connection connect = null;
		CallableStatement callableStatement = null;
		ResultSet rs = null;
		
		String sp_Tonghoptrangthaithevung = "{call fpt.scbpks_ppt.pr_tonghoptrangthaithevung(?,?,?)} ";
			
		List<CardTransStatus> listCardTransStatus = new ArrayList<CardTransStatus>();
			
		try {
			
			connect = localDataSource.getConnection();
			
			callableStatement = connect.prepareCall(sp_Tonghoptrangthaithevung);
			callableStatement.setInt(1, fromdate);
			callableStatement.setInt(2, todate);
			
			callableStatement.registerOutParameter(3, OracleTypes.CURSOR);
			callableStatement.executeUpdate();
			rs = (ResultSet) callableStatement.getObject(3);
			
			while(rs.next()) {
				
				CardTransStatus cardTransStatus = new CardTransStatus();
				cardTransStatus.setMaVung(rs.getString(1));
				cardTransStatus.setTenVung(rs.getString(2));
				cardTransStatus.setBrchCode(rs.getString(3));
				cardTransStatus.setBrchName(rs.getString(4));
				cardTransStatus.setTotalCard(rs.getInt(5));
				cardTransStatus.setSlChuaDuyetChuaKichHoat(rs.getInt(6));
				cardTransStatus.setSlChuaDuyetDaKichHoat(rs.getInt(7));
				cardTransStatus.setSlChuaNhapChuaKichHoat(rs.getInt(8));
				cardTransStatus.setSlChuaNhapDaKichHoat(rs.getInt(9));
				cardTransStatus.setSlDaDuyetChuaKichHoat(rs.getInt(10));
				cardTransStatus.setFromDate(rs.getInt(11));
				cardTransStatus.setToDate(rs.getInt(12));
				
				listCardTransStatus.add(cardTransStatus);
			}
		}
		catch (Exception ex){
			LOGGER.error(ex.getMessage());
		}
		finally{
			try {
				callableStatement.close();
				connect.close();
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		return listCardTransStatus;
	}

}
