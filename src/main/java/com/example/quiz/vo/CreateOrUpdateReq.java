package com.example.quiz.vo;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateOrUpdateReq {

	private int id;
	
	private String name;

	private String description;

	@JsonProperty("start_date")
	private LocalDate startDate;

	@JsonProperty("end_date")
	private LocalDate endDate;

	// 將原本問卷上的問題 questionId、content、type 集中在 questionList 
	@JsonProperty("question_list")
	private List<Question> questionList;
//	@JsonProperty("question_id")
//	private int questionId;
//
//	
//	private String content;
//
//	private String type;
//
//	@JsonProperty("is_necessary")
//	// Boolean 就會變 class，預設值為 null
//	private boolean necessary;

	
	@JsonProperty("is_published")
	private boolean published;

	
	public CreateOrUpdateReq() {
		super();
		// TODO Auto-generated constructor stub
	}

	public CreateOrUpdateReq(String name, String description, LocalDate startDate, LocalDate endDate, List<Question> questionList,
		boolean published) {
	super();
	this.name = name;
	this.description = description;
	this.startDate = startDate;
	this.endDate = endDate;
	this.questionList = questionList;
	this.published = published;
}

	public CreateOrUpdateReq(int id, String name, String description, LocalDate startDate, LocalDate endDate,
			List<Question> questionList, boolean published) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.startDate = startDate;
		this.endDate = endDate;
		this.questionList = questionList;
		this.published = published;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public List<Question> getQuestionList() {
		return questionList;
	}

	public boolean isPublished() {
		return published;
	}

	public int getId() {
		return id;
	}

}
