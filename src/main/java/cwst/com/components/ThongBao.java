package cwst.com.components;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.spring.annotation.SpringComponent;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@UIScope
@SpringComponent
public class ThongBao extends CustomComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ThongBao() {
		// A layout structure used for composition
		Panel panel = new Panel("");
		panel.setWidth("100%");
		VerticalLayout panelContent = new VerticalLayout();// #0072c6
		panel.setContent(panelContent);

		final Label lbHeader = new Label("<div style=\"font-style: normal; box-shadow: 5px 10px 18px #888888; width: 100%; height: 260px; color:red\"><h2 style=\"color: red\"><center>THÔNG BÁO</center></h2><h3 style=\"color: red\">SCB đã triển khai phát hành thẻ Contactless đối với thẻ MC debit Standard (từ ngày 01/11/2018) và thẻ TDQT Visa Platinum CBNV SCB (từ ngày 05/11/2018).</h3>	<h3 style=\"color: red\">Một số thẻ do chọn sai thông số nên P.TNT&NHĐT <span style = \"background-color: yellow\"> không cập nhật thông tin CHUYỂN FILE CTY MKS</span> trên chương trình Phân phối thẻ. P.TNT&NHĐT kính đề nghị Đơn vị thực hiện phát hành mới/cấp lại/gia hạn thẻ cho khách hàng.</h3><p>Thông báo tham khảo:</p><ul><li>Thông báo số 22432/TB-TGĐ.18 ngày 01/11/2018 về việc thông số phát hành thẻ quốc tế SCB trên chương trình Cardworks.</li><li>Thông báo số 22463/TB-TGĐ.18 ngày 01/11/2018 về việc triển khai phát hành thẻ thanh toán quốc tế Mastercard Contactless.</li><li>Thông báo số 22533/TB-TGĐ.18 ngày 05/11/2018 về việc triển khai thử nghiệm thẻ tín dụng quốc tế Visa Contactless cho CBNV SCB.</li><li>Thông báo số 22653/TB-TGĐ.18 ngày 09/11/2018 về việc liên quan công tác phát hành thẻ quốc tế Contactless.</li></ul></div>");
		lbHeader.setContentMode(ContentMode.HTML);
		panelContent.addComponent(lbHeader);
		setCompositionRoot(panel);
		panel.setSizeFull();
		panelContent.setSizeFull();
	}
}
