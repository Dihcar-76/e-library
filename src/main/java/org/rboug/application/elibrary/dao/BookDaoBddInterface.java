package org.rboug.application.elibrary.dao;

import org.rboug.application.elibrary.model.Book;
import org.rboug.application.elibrary.model.Language;

import java.util.List;

public interface BookDaoBddInterface {


    /**
     * Base pour assurer la persistance des entités dans une Bdd
     *
     * @param <ID_TYPE> type de l'identifiant de l'entité
     * @param <ENTITY_TYPE> type de l'entité
     */

    public Book create(Book entity);

    public Book retrieve(Long id);
    public Book update(Book entity);

    public void delete(Book entity);

    public Book getNew();
    public Book findById(Long id);
    public void refresh();
    public List<Book> getAll();
    public Long getItemsCount(String title, String description, String isbn, Integer nbOfPage, Language language);
    public List<Book> getPageItems(String title, String description, String isbn, Integer nbOfPage, Language language, int page, int pageSize);
}
