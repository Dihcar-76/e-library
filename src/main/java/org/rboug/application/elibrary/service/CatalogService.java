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
    * return true if update is a new book creation
    *
    *
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

    public CriteriaBuilder getCriteriaBuilder() {
        return bookDaoBdd.getCriteriaBuilder();
    }

    public Query createQuery1(CriteriaQuery<Long> countCriteria) {
        return bookDaoBdd.createQuery1(countCriteria);
    }

    public TypedQuery<Book> createQuery2(CriteriaQuery<Book> where) {
        return bookDaoBdd.createQuery2(where);
    }

    public List<Book> getAll() {

        CriteriaQuery<Book> criteria = this.getCriteriaBuilder()
                .createQuery(Book.class);
        return this.createQuery2(
                criteria.select(criteria.from(Book.class))).getResultList();
    }

    public void paginate(BookBean bookBean) {
        CriteriaBuilder builder = this.getCriteriaBuilder();

        // Populate this.count

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<Book> root = countCriteria.from(Book.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root, bookBean));
        bookBean.setCount((long) this.createQuery1(countCriteria)
                .getSingleResult());

        // Populate this.pageItems

        CriteriaQuery<Book> criteria = builder.createQuery(Book.class);
        root = criteria.from(Book.class);
        TypedQuery<Book> query = this.createQuery2(criteria
                .select(root).where(getSearchPredicates(root, bookBean)));
        query.setFirstResult(bookBean.getPage() * bookBean.getPageSize()).setMaxResults(
                bookBean.getPageSize());
        bookBean.setPageItems(query.getResultList());
    }

    public Predicate[] getSearchPredicates(Root<Book> root, BookBean bookBean) {
        CriteriaBuilder builder = this.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<>();

        String title = bookBean.getExample().getTitle();
        if (title != null && !"".equals(title)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("title")),
                    '%' + title.toLowerCase() + '%'));
        }
        String description = bookBean.getExample().getDescription();
        if (description != null && !"".equals(description)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("description")),
                    '%' + description.toLowerCase() + '%'));
        }
        String isbn = bookBean.getExample().getIsbn();
        if (isbn != null && !"".equals(isbn)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("isbn")),
                    '%' + isbn.toLowerCase() + '%'));
        }
        Integer nbOfPage = bookBean.getExample().getNbOfPage();
        if (nbOfPage != null && nbOfPage.intValue() != 0) {
            predicatesList.add(builder.equal(root.get("nbOfPage"), nbOfPage));
        }
        Language language = bookBean.getExample().getLanguage();
        if (language != null) {
            predicatesList.add(builder.equal(root.get("language"), language));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

    @Resource
    private SessionContext sessionContext;
    public Converter getConverter() {

        final BookBean ejbProxy = sessionContext
                .getBusinessObject(BookBean.class);

        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context,
                                      UIComponent component, String value) {

                return ejbProxy.findById(Long.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context,
                                      UIComponent component, Object value) {

                if (value == null) {
                    return "";
                }

                return String.valueOf(((Book) value).getId());
            }
        };
    }
}
