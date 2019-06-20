package org.rboug.application.elibrary.dao;

import org.rboug.application.elibrary.model.User;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public interface AccountDaoBddInterface {

    public boolean userExist(String login);

    public void create(User user);

    public User update(User user);

    public User findByLoginAndPassword(String login, String digestPassword);

    User findByEmail(String email);

    public void refresh(User user);
}
