package com.example.quiz.vo;

public class Statistics {  // 單題的統計結果

	private int qId;  // 題目編號
	
	private String qTitle;  // 題目名稱
	
	private boolean necessary;  // 該題是否為必填
	
	private String option;  // 題目的選項
	
	private int count;  // 選項被選次數

	public Statistics() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Statistics(int qId, String qTitle, boolean necessary, String option, int count) {
		super();
		this.qId = qId;
		this.qTitle = qTitle;
		this.necessary = necessary;
		this.option = option;
		this.count = count;
	}

	public int getqId() {
		return qId;
	}

	public String getqTitle() {
		return qTitle;
	}

	public boolean isNecessary() {
		return necessary;
	}

	public String getOption() {
		return option;
	}

	public int getCount() {
		return count;
	}
	
	
}
