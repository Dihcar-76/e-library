package org.rboug.application.elibrary.model;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.io.Serializable;


@Entity
public class Category implements Serializable {

    // ======================================
    // =             Attributes             =
    // ======================================

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Version // UPDATE MYENTITY SET ..., VERSION = VERSION + 1 WHERE ((ID = ?) AND (VERSION = ?)) If the WHERE clause fails to match a record (because the same entity has already been updated by another thread), then the persistence provider will throw an OptimisticLockException.
    @Column(name = "version")
    private int version;

    @Column(length = 100)
    @NotNull
    @Size(max = 100)
    private String name;

    // ======================================
    // =            Constructors            =
    // ======================================

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ======================================
    // =   Methods hash, equals, toString   =
    // ======================================

    @Override
    public String toString() {
        String result = getClass().getSimpleName() + " ";
        if (id != null)
            result += "id: " + id;
        result += ", version: " + version;
        if (name != null && !name.trim().isEmpty())
            result += ", name: " + name;
        return result;
    }
}