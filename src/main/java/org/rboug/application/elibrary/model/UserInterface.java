package org.rboug.application.elibrary.model;

import java.util.Date;

public interface UserInterface {
    public Long getId();

    public void setId(final Long id);

    public int getVersion();

    public void setVersion(final int version);

    public String getFullName();

    public String getFirstName();

    public void setFirstName(String firstName);

    public String getLastName();

    public void setLastName(String lastName);

    public String getTelephone();

    public void setTelephone(String telephone);

    public String getEmail();

    public void setEmail(String email);

    public String getLogin();

    public void setLogin(String login);

    public String getPassword();

    public void setPassword(String password);

    public String getUuid();

    public void setUuid(String uuid);

    public UserRole getRole();

    public void setRole(UserRole role);

    public Date getDateOfBirth();

    public void setDateOfBirth(Date dateOfBirth);
}
