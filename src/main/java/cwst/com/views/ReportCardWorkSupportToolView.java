package cwst.com.views;

import com.ibm.icu.text.SimpleDateFormat;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;
import cwst.com.SecurityUtils;
import cwst.com.services.FullBranchService;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@SpringView(name = ReportCardWorkSupportToolView.VIEW_NAME)
public class ReportCardWorkSupportToolView extends VerticalLayout implements View {

	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "bao-cao-cardworks-support-tool";
	//private static final Logger LOGGER = LoggerFactory.getLogger(ReportCardWorkSupportToolView.class);
	private static final Logger LOGGER = LogManager.getLogger(ReportCardDistributionView.class);

	@Value("${dir.report}")
	private String sDirReport;
	@Autowired
	protected DataSource localDataSource;
	@Autowired
	private FullBranchService fullBranch;
	private final transient DateField dfFromdate = new DateField("Từ Ngày");
	private final transient DateField dfTodate = new DateField("Đến Ngày");
	private final transient ComboBox cbBranch = new ComboBox();
	private transient String sSelectedCaption = "";

	@PostConstruct
	void init() {
		dfFromdate.setResolution(Resolution.DAY);
		dfFromdate.setDateFormat("dd/MM/yyyy");
		dfFromdate.setImmediate(true);

		dfTodate.setResolution(Resolution.DAY);
		dfTodate.setDateFormat("dd/MM/yyyy");
		dfTodate.setImmediate(true);

		setSizeFull();
		setSpacing(true);
		setMargin(true);

		final Button btTranMK = new Button("Báo Cáo - Phê Duyệt Hồ Sơ Thẻ Tín Dụng Quốc Tế Trên Cardworks");
		btTranMK.setStyleName(ValoTheme.BUTTON_LINK);
		btTranMK.setIcon(FontAwesome.ANGLE_DOUBLE_LEFT);
		btTranMK.addClickListener(evt -> {
			showForm(btTranMK.getCaption(), "CWST_PheDuyetTheTDQuocTe.jasper");
		});
		addComponent(btTranMK);
	}

	private void showForm(String type, String filename) {
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

		// Tao drop list danh sach don vi
		cbBranch.addItems("all");
		cbBranch.setItemCaption("all", "Tất cả");
		cbBranch.setNullSelectionItemId("all");
		cbBranch.setWidth(80f, Unit.PERCENTAGE);
		cbBranch.setImmediate(true);
		cbBranch.addValueChangeListener(evt -> {
			sSelectedCaption = cbBranch.getItemCaption(evt.getProperty().getValue());
		});
		fullBranch.findAll().forEach(s -> {
			cbBranch.addItems(s.getBranchCode().trim());
			cbBranch.setItemCaption(s.getBranchCode().trim(), s.getBranchName().trim() + " - " + s.getBranchCode().trim());
		});

		final Button btPDFDowload = new Button("PDF");
		btPDFDowload.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		btPDFDowload.setIcon(FontAwesome.DOWNLOAD);

		final Button btXLSXDowload = new Button("XLSX");
		btXLSXDowload.setStyleName(ValoTheme.BUTTON_BORDERLESS);
		btXLSXDowload.setIcon(FontAwesome.DOWNLOAD);

		// Dowload file pdf
		final StreamResource myResourcePDF = createTransMKResourcePDF(filename);
		final FileDownloader fileDownloaderPDF = new FileDownloader(myResourcePDF);
		fileDownloaderPDF.extend(btPDFDowload);

		// Dowload file xls
		final StreamResource myResourceXLSX = createTransMKResourceXLS(filename);
		final FileDownloader fileDownloaderXLSX = new FileDownloader(myResourceXLSX);
		fileDownloaderXLSX.extend(btXLSXDowload);

		buttonLayout.addComponent(btPDFDowload);
		buttonLayout.addComponent(btXLSXDowload);

		formLayout.addComponent(dfFromdate);
		formLayout.addComponent(dfTodate);
		formLayout.addComponent(cbBranch);
		formLayout.addComponent(buttonLayout);

		final Panel panel = new Panel();
		panel.setCaption(type);

		panel.setContent(formLayout);
		window.setContent(panel);
		getUI().addWindow(window);
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
		}, "cwst_baocao.pdf");
	}

	@SuppressWarnings("serial")
	private StreamResource createTransMKResourceXLS(final String filename) {
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
		}, "cwst_baocao.xlsx");
	}

	private ByteArrayOutputStream makeFileForDownLoad(String filename, String extension) throws JRException, SQLException {

		final Connection con = localDataSource.getConnection();
		final ByteArrayOutputStream output = new ByteArrayOutputStream();

		// Tham so truyen vao bao cao
		final String brchCde = String.valueOf(cbBranch.getValue());

		final Map<String, Object> parameters = new HashMap<String, Object>();
		final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

		parameters.put("fromdate", format.format(dfFromdate.getValue()));
		parameters.put("todate", format.format(dfTodate.getValue()));
		parameters.put("creusr", SecurityUtils.getUserName());
		if (!brchCde.equals("null") && !brchCde.equals("all")) {
			parameters.put("brchcde", brchCde);
			parameters.put("brchname", sSelectedCaption);
		}

		// final JasperReport jasperReport = JasperCompileManager.compileReport(sDirReport + "/" + filename);
		final JasperReport jasperReport = (JasperReport) JRLoader.loadObjectFromFile(sDirReport + "/" + filename);
		final JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, con);

		// Xuat file Excel
		if (extension.equals("XLSX")) {
			final JRXlsxExporter xls = new JRXlsxExporter();
			xls.setExporterInput(new SimpleExporterInput(jasperPrint));
			xls.setExporterOutput(new SimpleOutputStreamExporterOutput(output));
			xls.exportReport();
		} else if (extension.equals("PDF")) { // File PDF
			JasperExportManager.exportReportToPdfStream(jasperPrint, output);
		}
		return output;
	}

	@Override
	public void enter(ViewChangeEvent event) {

	}

}
