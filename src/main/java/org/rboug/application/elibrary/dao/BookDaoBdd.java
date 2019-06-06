package org.rboug.application.elibrary.dao;

import org.rboug.application.elibrary.model.Book;
import org.rboug.application.elibrary.model.Language;
import org.rboug.application.elibrary.view.admin.BookBean;

import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class BookDaoBdd implements ElibraryDaoBdd<Long, Book> {
    @PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Override
    public Book create(Book entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Book retrieve(Long entityId) {
        return null;
    }

    @Override
    public Book update(Book entity) {
        entityManager.merge(entity);
        return  entity;
    }

    @Override
    public void delete(Book entity) {
        entityManager.remove(entity);

    }

    @Override
    public Book getNew() {
        return null;
    }

    public Book findById(Long id) {
        return entityManager.find(Book.class, id);
    }

    public void flush() {
        entityManager.flush();
    }

    public CriteriaBuilder getCriteriaBuilder() {
        return entityManager.getCriteriaBuilder();
    }


    public Query createQuery1(CriteriaQuery<Long> countCriteria) {
        return entityManager.createQuery(countCriteria);
    }

    public TypedQuery<Book> createQuery2(CriteriaQuery<Book> where) {
        return entityManager.createQuery(where);
    }
}
