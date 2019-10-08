package com.ehr.com;

public class DTO {

	/**총글수*/
	private int totalCnt;
	/**글번호*/
	private int num;
	
	public DTO() {}

	public DTO(int totalCnt, int num) {
		super();
		this.totalCnt = totalCnt;
		this.num = num;
	}

	public int getTotalCnt() {
		return totalCnt;
	}

	public void setTotalCnt(int totalCnt) {
		this.totalCnt = totalCnt;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		return "DTO [totalCnt=" + totalCnt + ", num=" + num + "]";
	}

}
