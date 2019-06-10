package org.rboug.application.elibrary.service;

import org.rboug.application.elibrary.model.User;

public interface AccountServiceInterface {
    public boolean userExist(String login);

    public void create(User user);

    public User findByLoginPassword(String login, String digestPassword);

    public User update(User user);
}
