package org.rboug.application.elibrary.view.admin;

import org.rboug.application.elibrary.model.User;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.annotation.FacesConfig;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;

/**
 * Backing bean for User entities.
 *
 */
@FacesConfig(
        // Activates CDI build-in beans
        version = JSF_2_3
)
@Named
@Stateful
@ConversationScoped
public class UserBean implements Serializable {

    private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving User entities
    */

    private Long id;
    private User user;
    @Inject
    private Conversation conversation;
    @PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    private int page;
    private long count;
    private List<User> pageItems;
    private User example = new User();
    @Resource
    private SessionContext sessionContext;
    private User add = new User();

    public Long getId() {
        return this.id;
    }

   /*
    * Support updating and deleting User entities
    */

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return this.user;
    }

   /*
    * Support searching User entities with pagination
    */

    public void setUser(User user) {
        this.user = user;
    }

    public String create() {

        this.conversation.begin();
        this.conversation.setTimeout(1800000L);
        return "create?faces-redirect=true";
    }

    public void retrieve() {

        if (FacesContext.getCurrentInstance().isPostback()) {
            return;
        }

        if (this.conversation.isTransient()) {
            this.conversation.begin();
            this.conversation.setTimeout(1800000L);
        }

        if (this.id == null) {
            this.user = this.example;
        } else {
            this.user = findById(getId());
        }
    }

    public User findById(Long id) {

        return this.entityManager.find(User.class, id);
    }

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.entityManager.persist(this.user);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.user);
                return "view?faces-redirect=true&id=" + this.user.getId();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public String delete() {
        this.conversation.end();

        try {
            User deletableEntity = findById(getId());

            this.entityManager.remove(deletableEntity);
            this.entityManager.flush();
            return "search?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public int getPage() {
        return this.page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return 10;
    }

    public User getExample() {
        return this.example;
    }

    public void setExample(User example) {
        this.example = example;
    }

    public String search() {
        this.page = 0;
        return null;
    }

    public void paginate() {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        // Populate this.count

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<User> rootNode = countCriteria.from(User.class);
        countCriteria = countCriteria.select(builder.count(rootNode)).where(
                getSearchPredicates(rootNode));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<User> criteria = builder.createQuery(User.class);
        rootNode = criteria.from(User.class);
        TypedQuery<User> query = this.entityManager.createQuery(criteria
                .select(rootNode).where(getSearchPredicates(rootNode)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<User> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicateList = new ArrayList<>();

        String firstName = this.example.getFirstName();
        if (firstName != null && !"".equals(firstName)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("firstName")),
                    '%' + firstName.toLowerCase() + '%'));
        }
        String lastName = this.example.getLastName();
        if (lastName != null && !"".equals(lastName)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("lastName")),
                    '%' + lastName.toLowerCase() + '%'));
        }
        String telephone = this.example.getTelephone();
        if (telephone != null && !"".equals(telephone)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("telephone")),
                    '%' + telephone.toLowerCase() + '%'));
        }
        String email = this.example.getEmail();
        if (email != null && !"".equals(email)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("email")),
                    '%' + email.toLowerCase() + '%'));
        }
        String login = this.example.getLogin();
        if (login != null && !"".equals(login)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("login")),
                    '%' + login.toLowerCase() + '%'));
        }

        return predicateList.toArray(new Predicate[predicateList.size()]);
    }

   /*
    * Support listing and POSTing back User entities (e.g. from inside an HtmlSelectOneMenu)
    */

    public List<User> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    public List<User> getAll() {

        CriteriaQuery<User> criteria = this.entityManager.getCriteriaBuilder()
                .createQuery(User.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(User.class))).getResultList();
    }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

    public Converter getConverter() {

        final UserBean ejbProxy = this.sessionContext
                .getBusinessObject(UserBean.class);

        return new Converter() {

            @Override
            public Object getAsObject(FacesContext context,
                                      UIComponent component, String value) {

                return ejbProxy.findById(Long.valueOf(value));
            }

            @Override
            public String getAsString(FacesContext context,
                                      UIComponent component, Object value) {

                if (value == null) {
                    return "";
                }

                return String.valueOf(((User) value).getId());
            }
        };
    }

    public User getAdd() {
        return this.add;
    }

    public User getAdded() {
        User added = this.add;
        this.add = new User();
        return added;
    }
}
