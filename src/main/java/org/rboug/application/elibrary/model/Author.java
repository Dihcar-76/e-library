package org.rboug.application.elibrary.model;

import javax.persistence.*;


@Entity
@NamedQueries({
        @NamedQuery(name = Author.FIND_ALL, query = "SELECT a FROM Author a order by a.lastName")

})
public class Author extends Artist {

    // ======================================
    // =             Attributes             =
    // ======================================

    public static final String FIND_ALL = "Author.findAll";

    @Column(name = "preferred_language")
    @Convert(converter = LanguageConverter.class)
    private Language preferredLanguage;

    // ======================================
    // =            Constructors            =
    // ======================================

    public Author() {
    }

    public Author(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // ======================================
    // =        Getters and Setters         =
    // ======================================

    public Language getPreferredLanguage() {
        return preferredLanguage;
    }

    public void setPreferredLanguage(Language preferredLanguage) {
        this.preferredLanguage = preferredLanguage;
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
        if (firstName != null && !firstName.trim().isEmpty())
            result += ", firstName: " + firstName;
        if (lastName != null && !lastName.trim().isEmpty())
            result += ", lastName: " + lastName;
        if (bio != null && !bio.trim().isEmpty())
            result += ", bio: " + bio;
        if (dateOfBirth != null)
            result += ", dateOfBirth: " + dateOfBirth;
        if (age != null)
            result += ", age: " + age;
        if (preferredLanguage != null)
            result += ", preferredLanguage: " + preferredLanguage;
        return result;
    }
}