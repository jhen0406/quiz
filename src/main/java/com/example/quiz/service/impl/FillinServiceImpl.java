package com.example.quiz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.example.quiz.constants.OptionType;
import com.example.quiz.constants.ResMsg;
import com.example.quiz.entity.Quiz;
import com.example.quiz.entity.Response;
import com.example.quiz.repository.QuizDao;
import com.example.quiz.repository.ResponseDao;
import com.example.quiz.service.ifs.FillinService;
import com.example.quiz.vo.BasicRes;
import com.example.quiz.vo.Feedback;
import com.example.quiz.vo.FeedbackDetail;
import com.example.quiz.vo.FeedbackReq;
import com.example.quiz.vo.FeedbackRes;
import com.example.quiz.vo.Fillin;
import com.example.quiz.vo.FillinReq;
import com.example.quiz.vo.Question;
import com.example.quiz.vo.Statistics;
import com.example.quiz.vo.StatisticsRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class FillinServiceImpl implements FillinService {

	private ObjectMapper mapper = new ObjectMapper();

	@Autowired
	private QuizDao quizDao;

	@Autowired
	private ResponseDao responseDao;

	@Override
	public BasicRes fillin(FillinReq req) {
		// �Ѽ��ˬd
		BasicRes checkResult = checkParams(req);
		if (checkResult != null) {
			return checkResult;
		}
		// �ˬd�P�@�ӹq�ܸ��X�O�_�����ƶ�g�P�@�i�ݨ�
		if (responseDao.existsByQuizIdAndPhone(req.getQuizId(), req.getPhone())) {
			return new BasicRes(ResMsg.DUPLICATED_FILL_IN.getCode(), //
					ResMsg.DUPLICATED_FILL_IN.getMessage());
		}
		// �ˬd quiz_id �O�_�s�b��DB��
		// �]������|��� req �������׻P�D�ت��ﶵ�O�_�ŦX�A�ҥH�n�� findById
		Optional<Quiz> op = quizDao.findById(req.getQuizId());
		if (op.isEmpty()) {
			return new BasicRes(ResMsg.QUIZ_NOT_FOUND.getCode(), ResMsg.QUIZ_NOT_FOUND.getMessage());
		}
		Quiz quiz = op.get();
		// �q quiz �����X questions �r��
		String questionsStr = quiz.getQuestions();
		// �ϥ� ObjectMapper �N questionsStr �ন List<Question>
		// fillinStr �n���Ŧr��A���M�w�]�|�O null
		// �Y fillinStr = null�A������� fillinStr =
		// mapper.writeValueAsString(req.getqIdAnswerMap());
		// �����o�쪺���G��^�� fillinStr �ɡA�|����
		String fillinStr = "";
		try {
			// �إߥ��T�� List<Fillin>
			List<Fillin> newFillinList = new ArrayList<>();
			// �إߤw�s�W�� question_id �� list
			List<Integer> qIds = new ArrayList<>();
			List<Question> quList = mapper.readValue(questionsStr, new TypeReference<>() {
			});//

			for (Question item : quList) {
				// ���C�@�� Question �P fillin ��������
				// �D���D��: 1(��).2(��).3(��)
				List<Fillin> fillinList = req.getFillinList();
				for (Fillin fillin : fillinList) { // �^�����D��: 1.3
					// id ���@�P�A���L
					if (item.getId() != fillin.getqId()) {
						continue;
					}
					// �p�G qIds �w�g���]�t���D�s���A��ܤw�ˬd�L���D��
					// ���q�Ψӱư����ƪ����D�s��
					if (qIds.contains(fillin.getqId())) {
						continue;
					}
					// �N�w�s�W���D���D���[�J
					qIds.add(fillin.getqId());
					// �s�W�ۦP�D���� fillin
					// �������� fillin �[�� list ����]�O:
					// �W�����{���X�u���� question_id �M answer ���ˬd�A�ҥH��l���ݩʤ��e�i��O���X�k��
					// �����ϥ� Question item ���ȬO�]���o�ǭȳ��O�q DB �ӡA���w���ˬd�L
					newFillinList.add(new Fillin(item.getId(), item.getTitle(), item.getOptions(), //
							fillin.getAnswer(), item.getType(), item.isNecessary()));
					// �ˬd�ﶵ�P����
					checkResult = checkOptionAnswer(item, fillin); // ���k�G�t�~�إߨp����k: checkOptionAnswer
					if (checkResult != null) {
						return checkResult;
					}
				}
				if (item.isNecessary() && !qIds.contains(item.getId())) {
					return new BasicRes(ResMsg.ANSWER_IS_REQUIRED.getCode(), ResMsg.ANSWER_IS_REQUIRED.getMessage());
				}
			}
			fillinStr = mapper.writeValueAsString(newFillinList);
		} catch (JsonProcessingException e) {
			return new BasicRes(ResMsg.JSON_PROCESSING_EXCEPTION.getCode(),
					ResMsg.JSON_PROCESSING_EXCEPTION.getMessage());
		}
		responseDao.save(new Response(req.getQuizId(), req.getName(), req.getPhone(), req.getEmail(), //
				req.getAge(), fillinStr));
		return new BasicRes(ResMsg.SUCCESS.getCode(), ResMsg.SUCCESS.getMessage());
	}

	private BasicRes checkParams(FillinReq req) {
		if (req.getQuizId() <= 0) {
			return new BasicRes(ResMsg.PARAM_QUIZ_ID_ERROR.getCode(), //
					ResMsg.PARAM_QUIZ_ID_ERROR.getMessage());
		}
		if (!StringUtils.hasText(req.getName())) {
			return new BasicRes(ResMsg.PARAM_NAME_IS_REQUIRED.getCode(), //
					ResMsg.PARAM_NAME_IS_REQUIRED.getMessage());
		}
		if (!StringUtils.hasText(req.getName())) {
			return new BasicRes(ResMsg.PARAM_PHONE_IS_REQUIRED.getCode(), //
					ResMsg.PARAM_PHONE_IS_REQUIRED.getMessage());
		}
		if (!StringUtils.hasText(req.getName())) {
			return new BasicRes(ResMsg.PARAM_EMAIL_IS_REQUIRED.getCode(), //
					ResMsg.PARAM_EMAIL_IS_REQUIRED.getMessage());
		}
		if (req.getAge() <= 12 || req.getAge() > 99) {
			return new BasicRes(ResMsg.PARAM_AGE_NOT_QUALIFIED.getCode(), //
					ResMsg.PARAM_AGE_NOT_QUALIFIED.getMessage());
		}
		return null;
	}

	private BasicRes checkOptionAnswer(Question item, Fillin fillin) {
		// 1. �ˬd����]�n������
		// fillin �������רS���ȡA��^���~
		// ���`�����p�O: ���D�O����A�M��S���^���A�C�]���@�D�AqIds �N�|�]�t�ӥ�����D�� id
		// �]���Y���D�O����� qIds �S�S���]�t���D�� id �A�N��ܨS���^�����D�����D
		if (item.isNecessary() && !StringUtils.hasText(fillin.getAnswer())) {
			return new BasicRes(ResMsg.ANSWER_IS_REQUIRED.getCode(), ResMsg.ANSWER_IS_REQUIRED.getMessage());
		}
		// 2. �ư��D���O��� �� answerArray ������ > 1
		String answerStr = fillin.getAnswer();
		// �� answerStr(����) ���Φ��}�C
		String[] answerArray = answerStr.split(";");
		if (item.getType().equalsIgnoreCase(OptionType.SINGLE_CHOICE.getType()) //
				&& answerArray.length > 1) {
			return new BasicRes(ResMsg.ANSWER_OPTION_TYPE_IS_NOT_MATCH.getCode(),
					ResMsg.ANSWER_OPTION_TYPE_IS_NOT_MATCH.getMessage());
		}
		// 3. �ư�²���D�Foption type �O text(�]�����U�ӭn�ˬd�ﶵ�P���׬O�_�@�P)
		if (item.getType().equalsIgnoreCase(OptionType.TEXT.getType())) {
			return null;
		}
		// �� options ���� Array
		String[] optionArray = item.getOptions().split(";");
		// �� optionArray �ন List�A�]���n�ϥ� List ���� contains ��k
		List<String> optionList = List.of(optionArray);
		// 4. �ˬd���׸�ﶵ�@�P
		for (String str : answerArray) {
			// ���] item.getOptions() ���ȬO: "AB;BC;C;D"
			// �ন List �᪺ optionList = ["AB", "BC", "C", "D"]
			// ���] answerArray = [AB, B]
			// for �j�餤�N�O�� AB �M B ���O�_�Q�]�t�b optionList ��
			// List �� contains ��k�O��������A�ҥH�d�Ҥ��AAB�O���]�t�AB�O�S��
			// �ư��H�U:
			// 1) ���� && ���׿ﶵ���@�P
			if (item.isNecessary() && !optionList.contains(str)) {
				return new BasicRes(ResMsg.ANSWER_OPTION_IS_NOT_MATCH.getCode(),
						ResMsg.ANSWER_OPTION_IS_NOT_MATCH.getMessage());
			}
			// 2) �D���� && ������ && ���׿ﶵ���@�P
			if (!item.isNecessary() && StringUtils.hasText(str) && !optionList.contains(str)) {
				return new BasicRes(ResMsg.ANSWER_OPTION_IS_NOT_MATCH.getCode(),
						ResMsg.ANSWER_OPTION_IS_NOT_MATCH.getMessage());
			}
		}
		return null;
	}

	@Override
	public FeedbackRes feedback(FeedbackReq req) {
		Optional<Quiz> op = quizDao.findById(req.getQuizId());
		if (op.isEmpty()) {
			return new FeedbackRes(ResMsg.QUIZ_NOT_FOUND.getCode(), //
					ResMsg.QUIZ_NOT_FOUND.getMessage());
		}
		Quiz quiz = op.get();
		List<Feedback> feedbackList = new ArrayList<>();
		try {
			// �����P�@���ݨ����^�X
			List<Response> resList = responseDao.findByQuizId(req.getQuizId());
			// �M�� resList
			for (Response resItem : resList) {
				List<Fillin> fillinList = mapper.readValue(resItem.getFillin(), new TypeReference<>() {
				});
				FeedbackDetail detail = new FeedbackDetail(quiz.getName(), quiz.getDescription(), quiz.getStartDate(), //
						quiz.getEndDate(), resItem.getName(), resItem.getPhone(), //
						resItem.getEmail(), resItem.getAge(), fillinList);
				Feedback feedback = new Feedback(resItem.getName(), resItem.getFillinDateTime(), detail);
				feedbackList.add(feedback);
			}
		} catch (Exception e) {
			return new FeedbackRes(ResMsg.JSON_PROCESSING_EXCEPTION.getCode(),
					ResMsg.JSON_PROCESSING_EXCEPTION.getMessage());
		}
		return new FeedbackRes(ResMsg.SUCCESS.getCode(), ResMsg.SUCCESS.getMessage(), feedbackList);
	}

	@Override
	public StatisticsRes statistics(FeedbackReq req) {
		List<Response> responseList = responseDao.findByQuizId(req.getQuizId());
		// qId(�D��) �ﶵ , ����
		Map<Integer, Map<String, Integer>> countMap = new HashMap<>();
		
		for (Response item : responseList) {
			String fillinStr = item.getFillin();
			try {
				List<Fillin> fillinList = mapper.readValue(fillinStr, new TypeReference<>() {
				});
				// �p��Ҧ��^���� �D�� �ﶵ ����
			
				for (Fillin fillin : fillinList) {
					Map<String, Integer> optionCountMap = new HashMap<>(); 
					// �ư�²���D: ���C�J�έp
					if(fillin.getType().equalsIgnoreCase(OptionType.TEXT.getType())) {
						continue;
					}
					// �C�ӿﶵ�����O�Τ���(;)�걵
					String optionStr = fillin.getOptions(); // �`�������ﶵ
					String[] optionArray = optionStr.split(";"); // �����ﶵ�� ; ���j
					String answer = fillin.getAnswer();
					answer = ";" + answer + ";"; // �z�ѦP�U
					for (String option : optionArray) { // optionArray: ���, ������K, �������K, ���l���
						// ��ﵪ�פ��C�ӿﶵ�X�{������
						// �קK�Y�ӿﶵ�O�t�@�ӿﶵ���䤤�@����
						// �Ҧp: ���;������K;���l��� ���O�ﶵ�A�n��X����X�{���ơA������K�M���l����������
						// �ҥH�ݭn�b�C�ӿﶵ""�e��""�A�[�W�����A�|�Τ����O�]�����ת��걵�ϥΤ���
						// ����n��X�{���ƮɴN�|�O�� ;���; �ӧ�
						String newOption = ";" + option + ";";
						// �z�L�N�ﶵ�Q�ťը��N�A�o�˥i�H�p��X��֪�����
						String newAnswerStr = answer.replace(newOption, "");
						// �p��ӿﶵ�X�{������
						// �쥻�r����� - �Q���N��r�ꪺ���סA�n���H�ﶵ�����פ~�|�O�u��������
						int count = (answer.length() - newAnswerStr.length()) / newOption.length();
						// �O���C�@�D���έp
						optionCountMap = countMap.getOrDefault(fillin.getqId(), optionCountMap);
						// �����X�ﶵ(key)����������
						// getOrDefault(option, 0) : map ���S�� option(key) ���ܡA�N�|��^ 0
						int oldCount = optionCountMap.getOrDefault(option, 0);
						// �֥[ oldCount + count
						optionCountMap.put(option, oldCount + count);
						// �⦳�֥[���ƪ� optionCountMap �л\�^ countMap ��(�ۦP���D��)
						countMap.put(fillin.getqId(), optionCountMap);
//						Statistics statistics = new Statistics(fillin.getqId(),fillin.getQuestion(),//
//								fillin.isNecessary(), option, count);
					}

				}
			} catch (JsonProcessingException e) {
				return new StatisticsRes(ResMsg.JSON_PROCESSING_EXCEPTION.getCode(),
						ResMsg.JSON_PROCESSING_EXCEPTION.getMessage());
			}
		}
		 Optional<Quiz> op = quizDao.findById(req.getQuizId());
		 if(op.isEmpty()) {
			 return new StatisticsRes(ResMsg.QUIZ_NOT_FOUND.getCode(),
						ResMsg.QUIZ_NOT_FOUND.getMessage());
		 }
		 Quiz quiz = op.get();
		 return new StatisticsRes(ResMsg.SUCCESS.getCode(),
					ResMsg.SUCCESS.getMessage(),quiz.getName(),quiz.getStartDate(),quiz.getEndDate(),countMap);

		
	}

}
