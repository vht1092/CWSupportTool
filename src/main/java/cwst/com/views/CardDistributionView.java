package cwst.com.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.view.RedirectView;
import org.vaadin.haijian.ExcelExporter;

import com.vaadin.addon.contextmenu.ContextMenu;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ReadOnlyException;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.data.util.converter.Converter.ConversionException;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileResource;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.ResourceReference;
import com.vaadin.shared.Position;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.CellReference;
import com.vaadin.ui.Grid.CellStyleGenerator;
import com.vaadin.ui.Grid.HeaderCell;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.MultiSelectionModel;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Upload;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cwst.com.CwSupportToolUI;
import cwst.com.SecurityUtils;
import cwst.com.StringToDateFormat;
import cwst.com.TimeConverter;
import cwst.com.components.FileUploader;
import cwst.com.entities.CwstCrdDetail;
import cwst.com.entities.CwstSysUser;
import cwst.com.entities.FullBranch;
import cwst.com.services.CrdDetailService;
import cwst.com.services.FullBranchService;
import cwst.com.services.MessageByLocalService;
import cwst.com.services.SysUserService;
import de.datenhahn.vaadin.componentrenderer.ComponentRenderer;
import scala.annotation.meta.setter;

@SpringView(name = CardDistributionView.VIEW_NAME, ui = CwSupportToolUI.class)
public class CardDistributionView extends VerticalLayout implements View {

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(CardWorkSupportToolView.class);
	private static final Logger LOGGER = LogManager.getLogger(CardDistributionView.class);
	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "quan-ly-phan-phoi-the";

	@Value("${dir.attach.file}")
	private String dirFile;
	@Autowired
	private CrdDetailService crdDetService;
	@Autowired
	private FullBranchService fullBrchService;
	@Autowired
	private SysUserService sysUserService;

	private static final String TRANSFERED = "Đã chuyển", RECEIVED = "Đã nhận";
	private IndexedContainer idxContainer = new IndexedContainer();
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
	private final TimeConverter timeConverter = new TimeConverter();
	private DateField dfRecMkDate, dtTransMkDate, dfTransBranchDate, dfTransBranchRecDate, dfTransEmpDate,
			dfTransCustDate, dfFromDate, dfToDate;
	private CheckBox chbTransMk, chbRecMk, chbTransBranch, chbTransBranchRec, chbTransEmp, chbTransCust;
	private ComboBox cbxUnit, cbxBranch, cbxCrdType, cbxCustInfo, cbxStatus, cbxTransCustOption;
	private TextField txfTransBranchEmpCode, txfTransBranchEmpName, txfTransBranchFwBrch, txfTransEmpName, txfCif,
			txfTranEmpNote, txFilePin, txtNoteTranCust;

	private Label lblSumResults;
	final Window windowConfirm = new Window("Xác nhận thực hiện thao tác?");

	private static final String LOCKED = "Đã khóa", CHECKED = "Đã duyệt";
	private static final String UNLOCKED = "Chưa khóa", UNCHECKED = "Chưa duyệt";

	private TextArea txfTransBranchNote;
	private Grid grid;
	private BeanFieldGroup<CwstCrdDetail> fieldGroup;
	@Autowired
	private MessageByLocalService messageResource;

	@Autowired
	private SysUserService userService;

	private CwstSysUser user;

	private String userLogin;
	private String brnUserLogin;

	@PostConstruct
	void init() {
		if (SecurityUtils.isLoggedIn()) {
			userLogin = SecurityUtils.getUserName();
			brnUserLogin = sysUserService.getBrnOfUserDV(userLogin);
			initUI();
		}
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}

	private void initUI() {
		setSizeFull();
		setSpacing(true);

		initGrid();
		createContextMenu(grid);

		lblSumResults = new Label("Tổng số dòng:  0");
		lblSumResults.setStyleName("sumRecord");
		lblSumResults.setId("lblSumResults");

		// Button KHOA
		final Button btLock = new Button(messageResource.getMessage("button.lock"));
		btLock.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btLock.setIcon(FontAwesome.LOCK);
		btLock.addClickListener(evt -> {
			confirmDialog_ButtonLock();
			// cmdLockData_Button();
		});

		// Button DUYET
		final Button btCheck = new Button(messageResource.getMessage("button.check"));
		btCheck.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btCheck.setIcon(FontAwesome.CHECK);
		btCheck.addClickListener(evt -> {
			confirmDialog_ButtonCheck();
			// cmdCheckData_Button();
		});

		// Button DIEU CHINH
		final Button btReject = new Button(messageResource.getMessage("button.reject"));
		btReject.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btReject.setIcon(FontAwesome.UNLOCK);
		btReject.addClickListener(evt -> {
			confirmDialog_ButtonReject();
			// cmdUnLockData_Button();
		});

		System.out.println("Role: " + SecurityUtils.hasRole("ROLE_DONVICHECKER"));
		if (SecurityUtils.hasRole("ROLE_HO") || SecurityUtils.hasRole("ROLE_DONVI")
				|| SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			btLock.setVisible(true);
		} else {
			btLock.setVisible(false);
		}

		if (SecurityUtils.hasRole("ROLE_HOCHECKER") || SecurityUtils.hasRole("ROLE_DONVICHECKER")
				|| SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			btCheck.setVisible(true);
			btReject.setVisible(true);
		} else {
			btCheck.setVisible(false);
			btReject.setVisible(false);
		}

		final ExcelExporter excelExporter = new ExcelExporter();
		excelExporter.setCaption("XLS");
		excelExporter.setIcon(FontAwesome.DOWNLOAD);
		excelExporter.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		excelExporter.addClickListener(evt -> {
			excelExporter.setTableToBeExported(initDataExport());
		});

		final HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);
		buttonLayout.addComponent(btLock);
		buttonLayout.addComponent(btCheck);
		buttonLayout.addComponent(btReject);
		buttonLayout.addComponent(excelExporter);
		buttonLayout.setMargin(true);

		// Tao cac truong tim kiem
		VerticalLayout hLayout = initSearchField();

		final VerticalLayout layoutGrid = new VerticalLayout();
		layoutGrid.setSizeFull();
		layoutGrid.addComponents(lblSumResults, grid);
		layoutGrid.setComponentAlignment(grid, Alignment.TOP_CENTER);
		setSpacing(true);

		addComponent(hLayout);
		addComponents(layoutGrid);
		addComponent(buttonLayout);
		setComponentAlignment(hLayout, Alignment.TOP_CENTER);
		setExpandRatio(hLayout, 0);
		setExpandRatio(layoutGrid, 1);
		setExpandRatio(buttonLayout, 3);

		setStyleName("backColor");
	}

	private void initGrid() {
		// Thong tin chinh
		idxContainer = new IndexedContainer();
		// idxContainer.addContainerProperty("stt", String.class, "");
		idxContainer.addContainerProperty("id", String.class, "");
		idxContainer.addContainerProperty("cif", String.class, "");
		idxContainer.addContainerProperty("crd_brd", String.class, "");
		idxContainer.addContainerProperty("cust_name", String.class, "");
		idxContainer.addContainerProperty("main_sub", String.class, "");
		idxContainer.addContainerProperty("pan_mask", String.class, "");
		idxContainer.addContainerProperty("issue_type", String.class, "");
		idxContainer.addContainerProperty("issue_date", String.class, "");
		idxContainer.addContainerProperty("brch_code", String.class, "");
		idxContainer.addContainerProperty("gttn", String.class, "");
		idxContainer.addContainerProperty("saleofficer_code", String.class, "");
		// Chuyen file den MKS
		idxContainer.addContainerProperty("trans_mk", Boolean.class, false);
		idxContainer.addContainerProperty("trans_mk_date", String.class, "");
		idxContainer.addContainerProperty("trans_branch_fw", String.class, "");
		idxContainer.addContainerProperty("trans_branch_note", String.class, "");
		idxContainer.addContainerProperty("trans_mk_lock", Boolean.class, false);
		idxContainer.addContainerProperty("trans_mk_check", Boolean.class, false);
		// Don vi da nhan the
		idxContainer.addContainerProperty("trans_branch_rec", Boolean.class, false);
		idxContainer.addContainerProperty("trans_branch_rec_date", String.class, "");
		idxContainer.addContainerProperty("trans_branch_rec_lock", Boolean.class, false);
		idxContainer.addContainerProperty("trans_branch_rec_check", Boolean.class, false);
		// Giao the cho KH
		idxContainer.addContainerProperty("trans_cust", Boolean.class, false);
		idxContainer.addContainerProperty("trans_cust_date", String.class, "");
		// Tai file PIN
		// idxContainer.addContainerProperty("filePin", String.class, false);
		// idxContainer.addContainerProperty("buttonDowload", Component.class,
		// false);

		idxContainer.addContainerProperty("trans_cust_lock", Boolean.class, false);
		idxContainer.addContainerProperty("trans_cust_status", String.class, "");
		idxContainer.addContainerProperty("trans_cust_note", String.class, "");
		idxContainer.addContainerProperty("trans_cust_check", Boolean.class, false);
		idxContainer.addContainerProperty("xacnhan_gttn", Boolean.class, false);

		idxContainer.addContainerProperty("pan", String.class, "");

		// Tao button dowload tren grid
		GeneratedPropertyContainer gpc = new GeneratedPropertyContainer(idxContainer);

		gpc.addGeneratedProperty("buttonDowload", new PropertyValueGenerator<Component>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getValue(Item item, Object itemId, Object propertyId) {

				final HorizontalLayout hr = new HorizontalLayout();

				final Button btUpload = new Button("");
				final String sFileName = String
						.valueOf(idxContainer.getItem(itemId).getItemProperty("filePin").getValue());
				final String sId = String.valueOf(idxContainer.getItem(itemId).getItemProperty("id").getValue());
				final boolean sTranCust = (Boolean) idxContainer.getItem(itemId).getItemProperty("trans_cust")
						.getValue();
				final boolean sTranCustLock = (Boolean) idxContainer.getItem(itemId).getItemProperty("trans_cust_lock")
						.getValue();
				final boolean sTranCustCheck = (Boolean) idxContainer.getItem(itemId)
						.getItemProperty("trans_cust_check").getValue();
				btUpload.setIcon(FontAwesome.UPLOAD);
				btUpload.setStyleName(ValoTheme.BUTTON_LINK);
				btUpload.setDescription("Đính kèm file");
				btUpload.addClickListener(evt -> {
					showUploadFile(sId);
				});
				final Button btDowload = new Button("");
				btDowload.setIcon(FontAwesome.DOWNLOAD);
				btDowload.setStyleName(ValoTheme.BUTTON_LINK);
				btDowload.setDescription("Tải file");
				btDowload.addClickListener(downloadEvent -> {
					try {
						downloadFile(sFileName);
					} catch (Exception e) {
						Notification.show("Không tìm thấy file cần tải", Type.ERROR_MESSAGE);
						LOGGER.error("Khong the tai file: " + sFileName + " - Message: " + e.getMessage());
					}
				});

				// Neu thuc hien giao the cho KH va chua khoa du lieu moi hien
				// thi cho upload file

				if ((sTranCust && !sTranCustLock)
						|| (SecurityUtils.hasRole("ROLE_DONVICHECKER") && sTranCust && !sTranCustCheck)) {
					hr.addComponent(btUpload);
				}
				if (idxContainer.getItem(itemId).getItemProperty("filePin").getValue() != "") {
					hr.addComponent(btDowload);
				}

				return hr;
			}

			@Override
			public Class<Component> getType() {
				return Component.class;
			}
		});

		grid = new Grid();
		grid.setSizeFull();
		grid.setHeight("470");
		grid.setStyleName("border-grid");
		grid.setContainerDataSource(gpc);

		MultiSelectionModel selectionModel = new MultiSelectionModel();
		selectionModel.setSelectionLimit(5000);
		grid.setSelectionModel(selectionModel);

		// grid.getColumn("check").setWidth(60f);
		// grid.getColumn("stt").setWidth(60f);

		grid.getColumn("cif").setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.cifno"));
		grid.getColumn("cif").setWidth(80f);

		grid.getColumn("crd_brd")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.cardbranch"));
		grid.getColumn("crd_brd").setWidth(100f);
		grid.getColumn("cust_name")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.custname"));
		grid.getColumn("cust_name").setWidth(216f);
		grid.getColumn("main_sub").setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.submain"));
		grid.getColumn("pan_mask").setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.panmask"));
		grid.getColumn("pan_mask").setWidth(155f);
		grid.getColumn("issue_type")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.issuetype"));
		grid.getColumn("issue_date")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.issuedate"));
		grid.getColumn("brch_code")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.branch.code"));
		grid.getColumn("issue_date").setConverter(new StringToDateFormat());

		grid.getColumn("gttn")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.giaothetannoi"));
		grid.getColumn("gttn");

		grid.getColumn("saleofficer_code")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.saleofficercode"));
		grid.getColumn("saleofficer_code").setWidth(150f);
		// Chuyen file den CTY MK
		grid.getColumn("trans_mk")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.transfered"))
				.setRenderer(new HtmlRenderer(), new StringToBooleanConverter(FontAwesome.CHECK.getHtml(), ""));
		grid.getColumn("trans_mk_date")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.complete.date"));
		grid.getColumn("trans_branch_fw")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.fw.branch"));
		grid.getColumn("trans_branch_note")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.note"));
		grid.getColumn("trans_mk_lock")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.lock"))
				.setRenderer(new HtmlRenderer(), new StringToBooleanConverter(FontAwesome.LOCK.getHtml(), ""));
		grid.getColumn("trans_mk_check")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.check"))
				.setRenderer(new HtmlRenderer(), new StringToBooleanConverter(FontAwesome.CHECK_SQUARE.getHtml(), ""));
		// Don vi da nhan the
		grid.getColumn("trans_branch_rec")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.received"))
				.setRenderer(new HtmlRenderer(), new StringToBooleanConverter(FontAwesome.CHECK.getHtml(), ""));
		grid.getColumn("trans_branch_rec_date")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.complete.date"));
		grid.getColumn("trans_branch_rec_lock")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.lock"))
				.setRenderer(new HtmlRenderer(), new StringToBooleanConverter(FontAwesome.LOCK.getHtml(), ""));
		grid.getColumn("trans_branch_rec_check")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.check"))
				.setRenderer(new HtmlRenderer(), new StringToBooleanConverter(FontAwesome.CHECK_SQUARE.getHtml(), ""));
		// Giao the cho KH
		grid.getColumn("trans_cust")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.delivered"))
				.setRenderer(new HtmlRenderer(), new StringToBooleanConverter(FontAwesome.CHECK.getHtml(), ""));

		grid.getColumn("trans_cust_date")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.complete.date"));
		grid.getColumn("trans_cust_lock")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.lock"))
				.setRenderer(new HtmlRenderer(), new StringToBooleanConverter(FontAwesome.LOCK.getHtml(), ""));
		grid.getColumn("trans_cust_check")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.check"))
				.setRenderer(new HtmlRenderer(), new StringToBooleanConverter(FontAwesome.CHECK_SQUARE.getHtml(), ""));

		grid.getColumn("trans_cust_status").setHeaderCaption("Trạng thái");
		grid.getColumn("trans_cust_note").setHeaderCaption("Ghi chú");
		grid.getColumn("buttonDowload")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.attach.file"))
				.setRenderer(new ComponentRenderer());
		grid.getColumn("buttonDowload").setHidden(true);
		// grid.getColumn("filePin").setHidden(true);
		grid.getColumn("pan").setHidden(true);
		grid.getColumn("id").setHidden(true);
		grid.getColumn("trans_mk").setHidden(true);
		grid.getColumn("trans_branch_rec").setHidden(true);
		grid.getColumn("trans_cust").setHidden(true);
		grid.getColumn("xacnhan_gttn")
				.setHeaderCaption(messageResource.getMessage("carddistribution.grid.header.xacnhangiaothetannoi"))
				.setRenderer(new HtmlRenderer(), new StringToBooleanConverter(FontAwesome.CHECK_SQUARE.getHtml(), ""));

		if (SecurityUtils.hasRole("ROLE_DONVI") || SecurityUtils.hasRole("ROLE_DONVICHECKER")
				|| SecurityUtils.hasRole("ROLE_HOCHECKER") || SecurityUtils.hasRole("ROLE_HO")
				|| SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			grid.addItemClickListener(evt -> {
				cmdShowEditForm_GridClickListener(evt);
			});
		}

		if (SecurityUtils.getUserName() != null) {

			user = userService.findUserByUsername(SecurityUtils.getUserName());
			String temp = user.getUsrType();
			String ho = "HO";
			String donvi = "DONVI";
			String hoChecker = "HOCHECKER";
			String donviChecker = "DONVICHECKER";

			if (temp.equals(donvi) || temp.equals(donviChecker)) {
				grid.getColumn("trans_mk_date").setHidden(false);
				grid.getColumn("trans_mk_lock").setHidden(false);
				grid.getColumn("trans_mk_check").setHidden(false);
			}
		}

		grid.setCellStyleGenerator(new CellStyleGenerator() {
			private static final long serialVersionUID = 1L;

			@Override
			public String getStyle(CellReference cell) {
				// @formatter:off
				if (cell.getPropertyId().equals("trans_mk_check")
						|| cell.getPropertyId().equals("trans_branch_rec_check")
						|| cell.getPropertyId().equals("trans_cust_check") || cell.getPropertyId().equals("trans_mk")
						|| cell.getPropertyId().equals("trans_branch_rec") || cell.getPropertyId().equals("trans_cust")
				// @formatter:on
				) {
					if ((boolean) cell.getValue() == true) {
						return "icon-format-check";
					}
				}
				if (cell.getPropertyId().equals("trans_mk_lock") || cell.getPropertyId().equals("trans_branch_rec_lock")
						|| cell.getPropertyId().equals("trans_cust_lock")) {
					if ((boolean) cell.getValue() == true) {
						return "icon-format-lock";
					}
				}

				return "";
			}
		});
		HeaderRow extraHeader = grid.prependHeaderRow();
		/*
		 * --<< TAO GROUP COLUMN TREN GRID
		 * --->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->
		 * >--->>--->>--->>--->>--->>--->>--->>--->>
		 */
		/* HO */
		final Label lblOther = new Label(
				"<center style=\"color: darkblue; font-size:12px; text-shadow: 0px 0px 0px black, 0 0 0px blue, 0 0 0px darkblue;\">THÔNG TIN THẺ</center>");
		lblOther.setContentMode(ContentMode.HTML);
		extraHeader.join("id", "cif", "crd_brd", "cust_name", "main_sub", "pan_mask", "issue_type", "issue_date",
				"brch_code", "gttn", "saleofficer_code").setComponent(lblOther);

		/* Chuyen file MK */
		final Label lblOther2 = new Label(
				"<center style=\"color: darkblue; font-size:12px; text-shadow: 0px 0px 0px black, 0 0 0px blue, 0 0 0px darkblue;\">CHUYỂN FILE CTY MKS</center>");
		lblOther2.setContentMode(ContentMode.HTML);

		extraHeader.join("trans_mk", "trans_mk_date", "trans_branch_fw", "trans_branch_note", "trans_mk_lock",
				"trans_mk_check").setComponent(lblOther2);

		/* Don vi da nhan the */
		final Label lblOther3 = new Label(
				"<center style=\"color: darkblue; font-size:12px; text-shadow: 0px 0px 0px black, 0 0 0px blue, 0 0 0px darkblue;\">ĐƠN VỊ NHẬN THẺ TỪ CTY MKS</center>");
		lblOther3.setContentMode(ContentMode.HTML);
		extraHeader.join("trans_branch_rec", "trans_branch_rec_date", "trans_branch_rec_lock", "trans_branch_rec_check")
				.setComponent(lblOther3);

		/* Giao the cho khach hang */
		final Label lblOther4 = new Label(
				"<center style=\"color: darkblue; font-size:12px; text-shadow: 0px 0px 0px black, 0 0 0px blue, 0 0 0px darkblue;\">GIAO THẺ KHÁCH HÀNG</center>");
		lblOther4.setContentMode(ContentMode.HTML);
		extraHeader.join("trans_cust", "trans_cust_date", "trans_cust_lock", "trans_cust_check", "trans_cust_status",
				"trans_cust_note", "xacnhan_gttn").setComponent(lblOther4);
		/*
		 * --->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->
		 * >--->>--->>--->>--->>--->>--->>--->>--->>
		 */

	}

	/**
	 * Khoi tao searching form
	 */
	@SuppressWarnings("unchecked")
	private VerticalLayout initSearchField() {
		final VerticalLayout vMainLayout = new VerticalLayout();

		final HorizontalLayout hLayout = new HorizontalLayout();
		// hLayout.setSpacing(true);
		final FormLayout leftLayout = new FormLayout();
		leftLayout.setSpacing(true);

		final FormLayout centerLayout1 = new FormLayout();
		centerLayout1.setSpacing(true);

		final FormLayout centerLayout2 = new FormLayout();
		centerLayout2.setSpacing(true);

		final FormLayout rightLayout = new FormLayout();
		rightLayout.setSpacing(true);

		txfCif = new TextField("");
		txfCif.setWidth("200");
		// txfCif.setMaxLength(7);
		txfCif.setInputPrompt("Thông tin khách hàng...");

		cbxUnit = new ComboBox("Đơn Vị");
		cbxUnit.setWidth("200");
		cbxUnit.addContainerProperty("description", String.class, "");
		cbxUnit.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxUnit.setItemCaptionPropertyId("description");
		cbxUnit.setNullSelectionAllowed(true);
		cbxUnit.setInputPrompt(null);

		dfFromDate = new DateField("Từ ngày");
		dfFromDate.setWidth("200");
		dfToDate = new DateField("Đến ngày");
		dfToDate.setWidth("200");

		cbxBranch = new ComboBox("Chi Nhánh");
		cbxBranch.setWidth("200");

		cbxBranch.addContainerProperty("description", String.class, "");
		cbxBranch.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxBranch.setItemCaptionPropertyId("description");
		cbxBranch.setInputPrompt(null);
		cbxBranch.setNullSelectionAllowed(true);
		List<FullBranch> listBranch = fullBrchService.findByUserName(SecurityUtils.getUserName());

		if (!listBranch.isEmpty()) {
			listBranch.forEach(s -> {
				final Item item = cbxBranch.addItem(s.getBranchCode());
				item.getItemProperty("description")
						.setValue(s.getBranchCode() + " - " + s.getBranchName().toUpperCase());
			});
		}

		cbxBranch.addValueChangeListener(evt -> {

			if (cbxBranch.isEmpty()) {
				System.out.print("Empty Branch");
			} else {
				cbxUnit.removeAllItems();
				List<FullBranch> listUnit = fullBrchService
						.findByBranchCode(cbxBranch.getValue().toString().replaceAll("\\s", "")
								.substring(cbxBranch.getValue().toString().replaceAll("\\s", "").length() - 3, 3));
				if (!listUnit.isEmpty()) {
					listUnit.forEach(s -> {
						final Item item = cbxUnit.addItem(s.getBranchCode());
						item.getItemProperty("description")
								.setValue(s.getBranchCode() + " - " + s.getBranchName().toUpperCase());
					});
				}
			}

		});

		// final ComboBox cbUnit = new ComboBox("Đơn vị");
		cbxCrdType = new ComboBox("Loại thẻ");
		cbxCrdType.setWidth("200");
		cbxCrdType.addItems("ALL", "MC%", "MDC%", "MDS%", "MDT%", "VS%");
		cbxCrdType.setItemCaption("ALL", "Tất cả");
		cbxCrdType.setItemCaption("MC%", "MC Credit Card");
		cbxCrdType.setItemCaption("MDT%", "MC Debit T Card");
		cbxCrdType.setItemCaption("MDS%", "MC Debit S Card");
		cbxCrdType.setItemCaption("MDC%", "MC Debit C Card");
		cbxCrdType.setItemCaption("VS%", "Visa Credit Card");
		cbxCrdType.setValue(cbxCrdType.getItemIds().iterator().next());
		cbxCrdType.setNullSelectionAllowed(false);

		cbxCustInfo = new ComboBox("Thông tin KH");
		cbxCustInfo.setWidth("200");
		cbxCustInfo.addItems("", "0", "1", "2");
		cbxCustInfo.setItemCaption("", "");
		cbxCustInfo.setItemCaption("0", "HỌ & TÊN");
		cbxCustInfo.setItemCaption("1", "CIF");
		cbxCustInfo.setItemCaption("2", "LOC");
		cbxCustInfo.setValue(cbxCustInfo.getItemIds().iterator().next());
		cbxCustInfo.setNullSelectionAllowed(false);

		cbxStatus = new ComboBox("Trạng thái");
		cbxStatus.setWidth("200");
		cbxStatus.addItems("", "11", "12", "21", "22", "31", "32", "33", "34");
		cbxStatus.setItemCaption("", "");
		cbxStatus.setItemCaption("11", "Chưa duyệt - HO");
		cbxStatus.setItemCaption("12", "Đã duyệt - HO");
		cbxStatus.setItemCaption("21", "Chưa duyệt - ĐV nhận thẻ từ CTY MKS");
		cbxStatus.setItemCaption("22", "Đã duyệt - ĐV nhận thẻ từ CTY MKS");
		cbxStatus.setItemCaption("31", "Chưa duyệt - Giao thẻ KH");
		cbxStatus.setItemCaption("32", "Đã duyệt - Giao thẻ KH");
		cbxStatus.setItemCaption("34", "Thẻ giao tận nơi");
		cbxStatus.setItemCaption("33", "Thẻ chưa xác nhận GTTN thành công");
		// cbxStatus.setItemCaption("34", "Thẻ GTTN thành công");
		cbxStatus.setValue(cbxStatus.getItemIds().iterator().next());
		cbxStatus.setNullSelectionAllowed(false);

		final Button btSearch = new Button("TÌM KIẾM");
		btSearch.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btSearch.setIcon(FontAwesome.SEARCH);
		btSearch.setId("btnSearch");
		btSearch.addClickListener(evt -> {
			cmdSearch_buttonClick();
			setLblSumResults("Tổng số dòng:  " + idxContainer.size());
		});

		dfToDate.setDateFormat("dd/MM/yyyy");
		dfFromDate.setDateFormat("dd/MM/yyyy");

		leftLayout.addComponent(dfFromDate);
		centerLayout1.addComponent(cbxBranch);
		centerLayout2.addComponent(cbxCrdType);
		rightLayout.addComponent(cbxCustInfo);

		leftLayout.addComponent(dfToDate);
		centerLayout1.addComponent(cbxUnit);
		centerLayout2.addComponent(cbxStatus);
		rightLayout.addComponent(txfCif);

		centerLayout2.addComponent(btSearch);

		leftLayout.setStyleName("margin-label-search");
		centerLayout1.setStyleName("margin-label-search");
		centerLayout2.setStyleName("margin-label-search");
		rightLayout.setStyleName("margin-label-search");

		hLayout.addComponent(leftLayout);
		hLayout.addComponent(centerLayout1);
		hLayout.addComponent(centerLayout2);
		hLayout.addComponent(rightLayout);

		vMainLayout.addComponent(hLayout);
		vMainLayout.setStyleName("vMainLayout");
		vMainLayout.setComponentAlignment(hLayout, Alignment.TOP_CENTER);

		return vMainLayout;
	}

	/**
	 * Xu ly tao du lieu cho grid, thuc hien sau khi goi initGrid()
	 */
	@SuppressWarnings("unchecked")
	private void initData() {
		idxContainer.removeAllItems();

		idxContainer = new IndexedContainer();
		// idxContainer.addContainerProperty("stt", String.class, "");
		idxContainer.addContainerProperty("id", String.class, "");
		idxContainer.addContainerProperty("cif", String.class, "");
		idxContainer.addContainerProperty("crd_brd", String.class, "");
		idxContainer.addContainerProperty("cust_name", String.class, "");
		idxContainer.addContainerProperty("main_sub", String.class, "");
		idxContainer.addContainerProperty("pan_mask", String.class, "");
		idxContainer.addContainerProperty("issue_type", String.class, "");
		idxContainer.addContainerProperty("issue_date", String.class, "");
		idxContainer.addContainerProperty("brch_code", String.class, "");
		idxContainer.addContainerProperty("gttn", String.class, "");
		idxContainer.addContainerProperty("saleofficer_code", String.class, "");
		// Chuyen file den MKS
		idxContainer.addContainerProperty("trans_mk", Boolean.class, false);
		idxContainer.addContainerProperty("trans_mk_date", String.class, "");
		idxContainer.addContainerProperty("trans_branch_fw", String.class, "");
		idxContainer.addContainerProperty("trans_branch_note", String.class, "");
		idxContainer.addContainerProperty("trans_mk_lock", Boolean.class, false);
		idxContainer.addContainerProperty("trans_mk_check", Boolean.class, false);
		// Don vi da nhan the
		idxContainer.addContainerProperty("trans_branch_rec", Boolean.class, false);
		idxContainer.addContainerProperty("trans_branch_rec_date", String.class, "");
		idxContainer.addContainerProperty("trans_branch_rec_lock", Boolean.class, false);
		idxContainer.addContainerProperty("trans_branch_rec_check", Boolean.class, false);
		// Giao the cho KH
		idxContainer.addContainerProperty("trans_cust", Boolean.class, false);
		idxContainer.addContainerProperty("trans_cust_date", String.class, "");
		// Tai file PIN
		idxContainer.addContainerProperty("filePin", String.class, false);
		idxContainer.addContainerProperty("buttonDowload", Component.class, false);

		idxContainer.addContainerProperty("trans_cust_lock", Boolean.class, false);
		idxContainer.addContainerProperty("trans_cust_status", String.class, "");
		idxContainer.addContainerProperty("trans_cust_note", String.class, "");

		idxContainer.addContainerProperty("trans_cust_check", Boolean.class, false);
		idxContainer.addContainerProperty("", String.class, "");
		idxContainer.addContainerProperty("pan", String.class, "");
		idxContainer.addContainerProperty("xacnhan_gttn", Boolean.class, false);

		GeneratedPropertyContainer gpcSearch = new GeneratedPropertyContainer(idxContainer);
		gpcSearch.addGeneratedProperty("buttonDowload", new PropertyValueGenerator<Component>() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component getValue(Item item, Object itemId, Object propertyId) {

				final HorizontalLayout hr = new HorizontalLayout();

				final Button btUpload = new Button("");
				final String sFileName = String
						.valueOf(idxContainer.getItem(itemId).getItemProperty("filePin").getValue());
				final String sId = String.valueOf(idxContainer.getItem(itemId).getItemProperty("id").getValue());
				final boolean sTranCust = (Boolean) idxContainer.getItem(itemId).getItemProperty("trans_cust")
						.getValue();
				final boolean sTranCustLock = (Boolean) idxContainer.getItem(itemId).getItemProperty("trans_cust_lock")
						.getValue();
				final boolean sTranCustCheck = (Boolean) idxContainer.getItem(itemId)
						.getItemProperty("trans_cust_check").getValue();
				btUpload.setIcon(FontAwesome.UPLOAD);
				btUpload.setStyleName(ValoTheme.BUTTON_LINK);
				btUpload.setDescription("Đính kèm file");
				btUpload.addClickListener(evt -> {
					showUploadFile(sId);
				});
				final Button btDowload = new Button("");
				btDowload.setIcon(FontAwesome.DOWNLOAD);
				btDowload.setStyleName(ValoTheme.BUTTON_LINK);
				btDowload.setDescription("Tải file");
				btDowload.addClickListener(downloadEvent -> {
					try {
						downloadFile(sFileName);
					} catch (Exception e) {
						Notification.show("Không tìm thấy file cần tải", Type.ERROR_MESSAGE);
						LOGGER.error("Khong the tai file: " + sFileName + " - Message: " + e.getMessage());
					}
				});

				// Neu thuc hien giao the cho KH va chua khoa du lieu moi hien
				// thi cho upload file
				if ((sTranCust && !sTranCustLock)
						|| (SecurityUtils.hasRole("ROLE_DONVICHECKER") && sTranCust && !sTranCustCheck)) {
					hr.addComponent(btUpload);
				}
				if (idxContainer.getItem(itemId).getItemProperty("filePin").getValue() != "") {
					hr.addComponent(btDowload);
				}

				return hr;
			}

			@Override
			public Class<Component> getType() {
				return Component.class;
			}
		});

		final String sFromDate = dfFromDate.getValue() != null ? dateFormat.format(dfFromDate.getValue())
				: timeConverter.getCurrentTime("");// "20000101" ;
		final String sToDate = dfToDate.getValue() != null ? dateFormat.format(dfToDate.getValue())
				: timeConverter.getCurrentTime("");
		String sUnit = cbxUnit.getValue() != null ? cbxUnit.getValue().toString() : "";
		String sBrand = cbxBranch.getValue() != null ? cbxBranch.getValue().toString() : "";

		sUnit = sUnit.replaceAll("\\s", "");
		sBrand = sBrand.replaceAll("\\s", "");
		System.out.print(sUnit);
		System.out.print(sBrand);

		String sCrdType = "";
		if (cbxCrdType.getValue().toString() == "ALL") {
			sCrdType = "";
		} else {
			sCrdType = cbxCrdType.getValue() != null ? cbxCrdType.getValue().toString() : "";
		}

		// Search thong tin khach hang
		String key = "";
		if (cbxCustInfo.getValue().toString() == "") {
			key = "";
		} else {
			key = cbxCustInfo.getValue() != null ? cbxCustInfo.getValue().toString() : "";
		}

		String sCif = txfCif.getValue() != null ? txfCif.getValue().toString() : "";

		if (key.equals("")) {
			sCif = "";
		}

		// Search theo trang thai: Chua duyet / Da duyet
		String keyStatus = "";
		if (cbxStatus.getValue().toString() == "") {
			keyStatus = "";
		} else {
			keyStatus = cbxStatus.getValue() != null ? cbxStatus.getValue().toString() : "";
		}

		crdDetService
				.findAll(sFromDate, sToDate, sBrand, sUnit, sCrdType, SecurityUtils.getUserName(), sCif, key, keyStatus)
				.forEach(s -> {
					Object itemId = idxContainer.addItem();
					Item item = idxContainer.getItem(itemId);
					item.getItemProperty("cif").setValue(s[1] != null ? s[1].toString() : "");
					item.getItemProperty("crd_brd").setValue(s[2] != null ? s[2].toString() : "");
					item.getItemProperty("cust_name").setValue(s[3] != null ? s[3].toString() : "");
					item.getItemProperty("main_sub").setValue(s[4] != null ? s[4].toString() : "");
					item.getItemProperty("pan_mask").setValue(s[5] != null ? s[5].toString() : "");
					item.getItemProperty("issue_type").setValue(s[6] != null ? s[6].toString() : "");
					item.getItemProperty("issue_date").setValue(s[7] != null ? s[7].toString() : "");
					item.getItemProperty("brch_code").setValue(s[8] != null ? s[8].toString() : "");
					item.getItemProperty("saleofficer_code").setValue(s[65] != null ? s[65].toString() : "");

					// Chuyen file den MK
					item.getItemProperty("trans_mk").setValue(s[10].toString().equals("0") ? false : true);
					item.getItemProperty("trans_mk_date")
							.setValue(s[11] != null ? convertDateFormat(s[11].toString()) : "");
					item.getItemProperty("trans_branch_fw").setValue(s[31] != null ? s[31].toString() : "");
					item.getItemProperty("trans_branch_note").setValue(s[30] != null ? s[30].toString() : "");
					item.getItemProperty("trans_mk_lock").setValue(s[12].toString().equals("0") ? false : true);
					item.getItemProperty("trans_mk_check").setValue(s[33].toString().equals("0") ? false : true);

					// Don vi nhan the
					item.getItemProperty("trans_branch_rec").setValue(s[40].toString().equals("0") ? false : true);
					item.getItemProperty("trans_branch_rec_date")
							.setValue(s[41] != null ? convertDateFormat(s[41].toString()) : "");
					item.getItemProperty("trans_branch_rec_lock").setValue(s[42].toString().equals("0") ? false : true);
					item.getItemProperty("trans_branch_rec_check")
							.setValue(s[43].toString().equals("0") ? false : true);

					// Giao the cho KH
					item.getItemProperty("trans_cust").setValue(s[25].toString().equals("0") ? false : true);
					item.getItemProperty("trans_cust_date")
							.setValue(s[26] != null ? convertDateFormat(s[26].toString()) : "");
					item.getItemProperty("trans_cust_lock").setValue(s[27].toString().equals("0") ? false : true);
					item.getItemProperty("trans_cust_check").setValue(s[37].toString().equals("0") ? false : true);

					// Cot gttn s[66]
					if (s[66].toString().equals("1")) {
						item.getItemProperty("gttn").setValue("Y");
					} else if (s[66].toString().equals("2")) {
						item.getItemProperty("gttn").setValue("N");
					} else {
						item.getItemProperty("gttn").setValue("");
					}
					// if (s[66].toString().equals("1")) {
					// item.getItemProperty("gttn").setValue(true);
					// } else if (s[66].toString().equals("2")) {
					// item.getItemProperty("gttn").setValue(false);
					// } else {
					// item.getItemProperty("gttn").setValue(null);
					// }

					// Cot xacnhan_gttn s[68]

					if (s[68].toString().equals("1")) {
						item.getItemProperty("xacnhan_gttn").setValue(true);
					}
					// else if (!s[66].toString().equals("1") &&
					// !s[66].toString().equals("2")) {
					// item.getItemProperty("xacnhan_gttn").setValue(false);
					// }
					// item.getItemProperty("gttn").setValue(s[66].toString().equals("0")
					// ? false : true);
					// item.getItemProperty("xacnhan_gttn").setValue(s[68].toString().equals("0")
					// ? false : true);
					// item.getItemProperty("gttn").setValue(s[66].toString().equals("0")
					// ? false : true);
					// String x1 = s[26].toString();
					// String x2 = s[27].toString();
					// String gttt = s[66].toString();

					//
					if (s[63] == null) {
						item.getItemProperty("trans_cust_status").setValue(s[63] != null ? s[63].toString() : "");
					} else {

						String grid_transCustStatus = s[63].toString();
						String txt_transCustStatus = "";
						if (grid_transCustStatus.equals("00")) {

						} else if (grid_transCustStatus.toString().equals("01")) {
							txt_transCustStatus = "Giao thẻ thành công";
						} else if (grid_transCustStatus.toString().equals("02")) {
							txt_transCustStatus = "KH từ chối nhận thẻ";
						} else if (grid_transCustStatus.toString().equals("03")) {
							txt_transCustStatus = "KH hẹn đến nhận";
						} else if (grid_transCustStatus.toString().equals("04")) {
							txt_transCustStatus = "Không liên lạc được";
						} else if (grid_transCustStatus.toString().equals("05")) {
							txt_transCustStatus = "Đã chuyển đối tác chuyển phát thẻ";
						} else if (grid_transCustStatus.toString().equals("06")) {
							txt_transCustStatus = "Khác";
						} else if (grid_transCustStatus.toString().equals("07")) {
							txt_transCustStatus = "Thẻ PH sai thông số";
						}
						item.getItemProperty("trans_cust_status")
								.setValue(s[63] != null ? txt_transCustStatus.toString() : "");
					}

					item.getItemProperty("trans_cust_note").setValue(s[64] != null ? s[64].toString() : "");
					item.getItemProperty("pan").setValue(s[28] != null ? s[28].toString() : "");
					item.getItemProperty("id").setValue(s[32] != null ? s[32].toString() : "");
					item.getItemProperty("filePin").setValue(s[39] != null ? s[39].toString() : "");

					String flag = String.valueOf(item.getItemProperty("trans_cust_check").getValue().toString());
					if (flag.equals("true")) {

					}

				});
		grid.setContainerDataSource(gpcSearch);
	}

	private String convertDateFormat(String sTime) {
		if (sTime.equals("0")) {
			return null;
		}
		return timeConverter.convertStrToDateTime(sTime);
	}

	/**
	 * Xu ly khi click searching button
	 */
	private void cmdSearch_buttonClick() {
		initData();
	}

	/**
	 * Khoi tao context menu tren grid
	 */

	private void createContextMenu(AbstractComponent parentComponent) {
		ContextMenu contextMenu = new ContextMenu(parentComponent, true);
		if (SecurityUtils.hasRole("ROLE_HO") || SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			contextMenu.addItem(messageResource.getMessage("carddistribution.grid.header.tran.mks"),
					FontAwesome.CARET_SQUARE_O_RIGHT, e -> {
						// cmdUpdateData_ContextMenu("TOMK");//AAAA
						windowFormTransFileMK("TOMK");
					});
		}
		// ----- << Don vi da nhan the tu MK >> -----
		if (SecurityUtils.hasRole("ROLE_DONVI") || SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			contextMenu.addItem(messageResource.getMessage("carddistribution.grid.header.received.branch"),
					FontAwesome.CARET_SQUARE_O_RIGHT, e -> {
						// cmdUpdateData_ContextMenu("BRANCHREC", "");
						windowFormTransFileMK("BRANCHREC");
					});
		}
		// ----- << Giao the cho khach hang >> -----
		if (SecurityUtils.hasRole("ROLE_DONVI") || SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			contextMenu.addItem(messageResource.getMessage("carddistribution.grid.header.delivered.cust"),
					FontAwesome.CARET_SQUARE_O_RIGHT, e -> {
						// cmdUpdateData_ContextMenu("TOCUST", "");
						// windowFormTransFileMK("TOCUST");
						transCustPopupDialog("TOCUST");
					});
		}
		// ----- << Giao the tan noi >> -----
		if (SecurityUtils.hasRole("ROLE_DONVI") || SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			contextMenu.addItem(messageResource.getMessage("carddistribution.grid.header.delivered.gttn"),
					FontAwesome.CARET_SQUARE_O_RIGHT, e -> {
						// cmdUpdateData_ContextMenu("TOCUST", "");
						// windowFormTransFileMK("TOCUST");
						gttn_PopupDialog("TOCUST_GTTN");
					});
		}
	}

	/**
	 * Xu ly khoa the hang loat
	 */
	private void cmdLockData_Button() {
		final Collection<Object> selectedItem = grid.getSelectedRows();

		selectedItem.forEach(s -> {
			final String id = idxContainer.getItem(s).getItemProperty("id").getValue().toString();
			final String sCifNo = idxContainer.getItem(s).getItemProperty("cif").getValue().toString();
			final String sCustName = idxContainer.getItem(s).getItemProperty("cust_name").getValue().toString();
			final String sCrdType = idxContainer.getItem(s).getItemProperty("crd_brd").getValue().toString();

			String brnFwd = String.valueOf(idxContainer.getItem(s).getItemProperty("trans_branch_fw").getValue());
			if (brnFwd == null || brnFwd.trim().equals(""))
				brnFwd = "";
			else
				brnFwd = brnFwd.trim();

			boolean isFwdBrn = checkFwdBrn(brnFwd);

			if (!id.equals("") && isFwdBrn) {
				final long lid = Long.parseLong(id);
				if (crdDetService.lockData(lid)) {
					LOGGER.info(SecurityUtils.getUserName() + " - khoa du lieu - cif:" + sCifNo + "|custname:"
							+ sCustName + "|crdtype:" + sCrdType);
				} else if (id.equals("")) {
					Notification.show("Số Cif: " + sCifNo + " không thể khóa dữ liệu", Type.TRAY_NOTIFICATION);
				}
			}
		});

		Notification notif = new Notification("Ðã lưu dữ liệu", Notification.TYPE_WARNING_MESSAGE);
		notif.setDelayMsec(700);

		notif.setPosition(Position.MIDDLE_CENTER);
		notif.setStyleName("notifSaved");
		notif.show(Page.getCurrent());
		initData();
		grid.deselectAll();
		getUI().removeWindow(windowConfirm);
	}

	/**
	 * Xu ly mo khoa the hang loat
	 */
	private void cmdUnLockData_Button() {
		final Collection<Object> selectedItem = grid.getSelectedRows();
		selectedItem.forEach(s -> {
			final String id = idxContainer.getItem(s).getItemProperty("id").getValue().toString();
			final String sCifNo = idxContainer.getItem(s).getItemProperty("cif").getValue().toString();
			final String sCustName = idxContainer.getItem(s).getItemProperty("cust_name").getValue().toString();
			final String sCrdType = idxContainer.getItem(s).getItemProperty("crd_brd").getValue().toString();
			final String sCheckDonViNhanTheMK = String
					.valueOf(idxContainer.getItem(s).getItemProperty("trans_branch_rec_check").getValue());
			final String sNgayGiaoTheKH = String
					.valueOf(idxContainer.getItem(s).getItemProperty("trans_cust_date").getValue());
			final String sStatusGiaoTheKH = String
					.valueOf(idxContainer.getItem(s).getItemProperty("trans_cust_status").getValue().toString());
			final String sCheckGiaoTheKH = String
					.valueOf(idxContainer.getItem(s).getItemProperty("trans_cust_check").getValue().toString());

			String brnFwd = String.valueOf(idxContainer.getItem(s).getItemProperty("trans_branch_fw").getValue());
			if (brnFwd == null || brnFwd.trim().equals(""))
				brnFwd = "";
			else
				brnFwd = brnFwd.trim();

			boolean isFwdBrn = checkFwdBrn(brnFwd);

			if (!id.equals("") && isFwdBrn) {
				final long lid = Long.parseLong(id);
				if (crdDetService.unlockData(lid)) {
					LOGGER.info(SecurityUtils.getUserName() + " - mo khoa du lieu - cif:" + sCifNo + "|custname:"
							+ sCustName + "|crdtype:" + sCrdType);
				} else if (id.equals("")) {
					Notification.show("Số Cif: " + sCifNo + " không thể mở khóa dữ liệu", Type.TRAY_NOTIFICATION);
				}
			}

			@SuppressWarnings("deprecation")
			Notification notif = new Notification("Ðã lưu dữ liệu", Notification.TYPE_WARNING_MESSAGE);
			@SuppressWarnings("deprecation")
			Notification warning = new Notification(
					"ERROR: Vui lòng email 'RESET TRANG THAI GIAO THE' bao gổm số CIF, Ngày Phát Hành về email: ph_ktvhtnhs@scb.com.vn(hoặc gọi line #2327)",
					Notification.TYPE_WARNING_MESSAGE);

			// Khong cho phep Unlock khi DONVICHECKER da Duyet DON VI NHAN THE
			// TU MKS
			if (sCheckDonViNhanTheMK.equals("true") && (sNgayGiaoTheKH == "" || sNgayGiaoTheKH == "null")) {
				warning.setDelayMsec(2000);
				warning.setPosition(Position.MIDDLE_CENTER);
				warning.setStyleName("notifSaved");
				warning.show(Page.getCurrent());
			}
			/*
			 * Khong cho phep Unlock khi DONVICHECKER da Duyet GIAO THE KHACH
			 * HANG va co tinh trang la: GIAO THE THANH CONG hoac KH KHONG NHAN
			 * THE
			 */
			else if (sCheckGiaoTheKH.equals("true") && (sStatusGiaoTheKH.equals("Giao thẻ thành công")
					|| sStatusGiaoTheKH.equals("KH từ chối nhận thẻ"))) {
				warning.setDelayMsec(1400);
				warning.setPosition(Position.MIDDLE_CENTER);
				warning.setStyleName("notifSaved");
				warning.show(Page.getCurrent());
			} else {
				notif.setDelayMsec(1400);
				notif.setPosition(Position.MIDDLE_CENTER);
				notif.setStyleName("notifSaved");
				notif.show(Page.getCurrent());
			}

			initData();
			grid.deselectAll();
			getUI().removeWindow(windowConfirm);
		});

	}

	/**
	 * Xu ly duyet the hang loat
	 */

	private void cmdCheckData_Button() {
		final Collection<Object> selectedItem = grid.getSelectedRows();
		selectedItem.forEach(s -> {
			final String id = idxContainer.getItem(s).getItemProperty("id").getValue().toString();
			final String sCifNo = idxContainer.getItem(s).getItemProperty("cif").getValue().toString();
			final String sCustName = idxContainer.getItem(s).getItemProperty("cust_name").getValue().toString();
			final String sCrdType = idxContainer.getItem(s).getItemProperty("crd_brd").getValue().toString();

			String brnFwd = String.valueOf(idxContainer.getItem(s).getItemProperty("trans_branch_fw").getValue());
			if (brnFwd == null || brnFwd.trim().equals(""))
				brnFwd = "";
			else
				brnFwd = brnFwd.trim();
			boolean isFwdBrn = checkFwdBrn(brnFwd);

			if (!id.equals("") && isFwdBrn) {
				final long lid = Long.parseLong(id);
				if (crdDetService.checkData(lid)) {
					LOGGER.info(SecurityUtils.getUserName() + " - Duyet du lieu - cif:" + sCifNo + "|custname:"
							+ sCustName + "|crdtype:" + sCrdType);
				} else if (id.equals("")) {
					Notification.show("Số Cif: " + sCifNo + " không thể duyệt dữ liệu", Type.TRAY_NOTIFICATION);
				}
			}
		});

		Notification notif = new Notification("Ðã lưu dữ liệu", Notification.TYPE_WARNING_MESSAGE);
		notif.setDelayMsec(700);

		notif.setPosition(Position.MIDDLE_CENTER);
		notif.setStyleName("notifSaved");
		notif.show(Page.getCurrent());
		initData();
		grid.deselectAll();
		getUI().removeWindow(windowConfirm);
	}

	public void lockAllCard() {
		final String sFromDate = dfFromDate.getValue() != null ? dateFormat.format(dfFromDate.getValue())
				: timeConverter.getCurrentTime("");// "20000101" ;
		final String sToDate = dfToDate.getValue() != null ? dateFormat.format(dfToDate.getValue())
				: timeConverter.getCurrentTime("");
		String sUnit = cbxUnit.getValue() != null ? cbxUnit.getValue().toString() : "";
		String sBrand = cbxBranch.getValue() != null ? cbxBranch.getValue().toString() : "";
		String key = "";
		String keyStatus = "";

		sUnit = sUnit.replaceAll("\\s", "");
		sBrand = sBrand.replaceAll("\\s", "");
		String sCrdType = "";
		if (cbxCrdType.getValue().toString() == "ALL") {
			sCrdType = "";
		} else {
			sCrdType = cbxCrdType.getValue() != null ? cbxCrdType.getValue().toString() : "";
		}

		final String sCif = txfCif.getValue() != null ? txfCif.getValue().toString() : "";
		crdDetService
				.findAll(sFromDate, sToDate, sBrand, sUnit, sCrdType, SecurityUtils.getUserName(), sCif, key, keyStatus)
				.forEach(s -> {

					String id = (s[32] != null ? s[32].toString() : ""); // id
					String cif = (s[1] != null ? s[1].toString() : ""); // cif
					String custName = (s[3] != null ? s[3].toString() : ""); // cust_name
					String cardBrn = (s[2] != null ? s[2].toString() : ""); // crd_brd

					if (!id.equals("")) {
						final long lid = Long.parseLong(id);
						if (crdDetService.lockData(lid)) {
							LOGGER.info(SecurityUtils.getUserName() + " - Duyet du lieu - cif:" + cif + "|custname:"
									+ custName + "|crdtype:" + cardBrn);
						} else {
							Notification.show("Số Cif: " + cif + " không thể duyệt dữ liệu", Type.TRAY_NOTIFICATION);
						}
					}
				});

		Notification notif = new Notification("Ðã lưu dữ liệu", Notification.TYPE_WARNING_MESSAGE);
		notif.setDelayMsec(700);

		notif.setPosition(Position.MIDDLE_CENTER);
		notif.setStyleName("notifSaved");
		notif.show(Page.getCurrent());
		initData();
		grid.deselectAll();
		getUI().removeWindow(windowConfirm);
	}

	public void approveAllCard() {
		final String sFromDate = dfFromDate.getValue() != null ? dateFormat.format(dfFromDate.getValue())
				: timeConverter.getCurrentTime("");// "20000101" ;
		final String sToDate = dfToDate.getValue() != null ? dateFormat.format(dfToDate.getValue())
				: timeConverter.getCurrentTime("");
		String sUnit = cbxUnit.getValue() != null ? cbxUnit.getValue().toString() : "";
		String sBrand = cbxBranch.getValue() != null ? cbxBranch.getValue().toString() : "";
		String key = "";
		String keyStatus = "";

		sUnit = sUnit.replaceAll("\\s", "");
		sBrand = sBrand.replaceAll("\\s", "");
		String sCrdType = "";
		if (cbxCrdType.getValue().toString() == "ALL") {
			sCrdType = "";
		} else {
			sCrdType = cbxCrdType.getValue() != null ? cbxCrdType.getValue().toString() : "";
		}

		final String sCif = txfCif.getValue() != null ? txfCif.getValue().toString() : "";
		crdDetService
				.findAll(sFromDate, sToDate, sBrand, sUnit, sCrdType, SecurityUtils.getUserName(), sCif, key, keyStatus)
				.forEach(s -> {

					String id = (s[32] != null ? s[32].toString() : ""); // id
					String cif = (s[1] != null ? s[1].toString() : ""); // cif
					String custName = (s[3] != null ? s[3].toString() : ""); // cust_name
					String cardBrn = (s[2] != null ? s[2].toString() : ""); // crd_brd

					if (!id.equals("")) {
						final long lid = Long.parseLong(id);
						if (crdDetService.checkData(lid)) {
							LOGGER.info(SecurityUtils.getUserName() + " - Duyet du lieu - cif:" + cif + "|custname:"
									+ custName + "|crdtype:" + cardBrn);
						} else {
							Notification.show("Số Cif: " + cif + " không thể duyệt dữ liệu", Type.TRAY_NOTIFICATION);
						}
					}
				});

		Notification notif = new Notification("Ðã lưu dữ liệu", Notification.TYPE_WARNING_MESSAGE);
		notif.setDelayMsec(700);

		notif.setPosition(Position.MIDDLE_CENTER);
		notif.setStyleName("notifSaved");
		notif.show(Page.getCurrent());
		initData();
		grid.deselectAll();
		getUI().removeWindow(windowConfirm);
	}

	/**
	 * Xu ly cap nhat du lieu tu context menu
	 */
	private void cmdUpdateData_ContextMenu(String sType, String selectDate) {

		final Collection<Object> selectedItem = grid.getSelectedRows();
		selectedItem.forEach(s -> {

			final String sCifNo = String.valueOf(idxContainer.getItem(s).getItemProperty("cif").getValue());
			final String sCustName = String.valueOf(idxContainer.getItem(s).getItemProperty("cust_name").getValue());
			final String sCrdType = String.valueOf(idxContainer.getItem(s).getItemProperty("crd_brd").getValue());
			final String sPrinSupp = String.valueOf(idxContainer.getItem(s).getItemProperty("main_sub").getValue());
			final String sIssueType = String.valueOf(idxContainer.getItem(s).getItemProperty("issue_type").getValue());
			final String sIssueDate = String.valueOf(idxContainer.getItem(s).getItemProperty("issue_date").getValue());
			final String sPan = String.valueOf(idxContainer.getItem(s).getItemProperty("pan").getValue());
			final String sBrchCde = String.valueOf(idxContainer.getItem(s).getItemProperty("brch_code").getValue());
			final String sGttn = String.valueOf(idxContainer.getItem(s).getItemProperty("gttn").getValue());
			final String sSaleofficer_code = String
					.valueOf(idxContainer.getItem(s).getItemProperty("saleofficer_code").getValue());

			String brnFwd = String.valueOf(idxContainer.getItem(s).getItemProperty("trans_branch_fw").getValue());
			if (brnFwd == null || brnFwd.trim().equals(""))
				brnFwd = "";
			else
				brnFwd = brnFwd.trim();

			if (sType.equals("TOMK")) {
				crdDetService.saveDataFromFormContextMenu(sCifNo, sCustName, sPan, sCrdType, sPrinSupp, sIssueType,
						sIssueDate, sType, sBrchCde, "", "", "", "", selectDate);
				LOGGER.info(SecurityUtils.getUserName() + " - thuc hien " + sType + " - cif:" + sCifNo + "|custname:"
						+ sCustName + "|crdtype:" + sCrdType);
			}

			if (idxContainer.getItem(s).getItemProperty("trans_mk_lock") != null && sType.equals("BRANCHREC")) {

				if (sGttn.equals("Y") || sGttn.equals("N")) {
					Notification.show("Không thể thực hiện đối với thẻ giao tận nơi", Type.ERROR_MESSAGE);
					return;
				}

				final boolean a = (boolean) idxContainer.getItem(s).getItemProperty("trans_mk_lock").getValue();
				final boolean b = (boolean) idxContainer.getItem(s).getItemProperty("trans_mk_check").getValue();
				boolean c = checkFwdBrn(brnFwd);

				if (a && b && c) {
					crdDetService.saveDataFromFormContextMenu(sCifNo, sCustName, sPan, sCrdType, sPrinSupp, sIssueType,
							sIssueDate, sType, sBrchCde, "", "", "", "", selectDate);
					LOGGER.info(SecurityUtils.getUserName() + " - thuc hien " + sType + " - cif:" + sCifNo
							+ "|custname:" + sCustName + "|crdtype:" + sCrdType);
				} else if (!a & !b) {
					Notification.show("Không thể thực hiện bước tiếp theo khi chưa duyệt", Type.ERROR_MESSAGE);
				}
			}

			if (idxContainer.getItem(s).getItemProperty("trans_branch_rec_lock") != null && sType.equals("TOCUST")) {
				final boolean a = (boolean) idxContainer.getItem(s).getItemProperty("trans_branch_rec_lock").getValue();
				final boolean b = (boolean) idxContainer.getItem(s).getItemProperty("trans_branch_rec_check")
						.getValue();
				final boolean c = checkFwdBrn(brnFwd);

				if (a && b && c) {
					crdDetService.saveDataFromFormContextMenu(sCifNo, sCustName, sPan, sCrdType, sPrinSupp, sIssueType,
							sIssueDate, sType, sBrchCde, "", "", "", "", selectDate);
					LOGGER.info(SecurityUtils.getUserName() + " - thuc hien " + sType + " - cif:" + sCifNo
							+ "|custname:" + sCustName + "|crdtype:" + sCrdType);
				} else if (!a & !b) {
					Notification.show("Không thể thực hiện bước tiếp theo khi chưa duyệt", Type.ERROR_MESSAGE);
				}
			}
		});

		initData();
		grid.deselectAll();
	}

	/**
	 * XU LY CAP NHAT CHO POPUP DON VI GIAO THE CHO KHACH HANG
	 */
	private void cmdUpdateData_ContextMenu_TransCust(String sType, String selectDate, String keyStatus,
			String NoteStatus) {

		final Collection<Object> selectedItem = grid.getSelectedRows();
		selectedItem.forEach(s -> {

			final String sCifNo = String.valueOf(idxContainer.getItem(s).getItemProperty("cif").getValue());
			final String sCustName = String.valueOf(idxContainer.getItem(s).getItemProperty("cust_name").getValue());
			final String sCrdType = String.valueOf(idxContainer.getItem(s).getItemProperty("crd_brd").getValue());
			final String sPrinSupp = String.valueOf(idxContainer.getItem(s).getItemProperty("main_sub").getValue());
			final String sIssueType = String.valueOf(idxContainer.getItem(s).getItemProperty("issue_type").getValue());
			final String sIssueDate = String.valueOf(idxContainer.getItem(s).getItemProperty("issue_date").getValue());
			final String sPan = String.valueOf(idxContainer.getItem(s).getItemProperty("pan").getValue());
			final String sBrchCde = String.valueOf(idxContainer.getItem(s).getItemProperty("brch_code").getValue());
			final String sGttn = String.valueOf(idxContainer.getItem(s).getItemProperty("gttn").getValue());

			final String sSaleofficer_code = String
					.valueOf(idxContainer.getItem(s).getItemProperty("saleofficer_code").getValue());

			String brnFwd = String.valueOf(idxContainer.getItem(s).getItemProperty("trans_branch_fw").getValue());
			if (brnFwd == null || brnFwd.trim().equals(""))
				brnFwd = "";
			else
				brnFwd = brnFwd.trim();

			if (sGttn.equals("Y") || sGttn.equals("N")) {
				Notification.show("Không thể thực hiện đối với thẻ giao tận nơi", Type.ERROR_MESSAGE);
				return;
			}

			if (idxContainer.getItem(s).getItemProperty("trans_branch_rec_lock") != null && sType.equals("TOCUST")) {
				final boolean a = (boolean) idxContainer.getItem(s).getItemProperty("trans_branch_rec_lock").getValue();
				final boolean b = (boolean) idxContainer.getItem(s).getItemProperty("trans_branch_rec_check")
						.getValue();
				final boolean c = checkFwdBrn(brnFwd);

				if (a && b && c) {
					crdDetService.saveDataFromFormContextMenu_TransCust(sCifNo, sCustName, sPan, sCrdType, sPrinSupp,
							sIssueType, sIssueDate, sType, sBrchCde, "", "", "", "", selectDate, keyStatus, NoteStatus);
					LOGGER.info(SecurityUtils.getUserName() + " - thuc hien " + sType + " - cif:" + sCifNo
							+ "|custname:" + sCustName + "|crdtype:" + sCrdType);
				} else if (!a & !b) {
					Notification.show("Không thể thực hiện bước tiếp theo khi chưa duyệt", Type.ERROR_MESSAGE);
				}
			}
		});

		initData();
		grid.deselectAll();
	}

	/**
	 * XU LY GIAO THE TAN TOI
	 */
	private void cmdUpdateData_ContextMenu_GTTN(String sType, String selectDate, String keyStatus) {

		final Collection<Object> selectedItem = grid.getSelectedRows();
		selectedItem.forEach(s -> {

			final String sCifNo = String.valueOf(idxContainer.getItem(s).getItemProperty("cif").getValue());
			final String sCustName = String.valueOf(idxContainer.getItem(s).getItemProperty("cust_name").getValue());
			final String sCrdType = String.valueOf(idxContainer.getItem(s).getItemProperty("crd_brd").getValue());
			final String sPrinSupp = String.valueOf(idxContainer.getItem(s).getItemProperty("main_sub").getValue());
			final String sIssueType = String.valueOf(idxContainer.getItem(s).getItemProperty("issue_type").getValue());
			final String sIssueDate = String.valueOf(idxContainer.getItem(s).getItemProperty("issue_date").getValue());
			final String sPan = String.valueOf(idxContainer.getItem(s).getItemProperty("pan").getValue());
			final String sBrchCde = String.valueOf(idxContainer.getItem(s).getItemProperty("brch_code").getValue());
			final String sGttn = String.valueOf(idxContainer.getItem(s).getItemProperty("gttn").getValue());
			final String sSaleofficer_code = String
					.valueOf(idxContainer.getItem(s).getItemProperty("saleofficer_code").getValue());

			String brnFwd = String.valueOf(idxContainer.getItem(s).getItemProperty("trans_branch_fw").getValue());
			if (brnFwd == null || brnFwd.trim().equals(""))
				brnFwd = "";
			else
				brnFwd = brnFwd.trim();

			if (!sGttn.equals("Y") && !sGttn.equals("N")) {
				Notification.show(
						"Đây không phải thẻ giao tận nơi hoặc chưa được P.KT&VHT&NHS cập nhật trạng thái GTTN.",
						Type.ERROR_MESSAGE);
				return;
			}

			if (sType.equals("TOCUST_GTTN")) {
				// final boolean a = (boolean)
				// idxContainer.getItem(s).getItemProperty("trans_branch_rec_lock").getValue();
				// final boolean b = (boolean)
				// idxContainer.getItem(s).getItemProperty("trans_branch_rec_check")
				// .getValue();
				// final boolean c = checkFwdBrn(brnFwd);

				// if (!sBrchCde.equals("001") && !sBrchCde.equals("242")) {
				// Notification.show("Chức năng chưa triển khai trên toàn
				// hàng.", Type.ERROR_MESSAGE);
				// return;
				// }

				// if (a && b && c) {
				crdDetService.saveDataFromFormContextMenu_GTTN(sCifNo, sCustName, sPan, sCrdType, sPrinSupp, sIssueType,
						sIssueDate, sType, sBrchCde, "", "", "", "", selectDate, keyStatus);
				LOGGER.info(SecurityUtils.getUserName() + " - xac nhan GTTN" + sType + " - cif:" + sCifNo + "|custname:"
						+ sCustName + "|crdtype:" + sCrdType);
				// }
				// else if (!a & !b) {
				//
				// }
			}
		});

		initData();
		grid.deselectAll();
	}

	// Xu ly kiem soat: UnUse
	@SuppressWarnings("unused")
	private void cmdCheckData_ContextMenu() {
		final Collection<Object> selectedItem = grid.getSelectedRows();
		selectedItem.forEach(s -> {
			final String id = idxContainer.getItem(s).getItemProperty("id").getValue().toString();
			final String cif = idxContainer.getItem(s).getItemProperty("cif").getValue().toString();
			if (!id.equals("")) {
				final long lid = Long.parseLong(id);
				crdDetService.checkData(lid);
				LOGGER.info(SecurityUtils.getUserName() + " - kiem soat du lieu - cif:" + cif);
			}

		});

		initData();
		grid.deselectAll();
	}

	/**
	 * Xu ly the hien form dieu chinh khi chon case tren grid
	 */
	private void cmdShowEditForm_GridClickListener(ItemClickEvent evt) {
		final Date maxDate = new Date();
		final String sCifNo = idxContainer.getItem(evt.getItemId()).getItemProperty("cif").getValue().toString();
		final String sCustName = idxContainer.getItem(evt.getItemId()).getItemProperty("cust_name").getValue()
				.toString();
		final String sCrdType = idxContainer.getItem(evt.getItemId()).getItemProperty("crd_brd").getValue().toString();
		final String sPrinSupp = idxContainer.getItem(evt.getItemId()).getItemProperty("main_sub").getValue()
				.toString();
		final String sIssueType = idxContainer.getItem(evt.getItemId()).getItemProperty("issue_type").getValue()
				.toString();
		final String sIssueDate = idxContainer.getItem(evt.getItemId()).getItemProperty("issue_date").getValue()
				.toString();
		final String sPan = idxContainer.getItem(evt.getItemId()).getItemProperty("pan").getValue().toString();
		final String sPanMask = idxContainer.getItem(evt.getItemId()).getItemProperty("pan_mask").getValue().toString();
		final String sCustStatus = idxContainer.getItem(evt.getItemId()).getItemProperty("trans_cust_status").getValue()
				.toString();
		final String sCustNote = idxContainer.getItem(evt.getItemId()).getItemProperty("trans_cust_note").getValue()
				.toString();

		CwstCrdDetail gciCrdDetail = crdDetService.findOneByIdx(sCifNo, sCustName, sPan, sCrdType, sPrinSupp,
				sIssueType, sIssueDate);

		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(true);
		fieldGroup = new BeanFieldGroup<CwstCrdDetail>(CwstCrdDetail.class);

		fieldGroup.setBuffered(true);
		/* Truong hop chua co du lieu trong database */
		if (gciCrdDetail.getCifNo() == null) {
			gciCrdDetail = new CwstCrdDetail();
			gciCrdDetail.setCifNo(sCifNo);
			gciCrdDetail.setCustName(sCustName);
			gciCrdDetail.setCrdType(sCrdType);
			gciCrdDetail.setCrdPrinSupp(sPrinSupp);
			gciCrdDetail.setIssueType(sIssueType);
			gciCrdDetail.setIssueDate(sIssueDate);
			gciCrdDetail.setPan(sPan);
		} else {
			fieldGroup.setItemDataSource(gciCrdDetail);
		}
		fieldGroup.setItemDataSource(gciCrdDetail);
		fieldGroup.addCommitHandler(commitHandler_EditForm());

		// Field readonly
		final TextField tfPanMask = new TextField(messageResource.getMessage("carddistribution.grid.header.panmask"));
		tfPanMask.setValue(sPanMask);
		tfPanMask.setReadOnly(true);
		final Field<?> flCifNo = fieldGroup.buildAndBind(
				messageResource.getMessage("carddistribution.grid.header.cifno"), "cifNo", AbstractTextField.class);
		flCifNo.setReadOnly(true);
		final Field<?> flCustName = fieldGroup.buildAndBind(
				messageResource.getMessage("carddistribution.grid.header.custname"), "custName",
				AbstractTextField.class);
		flCustName.setReadOnly(true);
		final Field<?> flCrdType = fieldGroup.buildAndBind(
				messageResource.getMessage("carddistribution.grid.header.cardbranch"), "crdType",
				AbstractTextField.class);
		flCrdType.setReadOnly(true);
		final Field<?> flCrdPrinSupp = fieldGroup.buildAndBind(
				messageResource.getMessage("carddistribution.grid.header.submain"), "crdPrinSupp",
				AbstractTextField.class);
		flCrdPrinSupp.setReadOnly(true);
		final Field<?> flIssueDate = fieldGroup.buildAndBind(
				messageResource.getMessage("carddistribution.grid.header.issuedate"), "issueDate",
				AbstractTextField.class);
		flIssueDate.setReadOnly(true);
		final Field<?> flIssueType = fieldGroup.buildAndBind(
				messageResource.getMessage("carddistribution.grid.header.issuetype"), "issueType",
				AbstractTextField.class);
		flIssueType.setReadOnly(true);
		final Field<?> flPan = fieldGroup.buildAndBind("PAN", "pan", AbstractTextField.class);

		Field<?> fCustStatus = fieldGroup.buildAndBind(messageResource.getMessage("cust.status"), "transCustStatus",
				AbstractTextField.class);

		Label lbl_transCustStatus = new Label("");
		String fCustStatus_check = fCustStatus.toString();
		if (fCustStatus_check != null) {
			if (fCustStatus.equals("00")) {
				fCustStatus.setValue(null);
			} else if (fCustStatus.toString().equals("01")) {
				lbl_transCustStatus = new Label("Giao thẻ thành công");
			} else if (fCustStatus.toString().equals("02")) {
				lbl_transCustStatus = new Label("KH từ chối nhận thẻ");
			} else if (fCustStatus.toString().equals("03")) {
				lbl_transCustStatus = new Label("KH hẹn đến nhận");
			} else if (fCustStatus.toString().equals("04")) {
				lbl_transCustStatus = new Label("Không liên lạc được");
			} else if (fCustStatus.toString().equals("05")) {
				lbl_transCustStatus = new Label("Đã chuyển đối tác chuyển phát thẻ");
			} else if (fCustStatus.toString().equals("06")) {
				lbl_transCustStatus = new Label("Khác");
			} else if (fCustStatus.toString().equals("07")) {
				lbl_transCustStatus = new Label("Thẻ PH sai thông số");
			}
			fCustStatus.setReadOnly(true);
		}
		final Field<?> fCustNote = fieldGroup.buildAndBind("", "transCustNote", AbstractTextField.class);
		fCustNote.setReadOnly(true);

		flPan.setReadOnly(true);
		flPan.setVisible(false);

		/*
		 * --<< CHUYEN FILE CTY MK
		 * --->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->
		 * >--->>--->>--->>--->>--->>--->>--->>--->>
		 */

		chbTransMk = fieldGroup.buildAndBind(messageResource.getMessage("carddistribution.grid.header.tran.mks"),
				"transMk", CheckBox.class);
		chbTransMk.setReadOnly(gciCrdDetail.getTransMkLock());
		dtTransMkDate = new DateField(messageResource.getMessage("carddistribution.grid.header.complete.date"));
		dtTransMkDate.setDateFormat("dd/MM/yyyy");
		dtTransMkDate.setResolution(Resolution.DAY);
		dtTransMkDate.setRangeStart(null);
		dtTransMkDate.setRangeEnd(maxDate);
		// Chuyen tiep don vi
		txfTransBranchFwBrch = fieldGroup.buildAndBind(
				messageResource.getMessage("carddistribution.grid.header.fw.branch"), "fwBrnCde", TextField.class);
		// txfTransBranchFwBrch.setVisible(chbTransMk.getValue());
		txfTransBranchFwBrch.setNullRepresentation("");
		txfTransBranchFwBrch.setNullSettingAllowed(true);
		txfTransBranchFwBrch.setMaxLength(3);
		txfTransBranchFwBrch.addValidator(new RegexpValidator("[-]?[0-9]*\\.?,?[0-9]+", "Mã đơn vị phải là số"));
		// Ghi chu chuyen tiep don vi
		txfTransBranchNote = fieldGroup.buildAndBind("Ghi Chú", "transBranchNote", TextArea.class);
		// txfTransBranchNote.setVisible(chbTransMk.getValue());
		txfTransBranchNote.setNullRepresentation("");
		txfTransBranchNote.setNullSettingAllowed(true);
		txfTransBranchNote.setValidationVisible(false);
		txfTransBranchNote.setMaxLength(250);
		txfTransBranchNote.setWidth(90f, Unit.PERCENTAGE);
		txfTransBranchNote.addValidator(new StringLengthValidator("Tối đa 250 ký tự", 0, 250, true));

		/*
		 * --<< DON VI DA NHAN THE TU MK
		 * --->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->
		 * >--->>--->>--->>--->>--->>--->>--->>--->>
		 */
		chbTransBranchRec = fieldGroup.buildAndBind("Đơn Vị Đã Nhận Thẻ", "transBranchRec", CheckBox.class);
		chbTransBranchRec.setImmediate(true);
		chbTransBranchRec.setVisible(gciCrdDetail.getTransBranchLock());
		chbTransBranchRec.setVisible(gciCrdDetail.getTransBranchIscheck());

		// Them validate khi chua chon chuyen the ve don vi
		chbTransBranchRec.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				if ((boolean) value) {
					if (!(boolean) chbTransBranch.getValue()) {
						chbTransBranchRec.setValue(false);
						throw new InvalidValueException("Chưa giao thẻ về cho đơn vị");
					}
				}
			}
		});
		chbTransBranchRec.addValueChangeListener(chbTransBranchRecEvent -> {
			if ((boolean) chbTransBranchRecEvent.getProperty().getValue()) {
				dfTransBranchRecDate.setVisible(true);
			} else {
				dfTransBranchRecDate.setVisible(false);
			}
		});
		dfTransBranchRecDate = new DateField("Ngày nhận");
		dfTransBranchRecDate.setVisible(chbTransBranchRec.getValue());
		dfTransBranchRecDate.setDateFormat("dd/MM/yyyy");
		dfTransBranchRecDate.setResolution(Resolution.DAY);
		dfTransBranchRecDate.setRangeEnd(maxDate);
		dfTransBranchRecDate.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				Date date = (Date) value;
				if (dtTransMkDate.getValue() != null) {
					if (date.before(dtTransMkDate.getValue())) {
						throw new InvalidValueException(
								"Ngày đơn vị nhận thẻ không được trước ngày chuyển thẻ cho CTY MKS");
					}
				}
			}
		});
		/*
		 * -- << GIAO THE CHO KHACH HANG
		 * --->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->
		 * >--->>--->>--->>--->>--->>--->>--->>--->>
		 */

		chbTransCust = fieldGroup.buildAndBind("Giao thẻ khách hàng", "transCust", CheckBox.class);
		final boolean isTransCust = gciCrdDetail.getTransCustLock();
		if (isTransCust) {
			chbTransCust.setReadOnly(isTransCust);
			// dfTransCustDate.setReadOnly(isTransCust);
		}

		if ((gciCrdDetail.isTransEmpLock1() && !gciCrdDetail.isTransEmpIscheck1())
				|| (gciCrdDetail.isTransEmpLock2() && !gciCrdDetail.isTransEmpIscheck2())
				|| (gciCrdDetail.isTransEmpLock3() && !gciCrdDetail.isTransEmpIscheck3())) {
			chbTransCust.setReadOnly(true);
		} else
			chbTransCust.setReadOnly(false);

		chbTransCust.setVisible(gciCrdDetail.getTransEmpIscheck());

		chbTransCust.addValueChangeListener(flTransCustEvent -> {
			dfTransCustDate.setVisible((boolean) flTransCustEvent.getProperty().getValue());
		});
		chbTransCust.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				if ((boolean) value) {
					if (!(boolean) chbTransEmp.getValue()) {
						chbTransCust.setValue(false);
						throw new InvalidValueException("Chưa chuyển thẻ cho nhân viên");
					}
				}
			}
		});
		// Khoi tao date field TransEmpDate
		dfTransCustDate = new DateField("Ngày hoàn thành");
		dfTransCustDate.setDateFormat("dd/MM/yyyy");
		dfTransCustDate.setResolution(Resolution.DAY);
		dfTransCustDate.setRangeEnd(maxDate);
		dfTransCustDate.setVisible(chbTransCust.getValue());
		dfTransCustDate.addValidator(new Validator() {
			private static final long serialVersionUID = 1L;

			@Override
			public void validate(Object value) throws InvalidValueException {
				Date date = (Date) value;
				if (date.before(dfTransEmpDate.getValue())) {
					throw new InvalidValueException(
							"Ngày giao thẻ cho khách hàng không được trước ngày giao thẻ cho nhân viên");
				}
			}
		});
		/*--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->> */

		/*
		 * Xu ly load du lieu ngay hoan thanh tu database neu khong co du lieu
		 * se hien thi mac dinh ngay hien tai
		 */
		try {
			if (gciCrdDetail.getTransMkDate() == null) {
				dtTransMkDate.setValue(new Date());
			} else {
				dtTransMkDate.setValue(dateFormat.parse(gciCrdDetail.getTransMkDate()));
				txfTransBranchFwBrch.setValue(gciCrdDetail.getFwBrnCde());
				;
				txfTransBranchNote.setValue(gciCrdDetail.getTransBranchNote());
				;
			}

			if (gciCrdDetail.getTransBranchRecDate() == null) {
				dfTransBranchRecDate.setValue(new Date());
			} else {
				dfTransBranchRecDate.setValue(dateFormat.parse(gciCrdDetail.getTransBranchRecDate()));
			}

			if (gciCrdDetail.getTransCustDate() == null) {
				dfTransCustDate.setValue(new Date());
			} else {
				dfTransCustDate.setValue(dateFormat.parse(gciCrdDetail.getTransCustDate()));
			}
		} catch (ReadOnlyException | ConversionException | ParseException e1) {
			LOGGER.error("Load du lieu tu database den datefield - Message: " + e1.getMessage());
		}
		/*--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->> */

		// Khong cho dieu chinh giao the cho CT MK neu da bi khoa
		final boolean isTransMk = gciCrdDetail.getTransMkLock();
		chbTransMk.setReadOnly(isTransMk);
		dtTransMkDate.setReadOnly(isTransMk);
		txfTransBranchFwBrch.setReadOnly(isTransMk);
		txfTransBranchNote.setReadOnly(isTransMk);

		// Khong cho dieu chinh don vi nhan the khi da bi khoa
		final boolean isTransBranchRec = gciCrdDetail.getTransBranchRecLock();
		chbTransBranchRec.setReadOnly(isTransBranchRec);
		dfTransBranchRecDate.setReadOnly(isTransBranchRec);

		txFilePin = fieldGroup.buildAndBind("filePin", "filePin", TextField.class);
		txFilePin.setVisible(false);

		// Khoi tao nut save
		final Button btSave = new Button("Cập nhật");
		btSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btSave.setIcon(FontAwesome.SAVE);
		btSave.setClickShortcut(KeyCode.ENTER);
		btSave.addClickListener(btSaveEvt -> {
			cmdButtonSave();
		});

		btSave.setVisible(SecurityUtils.hasRole("ROLE_HO") || SecurityUtils.hasRole("ROLE_DONVI")
				|| SecurityUtils.hasRole("ROLE_SUPERADMIN"));

		formLayout.addComponent(flCifNo);
		formLayout.addComponent(flCustName);
		formLayout.addComponent(tfPanMask);
		formLayout.addComponent(flCrdType);
		formLayout.addComponent(flCrdPrinSupp);
		formLayout.addComponent(flIssueDate);
		formLayout.addComponent(flIssueType);

		// Xu ly phan quyen theo role ho, donvi, checker
		if (SecurityUtils.hasRole("ROLE_HOCHECKER") || SecurityUtils.hasRole("ROLE_HO")
				|| SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			formLayout.addComponent(chbTransMk);
			formLayout.addComponent(dtTransMkDate);
			formLayout.addComponent(txfTransBranchFwBrch);
			formLayout.addComponent(txfTransBranchNote);
		}

		if (SecurityUtils.hasRole("ROLE_DONVICHECKER") || SecurityUtils.hasRole("ROLE_DONVI")
				|| SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			formLayout.addComponent(chbTransBranchRec);
			formLayout.addComponent(dfTransBranchRecDate);
		}

		if (SecurityUtils.hasRole("ROLE_DONVICHECKER") || SecurityUtils.hasRole("ROLE_DONVI")
				|| SecurityUtils.hasRole("ROLE_SUPERADMIN")) {
			formLayout.addComponent(chbTransCust);
			dfTransCustDate.setReadOnly(isTransMk);
			formLayout.addComponent(dfTransCustDate);
		}

		Label lblTinhTang = new Label("-------- TÌNH TRẠNG GIAO THẺ KHÁCH HÀNG --------");
		if (fCustStatus_check != null) {
			lblTinhTang.setStyleName("lblTinhTang");
			formLayout.addComponent(lblTinhTang);
		}
		lbl_transCustStatus.setStyleName("lblTinhTang1");
		formLayout.addComponent(lbl_transCustStatus);

		String fCustNote_check = fCustNote.toString();
		if (fCustNote_check != null) {
			fCustNote.setWidth("350");
			fCustNote.setStyleName("lblTinhTang1");
			formLayout.addComponent(fCustNote);
		}

		btSave.setVisible(true);
		formLayout.addComponent(btSave);
		/*
		 * --->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->>--->
		 * >--->>--->>--->>--->>--->>--->>--->>--->>
		 */

		// Khoi tao cua so, hien thi form dieu chinh
		final Window window = new Window("Điều Chỉnh Thông Tin");
		window.center();
		window.setModal(true);
		window.setContent(formLayout);
		window.setWidth(40f, Unit.PERCENTAGE);
		window.setHeight(80f, Unit.PERCENTAGE);
		window.addCloseListener(closeEvent -> {
			getUI().removeWindow(window);
		});
		getUI().getWindows().forEach(s -> {
			getUI().removeWindow(s);
		});
		getUI().addWindow(window);
	}

	public void windowFormTransFileMK(String sType) {
		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(true);

		DateField dfTransFileMK = new DateField("Từ ngày");
		dfTransFileMK.setDateFormat("dd/MM/yyyy");
		dfTransFileMK.setWidth("250");
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
		String sDate = dateFormat.format(Calendar.getInstance().getTime());
		try {
			Date date = dateFormat.parse(sDate);
			dfTransFileMK.setValue(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Button: "Cập nhật"
		final Button btSave = new Button("Cập nhật");
		btSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btSave.setIcon(FontAwesome.SAVE);
		btSave.setClickShortcut(KeyCode.ENTER);
		btSave.addClickListener(btSaveEvt -> {

			String realDateTransFileMK = dateFormat1.format(dfTransFileMK.getValue());
			cmdUpdateData_ContextMenu(sType, realDateTransFileMK);
			getUI().getWindows().forEach(s -> {
				getUI().removeWindow(s);
			});
		});

		//

		formLayout.addComponent(dfTransFileMK);
		formLayout.addComponent(btSave);

		// Khoi tao cua so, hien thi form dieu chinh
		final Window window = new Window("Chọn ngày hoàn thành");
		window.center();
		window.setModal(true);
		window.setContent(formLayout);
		window.setWidth(40f, Unit.PERCENTAGE);
		window.setHeight(20f, Unit.PERCENTAGE);
		window.addCloseListener(closeEvent -> {
			getUI().removeWindow(window);
		});

		getUI().getWindows().forEach(s -> {
			getUI().removeWindow(s);
		});
		getUI().addWindow(window);
	}

	// Popup giao cap nhat thong tin giao the khach hang => Danh cho user nhap
	// DONVI
	public void transCustPopupDialog(String sType) {
		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(true);

		txtNoteTranCust = new TextField("");
		txtNoteTranCust.setWidth("400");
		txtNoteTranCust.setMaxLength(100);
		txtNoteTranCust.setInputPrompt("Ghi chú...");
		txtNoteTranCust.addValidator(new StringLengthValidator(
				messageResource.getMessage("login.txtfield.password.validate.null"), 3, 50, false));
		txtNoteTranCust.setValidationVisible(false);

		cbxTransCustOption = new ComboBox("Trạng thái");
		cbxTransCustOption.setWidth("200");
		cbxTransCustOption.addItems("00", "01", "02", "03", "04", "05", "06");
		cbxTransCustOption.setItemCaption("00", "");
		cbxTransCustOption.setItemCaption("01", "Giao thẻ thành công");
		cbxTransCustOption.setItemCaption("02", "KH từ chối nhận thẻ");
		cbxTransCustOption.setItemCaption("03", "KH hẹn đến nhận");
		cbxTransCustOption.setItemCaption("04", "Không liên lạc được");
		cbxTransCustOption.setItemCaption("05", "Đã chuyển đối tác chuyển phát thẻ");
		cbxTransCustOption.setItemCaption("06", "Khác");
		cbxTransCustOption.setValue(cbxTransCustOption.getItemIds().iterator().next());
		cbxTransCustOption.setNullSelectionAllowed(false);

		DateField dfTransFileMK = new DateField("Từ ngày");
		dfTransFileMK.setDateFormat("dd/MM/yyyy");
		dfTransFileMK.setWidth("200");
		dfTransFileMK.setEnabled(false);
		// dfTransFileMK.setReadOnly(true);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
		String sDate = dateFormat.format(Calendar.getInstance().getTime());
		try {
			Date date = dateFormat.parse(sDate);
			dfTransFileMK.setValue(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Button: "Cập nhật"
		final Button btSave = new Button("Cập nhật");
		btSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btSave.setIcon(FontAwesome.SAVE);
		btSave.setClickShortcut(KeyCode.ENTER);
		btSave.addClickListener(btSaveEvt -> {
			String keyStatus = String.valueOf(cbxTransCustOption.getValue());
			String NoteStatus = txtNoteTranCust.getValue();
			txtNoteTranCust.setValidationVisible(false);
			if (!keyStatus.equals("00") && !keyStatus.equals("01") && !keyStatus.equals("02")
					&& NoteStatus.equals("")) {
				try {
					txtNoteTranCust.validate();
				} catch (InvalidValueException e) {
					txtNoteTranCust.setValidationVisible(true);
					return;
				}
			}

			String realDateTransFileMK = dateFormat1.format(dfTransFileMK.getValue());
			cmdUpdateData_ContextMenu_TransCust(sType, realDateTransFileMK, keyStatus, NoteStatus);
			getUI().getWindows().forEach(s -> {
				getUI().removeWindow(s);
			});
		});

		//

		formLayout.addComponent(dfTransFileMK);
		formLayout.addComponent(cbxTransCustOption);
		formLayout.addComponent(txtNoteTranCust);
		formLayout.addComponent(btSave);

		// Khoi tao cua so, hien thi form dieu chinh
		final Window window = new Window("Giao thẻ khách hàng");
		window.center();
		window.setModal(true);
		window.setContent(formLayout);
		window.setWidth(40f, Unit.PERCENTAGE);
		window.setHeight(30f, Unit.PERCENTAGE);
		window.addCloseListener(closeEvent -> {
			getUI().removeWindow(window);
		});

		getUI().getWindows().forEach(s -> {
			getUI().removeWindow(s);
		});
		getUI().addWindow(window);
	}

	// Popup giao cap nhat thong tin GIAO THE TAN NOI => Danh cho user nhap
	// DONVI
	public void gttn_PopupDialog(String sType) {
		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(true);

		txtNoteTranCust = new TextField("");
		txtNoteTranCust.setWidth("400");
		txtNoteTranCust.setMaxLength(100);
		txtNoteTranCust.setInputPrompt("Ghi chú...");
		txtNoteTranCust.addValidator(new StringLengthValidator(
				messageResource.getMessage("login.txtfield.password.validate.null"), 3, 50, false));
		txtNoteTranCust.setValidationVisible(false);

		cbxTransCustOption = new ComboBox("Trạng thái");
		cbxTransCustOption.setWidth("200");
		cbxTransCustOption.addItems("00", "01");
		cbxTransCustOption.setItemCaption("00", "");
		cbxTransCustOption.setItemCaption("01", "Xác nhận GTTN thành công");
		cbxTransCustOption.setValue(cbxTransCustOption.getItemIds().iterator().next());
		cbxTransCustOption.setNullSelectionAllowed(false);

		DateField dfTransFileMK = new DateField("Từ ngày");
		dfTransFileMK.setDateFormat("dd/MM/yyyy");
		dfTransFileMK.setWidth("200");
		dfTransFileMK.setEnabled(false);
		// dfTransFileMK.setReadOnly(true);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat dateFormat1 = new SimpleDateFormat("yyyyMMdd");
		String sDate = dateFormat.format(Calendar.getInstance().getTime());
		try {
			Date date = dateFormat.parse(sDate);
			dfTransFileMK.setValue(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// Button: "Cập nhật"
		final Button btSave = new Button("Cập nhật");
		btSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btSave.setIcon(FontAwesome.SAVE);
		btSave.setClickShortcut(KeyCode.ENTER);
		btSave.addClickListener(btSaveEvt -> {
			String keyStatus = String.valueOf(cbxTransCustOption.getValue());
			// String NoteStatus = txtNoteTranCust.getValue();
			// txtNoteTranCust.setValidationVisible(false);
			// if (!keyStatus.equals("00") && !keyStatus.equals("01") &&
			// !keyStatus.equals("02")
			// && NoteStatus.equals("")) {
			// try {
			// txtNoteTranCust.validate();
			// } catch (InvalidValueException e) {
			// txtNoteTranCust.setValidationVisible(true);
			// return;
			// }
			// }

			String realDateTransFileMK = dateFormat1.format(dfTransFileMK.getValue());
			cmdUpdateData_ContextMenu_GTTN(sType, realDateTransFileMK, keyStatus);
			getUI().getWindows().forEach(s -> {
				getUI().removeWindow(s);
			});
		});

		//

		formLayout.addComponent(dfTransFileMK);
		formLayout.addComponent(cbxTransCustOption);
		// formLayout.addComponent(txtNoteTranCust);
		formLayout.addComponent(btSave);

		// Khoi tao cua so, hien thi form dieu chinh
		final Window window = new Window("Giao Thẻ Tận Nơi");
		window.center();
		window.setModal(true);
		window.setContent(formLayout);
		window.setWidth(40f, Unit.PERCENTAGE);
		window.setHeight(30f, Unit.PERCENTAGE);
		window.addCloseListener(closeEvent -> {
			getUI().removeWindow(window);
		});

		getUI().getWindows().forEach(s -> {
			getUI().removeWindow(s);
		});
		getUI().addWindow(window);
	}

	public void confirmDialog_ButtonLock() {
		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(false);
		formLayout.setStyleName("confirmDialog");

		final HorizontalLayout hLayoutConfirm = new HorizontalLayout();
		final FormLayout leftLayoutConfirm = new FormLayout();
		leftLayoutConfirm.setSpacing(true);

		final FormLayout rightLayoutConfirm = new FormLayout();
		rightLayoutConfirm.setSpacing(true);

		final Button btConfirm = new Button("Xác nhận");
		btConfirm.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btConfirm.setIcon(FontAwesome.SAVE);
		btConfirm.setClickShortcut(KeyCode.ENTER);
		btConfirm.addClickListener(btSaveEvt -> {
			cmdLockData_Button();
			// getUI().getWindows().forEach(s -> {
			// getUI().removeWindow(s);
			// });
		});

		final Button btCancel = new Button("   Hủy   ");
		btCancel.setStyleName(ValoTheme.BUTTON_DANGER);
		btCancel.setIcon(FontAwesome.SAVE);
		btCancel.setClickShortcut(KeyCode.ENTER);
		btCancel.addClickListener(btSaveEvt -> {

			getUI().getWindows().forEach(s -> {
				getUI().removeWindow(s);
			});
		});

		leftLayoutConfirm.addComponent(btConfirm);
		rightLayoutConfirm.addComponent(btCancel);

		hLayoutConfirm.addComponent(leftLayoutConfirm);
		hLayoutConfirm.addComponent(rightLayoutConfirm);

		formLayout.addComponent(hLayoutConfirm);

		// Khoi tao cua so, hien thi form dieu chinh

		windowConfirm.center();
		windowConfirm.setModal(true);
		windowConfirm.setContent(formLayout);
		windowConfirm.setStyleName("confirmDialog");
		windowConfirm.addCloseListener(closeEvent -> {
			getUI().removeWindow(windowConfirm);
		});

		getUI().getWindows().forEach(s -> {
			getUI().removeWindow(s);
		});
		getUI().addWindow(windowConfirm);
	}

	public void confirmDialog_ButtonCheck() {
		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(false);
		formLayout.setStyleName("confirmDialog");

		final HorizontalLayout hLayoutConfirm = new HorizontalLayout();
		final FormLayout leftLayoutConfirm = new FormLayout();
		leftLayoutConfirm.setSpacing(true);

		final FormLayout rightLayoutConfirm = new FormLayout();
		rightLayoutConfirm.setSpacing(true);

		final Button btConfirm = new Button("Xác nhận");
		btConfirm.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btConfirm.setIcon(FontAwesome.SAVE);
		btConfirm.setClickShortcut(KeyCode.ENTER);
		btConfirm.addClickListener(btSaveEvt -> {
			cmdCheckData_Button();
			// getUI().getWindows().forEach(s -> {
			// getUI().removeWindow(s);
			// });
		});

		final Button btCancel = new Button("   Hủy   ");
		btCancel.setStyleName(ValoTheme.BUTTON_DANGER);
		btCancel.setIcon(FontAwesome.SAVE);
		btCancel.setClickShortcut(KeyCode.ENTER);
		btCancel.addClickListener(btSaveEvt -> {

			getUI().getWindows().forEach(s -> {
				getUI().removeWindow(s);
			});
		});

		leftLayoutConfirm.addComponent(btConfirm);
		rightLayoutConfirm.addComponent(btCancel);

		hLayoutConfirm.addComponent(leftLayoutConfirm);
		hLayoutConfirm.addComponent(rightLayoutConfirm);

		formLayout.addComponent(hLayoutConfirm);

		// Khoi tao cua so, hien thi form dieu chinh

		windowConfirm.center();
		windowConfirm.setModal(true);
		windowConfirm.setContent(formLayout);
		windowConfirm.setStyleName("confirmDialog");
		windowConfirm.addCloseListener(closeEvent -> {
			getUI().removeWindow(windowConfirm);
		});

		getUI().getWindows().forEach(s -> {
			getUI().removeWindow(s);
		});
		getUI().addWindow(windowConfirm);
	}

	public void confirmDialog_ButtonReject() {
		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(false);
		formLayout.setStyleName("confirmDialog");

		final HorizontalLayout hLayoutConfirm = new HorizontalLayout();
		final FormLayout leftLayoutConfirm = new FormLayout();
		leftLayoutConfirm.setSpacing(true);

		final FormLayout rightLayoutConfirm = new FormLayout();
		rightLayoutConfirm.setSpacing(true);

		final Button btConfirm = new Button("Xác nhận");
		btConfirm.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btConfirm.setIcon(FontAwesome.SAVE);
		btConfirm.setClickShortcut(KeyCode.ENTER);
		btConfirm.addClickListener(btSaveEvt -> {
			cmdUnLockData_Button();
			// getUI().getWindows().forEach(s -> {
			// getUI().removeWindow(s);
			// });
		});

		final Button btCancel = new Button("   Hủy   ");
		btCancel.setStyleName(ValoTheme.BUTTON_DANGER);
		btCancel.setIcon(FontAwesome.SAVE);
		btCancel.setClickShortcut(KeyCode.ENTER);
		btCancel.addClickListener(btSaveEvt -> {

			getUI().getWindows().forEach(s -> {
				getUI().removeWindow(s);
			});
		});

		leftLayoutConfirm.addComponent(btConfirm);
		rightLayoutConfirm.addComponent(btCancel);

		hLayoutConfirm.addComponent(leftLayoutConfirm);
		hLayoutConfirm.addComponent(rightLayoutConfirm);

		formLayout.addComponent(hLayoutConfirm);

		// Khoi tao cua so, hien thi form dieu chinh

		windowConfirm.center();
		windowConfirm.setModal(true);
		windowConfirm.setContent(formLayout);
		windowConfirm.setStyleName("confirmDialog");
		windowConfirm.addCloseListener(closeEvent -> {
			getUI().removeWindow(windowConfirm);
		});

		getUI().getWindows().forEach(s -> {
			getUI().removeWindow(s);
		});
		getUI().addWindow(windowConfirm);
	}

	/**
	 * Xu ly luu du lieu tu form
	 */
	private CommitHandler commitHandler_EditForm() {
		return new CommitHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void preCommit(CommitEvent commitEvent) throws CommitException {
			}

			@Override
			public void postCommit(CommitEvent commitEvent) throws CommitException {
				try {
					/* Lay du lieu tu form */
					final boolean bolTransMk = (Boolean) commitEvent.getFieldBinder().getField("transMk").getValue();
					// final boolean bolRecMk = (Boolean)
					// commitEvent.getFieldBinder().getField("recMk").getValue();
					final boolean bolRecMk = false;
					// final boolean bolTransBranch = (Boolean)
					// commitEvent.getFieldBinder().getField("transBranch").getValue();
					final boolean bolTransBranch = false;
					final boolean bolTransBranchRec = (Boolean) commitEvent.getFieldBinder().getField("transBranchRec")
							.getValue();
					// final boolean bolTransEmp = (Boolean)
					// commitEvent.getFieldBinder().getField("transEmp").getValue();
					final boolean bolTransEmp = false;
					// final boolean bolTransEmpRender = (Boolean)
					// commitEvent.getFieldBinder().getField("transEmpIsRender").getValue();
					final boolean bolTransEmpRender = false;
					// final boolean bolTransEmpRender1 = (Boolean)
					// commitEvent.getFieldBinder().getField("transEmpIsRender1").getValue();
					final boolean bolTransEmpRender1 = false;
					// final boolean bolTransEmpRender2 = (Boolean)
					// commitEvent.getFieldBinder().getField("transEmpIsRender2").getValue();
					final boolean bolTransEmpRender2 = false;
					// final boolean bolTransEmpRenderLock1 = (Boolean)
					// commitEvent.getFieldBinder().getField("transEmpLock1").getValue();
					final boolean bolTransEmpRenderLock1 = false;
					// final boolean bolTransEmpRenderLock2 = (Boolean)
					// commitEvent.getFieldBinder().getField("transEmpLock2").getValue();
					final boolean bolTransEmpRenderLock2 = false;
					// final boolean bolTransEmpRenderLock3 = (Boolean)
					// commitEvent.getFieldBinder().getField("transEmpLock3").getValue();
					final boolean bolTransEmpRenderLock3 = false;
					final boolean bolTransCust = (Boolean) commitEvent.getFieldBinder().getField("transCust")
							.getValue();

					final String sCifNo = (String) commitEvent.getFieldBinder().getField("cifNo").getValue();
					final String sCustName = (String) commitEvent.getFieldBinder().getField("custName").getValue();
					final String sCrdType = (String) commitEvent.getFieldBinder().getField("crdType").getValue();
					final String sPrinSupp = (String) commitEvent.getFieldBinder().getField("crdPrinSupp").getValue();
					final String sIssueType = (String) commitEvent.getFieldBinder().getField("issueType").getValue();
					final String sIssueDate = (String) commitEvent.getFieldBinder().getField("issueDate").getValue();
					final String sPan = (String) commitEvent.getFieldBinder().getField("pan").getValue();
					// final String sTransBranchEmpCode = (String)
					// commitEvent.getFieldBinder().getField("transBranchEmpCode").getValue();
					final String sTransBranchEmpCode = "";
					// final String sTransBranchEmpName = (String)
					// commitEvent.getFieldBinder().getField("transBranchEmpName").getValue();
					final String sTransBranchEmpName = "";
					final String sTransBranchFwCde = (String) commitEvent.getFieldBinder().getField("fwBrnCde")
							.getValue();
					final String sTransBranchNote = (String) commitEvent.getFieldBinder().getField("transBranchNote")
							.getValue();

					String sTransEmpCode = "";
					String sTransEmpName = "";
					String sTransEmpNote = "";
					String sTransEmpDate = "";
					String sTransEmpDate1 = "";
					String sTransEmpDate2 = "";
					String sTransEmpRender = "";
					String sTransEmpRender1 = "";
					String sTransEmpRender2 = "";
					String sTransEmpRenderDate = "";
					String sTransEmpRenderDate1 = "";
					String sTransEmpRenderDate2 = "";
					boolean Renderlock1 = false;
					boolean Renderlock2 = false;
					boolean Renderlock3 = false;

					Renderlock1 = bolTransEmpRenderLock1;
					Renderlock2 = bolTransEmpRenderLock2;
					Renderlock3 = bolTransEmpRenderLock3;

					if (bolTransEmp) { // false
						sTransEmpCode = (String) commitEvent.getFieldBinder().getField("transEmpCode").getValue();
						sTransEmpName = (String) commitEvent.getFieldBinder().getField("transEmpName").getValue();
						sTransEmpNote = (String) commitEvent.getFieldBinder().getField("transEmpNote").getValue();
						sTransEmpDate = dateFormat.format(dfTransEmpDate.getValue());
					}

					String sTransCustDate = "";
					String sFilePin = "";
					if (bolTransCust) { // select
						sTransCustDate = dateFormat.format(dfTransCustDate.getValue());
					} else {
						if (commitEvent.getFieldBinder().getField("filePin").getValue() != null) {
							sFilePin = commitEvent.getFieldBinder().getField("filePin").getValue().toString();
							final File file = new File(dirFile + "/" + sFilePin);
							file.delete();
							sFilePin = "";
						}
					}

					if (bolTransEmpRender) { // false
						sTransEmpRenderDate = "";
						sTransEmpRender = (String) commitEvent.getFieldBinder().getField("transEmpRender").getValue();
					}

					if (bolTransEmpRender1) { // false
						sTransEmpDate1 = "";
						sTransEmpRenderDate1 = "";
						sTransEmpRender1 = (String) commitEvent.getFieldBinder().getField("transEmpRender1").getValue();
					}
					if (bolTransEmpRender2) { // false
						sTransEmpDate2 = "";
						sTransEmpRenderDate2 = "";
						sTransEmpRender2 = (String) commitEvent.getFieldBinder().getField("transEmpRender2").getValue();
					}
					final String sTransMkDate = bolTransMk == false ? "" : dateFormat.format(dtTransMkDate.getValue());
					// final String sRecMkDate = bolRecMk == false ? "" :
					// dateFormat.format(dfRecMkDate.getValue());
					final String sRecMkDate = "";
					// final String sTransBranchDate = bolTransBranch == false ?
					// "" : dateFormat.format(dfTransBranchDate.getValue());
					final String sTransBranchDate = "";
					final String sTransBranchRecDate = bolTransBranchRec == false ? ""
							: dateFormat.format(dfTransBranchRecDate.getValue());

					/* Luu du lieu xuong database */
					crdDetService.saveDataFromForm(sCifNo, sCustName, sPan, sCrdType, sPrinSupp, sIssueType, sIssueDate,
							bolTransMk, sTransMkDate, bolRecMk, sRecMkDate, bolTransBranch, sTransBranchDate,
							sTransBranchEmpCode, bolTransEmp, sTransEmpCode, sTransEmpName, sTransEmpDate,

							bolTransEmpRender, sTransEmpRenderDate, sTransEmpRender, Renderlock1,

							sTransEmpDate1, bolTransEmpRender1, sTransEmpRenderDate1, sTransEmpRender1, Renderlock2,

							sTransEmpDate2, bolTransEmpRender2, sTransEmpRenderDate2, sTransEmpRender2, Renderlock3,

							bolTransCust, sTransCustDate, sTransBranchEmpName, sTransBranchFwCde, sTransBranchNote,
							bolTransBranchRec, sTransBranchRecDate, sTransEmpNote, sFilePin);
					/* Ghi log thao tac cua user */
					final StringBuilder strbuilderLog = new StringBuilder(
							SecurityUtils.getUserName() + " - Cap nhat du lieu tu form: ");

					commitEvent.getFieldBinder().getFields().forEach(s -> {
						strbuilderLog.append(String.valueOf(s.getCaption() + ":" + s.getValue()) + "|");
					});
					strbuilderLog.append("transmks:" + sTransMkDate + "|");
					strbuilderLog.append("recmks:" + sRecMkDate + "|");
					strbuilderLog.append("transbranchdate:" + sTransBranchDate + "|");
					strbuilderLog.append("transbranchrecdate:" + sTransBranchRecDate + "|");
					strbuilderLog.append("transempdate:" + sTransEmpDate + "|");
					strbuilderLog.append("transcustdate:" + sTransCustDate);
					LOGGER.info(strbuilderLog.toString());
					/* Lam moi grid */
					getUI().access(new Runnable() {
						@Override
						public void run() {
							initData();
						}
					});
				} catch (NullPointerException ex) {

				}

			}
		};

	}

	// Tao du lieu xuat cho file Excel
	// Tao du lieu xuat cho file Excel
	@SuppressWarnings("unchecked")
	private Table initDataExport() {
		Table table = new Table();
		// table.addContainerProperty("STT", String.class, "");
		table.addContainerProperty("CIF", String.class, "");
		table.addContainerProperty("Loai_The", String.class, "");
		table.addContainerProperty("Ten_Chu_The", String.class, "");
		table.addContainerProperty("Chinh_Phu", String.class, "");
		table.addContainerProperty("So_The", String.class, "");
		table.addContainerProperty("Sale_Officer_Code", String.class, "");
		table.addContainerProperty("Loai_Phat_Hanh", String.class, "");
		table.addContainerProperty("Ngay_Phat_Hanh", String.class, "");
		table.addContainerProperty("Ma_Don_Vi", String.class, "");
		table.addContainerProperty("Chuyen_Don_vi", String.class, "");
		table.addContainerProperty("Ghi_Chú", String.class, "");
		// Chuyen file den MK
		// table.addContainerProperty("trans_mk", String.class, "");
		table.addContainerProperty("Ngay_Chuyen_CTYMK", String.class, "");
		table.addContainerProperty("Khoa_Chuyen_CTYMK", String.class, "");
		table.addContainerProperty("Duyet_Chuyen_CTYMK", String.class, "");
		// Don vi nhan the tu MK
		table.addContainerProperty("Ngay_Don_Vi_Nhan_The", String.class, "");
		table.addContainerProperty("Khoa_Don_Vi_Nhan_The", String.class, "");
		table.addContainerProperty("Duyet_Don_Vi_Nhan_The", String.class, "");
		// Giao the cho KH
		table.addContainerProperty("Ngay_Giao_The_KH", String.class, "");
		table.addContainerProperty("Khoa_Giao_The_KH", String.class, "");
		table.addContainerProperty("Duyet_Giao_The_KH", String.class, "");
		table.addContainerProperty("Trang_Thai_Giao_The_KH", String.class, "");
		table.addContainerProperty("Ghi_Chu_Giao_The_KH", String.class, "");

		idxContainer.getItemIds().forEach(s -> {
			final Item itemid = table.getItem(table.addItem());
			// thong tin the
			// final String sstt =
			// String.valueOf(idxContainer.getContainerProperty(s,
			// "stt").getValue());
			final String scif = String.valueOf(idxContainer.getContainerProperty(s, "cif").getValue());
			final String scrd_brd = String.valueOf(idxContainer.getContainerProperty(s, "crd_brd").getValue());
			final String scust_name = String.valueOf(idxContainer.getContainerProperty(s, "cust_name").getValue());
			final String smain_sub = String.valueOf(idxContainer.getContainerProperty(s, "main_sub").getValue());
			final String span_mask = String.valueOf(idxContainer.getContainerProperty(s, "pan_mask").getValue());
			final String sissue_type = String.valueOf(idxContainer.getContainerProperty(s, "issue_type").getValue());
			final String sissue_date = String.valueOf(idxContainer.getContainerProperty(s, "issue_date").getValue());
			final String sbrch_code = String.valueOf(idxContainer.getContainerProperty(s, "brch_code").getValue());
			final String sSaleofficer_code = String
					.valueOf(idxContainer.getContainerProperty(s, "saleofficer_code").getValue());
			final String fwd_brn = String.valueOf(idxContainer.getContainerProperty(s, "trans_branch_fw").getValue());
			final String note = String.valueOf(idxContainer.getContainerProperty(s, "trans_branch_note").getValue());
			// Chuyen file MK
			final String strans_mk_date = String
					.valueOf(idxContainer.getContainerProperty(s, "trans_mk_date").getValue());
			final boolean btrans_mk_lock = (boolean) idxContainer.getContainerProperty(s, "trans_mk_lock").getValue();
			final boolean btrans_mk_check = (boolean) idxContainer.getContainerProperty(s, "trans_mk_check").getValue();
			// Don vi nhan the
			final String trans_branch_rec_date = String
					.valueOf(idxContainer.getContainerProperty(s, "trans_branch_rec_date").getValue());
			final boolean trans_branch_rec_lock = (boolean) idxContainer
					.getContainerProperty(s, "trans_branch_rec_lock").getValue();
			final boolean trans_branch_rec_check = (boolean) idxContainer
					.getContainerProperty(s, "trans_branch_rec_check").getValue();
			// Giao the KH
			final String strans_cust_date = String
					.valueOf(idxContainer.getContainerProperty(s, "trans_cust_date").getValue());
			final boolean btrans_cust_lock = (boolean) idxContainer.getContainerProperty(s, "trans_cust_lock")
					.getValue();
			final boolean btrans_cust_check = (boolean) idxContainer.getContainerProperty(s, "trans_cust_check")
					.getValue();
			final String strans_cust_status = String
					.valueOf(idxContainer.getContainerProperty(s, "trans_cust_status").getValue());
			final String strans_cust_note = String
					.valueOf(idxContainer.getContainerProperty(s, "trans_cust_note").getValue());

			// itemid.getItemProperty("STT").setValue(sstt);
			itemid.getItemProperty("CIF").setValue(scif);
			itemid.getItemProperty("Loai_The").setValue(scrd_brd);
			itemid.getItemProperty("Ten_Chu_The").setValue(scust_name);
			itemid.getItemProperty("Chinh_Phu").setValue(smain_sub);
			itemid.getItemProperty("So_The").setValue(span_mask);
			itemid.getItemProperty("Sale_Officer_Code").setValue(sSaleofficer_code);
			itemid.getItemProperty("Loai_Phat_Hanh").setValue(sissue_type);
			itemid.getItemProperty("Ngay_Phat_Hanh").setValue(timeConverter.convertStrToDateTime(sissue_date));
			itemid.getItemProperty("Ma_Don_Vi").setValue(sbrch_code);
			itemid.getItemProperty("Chuyen_Don_vi").setValue(fwd_brn);
			itemid.getItemProperty("Ghi_Chú").setValue(note);

			// Chuyen file den cong ty MK
			itemid.getItemProperty("Ngay_Chuyen_CTYMK").setValue(strans_mk_date);
			if (btrans_mk_lock)
				itemid.getItemProperty("Khoa_Chuyen_CTYMK").setValue(LOCKED);
			else
				itemid.getItemProperty("Khoa_Chuyen_CTYMK").setValue(UNLOCKED);
			// ---
			if (btrans_mk_check)
				itemid.getItemProperty("Duyet_Chuyen_CTYMK").setValue(CHECKED);
			else
				itemid.getItemProperty("Duyet_Chuyen_CTYMK").setValue(UNCHECKED);

			// Don vi nhan the
			itemid.getItemProperty("Ngay_Don_Vi_Nhan_The").setValue(trans_branch_rec_date);
			if (trans_branch_rec_lock)
				itemid.getItemProperty("Khoa_Don_Vi_Nhan_The").setValue(LOCKED);
			else
				itemid.getItemProperty("Khoa_Don_Vi_Nhan_The").setValue(UNLOCKED);
			// ---
			if (trans_branch_rec_check)
				itemid.getItemProperty("Duyet_Don_Vi_Nhan_The").setValue(CHECKED);
			else
				itemid.getItemProperty("Duyet_Don_Vi_Nhan_The").setValue(UNCHECKED);

			// Giao the cho khach hang
			itemid.getItemProperty("Ngay_Giao_The_KH").setValue(strans_cust_date);
			if (btrans_cust_lock)
				itemid.getItemProperty("Khoa_Giao_The_KH").setValue(LOCKED);
			else
				itemid.getItemProperty("Khoa_Giao_The_KH").setValue(UNLOCKED);
			// ---
			if (btrans_cust_check)
				itemid.getItemProperty("Duyet_Giao_The_KH").setValue(CHECKED);
			else
				itemid.getItemProperty("Duyet_Giao_The_KH").setValue(UNCHECKED);

			itemid.getItemProperty("Trang_Thai_Giao_The_KH").setValue(strans_cust_status);
			itemid.getItemProperty("Ghi_Chu_Giao_The_KH").setValue(strans_cust_note);
		});
		return table;
	}

	private void cmdButtonSave() {
		try {
			// Kiem tra cac truong da thoa dieu kien chua
			chbTransMk.validate();

			if (chbTransCust.getValue()) {
				dfTransCustDate.validate();
			} else {
				dfTransCustDate.removeAllValidators();
			}

			// Tien hanh cap nhat du lieu
			fieldGroup.commit();

			Notification.show(messageResource.getMessage("notification.updated"), Type.HUMANIZED_MESSAGE);
			// Dong cua so
			getUI().getWindows().forEach(s -> {
				getUI().removeWindow(s);
			});
			grid.deselectAll();
		} catch (InvalidValueException e) {
			txfTransBranchFwBrch.setValidationVisible(true);
		} catch (CommitException e) {
			Notification.show(messageResource.getMessage("notification.update.fail"), Type.ERROR_MESSAGE);
			final StringBuilder strbuilderLog = new StringBuilder(SecurityUtils.getUserName()
					+ "cap nhat tu form edit that bai - Message: " + e.getMessage() + " - ");
			e.getFieldGroup().getFields().forEach(s -> {
				strbuilderLog.append(String.valueOf(s.getCaption() + ":" + s.getValue()) + "|");
			});
			LOGGER.error(strbuilderLog.toString());

		}
	}

	private void downloadFile(final String filename) throws FileNotFoundException {
		File file = new File(dirFile + "/" + filename);
		if (file.exists() && !file.isDirectory()) {
			FileResource rs = new FileResource(file);
			setResource("download", rs);
			ResourceReference rr = ResourceReference.create(rs, this, "download");
			getUI().getPage().open(rr.getURL(), "download");
		} else {
			throw new FileNotFoundException();
		}
	}

	private void showUploadFile(final String id) {
		getUI().getWindows().forEach(s -> {
			getUI().removeWindow(s);
		});
		final FormLayout uploadInfoLayout = new FormLayout();

		final Window window = new Window();
		window.setContent(uploadInfoLayout);
		window.setModal(true);
		window.setWidth(50f, Unit.PERCENTAGE);

		final FileUploader receiver = new FileUploader();
		receiver.setFileStoreDir(dirFile);

		final Upload upload = new Upload("Đính kèm file", receiver);
		upload.setImmediate(false);
		upload.setButtonCaption("Tải File Lên");

		upload.addFinishedListener(evt -> {
			CwstCrdDetail cwstCrdDetail = crdDetService.findOneById(Long.parseLong(id));
			if (cwstCrdDetail.getFilePin() != null) {
				final File file = new File(dirFile + "/" + cwstCrdDetail.getFilePin());
				file.delete();
			}
			cwstCrdDetail.setFilePin(receiver.getFilename());
			crdDetService.update(cwstCrdDetail);
			LOGGER.error(
					SecurityUtils.getUserName() + "  - upload file: " + receiver.getFilename() + " - case id: " + id);
			// Refresh grid
			getUI().access(new Runnable() {
				@Override
				public void run() {
					idxContainer.removeAllItems();
					initData();
					window.close();
				}
			});
		});

		uploadInfoLayout.addComponent(upload);
		upload.addFailedListener(failEvent -> {
			LOGGER.error("Khong the upload file: " + failEvent.getFilename());
		});

		getUI().addWindow(window);

	}

	public void setLblSumResults(String text) {
		lblSumResults.setValue(text);
	}

	/*
	 * Ktra fwd brn co cung brn avoi user thao tac ko hoac fwd brn la rong
	 */
	public boolean checkFwdBrn(String fwdBrn) {
		boolean result = false;
		if (fwdBrn != null && brnUserLogin != null) {
			fwdBrn = fwdBrn.trim();
			brnUserLogin = brnUserLogin.trim();

			if (brnUserLogin.equals("000") || fwdBrn.equals("") || fwdBrn.equals(brnUserLogin))
				result = true;
		}

		return result;
	}
}
