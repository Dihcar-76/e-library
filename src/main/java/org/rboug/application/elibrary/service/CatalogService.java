package org.rboug.application.elibrary.service;

import org.rboug.application.elibrary.dao.BookDaoBdd;
import org.rboug.application.elibrary.model.Author;
import org.rboug.application.elibrary.model.Book;
import org.rboug.application.elibrary.model.Language;
import org.rboug.application.elibrary.util.Loggable;
import org.rboug.application.elibrary.util.NumberGenerator;
import org.rboug.application.elibrary.util.ThirteenDigits;
import org.rboug.application.elibrary.view.admin.BookBean;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Stateful
public class CatalogService {
    @Inject
    BookDaoBdd bookDaoBdd;

    @Inject
    @ThirteenDigits
    private NumberGenerator generator;

    /*
    * return true if update is a new book creation or false if simple update
    *
    *@throws Exception
     */
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

    public Book findById(Long id) throws Exception{
        return bookDaoBdd.findById(id);
    }

    public void remove(Book deletableEntity) {
        bookDaoBdd.delete(deletableEntity);
    }

    public void flush() {
        bookDaoBdd.flush();
    }


    public List<Book> getAll() {
        return bookDaoBdd.getAll();
    }

    public Long getItemsCount(String title, String description, String isbn, Integer nbOfPage, Language language) {
       return bookDaoBdd.getItemsCount(title, description, isbn, nbOfPage, language);
    }

    public List<Book> getPageItems(String title, String description, String isbn, Integer nbOfPage, Language language, int page, int pageSize) {
        return bookDaoBdd.getPageItems(title, description, isbn, nbOfPage, language, page, pageSize);
    }
}
