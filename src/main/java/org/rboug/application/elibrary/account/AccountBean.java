package org.rboug.application.elibrary.account;

import de.svenjacobs.loremipsum.LoremIpsum;
import org.rboug.application.elibrary.model.User;
import org.rboug.application.elibrary.model.UserRole;
import org.rboug.application.elibrary.service.AccountServiceInterface;
import org.rboug.application.elibrary.util.PasswordUtils;
import org.rboug.application.elibrary.view.shopping.ShoppingCartBean;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.AlterableContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.AuthenticationStatus;
import javax.security.enterprise.SecurityContext;
import javax.security.enterprise.credential.Credential;
import javax.security.enterprise.credential.Password;
import javax.security.enterprise.credential.UsernamePasswordCredential;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.Objects;
import java.util.Random;

import static javax.security.enterprise.authentication.mechanism.http.AuthenticationParameters.withParams;

@Named
@SessionScoped
//@Transactional
public class AccountBean implements Serializable {

    // ======================================
    // =          Injection Points          =
    // ======================================

    @Inject
    private BeanManager beanManager;

    @PersistenceContext(unitName = "elibraryPU")
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
    @Inject
    private SecurityContext securityContext;
    @Inject
    AccountServiceInterface accountService;
    // ======================================
    // =          Business methods          =
    // ======================================

    //@Transactional
    public String doSignup() {
        // Does the login already exists ?
        if (accountService.userExist(user.getLogin())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Login already exists " + user.getLogin(),
                            "Choose a different login"));
            return null;
        }

        // Everything is ok, we can create the user
        user.setPassword(PasswordUtils.digestPassword(password1));//PasswordUtils.digestPassword(password1)
        accountService.create(user);
        //accountService.refresh(user);
        //login in
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)
                context.getExternalContext().getRequest();


        /*try {
            request.login(user.getLogin(), user.getPassword());
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Error!",
                    "Couldn't connect with this login and password internal error."));
            resetPasswords();
            return null;
        }*/
        try {
            request.login(user.getLogin(), user.getPassword());
        } catch (ServletException e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error " + user.getFirstName(), "login error"));
            return null;
        }

        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Hi " + user.getFirstName(), "Welcome to this elibrary"));
        loggedIn = true;
        if (user.getRole().equals(UserRole.ADMIN))
            admin = true;
        resetPasswords();
        return "/main?faces-redirect=true";
    }

    public String doSignin() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)
                context.getExternalContext().getRequest();
        try {
            request.login(user.getLogin(), PasswordUtils.digestPassword(user.getPassword()));
        } catch (ServletException e) {
            context.addMessage("signinForm:inputPassword", new FacesMessage(FacesMessage.SEVERITY_WARN, "Wrong user/password",
                    "Check your login and password or click on forgot password link."));
            return null;
        }
        User userFound = accountService.findByLoginAndPassword(user.getLogin(), PasswordUtils.digestPassword(user.getPassword()));
        if (Objects.isNull(userFound)) {
            context.addMessage("signinForm:inputPassword", new FacesMessage(FacesMessage.SEVERITY_WARN, "Wrong user/password",
                    "Check your login and password or click on forgot password link."));
            return null;
        }

        user = userFound;

        // If the user is an administrator
        if (user.getRole().equals(UserRole.ADMIN)) {
            admin = true;
        }
        // The user is now logged in
        loggedIn = true;
        if (admin){
            return "/admin/main?faces-redirect=true";
        }
        context.getExternalContext().getFlash().setKeepMessages(true);//keep messages after a redirect
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Welcome back " + user.getFirstName(), "You can browse the catalog"));
        return "/main?faces-redirect=true";
    }

    public String doLogout() {
        AlterableContext ctx = (AlterableContext) beanManager.getContext(SessionScoped.class);
        Bean<?> myBean = beanManager.getBeans(AccountBean.class).iterator().next();
        ctx.destroy(myBean);
        myBean = beanManager.getBeans(ShoppingCartBean.class).iterator().next();
        ctx.destroy(myBean);
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest)
                context.getExternalContext().getRequest();
        try {
            request.logout();
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage("Logout failed."));
        }
        //FacesContext.getCurrentInstance().getExternalContext().invalidateSession(); //for j_security_check
        return "/main";
    }

    public String doForgotPassword() {
        User userFound = accountService.findByEmail(user.getEmail());
        if (Objects.isNull(userFound)) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Unknown email",
                    "Unknown email address"));
            return null;
        } else {
            user = userFound;
            LoremIpsum loremIpsum = new LoremIpsum();
            Random rand = new Random();
            // Obtain a number between [0 - 49].
            int n = rand.nextInt(50);
            String temporaryPassword = loremIpsum.getWords(1, n);
            System.out.println("##" + temporaryPassword);
            user.setPassword(PasswordUtils.digestPassword(temporaryPassword));
            System.out.println("##" + PasswordUtils.digestPassword(temporaryPassword).toString());
            accountService.update(user);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Email sent",
                    "An email has been sent to " + user.getEmail() + " with temporary password: " + temporaryPassword));
            // TODO:send an email with the password "dummyPassword"
            return doLogout();
        }
    }

    public String doUpdateProfile() {
        if (password1 != null && !password1.isEmpty())
            user.setPassword(PasswordUtils.digestPassword(password1));
        accountService.update(user);
        resetPasswords();
        FacesContext context = FacesContext.getCurrentInstance();
        context.getExternalContext().getFlash().setKeepMessages(true);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Profile has been updated for " + user.getFirstName(),
                        "profile is now updated"));
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