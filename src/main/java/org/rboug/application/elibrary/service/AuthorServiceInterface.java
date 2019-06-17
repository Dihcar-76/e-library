package org.rboug.application.elibrary.service;

import org.rboug.application.elibrary.model.Author;
import org.rboug.application.elibrary.model.Language;

import java.util.List;

public interface AuthorServiceInterface {
    /*
     * return true if update is a new book creation or false if simple update
     *
     *@throws Exception
     */
    boolean createOrUpdateAuthor(Author a, Long id) throws Exception;

    Author findById(Long id) throws Exception;

    void remove(Author deletableEntity);

    void refresh();

    List<Author> getAll();

    long getItemsCount(String firstName, String lastName, String bio, Integer age, Language preferredLanguage);

    List<Author> getPageItems(String firstName, String lastName, String bio, Integer age, Language preferredLanguage, int page, int pageSize);
}
