package org.rboug.application.elibrary.service;

import org.rboug.application.elibrary.dao.ShoppingCartDaoBddInterface;
import org.rboug.application.elibrary.model.Country;
import org.rboug.application.elibrary.model.Invoice;
import org.rboug.application.elibrary.model.Item;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.TypedQuery;

@Stateful
public class ShoppingCartService implements ShoppingCartServiceInterface{
    @Inject
    ShoppingCartDaoBddInterface shoppingCartDaoBdd;
    @Override
    public Item findById(Long itemId) {
        return shoppingCartDaoBdd.findById(itemId);
    }

    @Override
    public TypedQuery<Country> findAllCountries() {
        return shoppingCartDaoBdd.findAllCountries();
    }

    @Override
    public void save(Invoice invoice) {
        shoppingCartDaoBdd.save(invoice);
    }
}
