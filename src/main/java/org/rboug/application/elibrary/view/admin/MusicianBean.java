package org.rboug.application.elibrary.view.admin;

import org.rboug.application.elibrary.model.Musician;

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
 * Backing bean for Musician entities.
 *
 */
@FacesConfig(
        // Activates CDI build-in beans
        version = JSF_2_3
)
@Named
@Stateful
@ConversationScoped
public class MusicianBean implements Serializable {

    private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Musician entities
    */

    private Long id;
    private Musician musician;
    @Inject
    private Conversation conversation;
    @PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    private int page;
    private long count;
    private List<Musician> pageItems;
    private Musician example = new Musician();
    @Resource
    private SessionContext sessionContext;
    private Musician add = new Musician();

    public Long getId() {
        return this.id;
    }

   /*
    * Support updating and deleting Musician entities
    */

    public void setId(Long id) {
        this.id = id;
    }

    public Musician getMusician() {
        return this.musician;
    }

   /*
    * Support searching Musician entities with pagination
    */

    public void setMusician(Musician musician) {
        this.musician = musician;
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
            this.musician = this.example;
        } else {
            this.musician = findById(getId());
        }
    }

    public Musician findById(Long id) {

        return this.entityManager.find(Musician.class, id);
    }

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.entityManager.persist(this.musician);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.musician);
                return "view?faces-redirect=true&id=" + this.musician.getId();
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
            Musician deletableEntity = findById(getId());

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

    public Musician getExample() {
        return this.example;
    }

    public void setExample(Musician example) {
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
        Root<Musician> rootNode = countCriteria.from(Musician.class);
        countCriteria = countCriteria.select(builder.count(rootNode)).where(
                getSearchPredicates(rootNode));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<Musician> criteria = builder.createQuery(Musician.class);
        rootNode = criteria.from(Musician.class);
        TypedQuery<Musician> query = this.entityManager.createQuery(criteria
                .select(rootNode).where(getSearchPredicates(rootNode)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<Musician> root) {

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
        String bio = this.example.getBio();
        if (bio != null && !"".equals(bio)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("bio")),
                    '%' + bio.toLowerCase() + '%'));
        }
        Integer age = this.example.getAge();
        if (age != null && age.intValue() != 0) {
            predicateList.add(builder.equal(root.get("age"), age));
        }
        String preferredInstrument = this.example.getPreferredInstrument();
        if (preferredInstrument != null && !"".equals(preferredInstrument)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("preferredInstrument")),
                    '%' + preferredInstrument.toLowerCase() + '%'));
        }

        return predicateList.toArray(new Predicate[predicateList.size()]);
    }

   /*
    * Support listing and POSTing back Musician entities (e.g. from inside an HtmlSelectOneMenu)
    */

    public List<Musician> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    public List<Musician> getAll() {

        CriteriaQuery<Musician> criteria = this.entityManager
                .getCriteriaBuilder().createQuery(Musician.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(Musician.class))).getResultList();
    }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

    public Converter getConverter() {

        final MusicianBean ejbProxy = this.sessionContext
                .getBusinessObject(MusicianBean.class);

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

                return String.valueOf(((Musician) value).getId());
            }
        };
    }

    public Musician getAdd() {
        return this.add;
    }

    public Musician getAdded() {
        Musician added = this.add;
        this.add = new Musician();
        return added;
    }
}
