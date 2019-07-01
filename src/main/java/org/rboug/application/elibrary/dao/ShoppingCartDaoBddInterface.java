package org.rboug.application.elibrary.dao;

import org.rboug.application.elibrary.model.Country;
import org.rboug.application.elibrary.model.Invoice;
import org.rboug.application.elibrary.model.Item;

import javax.persistence.TypedQuery;

public interface ShoppingCartDaoBddInterface {
    public Item findById(Long itemId);

    public TypedQuery<Country> findAllCountries();

    public void save(Invoice invoice);
}
