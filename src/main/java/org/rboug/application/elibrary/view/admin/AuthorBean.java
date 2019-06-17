package org.rboug.application.elibrary.view.admin;

import org.rboug.application.elibrary.model.Author;
import org.rboug.application.elibrary.model.Language;
import org.rboug.application.elibrary.service.AuthorServiceInterface;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
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
import javax.faces.annotation.FacesConfig;

import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;

/**
 * Backing bean for Author entities.
 *
 */
@FacesConfig(
        // Activates CDI build-in beans
        version = JSF_2_3
)
@Named
@Stateful
@ConversationScoped
public class AuthorBean implements Serializable {

    private static final long serialVersionUID = 1L;

   /*
    * Support creating and retrieving Author entities
    */

    private Long id;
    private Author author;
    @Inject
    private Conversation conversation;
    /*@PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;*/
    private int page;
    private long count;
    private List<Author> pageItems;
    private Author example = new Author();
    @Resource
    private SessionContext sessionContext;
    private Author add = new Author();

    public Long getId() {
        return this.id;
    }

   /*
    * Support updating and deleting Author entities
    */

    public void setId(Long id) {
        this.id = id;
    }

    public Author getAuthor() {
        return this.author;
    }

   /*
    * Support searching Author entities with pagination
    */

    public void setAuthor(Author author) {
        this.author = author;
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
            this.author = this.example;
        } else {
            try {
                this.author = authorService.findById(getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Inject
    AuthorServiceInterface authorService;

    public Author findById(Long id) {
        try {
            return  authorService.findById(id);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "No author found for this id " + id,
                            e.getMessage()));
            return null;
        }
    }

    public String update() {
        this.conversation.end();
        try {
            if (authorService.createOrUpdateAuthor(author, id)) {
                return "search?faces-redirect=true";
            } else {
                return "view?faces-redirect=true&id=" + author.getId();
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public String delete() {
        this.conversation.end();
        try {
            Author deletableEntity = authorService.findById(getId());

            this.authorService.remove(deletableEntity);
            this.authorService.refresh();
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

    public Author getExample() {
        return this.example;
    }

    public void setExample(Author example) {
        this.example = example;
    }

    public String search() {
        this.page = 0;
        return null;
    }

    public void paginate() {
        this.count = authorService.getItemsCount(this.example.getFirstName(), this.example.getLastName(),
                this.example.getBio(),
                this.example.getAge(), this.example.getPreferredLanguage());
        this.pageItems = authorService.getPageItems(this.example.getFirstName(), this.example.getLastName(),
                this.example.getBio(),
                this.example.getAge(), this.example.getPreferredLanguage(),
                this.page, getPageSize());
    }


   /*
    * Support listing and POSTing back Author entities (e.g. from inside an HtmlSelectOneMenu)
    */

    public List<Author> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    public List<Author> getAll() {

        return authorService.getAll();
    }

   /*
    * Support adding children to bidirectional, one-to-many tables
    */

    public Converter getConverter() {

        final AuthorBean ejbProxy = this.sessionContext
                .getBusinessObject(AuthorBean.class);

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

                return String.valueOf(((Author) value).getId());
            }
        };
    }

    public Author getAdd() {
        return this.add;
    }

    public Author getAdded() {
        Author added = this.add;
        this.add = new Author();
        return added;
    }
}
