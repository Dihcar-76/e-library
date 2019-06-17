package org.rboug.application.elibrary.service;

import org.rboug.application.elibrary.dao.AuthorDaoBddInterface;
import org.rboug.application.elibrary.model.Author;
import org.rboug.application.elibrary.model.Language;

import javax.ejb.Stateful;
import javax.inject.Inject;
import java.util.List;

@Stateful
public class AuthorService implements AuthorServiceInterface{
    @Inject
    AuthorDaoBddInterface authorDaoBdd;

    /*
     * return true if update is a new book creation or false if simple update
     *
     *@throws Exception
     */
    @Override
    public boolean createOrUpdateAuthor(Author a, Long id) throws Exception{
        if (id == null) {
            authorDaoBdd.create(a);
            return true;
        } else {
            authorDaoBdd.update(a);
            return false;
        }
    }

    @Override
    public Author findById(Long id) throws Exception{
        return authorDaoBdd.findById(id);
    }

    @Override
    public void remove(Author deletableEntity) {
        authorDaoBdd.delete(deletableEntity);
    }

    @Override
    public void refresh() {
        authorDaoBdd.refresh();
    }

    @Override
    public List<Author> getAll() {
        return authorDaoBdd.getAll();
    }

    @Override
    public long getItemsCount(String firstName, String lastName, String bio, Integer age, Language preferredLanguage) {
        return authorDaoBdd.getItemsCount(firstName, lastName, bio, age, preferredLanguage);
    }

    @Override
    public List<Author> getPageItems(String firstName, String lastName, String bio, Integer age, Language preferredLanguage, int page, int pageSize) {
        return authorDaoBdd.getPageItems(firstName, lastName, bio, age, preferredLanguage, page, pageSize);
    }
}
