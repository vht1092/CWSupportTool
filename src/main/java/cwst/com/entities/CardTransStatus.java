package cwst.com.entities;

import java.io.Serializable;

public class CardTransStatus implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String maVung;

	private String tenVung;
	
	private String brchCode;
	
	private String brchName;
	
	private int totalCard;
	
	private int slChuaDuyetChuaKichHoat;
	
	private int slChuaDuyetDaKichHoat;
	
	private int slChuaNhapChuaKichHoat;
	
	private int slChuaNhapDaKichHoat;
	
	private int slDaDuyetChuaKichHoat;
	
	private int fromDate;
	
	private int toDate;
	
	

	public CardTransStatus() {
	}



	public String getMaVung() {
		return maVung;
	}



	public void setMaVung(String maVung) {
		this.maVung = maVung;
	}



	public String getTenVung() {
		return tenVung;
	}



	public void setTenVung(String tenVung) {
		this.tenVung = tenVung;
	}



	public String getBrchCode() {
		return brchCode;
	}



	public void setBrchCode(String brchCode) {
		this.brchCode = brchCode;
	}



	public String getBrchName() {
		return brchName;
	}



	public void setBrchName(String brchName) {
		this.brchName = brchName;
	}



	public int getTotalCard() {
		return totalCard;
	}



	public void setTotalCard(int totalCard) {
		this.totalCard = totalCard;
	}



	public int getSlChuaDuyetChuaKichHoat() {
		return slChuaDuyetChuaKichHoat;
	}



	public void setSlChuaDuyetChuaKichHoat(int slChuaDuyetChuaKichHoat) {
		this.slChuaDuyetChuaKichHoat = slChuaDuyetChuaKichHoat;
	}



	public int getSlChuaDuyetDaKichHoat() {
		return slChuaDuyetDaKichHoat;
	}



	public void setSlChuaDuyetDaKichHoat(int slChuaDuyetDaKichHoat) {
		this.slChuaDuyetDaKichHoat = slChuaDuyetDaKichHoat;
	}



	public int getSlChuaNhapChuaKichHoat() {
		return slChuaNhapChuaKichHoat;
	}



	public void setSlChuaNhapChuaKichHoat(int slChuaNhapChuaKichHoat) {
		this.slChuaNhapChuaKichHoat = slChuaNhapChuaKichHoat;
	}



	public int getSlChuaNhapDaKichHoat() {
		return slChuaNhapDaKichHoat;
	}



	public void setSlChuaNhapDaKichHoat(int slChuaNhapDaKichHoat) {
		this.slChuaNhapDaKichHoat = slChuaNhapDaKichHoat;
	}



	public int getSlDaDuyetChuaKichHoat() {
		return slDaDuyetChuaKichHoat;
	}



	public void setSlDaDuyetChuaKichHoat(int slDaDuyetChuaKichHoat) {
		this.slDaDuyetChuaKichHoat = slDaDuyetChuaKichHoat;
	}



	public int getFromDate() {
		return fromDate;
	}



	public void setFromDate(int fromDate) {
		this.fromDate = fromDate;
	}



	public int getToDate() {
		return toDate;
	}



	public void setToDate(int toDate) {
		this.toDate = toDate;
	}
	
	




	
	

}