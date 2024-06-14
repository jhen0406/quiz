package com.example.quiz.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.example.quiz.constants.OptionType;
import com.example.quiz.constants.ResMsg;
import com.example.quiz.entity.Quiz;
import com.example.quiz.repository.QuizDao;
import com.example.quiz.service.ifs.QuizService;
import com.example.quiz.vo.BasicRes;
import com.example.quiz.vo.DeleteReq;
import com.example.quiz.vo.Question;
import com.example.quiz.vo.SearchReq;
import com.example.quiz.vo.SearchRes;
import com.example.quiz.vo.CreateOrUpdateReq;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class QuizServiceImpl implements QuizService {

	@Autowired
	private QuizDao quizDao;

	@Override
	public BasicRes createOrUpdate(CreateOrUpdateReq req) {
		// 檢查參數
		BasicRes checkResult = checkParams(req);
		// checkResult == null 時，表示參數檢查都正確
		// checkResult != null 時，表示檢查到錯誤
		if (checkResult != null) {
			return checkResult; // return 跳出整個方法
		}
		// 因為 Quiz 中的 questions 的資料格式是 String，所以要將 req 的 List<Question> 轉成 String
		// ObjectMapper 可以把物件(類別)轉成 JSON 格式的字串
		ObjectMapper mapper = new ObjectMapper();
		try {
			String questionStr = mapper.writeValueAsString(req.getQuestionList());
			
			// 若 req 中的 id > 0，表示更新已存在的資料；若 id = 0，則表示要新增
			if (req.getId() > 0  ) { // 檢查外部請求有無輸入 id
				// 以下兩種方式擇一
				// 使用方法1，透過 findById，若有資料，就會回傳一整筆的資料(可能資料量會較大)
				// 使用方法2，因為是透過 existsById 來判斷資料是否存在，所以回傳的資料永遠都只會是一個 bit(0 或 1)
				// 方法1. 透過 findById: 若有資料，回傳整筆資料
//				Optional<Quiz> op = quizDao.findById(req.getId());
//				// 判斷是否有資料
//				if (op.isEmpty()) { // op.isEmpty(): 表示沒資料
//					return new BasicRes(ResMsg.UPDATE_ID_NOT_FOUND.getCode(), //
//							ResMsg.UPDATE_ID_NOT_FOUND.getMessage());
//				}
//				Quiz quiz = op.get();  // 取舊值
//				// 設定新值(值從 req 來)
//				// 將 req 中的新值設定到舊的 quiz 中，不設定 id，因為 id 一樣
//				quiz.setName(req.getName());
//				quiz.setDescription(req.getDescription());
//				quiz.setStartDate(req.getStartDate());
//				quiz.setEndDate(req.getEndDate());
//				quiz.setQuestions(questionStr);  // 不能直接 req.getQuestion，因為req裡面的question是物件不是字串
//				quiz.setPublished(req.isPublished());
				// 方法 2: 透過 existsById: 回傳一個 bit 的值
				// 這邊要判斷從 req 帶進來的 id 是否真的有存在於 DB 中
				// 因為若 id 不存在，又不檢查，後續程式碼在呼叫 JPA 的 Save 方法時，會變成新增
				
//				==== 原本的 boo ===
//				boolean boo = quizDao.existsById(req.getId());
//				if (!boo) {  
//				=== 改用匿名類別 ===
				if (!quizDao.existsById(req.getId())) { // !boo 表示資料不存在，可能是被刪掉的 id
					return new BasicRes(ResMsg.UPDATE_ID_NOT_FOUND.getCode(), //
							ResMsg.UPDATE_ID_NOT_FOUND.getMessage());
				}
			}
			// =============================================================================
//			上述一整段 if 程式碼可以縮減成以下這段  ( 兩個 if 判斷由 && 合併成一個 if 判斷)
//			 if(req.getId() > 0 && !quizDao.existsById(req.getId())) {   
//				    return new BasicRes(ResMessage.UPDATE_ID_NOT_FOUND.getCode(), 
//				      ResMessage.UPDATE_ID_NOT_FOUND.getMessage());   
//				   }
			// =============================================================================
	
				// new 一個新的 Quiz，要把 id 放進去，因為是新的物件，要塞入全部的資料
				// 把 id 放進去的原因是，要取得 id，才能更新所有資料
//				quiz = new Quiz(req.getId(), req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(), //
//						questionStr, req.isPublished());

//				quiz.setId(req.getId());
			 // req.getId() = 0 (新增資料)
				// 新增資料不需要 getId，由 AI 自動生成 Id
		
//			Quiz quiz = new Quiz(req.getName(),req.getDescription(),req.getStartDate(),req.getEndDate(), //
//					questionsStr,req.isPublished());
//			quizDao.save(quiz);
			// 因為變數 quiz 只使用一次，因此可以使用匿名類別方式撰寫 ( 不需要變數接 )
			// new Quiz() 中帶入 req.getId() 是 PK，在呼叫 save 時，會先去檢查 PK 是否有存在於 DB 中，
			// 若存在 --> 更新；不存在 --> 新增
			// req 中沒有該欄位時，預設是 0，因為 id 的資料型態是 int
			quizDao.save(new Quiz(req.getId(),req.getName(), req.getDescription(), req.getStartDate(), req.getEndDate(), //
					questionStr, req.isPublished()));
		} catch (JsonProcessingException e) {
			return new BasicRes(ResMsg.JSON_PROCESSING_EXCEPTION.getCode(), //
					ResMsg.JSON_PROCESSING_EXCEPTION.getMessage());
		}

		// checkResult == null，參數正確，return null，繼續執行
//		return null;
		return new BasicRes(ResMsg.SUCCESS.getCode(), //
				ResMsg.SUCCESS.getMessage());
	}

	private BasicRes checkParams(CreateOrUpdateReq req) {
		// 檢查問卷參數
		// StringUtils.hasText(字串): 會檢查字串是否為 null、空字串、全空白字串，若是符合3種其中一項，會回 false
		// 前面加個驚嘆號表示反向的意思，若字串的檢查結果是 false 的話，就會進到 if 的實作區塊
		// !StringUtils.hasText(req.getName()) 等同於 StringUtils.hasText(req.getName()) ==
		// false
		// 有驚嘆號 沒驚嘆號

		// 檢查問卷名稱
		if (!StringUtils.hasText(req.getName())) {
			return new BasicRes(ResMsg.PARAM_QUIZ_NAME_ERROR.getCode(), //
					ResMsg.PARAM_QUIZ_NAME_ERROR.getMessage());
		}
		// 檢查問卷描述
		if (!StringUtils.hasText(req.getDescription())) {
			return new BasicRes(ResMsg.PARAM_DESCRIPTION_ERROR.getCode(), //
					ResMsg.PARAM_DESCRIPTION_ERROR.getMessage());
		}

		// 開始時間不能小於等於當前時間
		// LocalDate.now(): 取得系統當前時間
		// req.getStartDate().isAfter(LocalDate.now()): 若 req 中的開始時間比當前時間晚，會得到 true
		// !req.getStartDate().isAfter(LocalDate.now()): 前面有加驚嘆號，表示會得到相反的結果，就是開始時間
		// 會等於小於當前時間
		// LocalDate.now().isAfter(req.getStartDate()) =
		// !req.getStartDate().isAfter(LocalDate.now())
		if (req.getStartDate() == null || !req.getStartDate().isAfter(LocalDate.now())) {
			return new BasicRes(ResMsg.PARAM_START_DATE_ERROR.getCode(), //
					ResMsg.PARAM_START_DATE_ERROR.getMessage());
		}
		// 程式碼有執行到這行時，表示開始時間一定大於等於當前時間
		// 所以後續檢查結束時間時，只要確定結束時間是大於等於開始時間即可，因為只要結束時間是大於等於開始時間，
		// 就一定會是大於等於當前時間 => 省略 【!req.getEndDate().isAfter(LocalDate.now())】的判斷
		// 開始時間 >= 當前時間；結束時間 >= 開始時間 ==> 結束時間 >= 開始時間 >= 當前時間
		// 1. 結束時間不能小於等於當前時間 2. 結束時間不能小於開始時間
		if (req.getEndDate() == null || req.getEndDate().isBefore(req.getStartDate())) {
			return new BasicRes(ResMsg.PARAM_END_DATE_ERROR.getCode(), //
					ResMsg.PARAM_END_DATE_ERROR.getMessage());
		}
		// 檢查問題參數
		if (CollectionUtils.isEmpty(req.getQuestionList())) {
			return new BasicRes(ResMsg.PARAM_QUESTION_LIST_NOT_FOUND.getCode(), //
					ResMsg.PARAM_QUESTION_LIST_NOT_FOUND.getMessage());
		}
		// 一張問卷可能會有多個問題，所以要逐筆檢查每一題的參數
		for (Question item : req.getQuestionList()) {
			if (item.getId() <= 0) {  // 問卷編號一定從 1 開始
				return new BasicRes(ResMsg.PARAM_QUESTION_ID_ERROR.getCode(), //
						ResMsg.PARAM_QUESTION_ID_ERROR.getMessage());
			}
			if (!StringUtils.hasText(item.getTitle())) {
				return new BasicRes(ResMsg.PARAM_TITLE_ERROR.getCode(), //
						ResMsg.PARAM_TITLE_ERROR.getMessage());
			}

			if (!StringUtils.hasText(item.getType())) {
				return new BasicRes(ResMsg.PARAM_TYPE_ERROR.getCode(), //
						ResMsg.PARAM_TYPE_ERROR.getMessage());
			}
			// 當 option_type 是單選或多選時，options 就不能是空字串
			// 但 option_type 是文字時，options 允許是空字串
			// 以下條件檢查: 當 option_type 是單選或多選時，且 options 是空字串，返回錯誤
			if (item.getType().equals(OptionType.SINGLE_CHOICE.getType())
					|| item.getType().equals(OptionType.MULTI_CHOICE.getType())) {
				if (!StringUtils.hasText(item.getOptions())) {
					return new BasicRes(ResMsg.PARAM_OPTIONS_ERROR.getCode(), //
							ResMsg.PARAM_OPTIONS_ERROR.getMessage());
				}
			}
			// 以下是上述2個 if 合併寫法: ( 條件1 || 條件2 ) && 條件3
			// 第一個 if 條件式 && 第二個 if 條件式
			// 2個條件有被小括號包起來
//			if (item.getType().equals(OptionType.SINGLE_CHOICE.getType())
//					|| item.getType().equals(OptionType.MULTI_CHOICE.getType())
//					&& !StringUtils.hasText(item.getOptions())){
//				return new BasicRes(ResMsg.PARAM_OPTIONS_ERROR.getCode(), //
//						ResMsg.PARAM_OPTIONS_ERROR.getMessage());
//			}

		}
		return null;
	}

	@Override
	public SearchRes search(SearchReq req) {
		String name = req.getName();
		LocalDate start = req.getStartDate();
		LocalDate end = req.getEndDate();
		// 假設 name 是 null 或是全空白的字串，可以視為沒有輸入條件值，就表示要取得全部
		// JPA 的 containing 的方法，條件值是空字串時，會搜尋全部
		// 所以要把 name 的值是 null 或全空白字串時，轉換成空字串
		if (!StringUtils.hasText(name)) {
			name = "";
		}
		// 沒選取開始時間，會自動跳到1970，以搜尋全部資料
		if (start == null) {
			start = LocalDate.of(1970, 1, 1);
		}
		// 沒選取結束時間，會自動跳到2999，以搜尋全部資料
		if (end == null) {
			end = LocalDate.of(2999, 12, 31);
		}
//		List<Quiz> res = quizDao.findByNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(name, start, end);
//		return new SearchRes(ResMsg.SUCCESS.getCode(), ResMsg.SUCCESS.getMessage(),res);
		return new SearchRes(ResMsg.SUCCESS.getCode(), //
				ResMsg.SUCCESS.getMessage(), //
//				以下匿名類別相當於上方兩行
				quizDao.findByNameContainingAndStartDateGreaterThanEqualAndEndDateLessThanEqual(name, start, end));
	}

	@Override
	public BasicRes delete(DeleteReq req) {
		// 檢查參數
		if (!CollectionUtils.isEmpty(req.getIdList())) { // 如果 idList 不是空的
			// 刪除問卷
			try {
				quizDao.deleteAllById(req.getIdList()); // 就可以進行刪除問卷
			} catch (Exception e) {
				// 當 deleteAllById 方法中，id 的值不存在時，JPA 會報錯
				// 因為在刪除之前，JPA 會先搜尋帶入的 id 值，若沒結果就會報錯
				// 由於實際上也沒刪除任何資料，所以就不需要對這個 Exception 做處理
			}
		}
		return new BasicRes(ResMsg.SUCCESS.getCode(), ResMsg.SUCCESS.getMessage()); // 並回復成功訊息
	}

}
