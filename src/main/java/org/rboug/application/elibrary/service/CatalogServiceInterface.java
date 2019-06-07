package org.rboug.application.elibrary.service;

import org.rboug.application.elibrary.model.Book;
import org.rboug.application.elibrary.model.Language;

import java.util.List;

public interface CatalogServiceInterface {
    public boolean createOrUpdateBook(Book b, Long id) throws Exception;
    public Book findById(Long id) throws Exception;
    public void remove(Book deletableEntity);
    public void refresh();
    public List<Book> getAll();
    public Long getItemsCount(String title, String description, String isbn, Integer nbOfPage, Language language);
    public List<Book> getPageItems(String title, String description, String isbn, Integer nbOfPage, Language language, int page, int pageSize);
}
