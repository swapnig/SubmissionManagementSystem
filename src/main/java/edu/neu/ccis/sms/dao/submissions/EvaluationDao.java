package edu.neu.ccis.sms.dao.submissions;

import java.util.List;

import edu.neu.ccis.sms.entity.submissions.Evaluation;

/**
 * DAO interface class for Evaluation Entity bean; provides access methods for evaluation enities
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface EvaluationDao {
    /**
     * Gets all evaluations for all documents submitted in current system
     * 
     * @return - list of all evaluations done till now in this system
     */
    public List<Evaluation> getAllEvaluations();

    /**
     * Get a specific evaluation by its evaluation id
     * 
     * @param id
     *            - evaluation entity id
     * @return - Evaluation object for given evaluation id if it exists else returns null
     */
    public Evaluation getEvaluation(Long id);

    /**
     * Update evaluation details
     * 
     * @param modifiedEvaluation
     *            - modified evaluation object
     */
    public void updateEvaluation(Evaluation modifiedEvaluation);

    /**
     * Delete existing evaluation
     * 
     * @param evaluation
     *            - evaluation object to be deleted
     */
    public void deleteEvaluation(Evaluation evaluation);

    /**
     * Save a new evaluation
     * 
     * @param newEvaluation
     *            - new evaluation object to be saved
     */
    public void saveEvaluation(Evaluation newEvaluation);
}
