package org.rboug.application.elibrary.view.admin;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.rboug.application.elibrary.model.Item;
import org.rboug.application.elibrary.util.Loggable;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.PhaseId;
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
import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.annotation.FacesConfig;
import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;

/**
 * Backing bean for Item entities.
 * <p>
 * <p/>
 * <p>
 * This class provides CRUD functionality for all Item entities. It focuses purely on Java EE 6 standards (e.g.
 * <tt>&#64;ConversationScoped</tt> for state management, <tt>PersistenceContext</tt> for persistence,
 * <tt>CriteriaBuilder</tt> for searches) rather than introducing a CRUD framework or custom base class.
 */
@FacesConfig(
        // Activates CDI build-in beans
        version = JSF_2_3
)

@Named
@Stateful
@ConversationScoped
@Loggable
public class ItemBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * Support creating and retrieving Item entities
     */

    private Long id;

    private Item item;

    @Inject
    private Conversation conversation;

    @PersistenceContext(unitName = "applicationCDBookStorePU", type = PersistenceContextType.EXTENDED)

    private EntityManager entityManager;

    private int page;

    private long count;

    private List<Item> pageItems;

    private Item example = new Item();

    private List<Item> allItems = new ArrayList<>();

    private StreamedContent imageFromDB;

    @Resource
    private SessionContext sessionContext;

    private Item add = new Item();

    public Long getId() {
        return this.id;
    }
    /*
     * Support updating and deleting Item entities
     */
    public void setId(Long id) {
        this.id = id;
    }

    public Item getItem() {
        return this.item;
    }

    /*
     * Support searching Item entities with pagination
     */
    public void setItem(Item item) {
        this.item = item;
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
            this.item = this.example;
        } else {
            this.item = findById(getId());
        }
    }

    public Item findById(Long id) {
        return this.entityManager.find(Item.class, id);
    }

    public String update() {
        this.conversation.end();
        try {
            if (this.id == null) {
                this.entityManager.persist(this.item);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.item);
                return "view?faces-redirect=true&id=" + this.item.getId();
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
            Item deletableEntity = findById(getId());
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

    public Item getExample() {
        return this.example;
    }

    public void setExample(Item example) {
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
        Root<Item> root = countCriteria.from(Item.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();
        // Populate this.pageItems
        CriteriaQuery<Item> criteria = builder.createQuery(Item.class);
        root = criteria.from(Item.class);
        TypedQuery<Item> query = this.entityManager.createQuery(criteria
                .select(root).where(getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<Item> root) {
        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicatesList = new ArrayList<>();
        String title = this.example.getTitle();
        if (title != null && !"".equals(title)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("title")),
                    '%' + title.toLowerCase() + '%'));
        }
        String description = this.example.getDescription();
        if (description != null && !"".equals(description)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("description")),
                    '%' + description.toLowerCase() + '%'));
        }
        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

    /*
     * Support listing and POSTing back Item entities (e.g. from inside an HtmlSelectOneMenu)
     */
    public List<Item> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    public List<Item> getAll() {
        CriteriaQuery<Item> criteria = this.entityManager.getCriteriaBuilder()
                .createQuery(Item.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(Item.class))).getResultList();
    }

    /*
     * Support adding children to bidirectional, one-to-many tables
     */
    public Converter getConverter() {
        final ItemBean ejbProxy = this.sessionContext
                .getBusinessObject(ItemBean.class);
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
                return String.valueOf(((Item) value).getId());
            }
        };
    }

    public Item getAdd() {
        return this.add;
    }

    public Item getAdded() {
        Item added = this.add;
        this.add = new Item();
        return added;
    }

    public List<Item> getAllItems() {

        TypedQuery<Item> query = entityManager.createNamedQuery(Item.FIND_ALL, Item.class);
        this.allItems = query.getResultList();
        return allItems;
    }

    public StreamedContent getImageFromDB(){

        FacesContext context = FacesContext.getCurrentInstance();

        if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
            // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
            return new DefaultStreamedContent();
        }
        else {

            // Reading image from database assuming that product image (bytes)
            String id = context.getExternalContext().getRequestParameterMap().get("id");
            Item item = this.findById(Long.valueOf(id));
            byte[] image = item.getSmallImage();
            return new DefaultStreamedContent(new ByteArrayInputStream(image),
                    "image/png");
        }

    }
}