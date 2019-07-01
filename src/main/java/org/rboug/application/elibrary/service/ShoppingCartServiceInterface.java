package org.rboug.application.elibrary.service;

import org.rboug.application.elibrary.model.Country;
import org.rboug.application.elibrary.model.Invoice;
import org.rboug.application.elibrary.model.Item;

import javax.persistence.TypedQuery;

public interface ShoppingCartServiceInterface {
    public Item findById(Long itemId);

    public TypedQuery<Country> findAllCountries();

    public void save(Invoice invoice);
}
