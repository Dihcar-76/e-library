package org.rboug.application.elibrary.view.shopping;

import org.rboug.application.elibrary.model.Author;
import org.rboug.application.elibrary.model.Item;
import org.rboug.application.elibrary.util.Auditable;

import javax.enterprise.context.RequestScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import javax.faces.annotation.FacesConfig;

import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;


@FacesConfig(
        // Activates CDI build-in beans
        version = JSF_2_3
)
@Named
@RequestScoped
@Transactional
public class CatalogBean {

    // ======================================
    // =          Injection Points          =
    // ======================================

    @Inject
    private EntityManager em;

    // ======================================
    // =             Attributes             =
    // ======================================

    private String keyword;
    private List<Item> items;
    private Item item;
    private Long itemId;

    public List<Author> getAuthors() {
        TypedQuery<Author> typedQuery = em.createNamedQuery(Item.FIND_ALL_BOOK_AUTHORS, Author.class);
        typedQuery.setParameter("id", Long.valueOf(FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("id")));
        authors = typedQuery.getResultList();
        return authors;
    }

    public void setAuthors(List<Author> authors) {
        this.authors = authors;
    }

    private List<Author> authors = new ArrayList<>();

    // ======================================
    // =          Business methods          =
    // ======================================

    @Auditable
    public String doSearch() {
        TypedQuery<Item> typedQuery = em.createNamedQuery(Item.SEARCH, Item.class);
        typedQuery.setParameter("keyword", "%" + keyword.toUpperCase() + "%");
        items = typedQuery.getResultList();
        return null;
    }

    public String doViewItemById() {
        item = em.find(Item.class, itemId);
        return null;
    }

    // ======================================
    // =        Getters and Setters         =
    // ======================================

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }
}