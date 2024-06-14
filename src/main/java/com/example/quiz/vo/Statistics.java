package com.example.quiz.vo;

public class Statistics {  // ���D���έp���G

	private int qId;  // �D�ؽs��
	
	private String qTitle;  // �D�ئW��
	
	private boolean necessary;  // ���D�O�_������
	
	private String option;  // �D�ت��ﶵ
	
	private int count;  // �ﶵ�Q�隸��

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
