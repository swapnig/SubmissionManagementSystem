package edu.neu.ccis.sms.dao.users;

import java.util.List;

import edu.neu.ccis.sms.entity.categories.Member;
import edu.neu.ccis.sms.entity.categories.UserToMemberMapping;
import edu.neu.ccis.sms.entity.users.RoleType;
import edu.neu.ccis.sms.entity.users.User;

public interface UserDao {
    public List<User> getAllUser();

    public User getUser(Long id);

    public void updateUser(User modifiedUser);

    public void deleteUser(User user);

    public void saveUser(User newUser);

    // find user by username
    public User findUserByUsername(String username);

    // find user by username and password
    public User findUserByUsernameAndPassword(String username, String password);

    // register for member
    public UserToMemberMapping registerUserForMember(Long userId,
            Long memeberId, RoleType role);

    public UserToMemberMapping registerUserForMember(User userId,
            Member memeber, RoleType role);

	public User findUserByEmail(String email);
}
