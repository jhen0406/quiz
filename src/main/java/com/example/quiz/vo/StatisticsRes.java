package com.example.quiz.vo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class StatisticsRes extends BasicRes { // 整張問卷的統計結果，裡面有多個題目

	private String quizName;

	private LocalDate startDate;

	private LocalDate endDate;
	
	private Map<Integer, Map<String, Integer>> countMap;

	private List<Statistics> statisticsList;

	public StatisticsRes() {
		super();
	}

	public StatisticsRes(int statusCode, String message) {
		super(statusCode, message);
	}

	public StatisticsRes(int statusCode, String message,String quizName, LocalDate startDate, LocalDate endDate,
			Map<Integer, Map<String, Integer>> countMap) {
		super(statusCode, message);
		this.quizName = quizName;
		this.startDate = startDate;
		this.endDate = endDate;
		this.countMap = countMap;
	}

	public String getQuizName() {
		return quizName;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public List<Statistics> getStatisticsList() {
		return statisticsList;
	}

	public Map<Integer, Map<String, Integer>> getCountMap() {
		return countMap;
	}

 }
