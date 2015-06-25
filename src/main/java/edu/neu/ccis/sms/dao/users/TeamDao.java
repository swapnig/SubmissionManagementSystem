package edu.neu.ccis.sms.dao.users;

import java.util.List;

import edu.neu.ccis.sms.entity.users.Team;

/**
 * DAO interface class for Team Entity bean; provides access methods for team entities
 * 
 * @author Pramod R. Khare
 * @date 9-May-2015
 * @lastUpdate 7-June-2015
 */
public interface TeamDao {
    /**
     * Get all the teams in current installations
     * 
     * @return returns List of Teams else empty list
     */
    public List<Team> getAllTeams();

    /**
     * Get a specific team by its team-id
     * 
     * @param id
     *            - team id
     * @return team instance if it exists else returns null
     */
    public Team getTeam(Long id);

    /**
     * Update team details of an existing team
     * 
     * @param modifiedTeam
     *            - modified team object
     */
    public void updateTeam(Team modifiedTeam);

    /**
     * Delete existing team
     * 
     * @param team
     *            - team to be deleted
     */
    public void deleteTeam(Team team);

    /**
     * Saves a new team
     * 
     * @param newTeam
     *            - new team object to be saved
     */
    public void saveTeam(Team newTeam);
}
