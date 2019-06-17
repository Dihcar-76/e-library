package org.rboug.application.elibrary.model;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;


@Entity
@DiscriminatorColumn(name = "discriminator", discriminatorType = DiscriminatorType.CHAR)
@DiscriminatorValue("I")
@NamedQueries({
        @NamedQuery(name = Item.FIND_TOP_RATED, query = "SELECT i FROM Item i WHERE i.id in :ids"),
        @NamedQuery(name = Item.SEARCH, query = "SELECT i FROM Item i WHERE UPPER(i.title) LIKE :keyword OR UPPER(i.description) LIKE :keyword ORDER BY i.title"),
        @NamedQuery(name = Item.FIND_ALL, query = "SELECT i FROM Item i order by i.title"),
})
public class Item implements Serializable {

    // ======================================
    // =             Constants              =
    // ======================================

    public static final String FIND_TOP_RATED = "Item.findTopRated";
    public static final String SEARCH = "Item.search";
    public static final String FIND_ALL = "Item.findAll";

    // ======================================
    // =             Attributes             =
    // ======================================

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    protected Long id;

    @Version
    @Column(name = "version")
    protected int version;

    @Column(length = 200)
    @NotNull
    @Size(min = 1, max = 200)
    protected String title;

    @Column(length = 10000)
    @Size(min = 1, max = 10000)
    protected String description;

    @Column(name = "unit_cost")
    @Min(1)
    protected Float unitCost;

    protected Integer rank;

    @Column(name = "small_image_url")
    protected String smallImageURL;

    @Column(name = "medium_image_url")
    protected String mediumImageURL;

    private byte[] smallImage;



    // ======================================
    // =        Getters and Setters         =
    // ======================================

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public int getVersion() {
        return this.version;
    }

    public void setVersion(final int version) {
        this.version = version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Float getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(Float unitCost) {
        this.unitCost = unitCost;
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public String getSmallImageURL() {
        return smallImageURL;
    }

    public void setSmallImageURL(String smallImageURL) {
        this.smallImageURL = smallImageURL;
    }

    public String getMediumImageURL() {
        return mediumImageURL;
    }

    public void setMediumImageURL(String mediumImageURL) {
        this.mediumImageURL = mediumImageURL;
    }

    public byte[] getSmallImage() {
        return smallImage;
    }

    public void setSmallImage(byte[] smallImage) {
        this.smallImage = smallImage;
    }

    // ======================================
    // =   Methods hash, equals, toString   =
    // ======================================


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return version == item.version &&
                Objects.equals(id, item.id);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(id);
        return result;
    }

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (id != null)
            result += "id: " + id;
        result += ", version: " + version;
        if (title != null && !title.trim().isEmpty())
            result += ", title: " + title;
        if (description != null && !description.trim().isEmpty())
            result += ", description: " + description;
        if (unitCost != null)
            result += ", unitCost: " + unitCost;
        return result;
    }
}