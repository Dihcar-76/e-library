package org.rboug.application.elibrary.service;

import org.rboug.application.elibrary.dao.BookDaoBddInterface;
import org.rboug.application.elibrary.model.Book;
import org.rboug.application.elibrary.model.Language;
import org.rboug.application.elibrary.util.NumberGenerator;
import org.rboug.application.elibrary.util.ThirteenDigits;

import javax.ejb.Stateful;
import javax.inject.Inject;

import java.util.List;

@Stateful
public class BookService implements BookServiceInterface {
    @Inject
    BookDaoBddInterface bookDaoBdd;

    @Inject
    @ThirteenDigits
    private NumberGenerator generator;

    /*
    * return true if update is a new book creation or false if simple update
    *
    *@throws Exception
     */
    @Override
    public boolean createOrUpdateBook(Book b, Long id) throws Exception{
            if (id == null) {
                b.setIsbn(generator.generateNumber());
                bookDaoBdd.create(b);
                return true;
            } else {
                bookDaoBdd.update(b);
                return false;
            }
    }

    @Override
    public Book findById(Long id) throws Exception{
        return bookDaoBdd.findById(id);
    }

    @Override
    public void remove(Book deletableEntity) {
        bookDaoBdd.delete(deletableEntity);
    }

    @Override
    public void refresh() {
        bookDaoBdd.refresh();
    }

    @Override
    public List<Book> getAll() {
        return bookDaoBdd.getAll();
    }

    @Override
    public Long getItemsCount(String title, String description, String isbn, Integer nbOfPage, Language language) {
       return bookDaoBdd.getItemsCount(title, description, isbn, nbOfPage, language);
    }

    @Override
    public List<Book> getPageItems(String title, String description, String isbn, Integer nbOfPage, Language language, int page, int pageSize) {
        return bookDaoBdd.getPageItems(title, description, isbn, nbOfPage, language, page, pageSize);
    }
}
