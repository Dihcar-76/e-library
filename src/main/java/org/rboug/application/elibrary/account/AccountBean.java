package org.rboug.application.elibrary.account;

import de.svenjacobs.loremipsum.LoremIpsum;

import org.rboug.application.elibrary.model.User;
import org.rboug.application.elibrary.model.UserRole;
import org.rboug.application.elibrary.util.PasswordUtils;
import org.rboug.application.elibrary.view.shopping.ShoppingCartBean;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

@Named
@SessionScoped
@Transactional
public class AccountBean implements Serializable {

    // ======================================
    // =          Injection Points          =
    // ======================================

    @Inject
    private BeanManager beanManager;

    @Inject
    private EntityManager em;


    // ======================================
    // =             Attributes             =
    // ======================================

    // Logged user
    private User user = new User();
    private boolean loggedIn = false;
    private boolean admin;
    private String password1;
    private String password2;

    // ======================================
    // =          Business methods          =
    // ======================================

    @Transactional(dontRollbackOn = IllegalArgumentException.class)
    public String doSignup() {
        // Does the login already exists ?
        if (em.createNamedQuery(User.FIND_BY_LOGIN, User.class).setParameter("login", user.getLogin())
                .getResultList().size() > 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Login already exists " + user.getLogin(),
                            "Choose a different login"));
            return null;
        }

        // Everything is ok, we can create the user
        user.setPassword(PasswordUtils.digestPassword(password1));
        em.persist(user);
        // if (user.getEmail().contains("antonio"))
        //     throw new IllegalArgumentException("Wrong email");

        resetPasswords();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Hi " + user.getFirstName(), "Welcome to this website"));
        loggedIn = true;
        if (user.getRole().equals(UserRole.ADMIN))
            admin = true;
        return "/main";
    }

    public String doSignin() {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_LOGIN_PASSWORD, User.class);
        query.setParameter("login", user.getLogin());
        query.setParameter("password", PasswordUtils.digestPassword(user.getPassword()));
        try {
            user = query.getSingleResult();
            // If the user is an administrator
            if (user.getRole().equals(UserRole.ADMIN)) {
                admin = true;
            }
            // The user is now logged in
            loggedIn = true;
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome back " + user.getFirstName(), "You can now browse the catalog"));
            return "/main?faces-redirect=true";
        } catch (NoResultException e) {
            FacesContext.getCurrentInstance().addMessage("signinForm:inputPassword", new FacesMessage(FacesMessage.SEVERITY_WARN, "Wrong user/password",
                    "Check your inputs or ask for a new password"));
            return null;
        }
    }

    public String doLogout() {
        AlterableContext ctx = (AlterableContext) beanManager.getContext(SessionScoped.class);
        Bean<?> myBean = beanManager.getBeans(AccountBean.class).iterator().next();
        ctx.destroy(myBean);
        myBean = beanManager.getBeans(ShoppingCartBean.class).iterator().next();
        ctx.destroy(myBean);
        return "/main";
    }

    public String doForgotPassword() {
        TypedQuery<User> query = em.createNamedQuery(User.FIND_BY_EMAIL, User.class);
        query.setParameter("email", user.getEmail());
        try {
            user = query.getSingleResult();
            LoremIpsum loremIpsum = new LoremIpsum();
            Random rand = new Random();
            // Obtain a number between [0 - 49].
            int n = rand.nextInt(50);
            String temporaryPassword = loremIpsum.getWords(1, n);
            System.out.println("#################################################################################"+temporaryPassword);
            user.setPassword(PasswordUtils.digestPassword(temporaryPassword));
            System.out.println("#################################################################################"+PasswordUtils.digestPassword(temporaryPassword).toString());
            em.merge(user);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Email sent",
                    "An email has been sent to " + user.getEmail() + " with temporary password :" + temporaryPassword));
            // TODO:send an email with the password "dummyPassword"
            return doLogout();
        } catch (NoResultException e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Unknown email",
                    "This email address is unknonw in our system"));
            return null;
        }
    }

    public String doUpdateProfile() {
        if (password1 != null && !password1.isEmpty())
            user.setPassword(PasswordUtils.digestPassword(password1));
        em.merge(user);
        resetPasswords();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile has been updated for " + user.getFirstName(),
                        null));
        return null;
    }


    private void resetPasswords() {
        password1 = null;
        password2 = null;
    }

    // ======================================
    // =        Getters and Setters         =
    // ======================================

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getPassword1() {
        return password1;
    }

    public void setPassword1(String password1) {
        this.password1 = password1;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    public UserRole[] getRoles() {
        return UserRole.values();
    }
}