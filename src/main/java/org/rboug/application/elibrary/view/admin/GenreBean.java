package org.rboug.application.elibrary.view.admin;

import org.rboug.application.elibrary.model.Genre;

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
 * Backing bean for Genre entities.
 *
 */
@FacesConfig(
        // Activates CDI build-in beans
        version = JSF_2_3
)
@Named
@Stateful
@ConversationScoped
public class GenreBean implements Serializable {

    private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Genre entities
    */

    private Long id;
    private Genre genre;
    @Inject
    private Conversation conversation;
    @PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    private int page;
    private long count;
    private List<Genre> pageItems;
    private Genre example = new Genre();
    @Resource
    private SessionContext sessionContext;
    private Genre add = new Genre();

    public Long getId() {
        return this.id;
    }

   /*
    * Support updating and deleting Genre entities
    */

    public void setId(Long id) {
        this.id = id;
    }

    public Genre getGenre() {
        return this.genre;
    }

   /*
    * Support searching Genre entities with pagination
    */

    public void setGenre(Genre genre) {
        this.genre = genre;
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
            this.genre = this.example;
        } else {
            this.genre = findById(getId());
        }
    }

    public Genre findById(Long id) {

        return this.entityManager.find(Genre.class, id);
    }

    public String update() {
        this.conversation.end();

        try {
            if (this.id == null) {
                this.entityManager.persist(this.genre);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.genre);
                return "view?faces-redirect=true&id=" + this.genre.getId();
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
            Genre deletableEntity = findById(getId());

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

    public Genre getExample() {
        return this.example;
    }

    public void setExample(Genre example) {
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
        Root<Genre> rootNode = countCriteria.from(Genre.class);
        countCriteria = countCriteria.select(builder.count(rootNode)).where(
                getSearchPredicates(rootNode));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<Genre> criteria = builder.createQuery(Genre.class);
        rootNode = criteria.from(Genre.class);
        TypedQuery<Genre> query = this.entityManager.createQuery(criteria
                .select(rootNode).where(getSearchPredicates(rootNode)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<Genre> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicateList = new ArrayList<>();

        String name = this.example.getName();
        if (name != null && !"".equals(name)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("name")),
                    '%' + name.toLowerCase() + '%'));
        }

        return predicateList.toArray(new Predicate[predicateList.size()]);
    }

   /*
    * Support listing and POSTing back Genre entities (e.g. from inside an HtmlSelectOneMenu)
    */

    public List<Genre> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    public List<Genre> getAll() {

        CriteriaQuery<Genre> criteria = this.entityManager.getCriteriaBuilder()
                .createQuery(Genre.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(Genre.class))).getResultList();
    }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

    public Converter getConverter() {

        final GenreBean ejbProxy = this.sessionContext
                .getBusinessObject(GenreBean.class);

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

                return String.valueOf(((Genre) value).getId());
            }
        };
    }

    public Genre getAdd() {
        return this.add;
    }

    public Genre getAdded() {
        Genre added = this.add;
        this.add = new Genre();
        return added;
    }
}
