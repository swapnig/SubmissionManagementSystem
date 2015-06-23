package edu.neu.ccis.sms.dao.submissions;

import java.util.List;

import edu.neu.ccis.sms.entity.submissions.Evaluation;

/**
 * DAO interface class for Evaluation Entity bean; provides access methods for evaluations
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface EvaluationDao {
    /**
     * Gets all evaluations for all documents submitted in current system
     * 
     * @return
     */
    public List<Evaluation> getAllEvaluations();

    /**
     * Get a specific evaluation by its evluation id
     * 
     * @param id
     * @return
     */
    public Evaluation getEvaluation(Long id);

    /**
     * Update evaluation details
     * 
     * @param modifiedEvaluation
     */
    public void updateEvaluation(Evaluation modifiedEvaluation);

    /**
     * Delete existing evaluation
     * 
     * @param evaluation
     */
    public void deleteEvaluation(Evaluation evaluation);

    /**
     * Save a new evluation
     * 
     * @param newEvaluation
     */
    public void saveEvaluation(Evaluation newEvaluation);
}
