package com.javamentor.qa.platform.service.impl.model;

import com.javamentor.qa.platform.dao.abstracts.model.AnswerDao;
import com.javamentor.qa.platform.dao.abstracts.repository.ReadWriteDao;
import com.javamentor.qa.platform.models.entity.question.answer.Answer;
import com.javamentor.qa.platform.service.abstracts.model.AnswerService;
import com.javamentor.qa.platform.service.impl.repository.ReadWriteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AnswerServiceImpl extends ReadWriteServiceImpl<Answer, Long> implements AnswerService {

    private final AnswerDao answerDao;


    @Autowired
    public AnswerServiceImpl(AnswerDao answerDao) {
        super(answerDao);
        this.answerDao = answerDao;
    }


    public AnswerServiceImpl(ReadWriteDao<Answer, Long> readWriteDao, AnswerDao answerDao) {
        super(readWriteDao);
        this.answerDao = answerDao;
    }

    @Override
    @Transactional
    public Optional<Answer> getByAnswerIdAndUserId(Long answerId, Long userId) {
        return answerDao.getByAnswerIdAndUserId(answerId, userId);
    }

    @Override
    @Transactional
    public Optional<Answer> getAnswerByAnswerIdAndUserId(Long answerId, Long userId) {
        return answerDao.getAnswerByAnswerIdAndUserId(answerId, userId);
    }
}