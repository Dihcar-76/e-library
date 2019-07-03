package org.rboug.application.elibrary.dao;

import org.rboug.application.elibrary.model.Author;
import org.rboug.application.elibrary.model.Language;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class AuthorDaoBdd implements AuthorDaoBddInterface{
    @PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Override
    public Author create(Author entity) {
        entityManager.persist(entity);
        return entity;
    }

    @Override
    public Author retrieve(Long entityId) {
        return null;
    }

    @Override
    public Author update(Author entity) {
        entityManager.merge(entity);
        return  entity;
    }

    @Override
    public void delete(Author entity) {
        entityManager.remove(entity);

    }

    @Override
    public void refresh() {
        entityManager.flush();
    }

    @Override
    public List<Author> getAll() {
        CriteriaQuery<Author> criteria = this.entityManager.getCriteriaBuilder()
                .createQuery(Author.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(Author.class))).getResultList();
    }

    @Override
    public long getItemsCount(String firstName, String lastName, String bio, Integer age, Language preferredLanguage) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        // Populate count of items

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<Author> rootNode = countCriteria.from(Author.class);
        countCriteria = countCriteria.select(builder.count(rootNode)).where(
                getSearchPredicates(rootNode, firstName, lastName, bio, age, preferredLanguage));
        return this.entityManager.createQuery(countCriteria)
                .getSingleResult();
    }

    private Predicate[] getSearchPredicates(Root<Author> root, String firstName, String lastName, String bio, Integer age, Language preferredLanguage) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicateList = new ArrayList<>();

        if (firstName != null && !"".equals(firstName)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("firstName")),
                    '%' + firstName.toLowerCase() + '%'));
        }

        if (lastName != null && !"".equals(lastName)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("lastName")),
                    '%' + lastName.toLowerCase() + '%'));
        }

        if (bio != null && !"".equals(bio)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("bio")),
                    '%' + bio.toLowerCase() + '%'));
        }

        if (age != null && age.intValue() != 0) {
            predicateList.add(builder.equal(root.get("age"), age));
        }

        if (preferredLanguage != null) {
            predicateList.add(builder.equal(root.get("preferredLanguage"),
                    preferredLanguage));
        }

        return predicateList.toArray(new Predicate[predicateList.size()]);
    }

    @Override
    public List<Author> getPageItems(String firstName, String lastName, String bio, Integer age, Language preferredLanguage, int page, int pageSize) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery<Author> criteria = builder.createQuery(Author.class);
        Root<Author> root = criteria.from(Author.class);
        TypedQuery<Author> query = this.entityManager.createQuery(criteria
                .select(root).where(getSearchPredicates(root, firstName, lastName, bio, age, preferredLanguage)));
        query.setFirstResult(page * pageSize).setMaxResults(
                pageSize);
        return query.getResultList();
    }

    @Override
    public Author findById(Long id) {
        return entityManager.find(Author.class, id);
    }
}
