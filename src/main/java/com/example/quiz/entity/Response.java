package com.example.quiz.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "response")
public class Response {
	
	// 因為 PK 是 AI(Auto Incremental)，所以要加上此 Annotation
		// strategy : 指的是 AI 的生成策略
		// GenerationType.IDENTITY: 代表 PK 數字由資料庫來自動增加
		// 參閱 PPT spring boot_01基礎 p.25左右
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Id
	@Column(name = "id")
	private int id;
	
	@Column(name = "quiz_id")
	private int quizId;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "phone")
	private String phone;
	
	@Column(name = "email")
	private String email;
	
	@Column(name = "age")
	private int age;
	
	@Column(name = "fillin")
	private String fillin;

	@Column(name = "fillin_date_time") 
	// 【= LocalDateTime.now();】直接塞預設值(現在時間)，下面不須再新增帶有參數的預設建構方法，
	// 在 impl 也就不需透過 .getLocalDateTime 來取得
	private LocalDateTime fillinDateTime = LocalDateTime.now();
	
	public Response() {
		super();
	}

	public Response(int quizId, String name, String phone, String email, int age, String fillin) {
		super();
		this.quizId = quizId;
		this.name = name;
		this.phone = phone;
		this.email = email;
		this.age = age;
		this.fillin = fillin;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getQuizId() {
		return quizId;
	}

	public void setQuizId(int quizId) {
		this.quizId = quizId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public String getFillin() {
		return fillin;
	}

	public void setFillin(String fillin) {
		this.fillin = fillin;
	}

	public LocalDateTime getFillinDateTime() {
		return fillinDateTime;
	}
	 
	
	
}
