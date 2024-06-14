package com.example.quiz.vo;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FillinReq {

	@JsonProperty("quiz_id")  // @Jsonproperty 是用來讓該變數轉為 Json 型態(小駝峰變成用底線連接)
	private int quizId;

	private String name;

	private String phone;

	private String email;

	private int age;

	@JsonProperty("fillin_list")
	private List<Fillin> fillinList;

	public FillinReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FillinReq(int quizId, String name, String phone, String email, int age, List<Fillin> fillinList) {
		super();
		this.quizId = quizId;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.age = age;
		this.fillinList = fillinList;
	}

	public int getQuizId() {
		return quizId;
	}

	public String getName() {
		return name;
	}

	public String getPhone() {
		return phone;
	}

	public String getEmail() {
		return email;
	}

	public int getAge() {
		return age;
	}

	public List<Fillin> getFillinList() {
		return fillinList;
	}

	// 要執行帶有參數的建構方法，要建立在也有預設建構方法的前提下
	// Req 的一般建構方法主要功能在於單元測試 (JUnit Test)

}
