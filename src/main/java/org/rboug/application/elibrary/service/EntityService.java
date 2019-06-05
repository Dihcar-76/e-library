package org.rboug.application.elibrary.service;

import org.rboug.application.elibrary.model.Author;
import org.rboug.application.elibrary.model.Book;
import org.rboug.application.elibrary.util.Loggable;
import org.rboug.application.elibrary.util.NumberGenerator;
import org.rboug.application.elibrary.util.ThirteenDigits;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.SessionScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import java.io.Serializable;

@Stateful
public class EntityService{
    @PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Inject
    @ThirteenDigits
    private NumberGenerator generator;

    /*
    * return true if update is a new book creation
    *
    *
     */
    public boolean update(Object e, Long id) throws Exception{
            if (id == null) {
                if (e instanceof Book) {
                    Book b = (Book)e;
                    b.setIsbn(generator.generateNumber());
                    for (Author a : b.getAuthors()) {
                        b.addAuthor(a);
                    }
                }
                entityManager.persist(e);
                return true;
            } else {
                entityManager.merge(e);
                return false;
            }
    }
}
