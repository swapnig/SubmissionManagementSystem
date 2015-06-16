package edu.neu.ccis.sms.dao.users;

import java.util.List;

import edu.neu.ccis.sms.entity.users.Team;

public interface TeamDao {
    public List<Team> getAllTeams();

    public Team getTeam(Long id);

    public void updateTeam(Team modifiedTeam);

    public void deleteTeam(Team team);

    public void saveTeam(Team newTeam);
}
