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
		// �p�� A, B, C, D �X�{�F�X��
		Map<String, Integer> map = new HashMap<>();
		for(String item : list ) {
			String newStr = str.replace(item, "");  // �s���r��H�Ŧr����Nitem(A)
			int count = str.length() - newStr.length(); // �s�r����� - ��r����� = A �X�{������
			map.put(item, count);
		}
		System.out.println(map);

	}

}
