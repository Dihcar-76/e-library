package org.rboug.application.elibrary.dao;

import org.rboug.application.elibrary.model.User;

import javax.persistence.*;


public class AccountDaoBdd implements  AccountDaoBddInterface{

    @PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;


    public boolean userExist(String login) {
        return entityManager.createNamedQuery(User.FIND_BY_LOGIN, User.class).setParameter("login", login)
                .getResultList().size() > 0;
    }

    public void create(User user) {
        entityManager.persist(user);
    }

    public User update(User user) {
        entityManager.merge(user);
        return user;
    }

    public User findByLoginAndPassword(String login, String digestPassword){
        TypedQuery<User> query = entityManager.createNamedQuery(User.FIND_BY_LOGIN_PASSWORD, User.class);
        query.setParameter("login", login);
        query.setParameter("password", digestPassword);
        try{
            return query.getSingleResult();
        }
        catch(NoResultException e){
            return null;
        }
    }

    public User findByEmail(String email) {
        TypedQuery<User> query = entityManager.createNamedQuery(User.FIND_BY_EMAIL, User.class);
        query.setParameter("email", email);
        try {
            return query.getSingleResult();
        }
        catch(NoResultException e){
            return null;
        }
    }
}
