package org.rboug.application.elibrary.dao;

import org.rboug.application.elibrary.model.Country;
import org.rboug.application.elibrary.model.Invoice;
import org.rboug.application.elibrary.model.Item;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;

public class ShoppingCartDaoBdd implements ShoppingCartDaoBddInterface{
    @PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    @Override
    public Item findById(Long itemId) {
        return entityManager.find(Item.class, itemId);
    }

    @Override
    public TypedQuery<Country> findAllCountries() {
        return entityManager.createNamedQuery(Country.FIND_ALL, Country.class);
    }

    @Override
    public void save(Invoice invoice) {
        entityManager.persist(invoice);
    }
}
