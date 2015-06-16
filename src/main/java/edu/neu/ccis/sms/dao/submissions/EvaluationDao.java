package edu.neu.ccis.sms.dao.submissions;

import java.util.List;

import edu.neu.ccis.sms.entity.submissions.Evaluation;

public interface EvaluationDao {
    public List<Evaluation> getAllEvaluations();

    public Evaluation getEvaluation(Long id);

    public void updateEvaluation(Evaluation modifiedEvaluation);

    public void deleteEvaluation(Evaluation evaluation);

    public void saveEvaluation(Evaluation newEvaluation);
}
