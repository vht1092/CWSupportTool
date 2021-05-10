package cwst.com.views;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional.TxType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.data.Item;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import cwst.com.SecurityUtils;
import cwst.com.VNCharacterUtils;
import cwst.com.entities.FullBranch;
import cwst.com.services.BranchInfoService;
import cwst.com.services.FullBranchService;
import cwst.com.services.SysUserService;

@SpringView(name = UpdateBranchView.VIEW_NAME)
public class UpdateBranchView extends VerticalLayout implements View {
	private static final long serialVersionUID = 1L;
	public static final String VIEW_NAME = "cap-nhat-don-vi-ppt";

	private static final Logger LOGGER = LogManager.getLogger(UpdateBranchView.class);

	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private BranchInfoService brnInfoService;
	@Autowired
	private FullBranchService fullBrchService;

	private TextField txBrnCode;
	private TextField txBrnName;
	private TextField txEmpName;
	private TextField txEmpNewID;
	private TextField txEmpPhone;
	private TextField txBrnAddress;
	private TextField txEmail;

	private Button btnUpdate;
	private Button btnCancel;
	private Button btnSave;

	private ComboBox cbxBranch;

	private String brnCodeFirst = "";

	@PostConstruct
	void init() {
		String userInfo = SecurityUtils.getUserName();
		String brnCode = sysUserService.getBrnOfUserDV(userInfo);
		if (brnCode.equals("000")) {
			addComponent(layoutSearchBranch());
		} else {
			brnCodeFirst = brnCode;
		}

		addComponent(layoutBrnInfo());

		LOGGER.info("User " + userInfo + " view Branch " + brnCodeFirst + " info");

		txBrnCode.setReadOnly(false);
		txBrnCode.setValue(brnCodeFirst);
		txBrnCode.setReadOnly(true);

		brnInfoService.findBranchByBrnCode(brnCodeFirst).forEach(s -> {
			txBrnName.setValue(s[0] != null ? s[0].toString() : "");
			txEmpName.setValue(s[2] != null ? s[2].toString() : "");
			txEmpNewID.setValue(s[3] != null ? s[3].toString() : "");
			txEmpPhone.setValue(s[4] != null ? s[4].toString() : "");
			txBrnAddress.setValue(s[1] != null ? s[1].toString() : "");
			txEmail.setValue(s[5] != null ? s[5].toString() : "");
		});

		setReadOnlyData(true);
	}

	@Override
	public void enter(ViewChangeEvent event) {
		// TODO Auto-generated method stub
	}

	private FormLayout layoutSearchBranch() {
		FormLayout search = new FormLayout();
		search.setId("layoutInfo");
		search.setMargin(true);
		search.addStyleName("outlined");

		cbxBranch = new ComboBox("Đơn vị");
		cbxBranch.setWidth("200");

		cbxBranch.addContainerProperty("description", String.class, "");
		cbxBranch.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		cbxBranch.setItemCaptionPropertyId("description");
		cbxBranch.setInputPrompt(null);
		cbxBranch.setNullSelectionAllowed(true);
		List<FullBranch> listBranch = fullBrchService.findAll();

		if (!listBranch.isEmpty()) {
			listBranch.forEach(s -> {
				final Item item = cbxBranch.addItem(s.getBranchCode());
				item.getItemProperty("description")
						.setValue(s.getBranchCode() + " - " + s.getBranchName().toUpperCase());
			});
		}

		cbxBranch.addValueChangeListener(evt -> {
			if (cbxBranch.isEmpty()) {
				LOGGER.info("Empty Branch");
			} else {
				brnCodeFirst = cbxBranch.getValue().toString().replaceAll("\\s", "")
						.substring(cbxBranch.getValue().toString().replaceAll("\\s", "").length() - 3, 3);

				txBrnCode.setReadOnly(false);
				txBrnCode.setValue(brnCodeFirst);
				txBrnCode.setReadOnly(true);

				setReadOnlyData(false);
				brnInfoService.findBranchByBrnCode(brnCodeFirst).forEach(s -> {
					txBrnName.setValue(s[0] != null ? s[0].toString() : "");
					txEmpName.setValue(s[2] != null ? s[2].toString() : "");
					txEmpNewID.setValue(s[3] != null ? s[3].toString() : "");
					txEmpPhone.setValue(s[4] != null ? s[4].toString() : "");
					txBrnAddress.setValue(s[1] != null ? s[1].toString() : "");
					txEmail.setValue(s[5] != null ? s[5].toString() : "");
				});
				setReadOnlyData(true);
				btnUpdate.setVisible(true); // button update hien
				btnSave.setVisible(false); // button save an
				btnCancel.setVisible(false); // button cancel an
			}

		});

		search.addComponent(cbxBranch);

		return search;
	}

	private FormLayout layoutBrnInfo() {
		FormLayout layoutInfo = new FormLayout();
		layoutInfo.setId("layoutInfo");
		layoutInfo.setMargin(true);
		layoutInfo.addStyleName("outlined");
		// layoutInfo.setSizeFull();

		Label lbTitle = new Label("THÔNG TIN ĐƠN VỊ");
		lbTitle.setId("lbHeader");

		Label lbNote = new Label("(Vui lòng nhập thông tin không dấu)");

		txBrnCode = new TextField("Mã đơn vị");
		txBrnCode.setWidth("300");

		txBrnName = new TextField("Tên đơn vị");
		txBrnName.setWidth("300");

		txEmpName = new TextField("Họ và tên");
		txEmpName.setWidth("300");

		txEmpNewID = new TextField("CMND");
		txEmpNewID.setWidth("300");
		// txEmpNewID.addValidator(new RegexpValidator("[-]?[0-9]*\\.?,?[0-9]+",
		// "Số CMND phải là số"));

		txEmpPhone = new TextField("Số điện thoại"); // phone of nhan vien
		txEmpPhone.setWidth("300");
		// txEmpPhone.addValidator(new RegexpValidator("[-]?[0-9]*\\.?,?[0-9]+",
		// "Số Phone phải là số"));

		txEmail = new TextField(); // email of nhan vien
		txEmail.setInputPrompt("Email");
		txEmail.setWidth("300");

		Label lblEmailSCB = new Label(" @scb.com.vn");
		lblEmailSCB.setStyleName("xxx");

		txBrnAddress = new TextField("Địa chỉ"); // dia chi don vi
		txBrnAddress.setWidth("380");

		HorizontalLayout horiButton = new HorizontalLayout();
		HorizontalLayout FieldEmailLayout = new HorizontalLayout();

		horiButton.setSpacing(true);
		btnUpdate = new Button("Chỉnh sửa");
		btnUpdate.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		btnUpdate.addClickListener(eventClickBtnUpdate());

		btnCancel = new Button("Hủy");
		btnCancel.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		btnCancel.setVisible(false);
		btnCancel.addClickListener(eventClickBtnCancel());

		btnSave = new Button("Lưu");
		btnSave.setStyleName(ValoTheme.BUTTON_FRIENDLY);
		btnSave.setVisible(false);
		btnSave.addClickListener(eventClickBtnSave());

		FieldEmailLayout.addComponent(txEmail);
		FieldEmailLayout.addComponent(lblEmailSCB);

		horiButton.addComponents(btnUpdate, btnCancel, btnSave);

		layoutInfo.addComponents(lbTitle, lbNote, txBrnCode, txBrnName, txEmpName, txEmpNewID, txEmpPhone, txBrnAddress,
				FieldEmailLayout, horiButton);
		return layoutInfo;
	}

	private Button.ClickListener eventClickBtnUpdate() {
		return event -> {
			btnUpdate.setVisible(false); // button update an
			btnSave.setVisible(true); // button save hien
			btnCancel.setVisible(true); // button cancel hien
			setReadOnlyData(false); // cho chinh sua
		};
	}

	private Button.ClickListener eventClickBtnSave() {
		return event -> {
			// thanh cong
			btnUpdate.setVisible(true); // button update hien
			btnSave.setVisible(false); // button save an
			btnCancel.setVisible(false); // button cancel an
			setReadOnlyData(true); // ko cho chinh sua

			if (checkData() == false) {
				Notification.show("Vui lòng điền đủ thông tin", Type.WARNING_MESSAGE);
				return;
			}

			String brnCode = txBrnCode.getValue();
			String brnName = txBrnName.getValue();
			String empName = txEmpName.getValue();
			String empNewID = txEmpNewID.getValue();
			String empPhone = txEmpPhone.getValue();
			String brnAddress = txBrnAddress.getValue();
			String email = txEmail.getValue();

			if (checkDataIsNumber(empPhone) == false) {
				Notification.show("Số điện thoại phải là số", Type.WARNING_MESSAGE);
				return;
			}

			if (checkDataIsNumber(empNewID) == false) {
				Notification.show("Số CMND phải là số", Type.WARNING_MESSAGE);
				return;
			}

			brnName = VNCharacterUtils.removeAccent(brnName);
			empName = VNCharacterUtils.removeAccent(empName);
			brnAddress = VNCharacterUtils.removeAccent(brnAddress);
			email = VNCharacterUtils.removeAccent(email);

			if (email.indexOf("@scb.com.vn") == -1)
				email = email + "@scb.com.vn";

			try {
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				String log_userUpdate = SecurityUtils.getUserName() + " update_Branch_Code = " + brnCode
						+ ", empNewID = " + empNewID + ", empPhone = " + empPhone + ", brnAddress: " + brnAddress
						+ ", email: " + email + ". Update_Time: " + dateFormat.format(date);
				LOGGER.info(log_userUpdate);
				brnInfoService.updateBranchByBrnCode(brnCode, brnName, empName, empNewID, empPhone, brnAddress, email);
			} catch (Exception e) {
				LOGGER.info(e.toString());
			}
		};
	}

	private Button.ClickListener eventClickBtnCancel() {
		return event -> {
			btnUpdate.setVisible(true); // button update hien
			btnSave.setVisible(false); // button save an
			btnCancel.setVisible(false); // button cancel an
			setReadOnlyData(true); // ko cho chinh sua
		};
	}

	private void setReadOnlyData(boolean isEdit) {
		txBrnName.setReadOnly(isEdit);
		txEmpName.setReadOnly(isEdit);
		txEmpNewID.setReadOnly(isEdit);
		txEmpPhone.setReadOnly(isEdit);
		txBrnAddress.setReadOnly(isEdit);
		txEmail.setReadOnly(isEdit);
	}

	private boolean checkData() {
		String brnName = txBrnName.getValue();
		String empName = txEmpName.getValue();
		String empNewID = txEmpNewID.getValue();
		String empPhone = txEmpPhone.getValue();
		String brnAddress = txBrnAddress.getValue();
		String email = txEmail.getValue();

		boolean result = true;
		if (brnName == null || brnName.trim().equals(""))
			result = false;
		if (empName == null || empName.trim().equals(""))
			result = false;
		if (empNewID == null || empNewID.trim().equals(""))
			result = false;
		if (empPhone == null || empPhone.trim().equals(""))
			result = false;
		if (brnAddress == null || brnAddress.trim().equals(""))
			result = false;
		if (email == null || email.trim().equals(""))
			result = false;

		return result;
	}

	private boolean checkDataIsNumber(String str) {
		NumberFormat formatter = NumberFormat.getInstance();
		ParsePosition pos = new ParsePosition(0);
		formatter.parse(str, pos);
		return str.length() == pos.getIndex();
	}

}
