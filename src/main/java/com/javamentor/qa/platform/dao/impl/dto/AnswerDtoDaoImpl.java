package com.javamentor.qa.platform.dao.impl.dto;

import com.javamentor.qa.platform.dao.abstracts.dto.AnswerDtoDao;
import com.javamentor.qa.platform.models.dto.AnswerDto;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDtoDaoImpl implements AnswerDtoDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<AnswerDto> getAllAnswersDtoByQuestionId(Long questionId, Long userId) {
        return entityManager.createQuery("""
                SELECT
                a.id AS answerId,
                a.user.id as userID,
                a.question.id AS questionId,
                a.htmlBody AS body,
                a.persistDateTime AS persistDate,
                a.isHelpful AS isHelpful,
                a.dateAcceptTime AS dateAccept,
                COALESCE(SUM (CASE WHEN va.voteType = 'UP' THEN 1 WHEN va.voteType = 'DOWN' THEN -1 END), 0) AS countValuable,
                COALESCE(ra.count, 0)  AS countUserReputation,
                a.user.imageLink AS image,
                a.user.nickname AS nickname,
                COALESCE(COUNT (va.id), 0) as countVote,
                COALESCE(va.voteType, 0) AS voteType
                FROM Answer a
                LEFT JOIN VoteAnswer va ON a.id = va.answer.id
                LEFT JOIN Reputation ra ON a.id = ra.answer.id
                WHERE a.question.id = :questionId and a.user.id = :userId
                GROUP BY a.id, a.user.id, a.question.id, a.htmlBody, a.persistDateTime, a.isHelpful,
                a.dateAcceptTime, User.imageLink, User.nickname, va.voteType
                """, AnswerDto.class)
                .setParameter("questionId", questionId)
                .setParameter("userId", userId)
                .getResultList();
    }
}