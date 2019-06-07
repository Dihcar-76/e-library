package org.rboug.application.elibrary.model;

import java.util.Date;
import java.util.Set;

public interface BookInterface {
    public String getIsbn();
    public void setIsbn(String isbn);
    public Integer getNbOfPage();
    public void setNbOfPage(Integer nbOfPage);
    public Date getPublicationDate();
    public void setPublicationDate(Date publicationDate);
    public Language getLanguage();
    public void setLanguage(Language language);
    public Publisher getPublisher();
    public void setPublisher(final Publisher publisher);
    public Category getCategory();
    public void setCategory(final Category category);
    public Set<Author> getAuthors();
    public void setAuthors(final Set<Author> authors);
}
