package com.paulawaite.fbtr.persistence;

import com.paulawaite.fbtr.entity.Role;
import com.paulawaite.fbtr.entity.User;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by paulawaite on 2/2/16.
 */
public class UserTest {

    AbstractDao dao;
    DatabaseUtility databaseUtility;
    List<User> users;

    @Before
    public void setUp() throws Exception {
        dao = new AbstractDao(User.class);
        databaseUtility = new DatabaseUtility();
        databaseUtility.runSQL("cleandb.sql");
        databaseUtility.runSQL("createTestData.sql");
        users = dao.getAll();
    }


    @Test
    public void testGetAllUsers() throws Exception {
        assertTrue(users.size() > 0);
        assertFalse(users.get(0).getFirstName().equals(""));
    }

    @Test
    public void testUpdateUser() throws Exception {
        User user = users.get(0);
        int id = user.getUserId();
        String updateValue = LocalDate.now().toString();
        String emailBeforeUpdate = user.getEmail();
        // it would be a good idea to test each value like this

        user.setEmail(user.getEmail() + updateValue);

        dao.update(user);

        User updatedUser = (User) dao.get(id);

        assertEquals(emailBeforeUpdate + updateValue, updatedUser.getEmail());

    }


    /*Error deleting  User(userId=75, firstName=Unit0, lastName=Test0, email=UserDaoTester@gmail.com02017-11-01, password=supersecret0, createDate=null, updateDate=null, userName=UnitTester0)
    org.hibernate.HibernateException: Illegal attempt to associate a collection with two open sessions. Collection : [com.paulawaite.fbtr.entity.User.roles#User(userId=0, firstName=null, lastName=null, email=null, password=null, createDate=null, updateDate=null, userName=UnitTester0)]
    Collection contents: [[Role(roleId=68, role=user, updateDate=null, createDate=null, user
    */

    @Ignore
    @Test
    public void testDeleteUser() throws Exception {
        int sizeBeforeDelete = users.size();
        User userToDelete = users.get(0);
        int id = userToDelete.getUserId();
        dao.delete(userToDelete);
        int sizeAfterDelete = dao.getAll().size();

        User deletedUser = (User) dao.get(id);

        assertEquals(sizeBeforeDelete - 1, sizeAfterDelete);
        assertNull(deletedUser);

    }


    @Test
    public void testAddUser() throws Exception {

        int insertedUserId = 0;

        User user = new User();
        user.setFirstName("Unit");
        user.setLastName("Test");
        user.setUserName("UnitTesterB");
        user.setEmail("UserDaoTesterB@gmail.com");
        user.setPassword("supersecret");

        Role role = new Role();
        role.setName("admin");
        role.setUser(user);

        user.addRole(role);

        insertedUserId = dao.create(user);
        User retrievedUser = (User) dao.get(insertedUserId);

        assertTrue(insertedUserId > 0);
        assertEquals(user, retrievedUser);
        assertEquals(1, retrievedUser.getRoles().size());
        assertTrue(retrievedUser.getRoles().contains(role));

    }

    @Test
    public void testGetAllUsersWithLastNameExact() throws Exception {
        users = dao.findByProperty("lastName", "Test1");
        assertTrue(users.size() > 0);
        assertTrue(users.get(0).getFirstName().equals("Unit1"));
    }




}