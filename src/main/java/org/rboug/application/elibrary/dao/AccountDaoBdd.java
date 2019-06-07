package org.rboug.application.elibrary.dao;

import org.rboug.application.elibrary.model.User;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name = User.FIND_BY_EMAIL, query = "SELECT u FROM User u WHERE u.email = :email"),
        @NamedQuery(name = User.FIND_BY_UUID, query = "SELECT u FROM User u WHERE u.uuid = :uuid"),
        @NamedQuery(name = User.FIND_BY_LOGIN, query = "SELECT u FROM User u WHERE u.login = :login"),
        @NamedQuery(name = User.FIND_BY_LOGIN_PASSWORD, query = "SELECT u FROM User u WHERE u.login = :login AND u.password = :password"),
        @NamedQuery(name = User.FIND_ALL, query = "SELECT u FROM User u"),
        @NamedQuery(name = User.UPDATE_UUID, query = "UPDATE User u SET u.uuid = null WHERE u.login = :login")
})
public class AccountDaoBdd {

    public static final String FIND_BY_EMAIL = "User.findByEmail";
    public static final String FIND_BY_LOGIN = "User.findByLogin";
    public static final String FIND_BY_UUID = "User.findByUUID";
    public static final String FIND_BY_LOGIN_PASSWORD = "User.findByLoginAndPassword";
    public static final String FIND_ALL = "User.findAll";
    public static final String UPDATE_UUID = "User.update_UUID";

    @PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;


    public boolean userExist(String login) {
        return entityManager.createNamedQuery(AccountDaoBdd.FIND_BY_LOGIN, User.class).setParameter("login", login)
                .getResultList().size() > 0;
    }

    public void create(User user) {
        entityManager.persist(user);
    }
}
