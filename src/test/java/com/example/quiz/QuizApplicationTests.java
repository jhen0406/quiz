package com.example.quiz;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class QuizApplicationTests {

	@Test
	public void test3() {
		List<String> list = List.of("A","B","C","D");
		String str = "AABBBBCDDDCCEEEAAB";
		// 計算 A, B, C, D 出現了幾次
		Map<String, Integer> map = new HashMap<>();
		for(String item : list ) {
			String newStr = str.replace(item, "");  // 新的字串以空字串取代item(A)
			int count = str.length() - newStr.length(); // 新字串長度 - 原字串長度 = A 出現的次數
			map.put(item, count);
		}
		System.out.println(map);

	}

}
