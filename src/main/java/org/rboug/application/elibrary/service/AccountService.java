package org.rboug.application.elibrary.service;

import org.rboug.application.elibrary.dao.AccountDaoBddInterface;
import org.rboug.application.elibrary.model.User;

import javax.ejb.Stateful;
import javax.inject.Inject;

@Stateful
public class AccountService implements AccountServiceInterface{
    @Inject
    AccountDaoBddInterface accountDaoBdd;

    @Override
    public boolean userExist(String login) {
        return accountDaoBdd.userExist(login);
    }

    @Override
    public void create(User user){
        accountDaoBdd.create(user);
    }

    @Override
    public User findByLoginAndPassword(String login, String digestPassword){
        return accountDaoBdd.findByLoginAndPassword(login, digestPassword);
    }

    @Override
    public User update(User user) {
        return accountDaoBdd.update(user);
    }

    @Override
    public User findByEmail(String email) {
        return accountDaoBdd.findByEmail(email);
    }

    @Override
    public void refresh(User user) {
        accountDaoBdd.refresh(user);
    }
}
