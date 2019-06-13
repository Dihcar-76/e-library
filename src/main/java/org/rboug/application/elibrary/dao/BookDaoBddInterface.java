package org.rboug.application.elibrary.dao;

import org.rboug.application.elibrary.model.Book;
import org.rboug.application.elibrary.model.Language;

import java.util.List;

public interface BookDaoBddInterface {


    /**
     * Base pour assurer la persistance des entités book dans une Bdd
     *
     */

    public Book create(Book book);

    public Book retrieve(Long id);

    public Book update(Book book);

    public void delete(Book book);

    public Book findById(Long id);

    public void refresh();

    public List<Book> getAll();

    public Long getItemsCount(String title, String description, String isbn, Integer nbOfPage, Language language);

    public List<Book> getPageItems(String title, String description, String isbn, Integer nbOfPage, Language language, int page, int pageSize);
}
