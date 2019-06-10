package org.rboug.application.elibrary.service;

import org.rboug.application.elibrary.dao.AccountDaoBdd;
import org.rboug.application.elibrary.dao.AccountDaoBddInterface;
import org.rboug.application.elibrary.model.User;

import javax.ejb.Stateful;
import javax.inject.Inject;

@Stateful
public class AccountService implements AccountServiceInterface{
    @Inject
    AccountDaoBddInterface accountDaoBdd;

    public boolean userExist(String login) {
        return accountDaoBdd.userExist(login);
    }

    public void create(User user){
        accountDaoBdd.create(user);
    }

    public User findByLoginPassword(String login, String digestPassword){
        return accountDaoBdd.findByLoginPassword(login, digestPassword);
    }

    public User update(User user) {
        return accountDaoBdd.update(user);
    }
}
