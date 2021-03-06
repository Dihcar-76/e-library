package org.rboug.application.elibrary.dao;

import org.rboug.application.elibrary.model.Book;
import org.rboug.application.elibrary.model.Language;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class BookDaoBdd implements BookDaoBddInterface {
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
    public Book findById(Long id) {
        return entityManager.find(Book.class, id);
    }

    @Override
    public void refresh() {
        entityManager.flush();
    }

    @Override
    public List<Book> getAll() {
        CriteriaQuery<Book> criteria = this.entityManager.getCriteriaBuilder()
                .createQuery(Book.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(Book.class))).getResultList();
    }

    @Override
    public Long getItemsCount(String title, String description, String isbn, Integer nbOfPage, Language language) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        // Populate count of items

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<Book> rootNode = countCriteria.from(Book.class);
        countCriteria = countCriteria.select(builder.count(rootNode)).where(
                getSearchPredicates(rootNode, title, description, isbn, nbOfPage, language));
        return this.entityManager.createQuery(countCriteria)
                .getSingleResult();
    }

    private Predicate[] getSearchPredicates(Root<Book> root, String title, String description, String isbn, Integer nbOfPage, Language language) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicateList = new ArrayList<>();

        if (title != null && !"".equals(title)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("title")),
                    '%' + title.toLowerCase() + '%'));
        }

        if (description != null && !"".equals(description)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("description")),
                    '%' + description.toLowerCase() + '%'));
        }

        if (isbn != null && !"".equals(isbn)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("isbn")),
                    '%' + isbn.toLowerCase() + '%'));
        }

        if (nbOfPage != null && nbOfPage.intValue() != 0) {
            predicateList.add(builder.equal(root.get("nbOfPage"), nbOfPage));
        }

        if (language != null) {
            predicateList.add(builder.equal(root.get("language"), language));
        }

        return predicateList.toArray(new Predicate[predicateList.size()]);
    }

    @Override
    public List<Book> getPageItems(String title, String description, String isbn, Integer nbOfPage, Language language, int page, int pageSize) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Book> criteria = builder.createQuery(Book.class);
        Root<Book> root = criteria.from(Book.class);
        TypedQuery<Book> query = this.entityManager.createQuery(criteria
                .select(root).where(getSearchPredicates(root, title, description, isbn, nbOfPage, language)));
        query.setFirstResult(page * pageSize).setMaxResults(
                pageSize);
        return query.getResultList();
    }
}
