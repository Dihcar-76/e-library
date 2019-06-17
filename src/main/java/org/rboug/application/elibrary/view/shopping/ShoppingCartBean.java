package org.rboug.application.elibrary.view.shopping;

import org.rboug.application.elibrary.model.*;
import org.rboug.application.elibrary.account.AccountBean;

import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSContext;
import javax.jms.Queue;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@Named
@SessionScoped
@Transactional
public class ShoppingCartBean implements Serializable {

    // ======================================
    // =          Injection Points          =
    // ======================================

    @Inject
    private AccountBean accountBean;

    @Inject
    private EntityManager em;

    // ======================================
    // =             Attributes             =
    // ======================================

    private List<ShoppingCartItem> cartItems = new ArrayList<>();
    private Address address = new Address();
    private String country = new String();
    private CreditCard creditCard = new CreditCard();
    private Long invoice_id;

    // ======================================
    // =          Business methods          =
    // ======================================

    public String addItemToCart() {
        Item item = em.find(Item.class, getParamId("itemId"));

        boolean itemFound = false;
        for (ShoppingCartItem cartItem : cartItems) {
            // If item already exists in the shopping cart we just change the quantity
            if (cartItem.getItem().equals(item)) {
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                itemFound = true;
            }
        }
        if (!itemFound)
            // Otherwise it's added to the shopping cart
            cartItems.add(new ShoppingCartItem(item, 1));

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, item.getTitle() + " added to the shopping cart",
                "You can now add more stuff if you want"));

        return "/shopping/viewItem.xhtml?faces-redirect=true&includeViewParams=true";
    }

    public String removeItemFromCart() {
        Item item = em.find(Item.class, getParamId("itemId"));

        for (ShoppingCartItem cartItem : cartItems) {
            if (cartItem.getItem().equals(item)) {
                cartItems.remove(cartItem);
                return null;
            }
        }

        return null;
    }

    public String updateQuantity() {
        return null;
    }

    public String confirmation() {

        // Creating the invoice
        User user = accountBean.getUser();
        Invoice invoice = new Invoice(user, user.getFirstName(), user.getLastName(), user.getEmail(), address.getStreet1(), address.getCity(), address.getZipcode(), country);
        invoice.setTelephone(user.getTelephone());
        invoice.setStreet2(address.getStreet2());
        for (ShoppingCartItem cartItem : cartItems) {
            invoice.addInvoiceLine(new InvoiceLine(cartItem.getQuantity(), cartItem.getItem().getTitle(), cartItem.getItem().getUnitCost(), cartItem.getItem()));
        }
        //persist invoice
        em.persist(invoice);
        this.invoice_id = invoice.getId();

        // Clear the shopping cart
        cartItems = new ArrayList<>();

        // Displaying the invoice creation
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Order created",
                "You will receive a confirmation email"));
        FacesContext.getCurrentInstance().getExternalContext().getFlash().put("id", invoice_id);
        return "showPdf?faces-redirect=true";
    }

    public List<ShoppingCartItem> getCartItems() {
        return cartItems;
    }

    public boolean shoppingCartIsEmpty() {
        return getCartItems() == null || getCartItems().size() == 0;
    }

    public boolean invoice_idIsNull(){
        return invoice_id==null;
    }

    public Float getTotal() {
        if (cartItems == null || cartItems.isEmpty())
            return 0f;

        Float total = 0f;

        // Sum up the quantities
        for (ShoppingCartItem cartItem : cartItems) {
            total += (cartItem.getSubTotal());
        }
        return total;
    }

    protected Long getParamId(String param) {
        FacesContext context = FacesContext.getCurrentInstance();
        Map<String, String> map = context.getExternalContext().getRequestParameterMap();
        return Long.valueOf(map.get(param));
    }

    // ======================================
    // =        Getters and Setters         =
    // ======================================

    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public CreditCardType[] getCreditCardTypes() {
        return CreditCardType.values();
    }

    public String[] getCountries() {
        TypedQuery<Country> query = em.createNamedQuery(Country.FIND_ALL, Country.class);
        List<Country> countries = query.getResultList();
        String[] result = new String[countries.size()];
        for (int i = 0; i < countries.size(); i++) {
            result[i] = countries.get(i).getName();
        }
        return result;
    }
    public Long getInvoice_id() {
        return invoice_id;
    }

    public void setInvoice_id(Long invoice_id) {
        this.invoice_id = invoice_id;
    }
}