package com.zqu.pa.service.exam.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.zqu.pa.common.ServerResponse;
import com.zqu.pa.dao.exam.AnswerMapper;
import com.zqu.pa.dao.exam.ChoiceMapper;
import com.zqu.pa.dao.exam.QuestionBankMapper;
import com.zqu.pa.dao.exam.QuestionExamCategoryMapper;
import com.zqu.pa.entity.exam.Answer;
import com.zqu.pa.entity.exam.Choice;
import com.zqu.pa.entity.exam.QuestionBank;
import com.zqu.pa.entity.exam.QuestionBankExample;
import com.zqu.pa.entity.exam.QuestionExamCategoryKey;
import com.zqu.pa.service.exam.ExamPaperService;
import com.zqu.pa.service.exam.QuestionBankService;
import com.zqu.pa.vo.exam.Paper;
import com.zqu.pa.vo.exam.Question;
import com.zqu.pa.vo.exam.QuestionContent;
import com.zqu.pa.vo.exam.ResponseQuestionBank;
import com.zqu.pa.vo.userInfo.UserBasicInfo;

@Service
public class QuestionBankServiceImpl implements QuestionBankService {

    private Logger logger = LoggerFactory.getLogger(QuestionBankServiceImpl.class);
    
    @Autowired
    private QuestionBankMapper questionBankMapper;
    
    @Autowired
    private ChoiceMapper choiceMapper;
    
    @Autowired
    private AnswerMapper answerMapper;
    
    @Autowired
    private QuestionExamCategoryMapper questionExamCategoryMapper;
    
    @Autowired
    private ExamPaperService examPaperService;
    
    @Override
    public ResponseQuestionBank getUploadQuestion(MultipartFile file, Integer categoryId) {
        
        
        return null;
    }

    @Transactional
    @Override
    public ServerResponse analyseFile(MultipartFile file, Integer categoryId) {
        
        //File excelFile = new File("C:\\Users\\ZC\\Desktop\\question_bank.xls"); // 创建文件对象          
        InputStream in;
        ResponseQuestionBank rqb = new ResponseQuestionBank();
        List<QuestionContent> singleQuestion = new ArrayList<QuestionContent>();
        List<QuestionContent> multipleQuestion = new ArrayList<QuestionContent>();
        rqb.setCategoryId(categoryId);
        
        try {
            //in = new FileInputStream(excelFile);
            in = file.getInputStream();
            Workbook workbook =  new HSSFWorkbook(in);
            int sheetCount = workbook.getNumberOfSheets(); // Sheet的数量
            logger.debug("遍历文件第一个Sheet");
            Sheet sheet = workbook.getSheetAt(0);   // 遍历第一个Sheet
            int rowNum = sheet.getPhysicalNumberOfRows(); //获取此sheet总行数
            for (int i = 0; i < rowNum; i++) {
                QuestionContent q = new QuestionContent();
                Row row = sheet.getRow(i);                          
                Cell cell = row.getCell(0);
                String q_content = cell.getStringCellValue().trim();
                q.setQuestionContent(q_content); 
                
                row = sheet.getRow(++i);
                List<String> choice = new ArrayList<String>();
                for (Cell cell1 : row) {
                    if (!cell1.getStringCellValue().trim().equals(""))
                        choice.add(cell1.getStringCellValue().trim());
                }
                q.setChoice(choice);
                
                row = sheet.getRow(++i);
                List<Integer> answer = new ArrayList<Integer>();
                for (Cell cell1 : row) {
                    if (!cell1.getStringCellValue().trim().equals(""))
                        //将字符串ABCD等转为ASCII码减去64使A=1,B=2以此类推
                        answer.add(cell1.getStringCellValue().trim().getBytes()[0] - 64);   
                }
                q.setAnswer(answer);
                singleQuestion.add(q);
            }
            
            logger.debug("遍历文件第二个Sheet");
            sheet = workbook.getSheetAt(1);   // 遍历第二个Sheet
            rowNum = sheet.getPhysicalNumberOfRows(); //获取此sheet总行数
            for (int i = 0; i < rowNum; i++) {
                QuestionContent q = new QuestionContent();
                Row row = sheet.getRow(i);                          
                Cell cell = row.getCell(0);
                String q_content = cell.getStringCellValue().trim();
                q.setQuestionContent(q_content); 
                
                row = sheet.getRow(++i);
                List<String> choice = new ArrayList<String>();
                for (Cell cell1 : row) {
                    if (!cell1.getStringCellValue().trim().equals(""))
                        choice.add(cell1.getStringCellValue().trim());
                }
                q.setChoice(choice);
                
                row = sheet.getRow(++i);
                List<Integer> answer = new ArrayList<Integer>();
                for (Cell cell1 : row) {
                    if (!cell1.getStringCellValue().trim().equals(""))
                        //将字符串ABCD等转为ASCII码减去64使A=1,B=2以此类推
                        answer.add(cell1.getStringCellValue().trim().getBytes()[0] - 64);   
                }
                q.setAnswer(answer);
                multipleQuestion.add(q);
            }
            
        } catch (FileNotFoundException e) {
            
            e.printStackTrace();
            return ServerResponse.createByErrorMessage("文件不存在");
        } catch (IOException e) {
            
            e.printStackTrace();
            return ServerResponse.createByError();
        } catch (Exception e) {
            
            e.printStackTrace();
            return ServerResponse.createByError();
        }

        rqb.setSingleQuestion(singleQuestion);
        rqb.setMultipleQuestion(multipleQuestion);
        rqb.setCategoryId(categoryId);
        return ServerResponse.createBySuccess(rqb);
    }

    @Transactional
    @Override
    public Integer saveQuestionBank(ResponseQuestionBank responseQuestionBank) {
        
        int categoryId = responseQuestionBank.getCategoryId();
        
        //单选题
        int type = 0;
        List<QuestionContent> singleQuestion = responseQuestionBank.getSingleQuestion();
        int i = insertQuestion(singleQuestion, type, categoryId);
        if (i != 1)
            return i;
        //多选题
        type = 1;
        List<QuestionContent> multipleQuestion = responseQuestionBank.getMultipleQuestion();
        i = insertQuestion(multipleQuestion, type, categoryId);
        if (i != 1)
            return i;
        return 1;
    }

    //插入题库信息
    public int insertQuestion(List<QuestionContent> question, Integer type, Integer categoryId) {
        
        UserBasicInfo basicInfo = (UserBasicInfo)SecurityUtils.getSubject().getSession().getAttribute("basicInfo");
        if (basicInfo == null) {
            logger.error("用户未登录");
            return -1;
        }            
        String userId = basicInfo.getUserId();
        int branchId = basicInfo.getBranchId();
        int roleId = basicInfo.getRoleId();
        
        for (QuestionContent qc : question) {
            //插入题库表问题
            QuestionBank q = new QuestionBank();
            q.setBranchId(branchId);
            q.setCreateId(userId);
            q.setQuestionContent(qc.getQuestionContent());
            q.setQuestionType(type);
            q.setReview(0);
            questionBankMapper.inesertQuestion(q);
            //插入此题分类
            QuestionExamCategoryKey qeck = new QuestionExamCategoryKey();
            qeck.setQuestionId(q.getQuestionId());
            qeck.setCategoryId(categoryId);
            questionExamCategoryMapper.insert(qeck);
            
            //插入选项表各个选项
            List<String> listChoice = qc.getChoice();
            //对应ABCD选项1，2，3，4
            int index = 1;
            for (String c : listChoice) {
                Choice choice = new Choice();
                choice.setQuestionId(q.getQuestionId());            
                choice.setChoiceContent(c);
                choice.setChoice(index++);
                choiceMapper.insertSelective(choice);
            }
            
            //插入答案表答案
            List<Integer> listAnswer = qc.getAnswer();
            for (Integer a : listAnswer) {
                Answer answer = new Answer();
                answer.setQuestionId(q.getQuestionId());
                answer.setChoice(a);
                answerMapper.insertSelective(answer);
            }
        }
        return 1;
    }

    @Override
    public Paper getQuestionBank(Integer categoryId) {
        
        Paper paper = new Paper();
        List<Integer> category = new ArrayList<>();
        category.add(categoryId);
        //找出此次考试所有分类下的所有question_id
        List<Integer> listQuestionId = examPaperService.listCategoryQuestionId(category);
        if(listQuestionId == null || listQuestionId.size() == 0) {
            logger.info("考试题库分类ID为" + categoryId + " 下没有题目或无此题库");
            return null;
        }
        
        //type = 0:单选题
        //type = 1:多选题
        Integer type = 0;
        paper.setSingleQuestion(getQuestion(type, listQuestionId));
        type = 1;
        paper.setMultipleQuestion(getQuestion(type, listQuestionId));
        return paper;
    }
    
    //获取对应ID和类型的题目
    public List<Question> getQuestion(Integer type, List<Integer> listQuestionId) {

        List<Question> question = new ArrayList<Question>();
        //随机选择获得题库信息
        QuestionBankExample example = new QuestionBankExample();
        example.createCriteria().andQuestionTypeEqualTo(type).andQuestionIdIn(listQuestionId);
        List<QuestionBank> listQuestionBank = questionBankMapper.selectByExample(example);
        //获取的题目ID集合
        List<Integer> listQuestionID = new ArrayList<Integer>();
        for (int i = 0; i < listQuestionBank.size(); i++)
            listQuestionID.add(listQuestionBank.get(i).getQuestionId());
        //根据题目ID集合查找选项集合
        List<Choice> choice = examPaperService.listChoice(listQuestionID);
        //根据题目ID集合查找答案集合
        List<Answer> answer = examPaperService.listAnswer(listQuestionID);
        
        //将题目集合、选项集合、答案集合根据题目ID合并成Question类集合
        Integer questionID = 0;
        for (int i = 0; i < listQuestionBank.size(); i++) {
            questionID = listQuestionBank.get(i).getQuestionId();
            List<String> listChoice = new ArrayList<String>();
            List<Integer> listAnswer = new ArrayList<Integer>();
            for (int j = 0; j < choice.size(); j++) {              
                if (questionID == choice.get(j).getQuestionId())
                    listChoice.add(choice.get(j).getChoiceContent());
            }
            for (int k = 0; k < answer.size(); k++) {               
                if (questionID == answer.get(k).getQuestionId())
                    listAnswer.add(answer.get(k).getChoice());
            }
            Question q = new Question();
            q.setQuestionId(questionID);
            q.setQuestionContent(listQuestionBank.get(i).getQuestionContent());
            q.setChoice(listChoice);
            q.setAnswer(listAnswer);
            question.add(q);
           
        }
      
        return question;
    }
}
