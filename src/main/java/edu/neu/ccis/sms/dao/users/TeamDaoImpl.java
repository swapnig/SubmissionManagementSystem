package edu.neu.ccis.sms.dao.users;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import edu.neu.ccis.sms.entity.users.Team;
import edu.neu.ccis.sms.util.HibernateUtil;

public class TeamDaoImpl implements TeamDao {
    private Session currentSession;
    private Transaction currentTransaction;

    public TeamDaoImpl() {
    }

    public Session openCurrentSessionwithTransaction() {
        currentSession = getSessionFactory().openSession();
        currentTransaction = currentSession.beginTransaction();
        return currentSession;
    }

    public void closeCurrentSessionwithTransaction() {
        currentTransaction.commit();
        currentSession.close();
    }

    private static SessionFactory getSessionFactory() {
        return HibernateUtil.getSessionFactory();
    }

    public Session getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(Session currentSession) {
        this.currentSession = currentSession;
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    @Override
    public void saveTeam(Team newTeam) {
        openCurrentSessionwithTransaction();
        getCurrentSession().save(newTeam);
        closeCurrentSessionwithTransaction();
    }

    @Override
    public void updateTeam(Team modifiedTeam) {
        openCurrentSessionwithTransaction();
        getCurrentSession().update(modifiedTeam);
        closeCurrentSessionwithTransaction();
    }

    public Team findByTeamId(Long id) {
        openCurrentSessionwithTransaction();
        Team team = (Team) getCurrentSession().get(Team.class, id);
        closeCurrentSessionwithTransaction();
        return team;
    }

    @Override
    public void deleteTeam(Team user) {
        openCurrentSessionwithTransaction();
        getCurrentSession().delete(user);
        closeCurrentSessionwithTransaction();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Team> getAllTeams() {
        openCurrentSessionwithTransaction();
        List<Team> users = (List<Team>) getCurrentSession().createQuery(
                "from Team").list();
        closeCurrentSessionwithTransaction();
        return users;
    }

    public void deleteAll() {
        List<Team> usersList = getAllTeams();
        for (Team user : usersList) {
            deleteTeam(user);
        }
    }

    @Override
    public Team getTeam(Long id) {
        return findByTeamId(id);
    }
}