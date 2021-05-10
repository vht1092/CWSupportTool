package cwst.com.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Item;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.DefaultFieldGroupFieldFactory;
import com.vaadin.data.fieldgroup.FieldGroup.CommitEvent;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.fieldgroup.FieldGroup.CommitHandler;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.data.util.converter.StringToBooleanConverter;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.event.ShortcutAction;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Field;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.MultiSelectionModel;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.TreeTable;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.renderers.HtmlRenderer;
import com.vaadin.ui.themes.ValoTheme;

import cwst.com.SecurityUtils;
import cwst.com.entities.CwstSysUser;
import cwst.com.entities.CwstSysUsrBranch;
import cwst.com.entities.FullBranch;
import cwst.com.services.CwstSysPermissionService;
import cwst.com.services.FullBranchService;
import cwst.com.services.SysUserService;
import cwst.com.services.SysUsrBranchService;

@SpringView(name = UserManagerView.VIEW_NAME)
public class UserManagerView extends VerticalLayout implements View {

	// private static final Logger LOGGER = LoggerFactory.getLogger(UserManagerView.class);
	private static final Logger LOGGER = LogManager.getLogger(UserManagerView.class);
	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "quan-ly-nguoi-dung";
	@Autowired
	private SysUserService userService;
	@Autowired
	private FullBranchService fullBranchService;
	@Autowired
	private SysUsrBranchService sysUsrBranchService;
	@Autowired
	private CwstSysPermissionService cwstSysPermissionService;

	private final ComboBox cboxBranch = new ComboBox("Đơn vị");
	private BeanItemContainer<CwstSysUser> container;
	private BeanItemContainer<FullBranch> containerBranch;
	private BeanFieldGroup<CwstSysUser> formFieldGroup;
	private TextField flUsername;
	private TextField flUserid;
	private Grid gridUser;
	private Grid gridBranch;
	private TreeTable ttable;

	@PostConstruct
	void init() {
		initUI();
	}

	@Override
	public void enter(ViewChangeEvent event) {		
	}

	@SuppressWarnings("unchecked")
	void initUI() {
		setSizeFull();
		setMargin(true);
		setSpacing(true);
		container = new BeanItemContainer<CwstSysUser>(CwstSysUser.class, userService.findAll());
		containerBranch = new BeanItemContainer<FullBranch>(FullBranch.class, fullBranchService.findAll());
		final Label lbTitle = new Label("QUẢN LÝ USER");
		lbTitle.setStyleName(ValoTheme.LABEL_H3);

		final Button btCreateNew = new Button("Thêm mới");
		btCreateNew.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		btCreateNew.setIcon(FontAwesome.PLUS);
		btCreateNew.addClickListener(evt -> {
			showEditForm(null);
		});

		ttable = new TreeTable();

		cboxBranch.addContainerProperty("description", String.class, "");
		cboxBranch.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cboxBranch.setItemCaptionPropertyId("description");
		fullBranchService.findAll().forEach(s -> {
			final Item item = cboxBranch.addItem(s.getBranchCode());
			item.getItemProperty("description").setValue(s.getBranchName());
		});

		// Grid Branch
		gridBranch = new Grid();
		gridBranch.setWidth(90f, Unit.PERCENTAGE);
		gridBranch.setContainerDataSource(containerBranch);
		gridBranch.setSelectionMode(SelectionMode.MULTI);
		gridBranch.setColumns("branchCode", "branchName");
		gridBranch.getColumn("branchCode").setHeaderCaption("Mã đơn vị");
		gridBranch.getColumn("branchName").setHeaderCaption("Tên đơn vị");

		// Grid Users
		gridUser = new Grid();
		gridUser.setContainerDataSource(container);
		gridUser.setSizeFull();
		gridUser.setColumns("username", "fullname", "isLock", "desct");
		gridUser.getColumn("username").setHeaderCaption("Tên đăng nhập");
		gridUser.getColumn("fullname").setHeaderCaption("Họ tên");
		gridUser.getColumn("desct").setHeaderCaption("Mô tả");
		gridUser.getColumn("isLock").setHeaderCaption("Khóa");
		gridUser.getColumn("isLock").setRenderer(new HtmlRenderer(), new StringToBooleanConverter(FontAwesome.LOCK.getHtml(), ""));
		gridUser.addSelectionListener(evt -> {
			if (gridUser.getSelectedRow() != null) {
				final BeanItem<CwstSysUser> beanFdsSysUser = new BeanItem<CwstSysUser>((CwstSysUser) gridUser.getSelectedRow());
				showEditForm(beanFdsSysUser);
			}
		});

		addComponent(lbTitle);
		addComponent(gridUser);
		addComponent(btCreateNew);
	}

	// FORM CAP NHAT USER
	@SuppressWarnings("unchecked")
	private void showEditForm(BeanItem<CwstSysUser> bean) {

		// Dong tat ca cac window dang mo
		getUI().getWindows().forEach(s -> {
			getUI().removeWindow(s);
		});

		final ComboBox cbTypeUser = new ComboBox();
		cbTypeUser.addItem("HO");
		cbTypeUser.addItem("HOCHECKER");
		cbTypeUser.addItem("DONVI");
		cbTypeUser.addItem("DONVICHECKER");
		cbTypeUser.addItem("DVKH");
		cbTypeUser.setNullSelectionAllowed(false);

		gridBranch.deselectAll();
		formFieldGroup = new BeanFieldGroup<CwstSysUser>(CwstSysUser.class);
		formFieldGroup.setBuffered(true);
		formFieldGroup.addCommitHandler(getCommitHandler());
		if (bean != null) {
			formFieldGroup.setItemDataSource(bean);
		} else {
			// CwstSysUser gciSysUser = new CwstSysUser();
			formFieldGroup.setItemDataSource(new CwstSysUser());
		}
		formFieldGroup.setFieldFactory(new DefaultFieldGroupFieldFactory() {
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("rawtypes")
			@Override
			public <T extends Field> T createField(Class<?> type, Class<T> fieldType) {
				if (fieldType.isAssignableFrom(ComboBox.class)) {
					return (T) cbTypeUser;
				}
				return super.createField(type, fieldType);
			}
		});

		flUserid = formFieldGroup.buildAndBind("id", "id", TextField.class);
		flUsername = formFieldGroup.buildAndBind("Tên Đăng Nhập", "username", TextField.class);
		// Them validator truong hop khong nhap truong username
		flUsername.addValidator(new NullValidator("Vui lòng nhập thông tin", false));
		flUsername.setValidationVisible(false);
		flUsername.setNullRepresentation("");
		flUsername.addBlurListener(evt -> {
			if (flUsername.getValue() != null) {
				CwstSysUser user = cmdFormCheckUserExist(flUsername.getValue().toString());
				if (user != null) {
					formFieldGroup.setItemDataSource(user);
				}
				cmdGridView_Selected();
				cmdGridBranch_Selected();
			}
		});
		// Form
		final TextField flFullname = formFieldGroup.buildAndBind("Họ Tên", "fullname", TextField.class);
		flFullname.setNullRepresentation("");
		final TextField flDesc = formFieldGroup.buildAndBind("Mô Tả", "desct", TextField.class);
		flDesc.setNullRepresentation("");
		final CheckBox flIsLock = formFieldGroup.buildAndBind("Khóa", "isLock", CheckBox.class);
		final ComboBox flUsrType = formFieldGroup.buildAndBind("Loại Người Dùng", "usrType", ComboBox.class);

		// Danh sach view
		ttable.addContainerProperty("id", Long.class, "");
		ttable.addContainerProperty("name", String.class, "");
		ttable.addContainerProperty("desc", String.class, "");
		ttable.addContainerProperty("parent", String.class, "");
		ttable.addContainerProperty("view", CheckBox.class, null);
		ttable.addContainerProperty("edit", CheckBox.class, null);
		ttable.addContainerProperty("delete", CheckBox.class, null);
		ttable.addContainerProperty("checker", CheckBox.class, null);
		ttable.setVisibleColumns("desc", "view", "edit", "checker", "delete");
		ttable.setWidth(90f, Unit.PERCENTAGE);
		ttable.setHeight(250f, Unit.PIXELS);
		ttable.setColumnHeader("desc", "Tên Màn Hình");
		ttable.setColumnHeader("view", "Xem");
		ttable.setColumnHeader("checker", "Kiểm soát");
		ttable.setColumnHeader("delete", "Xóa");
		ttable.setColumnHeader("edit", "Điều Chỉnh");

		cmdGridView_Selected();

		final Button btSave = new Button("Lưu");
		btSave.setStyleName(ValoTheme.BUTTON_PRIMARY);
		btSave.setIcon(FontAwesome.SAVE);

		// Su kien button luu
		btSave.addClickListener(evt -> {
			try {
				flUsername.validate();// Kiem tra username co null hay khong
				formFieldGroup.commit();
				// Cap nhat xong thi ban ra thong bao o goc phai ben duoi
				Notification.show("Đã cập nhật", Type.TRAY_NOTIFICATION);
				
				getUI().getWindows().forEach(s -> {
					getUI().removeWindow(s);
				});
			} catch (CommitException | InvalidValueException e) {
				flUsername.setValidationVisible(true);
				LOGGER.error(e.getMessage());
				e.printStackTrace();
			}
		});
		btSave.setClickShortcut(ShortcutAction.KeyCode.ENTER);
		// Lam cho select cac don vi da duoc cap quyen
		cmdGridBranch_Selected();

		final FormLayout formLayout = new FormLayout();
		formLayout.setMargin(true);
		formLayout.addComponent(flUsername);
		formLayout.addComponent(flFullname);
		formLayout.addComponent(flDesc);
		formLayout.addComponent(flUsrType);
		formLayout.addComponent(flIsLock);
		//formLayout.addComponent(ttable);
		formLayout.addComponent(gridBranch);
		formLayout.addComponent(btSave);

		final Window window = new Window();
		window.center();
		window.setModal(true);
		window.setHeight(90f, Unit.PERCENTAGE);
		window.setWidth(70f, Unit.PERCENTAGE);
		window.setContent(formLayout);
		window.addCloseListener(evtclose -> {
			getUI().removeWindow(window);
		});
		getUI().addWindow(window);
		gridUser.deselectAll();
	}

	private CwstSysUser cmdFormCheckUserExist(String username) {
		final CwstSysUser sysUser = userService.findUserByUsername(username);
		return sysUser != null ? sysUser : null;
	}

	private void cmdGridBranch_Selected() {

		if (formFieldGroup.getField("username").getValue() != null) {
			flUsername.setReadOnly(true);
			List<CwstSysUsrBranch> listUserBrach = sysUsrBranchService
					.findAllByUsername(String.valueOf(formFieldGroup.getField("username").getValue()));
			List<FullBranch> listBranch = containerBranch.getItemIds();
			List<FullBranch> temp = new ArrayList<FullBranch>();
			MultiSelectionModel multiselectionModel = (MultiSelectionModel) gridBranch.getSelectionModel();
			for (FullBranch a : listBranch) {
				for (CwstSysUsrBranch b : listUserBrach) {
					if (a.getBranchCode().equals(b.getId().getBranchCode())) {
						temp.add(a);
					}
				}
			}
			getUI().access(new Runnable() {
				@Override
				public void run() {
					multiselectionModel.setSelected(temp);
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	private void cmdGridView_Selected() {
		// Bo dau , dong do texfield vaadin tu dong them vao
		List<Object[]> listViewPer = cwstSysPermissionService.findViewForPermission(Long.parseLong(flUserid.getValue().toString().replace(",", "")));
		// Tao cay
		ttable.removeAllItems();
		listViewPer.forEach(rs -> {
			Object newItemId = ttable.addItem();
			Item row = ttable.getItem(newItemId);

			row.getItemProperty("id").setValue(Long.parseLong(rs[0].toString()));
			row.getItemProperty("name").setValue(rs[1].toString());
			row.getItemProperty("desc").setValue(rs[2].toString());
			row.getItemProperty("parent").setValue(rs[3].toString());
			// Tao view checkbox
			final CheckBox cbView = new CheckBox();
			if (!rs[4].toString().equals("0")) {
				cbView.setValue(true);
			}
			row.getItemProperty("view").setValue(cbView);
			// Tao check checkbox
			final CheckBox cbChecker = new CheckBox();
			if (!rs[5].toString().equals("0")) {
				cbChecker.setValue(true);
			}
			row.getItemProperty("checker").setValue(cbChecker);

			// Tao delete checkbox
			final CheckBox cbDelete = new CheckBox();

			if (!rs[6].toString().equals("0")) {
				cbDelete.setValue(true);
			}
			row.getItemProperty("delete").setValue(cbDelete);
			// Tao edit checkbox
			final CheckBox cbEdit = new CheckBox();
			cbEdit.addValueChangeListener(edtEvent -> {
				if ((Boolean) edtEvent.getProperty().getValue()) {
					cbView.setValue(true);
				}
			});
			if (!rs[7].toString().equals("0")) {
				cbEdit.setValue(true);
			}
			row.getItemProperty("edit").setValue(cbEdit);
		});

	}

	private CommitHandler getCommitHandler() {
		return new CommitHandler() {
			private static final long serialVersionUID = 1L;

			@Override
			public void preCommit(CommitEvent commitEvent) throws CommitException {
			}

			@SuppressWarnings("unchecked")
			@Override
			public void postCommit(CommitEvent commitEvent) throws CommitException {
				final String userName = String.valueOf(commitEvent.getFieldBinder().getField("username").getValue());
				final String fullName = String.valueOf(commitEvent.getFieldBinder().getField("fullname").getValue());
				final String desc = String.valueOf(commitEvent.getFieldBinder().getField("desct").getValue());
				final String type = String.valueOf(commitEvent.getFieldBinder().getField("usrType").getValue());
				final List<String> listBranchCode = new ArrayList<>();

				// Tao lai danh sach don vi duoc truy cap
				gridBranch.getSelectedRows().forEach(s -> {
					final String branchCde = String.valueOf(containerBranch.getItem(s).getItemProperty("branchCode").getValue());
					if (!branchCde.equals("null")) {
						listBranchCode.add(branchCde);
					}
				});

				Boolean active = (Boolean) commitEvent.getFieldBinder().getField("isLock").getValue();
				// Cap nhat vao database
				CwstSysUser cwstSysUser = userService.saveUser(userName, fullName, desc, listBranchCode, active, type);

				// Chay cap nha lai quyen tren man hinh
				// ArayView se luu ten cac view duoc cap quyen
				final Map<Long, Boolean[]> arrView = new HashMap<Long, Boolean[]>();
				ttable.getItemIds().forEach(s -> {
					final CheckBox cbView = (CheckBox) ttable.getContainerProperty(s, "view").getValue();
					final CheckBox cbDel = (CheckBox) ttable.getContainerProperty(s, "delete").getValue();
					final CheckBox cbEdit = (CheckBox) ttable.getContainerProperty(s, "edit").getValue();
					final CheckBox cbCheck = (CheckBox) ttable.getContainerProperty(s, "checker").getValue();
					// {ten id, quyen view, quyen xoa, quyen edit, quyen kiem tra}
					arrView.put((Long) ttable.getContainerProperty(s, "id").getValue(),
							new Boolean[] { cbView.getValue(), cbDel.getValue(), cbEdit.getValue(), cbCheck.getValue() });
				});
				// Xoa tat ca cac quyen dang co
				if (cwstSysUser != null) {
					cwstSysPermissionService.delete(cwstSysUser.getId());
					for (Map.Entry<Long, Boolean[]> entry : arrView.entrySet()) {
						// Neu co cap quyen tren man hinh thi moi tien hanh cap nhat vao database
						if ((boolean) entry.getValue()[0] || (boolean) entry.getValue()[1] || (boolean) entry.getValue()[2]
								|| (boolean) entry.getValue()[3]) {
							cwstSysPermissionService.saveAllPermissionByViewIdAndUserId(cwstSysUser.getId(), entry.getKey(),
									(boolean) entry.getValue()[0], (boolean) entry.getValue()[1], (boolean) entry.getValue()[2],
									(boolean) entry.getValue()[3]);
							LOGGER.info(SecurityUtils.getUserName() + " cap nhat permission - userid:" + cwstSysUser.getId() + "|viewid:"
									+ entry.getKey() + "|view:" + entry.getValue()[0] + "|delete:" + entry.getValue()[1] + "|edit:"
									+ entry.getValue()[2] + "|checker:" + entry.getValue()[3]);
						}
					}
				}

				/* Ghi log thao tac cua user */
				final StringBuilder strbuilderLog = new StringBuilder(SecurityUtils.getUserName() + " - Cap nhat du lieu tu form: ");
				// LOGGER.info(SecurityUtils.getUserName() + " - Cap nhat du lieu tu form - cif:" + sCifNo + " |custname: " + sCustName + "|crdtype: "
				// + sCrdType + "|empname:" + sTransBranchEmpName + "|empcode:" + sTransBranchEmpCode + "|fwbranch:" + sTransBranchFwCde);
				commitEvent.getFieldBinder().getFields().forEach(s -> {
					strbuilderLog.append(String.valueOf(s.getCaption() + ":" + s.getValue()) + "|");
				});
				LOGGER.info(strbuilderLog.toString());

				// Lam tuoi lai danh sach user
				getUI().access(new Runnable() {
					@Override
					public void run() {
						container.removeAllItems();
						container.addAll(userService.findAll());
					}
				});
			}

		};
	}

}
