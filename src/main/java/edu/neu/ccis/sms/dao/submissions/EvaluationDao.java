package edu.neu.ccis.sms.dao.submissions;

import java.util.List;

import edu.neu.ccis.sms.entity.submissions.Evaluation;

/**
 * DAO interface class for Evaluation Entity bean
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface EvaluationDao {
    public List<Evaluation> getAllEvaluations();

    public Evaluation getEvaluation(Long id);

    public void updateEvaluation(Evaluation modifiedEvaluation);

    public void deleteEvaluation(Evaluation evaluation);

    public void saveEvaluation(Evaluation newEvaluation);
}
