package edu.neu.ccis.sms.constants;

/**
 * Interface containing keys for all the views (jsp files) used by the application
 *
 * @author Swapnil Gupta
 * @version SMS 1.0
 * @since June 1, 2015
 * @ModifiedBy Pramod Khare
 * @LastModified June 20, 2015
 */
public interface JspViews {

	/** Key for the Dashboard view. */
	public static final String DASHBOARD_VIEW = "/pages/dashboard.jsp";

	/** Key for the add member view. */
	public static final String ADD_MEMBER_VIEW = "/pages/member/addMember.jsp";

	/** Key for view registrable member view. */
	public static final String VIEW_REGISTRABLE_MEMBER_VIEW = "/pages/member/viewRegistrableMember.jsp";

	/** Key for view submittable member view. */
	public static final String VIEW_SUBMITTABLE_MEMBER_VIEW = "/pages/member/viewSubmittableMember.jsp";

	/** Key for register for a member view. */
	public static final String REGISTER_FOR_MEMBER_VIEW = "/pages/member/registerForMember.jsp";

	/** Key for view member details view. */
	public static final String VIEW_MEMBER_DETAILS_VIEW = "/pages/member/viewMemberDetails.jsp";
	
	/** Key for upload submission document for submittable member page */
	public static final String SUBMIT_TO_MEMBER_VIEW = "/pages/submit_to_member.jsp";
	
	/** Key for upload evaluations page */
    public static final String UPLOAD_EVALUATIONS_VIEW = "/pages/upload_evaluations.jsp";

    /** Key for downloading submissions for evaluation page */
    public static final String DOWNLOAD_SUBMISSIONS_VIEW = "/pages/document_retrieval.jsp";

    /** Key for disseminate evaluations for a particular submittable member to students page */
    public static final String DISSEMINATE_EVALUATIONS_VIEW = "/pages/disseminate_evaluations.jsp";

    /** Key for allocate evaluators for submissions for a particular submittable member page */
    public static final String ALLOCATE_EVALUATORS_VIEW = "/pages/allocate_to_evaluators.jsp";

    /** Error page */
    public static final String ERROR_PAGE_VIEW = "/pages/error.jsp";

    /** Register a new user page */
    public static final String REGISTER_USER_VIEW = "/pages/user_registration.jsp";
}
