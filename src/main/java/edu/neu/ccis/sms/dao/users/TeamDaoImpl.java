package edu.neu.ccis.sms.dao.users;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.entity.users.Team;
import edu.neu.ccis.sms.util.HibernateUtil;

/**
 * DAO implementation class for Team Entity bean; Provides team entity access methods
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public class TeamDaoImpl implements TeamDao {
    /** Hibernate session instance */
    private Session currentSession;

    /** Hibernate session transaction instance */
    private Transaction currentTransaction;

    /** Default Constructor */
    public TeamDaoImpl() {
    }

    /**
     * private utility book-keeping method to open hibernate session with a new transaction
     * 
     * @return - Hibernate session instance
     */
    private Session openCurrentSessionwithTransaction() {
        currentSession = HibernateUtil.getSessionFactory().openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }

    /**
     * private utility book-keeping method to close current transaction along with hibernate session, committing any new
     * changes to entities; nulling out old references;
     */
    private void closeCurrentSessionwithTransaction() {
        currentTransaction.commit();
        currentSession.close();
        currentTransaction = null;
        currentSession = null;
    }

    /**
     * Getter method for current active hibernate session, if there isn't any active session then returns null
     * 
     * @return - current active hibernate session instance else null
     */
    public Session getCurrentSession() {
        return currentSession;
    }

    /**
     * Getter method for current active hibernate transaction, if there isn't any active transaction then returns null
     * 
     * @return - current active hibernate transaction instance else null
     */
    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    /**
     * Saves a new team
     * 
     * @param newTeam
     *            - new team object to be saved
     */
    @Override
    public void saveTeam(Team newTeam) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newTeam);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Update team details of an existing team
     * 
     * @param modifiedTeam
     *            - modified team object
     */
    @Override
    public void updateTeam(Team modifiedTeam) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedTeam);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Find a specific team by its team-id
     * 
     * @param id
     *            - team id
     * @return team instance if it exists else returns null
     */
    public Team findByTeamId(Long id) {
        openCurrentSessionwithTransaction();
        Team team = (Team) getCurrentSession().get(Team.class, id);
        closeCurrentSessionwithTransaction();
        return team;
    }

    /**
     * Delete existing team
     * 
     * @param team
     *            - team to be deleted
     */
    @Override
    public void deleteTeam(Team user) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(user);
        closeCurrentSessionwithTransaction();
    }

    /**
     * Get all the teams in current installations
     * 
     * @return returns List of Teams else empty list
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<Team> getAllTeams() {
        openCurrentSessionwithTransaction();
        List<Team> users = (List<Team>) getCurrentSession().createQuery("from Team").list();
        closeCurrentSessionwithTransaction();
        return users;
    }

    /**
     * Get a specific team by its team-id
     * 
     * @param id
     *            - team id
     * @return team instance if it exists else returns null
     */
    @Override
    public Team getTeam(Long id) {
        return findByTeamId(id);
    }
}