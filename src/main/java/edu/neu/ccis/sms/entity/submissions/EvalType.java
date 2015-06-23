package edu.neu.ccis.sms.entity.submissions;

/**
 * Decides what are final evaluation strategy types to calculate the final grades for a submitted-document - e.g. As
 * there can be multiple evaluations for a given submission, how to calculate the final grades for document. They can be
 * 1) AVERAGE - average out all evaluation results, 2) MAXIMUM - Take the maximum result from available evaluations, 3)
 * MINIMUM - Take the minimum result from available evaluations
 * 
 * @Default value - will be always AVERAGE
 * @author Pramod R. Khare
 * @date 14-June-2015
 * @lastUpdate 15-June-2015
 */
public enum EvalType {
    /** Will calculate final evaluation of a submission as average out all evaluation results, */
    AVERAGE,

    /** Will calculate final evaluation of a submission as the maximum result from available evaluations */
    MAXIMUM,

    /** Will calculate final evaluation of a submission as the minimum result from available evaluations */
    MINIMUM
}
