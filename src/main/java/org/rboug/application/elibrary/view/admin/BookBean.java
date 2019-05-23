package org.rboug.application.elibrary.view.admin;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.rboug.application.elibrary.model.Author;
import org.rboug.application.elibrary.model.Book;
import org.rboug.application.elibrary.model.Item;
import org.rboug.application.elibrary.model.Language;
import org.rboug.application.elibrary.util.Loggable;
import org.rboug.application.elibrary.util.NumberGenerator;
import org.rboug.application.elibrary.util.ThirteenDigits;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.ejb.Stateful;
import javax.enterprise.context.Conversation;
import javax.enterprise.context.ConversationScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;
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
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;
import java.util.Objects;
import javax.faces.annotation.FacesConfig;

import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;

/**
 * Backing bean for Book entities.
 * <p/>
 * This class provides CRUD functionality for all Book entities. It focuses purely on Java EE 6 standards (e.g.
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
public class BookBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /*
     * Support creating and retrieving Book entities
     */

    private Long id;
    private Book book;
    @Inject
    private Conversation conversation;
    @PersistenceContext(unitName = "elibraryPU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;
    private int page;
    private long count;
    private List<Book> pageItems;
    private Book example = new Book();
    @Resource
    private SessionContext sessionContext;
    private Book add = new Book();
    @Inject
    @ThirteenDigits
    private NumberGenerator generator;

    private UploadedFile file;

    public String[] getSelectedAuthors() {
        return selectedAuthors;
    }

    public void setSelectedAuthors(String[] selectedAuthors) {
        this.selectedAuthors = selectedAuthors;
    }

    private String[] selectedAuthors;

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
    }

    public void noImage() {

    }

    public void upload() {//FileUploadEvent  event
        if (Objects.nonNull(this.file)) {
            try {
                //this.file = event.getFile();

                InputStream input = this.file.getInputstream();
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                for (int length = 0; (length = input.read(buffer)) > 0; ) {
                    output.write(buffer, 0, length);
                }
                this.book.setSmallImage(output.toByteArray());
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
            if (Objects.isNull(book.getSmallImage())) {
                try {
                    InputStream input = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream("/resources/noimage.png");
                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    for (int length = 0; (length = input.read(buffer)) > 0; ) {
                        output.write(buffer, 0, length);
                    }
                    this.book.setSmallImage(output.toByteArray());

                } catch (IOException e1) {
                    FacesMessage message1 = new FacesMessage("Error upload noimage file." + file.getFileName());
                    FacesContext.getCurrentInstance().addMessage(null, message1);
                    e1.printStackTrace();
                }
            }
        }
    }


    /*
     * Support updating and deleting Book entities
     */
    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /*
     * Support searching Book entities with pagination
     */

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
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
            this.book = this.example;
        } else {
            this.book = findById(getId());
        }
    }

    public Book findById(Long id) {

        return this.entityManager.find(Book.class, id);
    }

    public String update() {
        this.conversation.end();
        upload();//image

        try {
            if (id == null) {
                book.setIsbn(generator.generateNumber());
                //System.out.println("Selected Values");
                for (Author a : book.getAuthors()) {
                    book.addAuthor(a);
                    //System.out.println(a);
                }
                entityManager.persist(book);
                return "search?faces-redirect=true";
            } else {
                entityManager.merge(book);
                return "view?faces-redirect=true&id=" + book.getId();
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public String delete() {
        this.conversation.end();

        try {
            Book deletableEntity = findById(getId());

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

    public Book getExample() {
        return this.example;
    }

    public void setExample(Book example) {
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
        Root<Book> root = countCriteria.from(Book.class);
        countCriteria = countCriteria.select(builder.count(root)).where(
                getSearchPredicates(root));
        this.count = this.entityManager.createQuery(countCriteria)
                .getSingleResult();

        // Populate this.pageItems

        CriteriaQuery<Book> criteria = builder.createQuery(Book.class);
        root = criteria.from(Book.class);
        TypedQuery<Book> query = this.entityManager.createQuery(criteria
                .select(root).where(getSearchPredicates(root)));
        query.setFirstResult(this.page * getPageSize()).setMaxResults(
                getPageSize());
        this.pageItems = query.getResultList();
    }

    private Predicate[] getSearchPredicates(Root<Book> root) {

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
        String isbn = this.example.getIsbn();
        if (isbn != null && !"".equals(isbn)) {
            predicatesList.add(builder.like(
                    builder.lower(root.<String>get("isbn")),
                    '%' + isbn.toLowerCase() + '%'));
        }
        Integer nbOfPage = this.example.getNbOfPage();
        if (nbOfPage != null && nbOfPage.intValue() != 0) {
            predicatesList.add(builder.equal(root.get("nbOfPage"), nbOfPage));
        }
        Language language = this.example.getLanguage();
        if (language != null) {
            predicatesList.add(builder.equal(root.get("language"), language));
        }

        return predicatesList.toArray(new Predicate[predicatesList.size()]);
    }

    /*
     * Support listing and POSTing back Book entities (e.g. from inside an HtmlSelectOneMenu)
     */

    public List<Book> getPageItems() {
        return this.pageItems;
    }

    public long getCount() {
        return this.count;
    }

    public List<Book> getAll() {

        CriteriaQuery<Book> criteria = this.entityManager.getCriteriaBuilder()
                .createQuery(Book.class);
        return this.entityManager.createQuery(
                criteria.select(criteria.from(Book.class))).getResultList();
    }

    /*
     * Support adding children to bidirectional, one-to-many tables
     */

    public Converter getConverter() {

        final BookBean ejbProxy = this.sessionContext
                .getBusinessObject(BookBean.class);

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

                return String.valueOf(((Book) value).getId());
            }
        };
    }

    public Book getAdd() {
        return this.add;
    }

    public Book getAdded() {
        Book added = this.add;
        this.add = new Book();
        return added;
    }


}
