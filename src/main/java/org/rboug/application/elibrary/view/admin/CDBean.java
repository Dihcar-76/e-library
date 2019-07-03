package org.rboug.application.elibrary.view.admin;


import org.primefaces.model.UploadedFile;
import org.rboug.application.elibrary.model.CD;
import org.rboug.application.elibrary.model.Genre;
import org.rboug.application.elibrary.model.Label;

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
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;

/**
 * Backing bean for CD entities.
 *
 */
@FacesConfig(
        // Activates CDI build-in beans
        version = JSF_2_3
)
@Named("CDBean")
@Stateful
@ConversationScoped
public class CDBean implements Serializable {

    private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving CD entities
    */

    private Long id;
    private CD CD;
    @Inject
    private Conversation conversation;
    @PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    private int page;
    private long count;
    private List<CD> pageItems;
    private CD example = new CD();
    @Resource
    private SessionContext sessionContext;
    private CD add = new CD();
    private UploadedFile file;

    public Long getId() {
        return this.id;
    }


    public void setId(Long id) {
        this.id = id;
    }

    public CD getCD() {
        return this.CD;
    }

    public void setCD(CD CD) {
        this.CD = CD;
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

    public CD getExample() {
        return this.example;
    }

    public void setExample(CD example) {
        this.example = example;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }
    // ======================================
    // =        methods         =
    // ======================================

    public void uploadImage() {//FileUploadEvent  event
        if (Objects.nonNull(this.file)) {
            try {
                //this.file = event.getFile();
                InputStream input = this.file.getInputstream();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                for (int length = 0; (length = input.read(buffer)) > 0; ) {
                    output.write(buffer, 0, length);
                }
                this.CD.setSmallImage(output.toByteArray());
                //entityManager.merge(book);
                FacesMessage message = new FacesMessage("Uploaded!" + file.getFileName());
                FacesContext.getCurrentInstance().addMessage(null, message);
            } catch (IOException e) {
                FacesMessage message = new FacesMessage("Error upload." + file.getFileName());
                FacesContext.getCurrentInstance().addMessage(null, message);
                e.printStackTrace();
            }
        } else {
            FacesMessage message = new FacesMessage("Image is empty.");
            FacesContext.getCurrentInstance().addMessage(null, message);
            if (Objects.isNull(CD.getSmallImage())) {
                try {
                    InputStream input = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/noimage.png");
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    for (int length = 0; (length = input.read(buffer)) > 0; ) {
                        output.write(buffer, 0, length);
                    }
                    this.CD.setSmallImage(output.toByteArray());

                } catch (IOException e) {
                    FacesMessage message1 = new FacesMessage("Error upload no image file." + file.getFileName());
                    FacesContext.getCurrentInstance().addMessage(null, message1);
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * Support updating and deleting CD entities
     */
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
            this.CD = this.example;
        } else {
            this.CD = findById(getId());
        }
    }

    public CD findById(Long id) {

        return this.entityManager.find(CD.class, id);
    }

    public String update() {
        this.conversation.end();
        uploadImage();//image
        try {
            if (this.id == null) {
                this.entityManager.persist(this.CD);
                return "search?faces-redirect=true";
            } else {
                this.entityManager.merge(this.CD);
                return "view?faces-redirect=true&id=" + this.CD.getId();
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
            CD deletableEntity = findById(getId());

            this.entityManager.remove(deletableEntity);
            this.entityManager.flush();
            return "search?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(e.getMessage()));
            return null;
        }
    }

    /*
     * Support searching CD entities with pagination
     */

    public String search() {
        this.page = 0;
        return null;
    }

    public void paginate() {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();

        // Populate this.count

        CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
        Root<CD> rootNode = countCriteria.from(CD.class);
        countCriteria = countCriteria.select(builder.count(rootNode)).where(
                getSearchPredicates(rootNode));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<CD> criteria = builder.createQuery(CD.class);
        rootNode = criteria.from(CD.class);
        TypedQuery<CD> query = this.entityManager.createQuery(criteria.select(
                rootNode).where(getSearchPredicates(rootNode)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<CD> root) {

        CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
        List<Predicate> predicateList = new ArrayList<>();

        String title = this.example.getTitle();
        if (title != null && !"".equals(title)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("title")),
                    '%' + title.toLowerCase() + '%'));
        }
        String description = this.example.getDescription();
        if (description != null && !"".equals(description)) {
            predicateList.add(builder.like(
                    builder.lower(root.<String>get("description")),
                    '%' + description.toLowerCase() + '%'));
        }
        Label label = this.example.getLabel();
        if (label != null) {
            predicateList.add(builder.equal(root.get("label"), label));
        }
        Genre genre = this.example.getGenre();
        if (genre != null) {
            predicateList.add(builder.equal(root.get("genre"), genre));
        }

        return predicateList.toArray(new Predicate[predicateList.size()]);
    }

   /*
    * Support listing and POSTing back CD entities (e.g. from inside an HtmlSelectOneMenu)
    */

    public List<CD> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    public List<CD> getAll() {

        CriteriaQuery<CD> criteria = this.entityManager.getCriteriaBuilder()
                .createQuery(CD.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(CD.class))).getResultList();
    }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

    public Converter getConverter() {

        final CDBean ejbProxy = this.sessionContext
                .getBusinessObject(CDBean.class);

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

                return String.valueOf(((CD) value).getId());
            }
        };
    }

    public CD getAdd() {
        return this.add;
    }

    public CD getAdded() {
        CD added = this.add;
        this.add = new CD();
        return added;
    }
}
