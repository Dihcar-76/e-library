package org.rboug.application.elibrary.model;

import java.util.Date;
import java.util.Set;

public interface InvoiceInterface {

    public Long getId();

    public void setId(Long id);

    public int getVersion();

    public void setVersion(int version);

    public Date getInvoiceDate();

    public void setInvoiceDate(Date invoiceDate);

    public int getMonth();

    public Float getTotalBeforeDiscount();

    public void setTotalBeforeDiscount(Float totalBeforeDiscount);

    public Float getVatRate();

    public void setVatRate(Float vatRate);

    public Float getVat();

    public void setVat(Float vat);

    public Float getDiscount();

    public void setDiscount(Float discount);

    public Float getDiscountRate();

    public void setDiscountRate(Float discountRate);

    public Float getTotalAfterDiscount();

    public void setTotalAfterDiscount(Float totalAfterDiscount);

    public Float getTotalAfterVat() ;

    public void setTotalAfterVat(Float totalAfterVat);

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    public String getTelephone();

    public void setTelephone(String telephone);

    public String getEmail();

    public void setEmail(String email);

    public String getStreet1();

    public void setStreet1(String street1);

    public String getStreet2();

    public void setStreet2(String street2);

    public String getCity();

    public void setCity(String city);

    public String getState();

    public void setState(String state);

    public String getZipcode();

    public void setZipcode(String zipcode);

    public String getCountry();

    public void setCountry(String country);

    public Set<InvoiceLine> getInvoiceLines();

    public void setInvoiceLines(Set<InvoiceLine> invoiceLines);

    public void addInvoiceLine(InvoiceLine invoiceLine);
}
