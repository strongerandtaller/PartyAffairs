package com.zqu.pa.dao.study;

import java.util.List;

import com.zqu.pa.entity.study.StudyDocument;

public interface StudyDocumentMapper {
    int deleteByPrimaryKey(Integer documentId);

    int insert(StudyDocument record);

    int insertSelective(StudyDocument record);
    
    String getUserNameByUserId(String userId);

    StudyDocument selectByPrimaryKey(Integer documentId);
    
    List<StudyDocument> selectMustPutonByUserId(String userId);
    
    List<StudyDocument> selectAll();
    
    List<StudyDocument> selectPutOn();

    List<StudyDocument> selectPutOff();
    
    
    List<StudyDocument> selectPutonByLabelId(List<Integer> idList);
    
    int selectDocumentIdByFilePath(String filePath);

    int updateByPrimaryKeySelective(StudyDocument record);

    int updateByPrimaryKey(StudyDocument record);
}