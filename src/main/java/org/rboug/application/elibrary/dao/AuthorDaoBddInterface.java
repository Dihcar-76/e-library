package org.rboug.application.elibrary.dao;

import org.rboug.application.elibrary.model.Author;
import org.rboug.application.elibrary.model.Language;

import java.util.List;

public interface AuthorDaoBddInterface {
    Author create(Author entity);

    Author retrieve(Long entityId);

    Author update(Author entity);

    void delete(Author entity);

    Author findById(Long id);

    void refresh();

    List<Author> getAll();

    long getItemsCount(String firstName, String lastName, String bio, Integer age, Language preferredLanguage);

    List<Author> getPageItems(String firstName, String lastName, String bio, Integer age, Language preferredLanguage, int page, int pageSize);

}
