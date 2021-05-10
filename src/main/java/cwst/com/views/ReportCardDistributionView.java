package cwst.com.views;

import com.ibm.icu.text.SimpleDateFormat;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import cwst.com.AdvancedFileDownloader;
import cwst.com.AdvancedFileDownloader.AdvancedDownloaderListener;
import cwst.com.AdvancedFileDownloader.DownloaderEvent;
import cwst.com.SecurityUtils;
import cwst.com.SpringConfigurationValueHelper;
import cwst.com.SpringContextHelper;
import cwst.com.entities.CardDetail;
import cwst.com.entities.CardTransStatus;
import cwst.com.services.CrdDetailService;
import cwst.com.services.SysUserService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.vaadin.simplefiledownloader.SimpleFileDownloader;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@SpringView(name = ReportCardDistributionView.VIEW_NAME)
public class ReportCardDistributionView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "bao-cao-phan-phoi-the";
	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(ReportCardDistributionView.class);

	private static final Logger LOGGER = LogManager.getLogger(ReportCardDistributionView.class);

	@Value("${dir.report}")
	private String sDirReport;
	@Autowired
	protected DataSource localDataSource;

	@Autowired
	private SysUserService sysUserService;
	
	@Autowired
	private CrdDetailService crdDetailService;
	
	private SpringConfigurationValueHelper configurationHelper;


	private final transient DateField dfFromdate = new DateField("Từ Ngày");
	private final transient DateField dfTodate = new DateField("Đến Ngày");
	private final transient TextField tfnumberofday = new TextField("Cộng thêm số ngày");
	private final transient TextField tfBrnCode = new TextField("Mã đơn vị");
	List<CardTransStatus> listTrangThaiTheVung;
	List<CardDetail> listChuaDuyetChuaKichHoat;
	List<CardDetail> listChuaDuyetDaKichHoat;
	List<CardDetail> listChuaNhapChuaKichHoat;
	List<CardDetail> listChuaNhapDaKichHoat;
	List<CardDetail> listDaDuyetChuaKichHoat;
	private String fileNameExport;
	private int rowNumExport = 0;
	private String fileNameOutput = null;
	private Path pathExport = null;
	
	@PostConstruct
	void init() {

		final SpringContextHelper helper = new SpringContextHelper(VaadinServlet.getCurrent().getServletContext());
		configurationHelper = (SpringConfigurationValueHelper) helper.getBean("springConfigurationValueHelper");
		
		dfFromdate.setResolution(Resolution.DAY);
		dfFromdate.setDateFormat("dd/MM/yyyy");
		dfFromdate.setImmediate(true);
		dfFromdate.setValidationVisible(false);
		dfFromdate.addValidator(new NullValidator("Không được để trống", false));

		dfTodate.setResolution(Resolution.DAY);
		dfTodate.setDateFormat("dd/MM/yyyy");
		dfTodate.setImmediate(true);
		dfTodate.setValidationVisible(false);
		dfTodate.addValidator(new NullValidator("Không được để trống", false));

		tfnumberofday.setValue("65");
		tfnumberofday.setImmediate(true);
		tfnumberofday.setValidationVisible(false);
		tfnumberofday.setMaxLength(2);
		tfnumberofday.addValidator(new RegexpValidator("[-]?[0-9]*\\.?,?[0-9]+", "Số ngày phải là số"));

		tfBrnCode.setMaxLength(3);
		tfBrnCode.addValidator(new RegexpValidator("[-]?[0-9]*\\.?,?[0-9]+", "Mã đơn vị phải là số"));

		setSizeFull();
		setSpacing(true);
		setMargin(true);

		final Button btRecMKButNoTransBranch = new Button(
				"1. Báo Cáo - Danh Sách Thẻ Đơn Vị Chưa Nhận Từ MK Theo Ngày");
		btRecMKButNoTransBranch.setStyleName(ValoTheme.BUTTON_LINK);
		btRecMKButNoTransBranch.setIcon(FontAwesome.ANGLE_DOUBLE_LEFT);
		btRecMKButNoTransBranch.addClickListener(evt -> {
			showForm(btRecMKButNoTransBranch.getCaption(), "DSTheDVChuaNhanTuMK.jasper");
		});

		final Button btTransBranch = new Button("2. Báo Cáo - Danh Sách Thẻ Đơn Vị Đã Nhận Từ MK Theo Ngày");
		btTransBranch.setStyleName(ValoTheme.BUTTON_LINK);
		btTransBranch.setIcon(FontAwesome.ANGLE_DOUBLE_LEFT);
		btTransBranch.addClickListener(evt -> {
			showForm(btTransBranch.getCaption(), "DSTheDVDaNhanTuMK.jasper");
		});

		final Button btTranEmpButNoTranCust = new Button(
				"3. Báo Cáo - Danh Sách Thẻ Đơn Vị Đã Nhận Nhưng Chưa Giao Cho Khách Hàng");
		btTranEmpButNoTranCust.setStyleName(ValoTheme.BUTTON_LINK);
		btTranEmpButNoTranCust.setIcon(FontAwesome.ANGLE_DOUBLE_LEFT);
		btTranEmpButNoTranCust.addClickListener(evt -> {
			showForm(btTranEmpButNoTranCust.getCaption(), "DSTheDaGiaoNVKDNhungChuaGiaoKH.jasper");
		});

		final Button btTranCust = new Button("4. Báo Cáo - Danh Sách Thẻ Đã Giao Cho KH");
		btTranCust.setStyleName(ValoTheme.BUTTON_LINK);
		btTranCust.setIcon(FontAwesome.ANGLE_DOUBLE_LEFT);
		btTranCust.addClickListener(evt -> {
			showForm(btTranCust.getCaption(), "DSTheDaGiaoKH.jasper");
		});

		final Button btNoTranCust = new Button("5.	Báo Cáo - Danh Sách Thẻ KH Từ Chối Nhận Thẻ");
		btNoTranCust.setStyleName(ValoTheme.BUTTON_LINK);
		btNoTranCust.setIcon(FontAwesome.ANGLE_DOUBLE_LEFT);
		btNoTranCust.addClickListener(evt -> {
			showForm(btNoTranCust.getCaption(), "DSKHKhongNhanThe.jasper");
		});
		
		final Button btPeriodic = new Button("6.	Báo Cáo - Tổng hợp định kỳ PPT");
		btPeriodic.setStyleName(ValoTheme.BUTTON_LINK);
		btPeriodic.setIcon(FontAwesome.ANGLE_DOUBLE_LEFT);
		btPeriodic.addClickListener(evt -> {
			showForm(btPeriodic.getCaption(), "BaoCaoDinhKyPPT");
		});

		String userLogin = SecurityUtils.getUserName();
		if (userLogin.equals("huynhnt")) {
			addComponent(btRecMKButNoTransBranch);
			addComponent(btTransBranch);
			addComponent(btTranEmpButNoTranCust);
			addComponent(btTranCust);
			addComponent(btNoTranCust);
		}
		
		if(SecurityUtils.hasRole("ROLE_SUPERADMIN") || SecurityUtils.hasRole("ROLE_HO") || SecurityUtils.hasRole("ROLE_HOCHECKER"))
			addComponent(btRecMKButNoTransBranch);
		
		addComponent(btTransBranch);
		addComponent(btTranEmpButNoTranCust);
		addComponent(btTranCust);
		addComponent(btNoTranCust);
		
		if(SecurityUtils.hasRole("ROLE_SUPERADMIN") || SecurityUtils.hasRole("ROLE_HO") || SecurityUtils.hasRole("ROLE_HOCHECKER") || SecurityUtils.hasRole("ROLE_DVKH"))
			addComponent(btPeriodic);
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

	private void showForm(String caption, String filename) {
		// Dong tat ca window dang mo
		getUI().getWindows().forEach(s -> {
			getUI().removeWindow(s);
		});

		final Window window = new Window();
		window.setModal(true);
		window.center();
		window.setWidth(450f, Unit.PIXELS);
		window.setResizable(false);

		final FormLayout formLayout = new FormLayout();
		formLayout.setSizeFull();
		formLayout.setSpacing(true);
		formLayout.setMargin(true);

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);

		SimpleFileDownloader downloader = new SimpleFileDownloader();
		addExtension(downloader);

		final Button btPDFDowload = new Button("PDF");
		btPDFDowload.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		btPDFDowload.setIcon(FontAwesome.DOWNLOAD);
		btPDFDowload.addClickListener(btpdfdowloadEvt -> {
			if (checkValidate(filename)) {
				downloader.setFileDownloadResource(createTransMKResourcePDF(filename));
				downloader.download();
			}
		});

		final Button btXLSXDowload = new Button("XLSX");
		btXLSXDowload.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		btXLSXDowload.setIcon(FontAwesome.DOWNLOAD);
		btXLSXDowload.addClickListener(btpdfdowloadEvt -> {
			
			if(filename.equals("BaoCaoDinhKyPPT")) {
				final SimpleDateFormat formatDate = new SimpleDateFormat("yyyyMMdd");
				int tungay = Integer.parseInt(formatDate.format(dfFromdate.getValue()));
				int denngay = Integer.parseInt(formatDate.format(dfTodate.getValue()));
				
				
				//EXPORT LIST TO EXCEL FILE
	            XSSFWorkbook workbookExport = new XSSFWorkbook();
	            XSSFSheet sheet0 = workbookExport.createSheet("TongHopTrangThaiTheVung");
	            XSSFSheet sheet1 = workbookExport.createSheet("1_ChuaDuyet_ChuaKichHoat");
	            XSSFSheet sheet2 = workbookExport.createSheet("2_ChuaDuyet_DaKichHoat");
	            XSSFSheet sheet3 = workbookExport.createSheet("3_ChuaNhap_ChuaKichHoat");
	            XSSFSheet sheet4 = workbookExport.createSheet("4_ChuaNhap_DaKichHoat");
	            XSSFSheet sheet5 = workbookExport.createSheet("5_DaDuyet_ChuaKichHoat");
//	            XSSFSheet sheetExportByBIN = workbookExport.createSheet("Upload summary by BIN");
		        
	            DataFormat format = workbookExport.createDataFormat();
	            CellStyle styleNumber;
	            styleNumber = workbookExport.createCellStyle();
	            styleNumber.setDataFormat(format.getFormat("0.0"));
	            

	            
		        LOGGER.info("Creating excel");

		        Object[] rowHeader = {"STT","CIF","LOC","HOTEN","SDT","SO_THE_CHE","LOAI_THE","NGAY_PHAT_HANH","DON_VI","TEN_DON_VI",
            			"TINH_TRANG_DUYET","NGAY_DV_NHANTHE_TU_MKS","NGAYNHAP_GIAOTHE_KH","TINH_TRANG_TREN_PPT"};
		        
		        
		        
		      //SHEET0-----------------------------------------------
		        listTrangThaiTheVung = crdDetailService.getTongHopTrangThaiTheVung(tungay, denngay);
		        rowNumExport = 0;
		        if(rowNumExport == 0) {
	            	int colNum = 0;	 
	            	XSSFRow row = sheet0.createRow(rowNumExport++);      
	            	Object[] rowHeader0 = {"STT","MA_VUNG","TEN_VUNG","BRCH_CDE","BRCH_NAME","TONGTHEPH","SL_CHUADUYETCHUAKICHHOAT",
			        		"SL_CHUADUYETDAKICHHOAT","SL_CHUANHAPCHUAKICHHOAT","SL_CHUANHAPDAKICHHOAT","SL_DADUYETCHUAKICHHOAT","FROMDATE","TODATE"};
	            	for (Object field : rowHeader0) {
	            		Cell cell = row.createCell(colNum++, CellType.STRING);
	            		cell.setCellValue((String)field);
	            	}      
	            	LOGGER.info("Created row " + rowNumExport + " for header sheet in excel.");
		        }
		        
		        for(CardTransStatus item : listTrangThaiTheVung) {
					XSSFRow row = sheet0.createRow(rowNumExport++);
					
					int stt = rowNumExport-1;
					row.createCell(0).setCellValue(stt);
					row.createCell(1).setCellValue(item.getMaVung());
					row.createCell(2).setCellValue(item.getTenVung());
					row.createCell(3).setCellValue(item.getBrchCode());
					row.createCell(4).setCellValue(item.getBrchName());
					row.createCell(5,CellType.NUMERIC).setCellValue(item.getTotalCard());
					row.createCell(6,CellType.NUMERIC).setCellValue(item.getSlChuaDuyetChuaKichHoat());
					row.createCell(7,CellType.NUMERIC).setCellValue(item.getSlChuaDuyetDaKichHoat());
					row.createCell(8,CellType.NUMERIC).setCellValue(item.getSlChuaNhapChuaKichHoat());
					row.createCell(9,CellType.NUMERIC).setCellValue(item.getSlChuaNhapDaKichHoat());
					row.createCell(10,CellType.NUMERIC).setCellValue(item.getSlDaDuyetChuaKichHoat());
					row.createCell(11,CellType.NUMERIC).setCellValue(item.getFromDate());
					row.createCell(12,CellType.NUMERIC).setCellValue(item.getToDate());
		        }
		        sheet0.createFreezePane(0, 1);
		        for (int i=0; i<15; i++)  
		        	sheet0.autoSizeColumn(i);
		        
		        //SHEET1-----------------------------------------------
		        listChuaDuyetChuaKichHoat = crdDetailService.getListChuaDuyetChuaKichHoat(tungay, denngay);
		        rowNumExport = 0;
		        if(rowNumExport == 0) {
	            	int colNum = 0;	 
	            	XSSFRow row = sheet1.createRow(rowNumExport++);         	
	            	for (Object field : rowHeader) {
	            		Cell cell = row.createCell(colNum++, CellType.STRING);
	            		cell.setCellValue((String)field);
	            	}      
	            	LOGGER.info("Created row " + rowNumExport + " for header sheet in excel.");
		        }
		        
		        for(CardDetail item : listChuaDuyetChuaKichHoat) {
					XSSFRow row = sheet1.createRow(rowNumExport++);
					
					int stt = rowNumExport-1;
					row.createCell(0).setCellValue(stt);
					row.createCell(1).setCellValue(item.getCif());
					row.createCell(2).setCellValue(item.getLoc());
					row.createCell(3).setCellValue(item.getCustName());
					row.createCell(4).setCellValue(item.getPhoneNo());
					row.createCell(5).setCellValue(item.getPanMask());
					row.createCell(6).setCellValue(item.getCardType());
					row.createCell(7).setCellValue(item.getIssueDate());
					row.createCell(8).setCellValue(item.getBrchCde());
					row.createCell(9).setCellValue(item.getBrchName());
					row.createCell(10).setCellValue(item.getStatusCheck());
					row.createCell(11).setCellValue(item.getTransMkDate());
					row.createCell(12).setCellValue(item.getTransCustDate());
					row.createCell(13).setCellValue(item.getTransCustStatus());
					
		        }
		        sheet1.createFreezePane(0, 1);
		        for (int i=0; i<15; i++)  
		        	sheet1.autoSizeColumn(i);
		        
		        //SHEET2---------------------------------------------------------
		        listChuaDuyetDaKichHoat = crdDetailService.getListChuaDuyetDaKichHoat(tungay, denngay);
		        rowNumExport = 0;
		        if(rowNumExport == 0) {
	            	int colNum = 0;	 
	            	XSSFRow row = sheet2.createRow(rowNumExport++);         	
	            	for (Object field : rowHeader) {
	            		Cell cell = row.createCell(colNum++, CellType.STRING);
	            		cell.setCellValue((String)field);
	            	}      
	            	LOGGER.info("Created row " + rowNumExport + " for header sheet in excel.");
		        }
		        
		        for(CardDetail item : listChuaDuyetDaKichHoat) {
					XSSFRow row = sheet2.createRow(rowNumExport++);
					int stt = rowNumExport-1;
					row.createCell(0).setCellValue(stt);
					row.createCell(1).setCellValue(item.getCif());
					row.createCell(2).setCellValue(item.getLoc());
					row.createCell(3).setCellValue(item.getCustName());
					row.createCell(4).setCellValue(item.getPhoneNo());
					row.createCell(5).setCellValue(item.getPanMask());
					row.createCell(6).setCellValue(item.getCardType());
					row.createCell(7).setCellValue(item.getIssueDate());
					row.createCell(8).setCellValue(item.getBrchCde());
					row.createCell(9).setCellValue(item.getBrchName());
					row.createCell(10).setCellValue(item.getStatusCheck());
					row.createCell(11).setCellValue(item.getTransMkDate());
					row.createCell(12).setCellValue(item.getTransCustDate());
					row.createCell(13).setCellValue(item.getTransCustStatus());
					
		        }
		        sheet2.createFreezePane(0, 1);
		        for (int i=0; i<15; i++)  
		        	sheet2.autoSizeColumn(i);
		        
		      //SHEET3---------------------------------------------------------
		        listChuaNhapChuaKichHoat = crdDetailService.getListChuaNhapChuaKichHoat(tungay, denngay);
		        rowNumExport = 0;
		        if(rowNumExport == 0) {
	            	int colNum = 0;	 
	            	XSSFRow row = sheet3.createRow(rowNumExport++);         	
	            	for (Object field : rowHeader) {
	            		Cell cell = row.createCell(colNum++, CellType.STRING);
	            		cell.setCellValue((String)field);
	            	}      
	            	LOGGER.info("Created row " + rowNumExport + " for header sheet in excel.");
		        }
		        
		        for(CardDetail item : listChuaNhapChuaKichHoat) {
					XSSFRow row = sheet3.createRow(rowNumExport++);
					int stt = rowNumExport-1;
					row.createCell(0).setCellValue(stt);
					row.createCell(1).setCellValue(item.getCif());
					row.createCell(2).setCellValue(item.getLoc());
					row.createCell(3).setCellValue(item.getCustName());
					row.createCell(4).setCellValue(item.getPhoneNo());
					row.createCell(5).setCellValue(item.getPanMask());
					row.createCell(6).setCellValue(item.getCardType());
					row.createCell(7).setCellValue(item.getIssueDate());
					row.createCell(8).setCellValue(item.getBrchCde());
					row.createCell(9).setCellValue(item.getBrchName());
					row.createCell(10).setCellValue(item.getStatusCheck());
					row.createCell(11).setCellValue(item.getTransMkDate());
					row.createCell(12).setCellValue(item.getTransCustDate());
					row.createCell(13).setCellValue(item.getTransCustStatus());
					
		        }
		        sheet3.createFreezePane(0, 1);
		        for (int i=0; i<15; i++)  
		        	sheet3.autoSizeColumn(i);
		        
		      //SHEET4---------------------------------------------------------
		        listChuaNhapDaKichHoat = crdDetailService.getListChuaNhapDaKichHoat(tungay, denngay);
		        rowNumExport = 0;
		        if(rowNumExport == 0) {
	            	int colNum = 0;	 
	            	XSSFRow row = sheet4.createRow(rowNumExport++);         	
	            	for (Object field : rowHeader) {
	            		Cell cell = row.createCell(colNum++, CellType.STRING);
	            		cell.setCellValue((String)field);
	            	}      
	            	LOGGER.info("Created row " + rowNumExport + " for header sheet in excel.");
		        }
		        
		        for(CardDetail item : listChuaNhapDaKichHoat) {
					XSSFRow row = sheet4.createRow(rowNumExport++);
					int stt = rowNumExport-1;
					row.createCell(0).setCellValue(stt);
					row.createCell(1).setCellValue(item.getCif());
					row.createCell(2).setCellValue(item.getLoc());
					row.createCell(3).setCellValue(item.getCustName());
					row.createCell(4).setCellValue(item.getPhoneNo());
					row.createCell(5).setCellValue(item.getPanMask());
					row.createCell(6).setCellValue(item.getCardType());
					row.createCell(7).setCellValue(item.getIssueDate());
					row.createCell(8).setCellValue(item.getBrchCde());
					row.createCell(9).setCellValue(item.getBrchName());
					row.createCell(10).setCellValue(item.getStatusCheck());
					row.createCell(11).setCellValue(item.getTransMkDate());
					row.createCell(12).setCellValue(item.getTransCustDate());
					row.createCell(13).setCellValue(item.getTransCustStatus());
					
		        }
		        sheet4.createFreezePane(0, 1);
		        for (int i=0; i<15; i++)  
		        	sheet4.autoSizeColumn(i);
		        
		      //SHEET5---------------------------------------------------------
		        listDaDuyetChuaKichHoat = crdDetailService.getListDaDuyetChuaKichHoat(tungay, denngay);
		        rowNumExport = 0;
		        if(rowNumExport == 0) {
	            	int colNum = 0;	 
	            	XSSFRow row = sheet5.createRow(rowNumExport++);         	
	            	for (Object field : rowHeader) {
	            		Cell cell = row.createCell(colNum++, CellType.STRING);
	            		cell.setCellValue((String)field);
	            	}      
	            	LOGGER.info("Created row " + rowNumExport + " for header sheet in excel.");
		        }
		        
		        for(CardDetail item : listDaDuyetChuaKichHoat) {
					XSSFRow row = sheet5.createRow(rowNumExport++);
					int stt = rowNumExport-1;
					row.createCell(0).setCellValue(stt);
					row.createCell(1).setCellValue(item.getCif());
					row.createCell(2).setCellValue(item.getLoc());
					row.createCell(3).setCellValue(item.getCustName());
					row.createCell(4).setCellValue(item.getPhoneNo());
					row.createCell(5).setCellValue(item.getPanMask());
					row.createCell(6).setCellValue(item.getCardType());
					row.createCell(7).setCellValue(item.getIssueDate());
					row.createCell(8).setCellValue(item.getBrchCde());
					row.createCell(9).setCellValue(item.getBrchName());
					row.createCell(10).setCellValue(item.getStatusCheck());
					row.createCell(11).setCellValue(item.getTransMkDate());
					row.createCell(12).setCellValue(item.getTransCustDate());
					row.createCell(13).setCellValue(item.getTransCustStatus());
					
		        }
		        
		        sheet5.createFreezePane(0, 1);
		        for (int i=0; i<15; i++)  
		        	sheet5.autoSizeColumn(i);
		        
		        try {
		        	
		        	fileNameOutput = "BaoCaoDinhKyPPT_" + tungay + "_" + denngay + ".xlsx";
		        	pathExport = Paths.get(configurationHelper.getPathFileRoot() + "\\Export");
		        	if(Files.notExists(pathExport)) {
		        		Files.createDirectories(pathExport);
		            }
		        	FileOutputStream outputStream = new FileOutputStream(pathExport + "\\" + fileNameOutput);
		            LOGGER.info("Created file excel output " + fileNameOutput);
		            workbookExport.write(outputStream);
		            LOGGER.info("Write data to " + fileNameOutput + " completed");
		            workbookExport.close();
		            outputStream.close();
		            LOGGER.info("Done");
			        LOGGER.info("Export excel file " + fileNameOutput);
			        messageExportXLSX("Info","Export compeleted.");
			        
		        } catch (FileNotFoundException e) {
		            LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		        } catch (IOException e) {
		            LOGGER.error(ExceptionUtils.getFullStackTrace(e));
		        }
			} else {
				if (checkValidate(filename)) {
					downloader.setFileDownloadResource(createTransMKResourceXLSX(filename));
					downloader.download();
				}
			}

		});

		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(1000000);

		final Button btViewReport = new Button("View Report");
		btViewReport.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		btViewReport.setIcon(FontAwesome.TWITTER);
		btViewReport.addClickListener(btviewEvt -> {
			try {
				dfFromdate.validate();
				tfnumberofday.validate();
				File dir = new File(sDirReport);
				File[] foundFiles = dir.listFiles(new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.endsWith(".html");
					}
				});

				for (File file : foundFiles) {
					if (file.exists()) {
						file.delete();
					}
				}

				viewReport(filename, randomInt);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
				Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
			}

		});
		
		if(!filename.equals("BaoCaoDinhKyPPT")) {
			buttonLayout.addComponent(btViewReport);
			buttonLayout.addComponent(btPDFDowload);
		}
		buttonLayout.addComponent(btXLSXDowload);

		if (SecurityUtils.hasRole("ROLE_DONVI") || SecurityUtils.hasRole("ROLE_DONVICHECKER")) {
			String userInfo = SecurityUtils.getUserName();
			String brn = sysUserService.getBrnOfUserDV(userInfo);
			tfBrnCode.setValue(brn);
			tfBrnCode.setReadOnly(true);
		}

		if (filename.equals("DSKHKhongNhanThe.jasper")) {
			formLayout.addComponent(dfFromdate);
			formLayout.addComponent(tfnumberofday);
			// formLayout.addComponent(tfBrnCode);
		} else {
			formLayout.addComponent(dfFromdate);
			formLayout.addComponent(dfTodate);
			if(!filename.equals("BaoCaoDinhKyPPT"))
				formLayout.addComponent(tfBrnCode);
		}

		formLayout.addComponent(buttonLayout);

		final Panel panel = new Panel();
		panel.setCaption(caption);

		panel.setContent(formLayout);
		window.setContent(panel);
		getUI().addWindow(window);
	}

	private boolean checkValidate(final String filename) {
		try {
			if (!filename.equals("DSKHKhongNhanThe.jasper")) {
				dfFromdate.validate();
				dfTodate.validate();
			} else {
				dfFromdate.validate();
				tfnumberofday.validate();
			}
			return true;
		} catch (InvalidValueException ex) {

			if (!filename.equals("DSKHKhongNhanThe.jasper")) {
				dfFromdate.setValidationVisible(true);
				dfTodate.setValidationVisible(true);
			} else {
				dfFromdate.setValidationVisible(true);
				tfnumberofday.setValidationVisible(true);
			}

			return false;
		}

	}

	@SuppressWarnings("serial")
	private StreamResource createTransMKResourcePDF(final String filename) {
		return new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {

				try {
					final ByteArrayOutputStream outpuf = makeFileForDownLoad(filename, "PDF");
					return new ByteArrayInputStream(outpuf.toByteArray());

				} catch (JRException e) {
					LOGGER.error(e.getMessage());
				} catch (SQLException e) {
					LOGGER.error(e.getMessage());
				}
				return null;

			}
		}, "qlppt_baocao.pdf");
	}

	@SuppressWarnings("serial")
	private StreamResource createTransMKResourceXLSX(final String filename) {
		return new StreamResource(new StreamSource() {
			@Override
			public InputStream getStream() {

				try {
					final ByteArrayOutputStream outpuf = makeFileForDownLoad(filename, "XLSX");
					return new ByteArrayInputStream(outpuf.toByteArray());

				} catch (JRException e) {
					LOGGER.error(e.getMessage());
				} catch (SQLException e) {
					LOGGER.error(e.getMessage());
				}
				return null;

			}
		}, "qlppt_baocao.xlsx");
	}

	private ByteArrayOutputStream makeFileForDownLoad(String filename, String extension)
			throws JRException, SQLException {

		final Connection con = localDataSource.getConnection();
		final ByteArrayOutputStream output = new ByteArrayOutputStream();

		// Tham so truyen vao cho bao cao
		final Map<String, Object> parameters = new HashMap<String, Object>();
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

		if (filename.equals("DSKHKhongNhanThe.jasper")) {
			parameters.put("date", Integer.parseInt(format.format(dfFromdate.getValue())));
			parameters.put("numberofday", Integer.parseInt(String.valueOf(tfnumberofday.getValue())));
		} else {
			parameters.put("fromdate", Integer.parseInt(format.format(dfFromdate.getValue())));
			parameters.put("todate", Integer.parseInt(format.format(dfTodate.getValue())));
			parameters.put("crdcde", String.valueOf(tfBrnCode.getValue()));
		}

		// Dung file da compile
		final JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(sDirReport + "/" + filename);
		// Load file XML va compile
		// final JasperReport jasperReport =
		// JasperCompileManager.compileReport(sDirReport + "/" + filename);
		final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, con);

		// Xuat file Excel
		if (extension.equals("XLSX")) {
			final JRXlsxExporter xlsx = new JRXlsxExporter();
			xlsx.setExporterInput(new SimpleExporterInput(jasperPrint));
			xlsx.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
			xlsx.exportReport();
		} else if (extension.equals("PDF")) { // File PDF
			JasperExportManager.exportReportToPdfStream(jasperPrint, output);
		}
		return output;

	}

	private void viewReport(String filename, int random) throws JRException, SQLException, MalformedURLException {

		final Connection con = localDataSource.getConnection();

		// Tham so truyen vao cho bao cao
		final Map<String, Object> parameters = new HashMap<String, Object>();
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

		if (filename.equals("DSKHKhongNhanThe.jasper")) {
			parameters.put("date", Integer.parseInt(format.format(dfFromdate.getValue())));
			parameters.put("numberofday", Integer.parseInt(String.valueOf(tfnumberofday.getValue())));
		} else {
			parameters.put("fromdate", Integer.parseInt(format.format(dfFromdate.getValue())));
			parameters.put("todate", Integer.parseInt(format.format(dfTodate.getValue())));
			parameters.put("crdcde", String.valueOf(tfBrnCode.getValue()));
		}

		final JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(sDirReport + "/" + filename);
		final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, con);
		JasperExportManager.exportReportToHtmlFile(jasperPrint, sDirReport + "/" + "reporttemp" + random + ".html");
		FileResource resource = new FileResource(new File(sDirReport + "/" + "reporttemp" + random + ".html"));
		BrowserFrame frame = new BrowserFrame("Report", resource);
		frame.setWidth("90%");
		frame.setHeight("90%");
		Window window = new Window();
		// window.setSizeFull();
		window.setWidth("95%");
		window.setHeight("95%");
		window.setContent(frame);
		UI.getCurrent().addWindow(window);

	}
	
	private void messageExportXLSX(String caption, String text) {
		Window confirmDialog = new Window();
		FormLayout content = new FormLayout();
        content.setMargin(true);
		Button bOK = new Button("OK");
		Label lbText = new Label(text);
		confirmDialog.setCaption(caption);
		confirmDialog.setWidth(300.0f, Unit.PIXELS);
		
		 bOK.addClickListener(event -> {
			SimpleFileDownloader downloader = new SimpleFileDownloader();
			addExtension(downloader);
			StreamResource resource = getStream(new File(pathExport + "\\" + fileNameOutput));
			downloader.setFileDownloadResource(resource);
			downloader.download();
         	confirmDialog.close();
         });
		
		VerticalLayout layoutBtn = new VerticalLayout();
		layoutBtn.addComponent(lbText);
        layoutBtn.addComponents(bOK);
        content.addComponent(layoutBtn);
        
        confirmDialog.setContent(content);

        getUI().addWindow(confirmDialog);
        // Center it in the browser window
        confirmDialog.center();
        confirmDialog.setResizable(false);
	}
	
	private StreamResource getStream(File inputfile) {
	    
	    StreamResource.StreamSource source = new StreamResource.StreamSource() {

	        public InputStream getStream() {
	           
	            InputStream input=null;
	            try
	            {
	                input = new  FileInputStream(inputfile);
	            } 
	            catch (FileNotFoundException e)
	            {
	                e.printStackTrace();
	            }
	              return input;

	        }
	    };
	    StreamResource resource = new StreamResource ( source, inputfile.getName());
	    return resource;
	}

}
