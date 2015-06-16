package edu.neu.ccis.sms.dao.users;

import java.util.List;

import edu.neu.ccis.sms.entity.users.Team;

/**
 * DAO interface class for Team Entity bean
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface TeamDao {
    public List<Team> getAllTeams();

    public Team getTeam(Long id);

    public void updateTeam(Team modifiedTeam);

    public void deleteTeam(Team team);

    public void saveTeam(Team newTeam);
}
