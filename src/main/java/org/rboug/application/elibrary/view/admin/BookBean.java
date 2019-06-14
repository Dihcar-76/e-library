package org.rboug.application.elibrary.view.admin;

import org.primefaces.model.UploadedFile;
import org.rboug.application.elibrary.model.Author;
import org.rboug.application.elibrary.model.Book;
import org.rboug.application.elibrary.service.BookServiceInterface;
import org.rboug.application.elibrary.util.Loggable;
import org.rboug.application.elibrary.util.NumberGenerator;
import org.rboug.application.elibrary.util.ThirteenDigits;

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
import javax.persistence.*;
import java.io.*;
import java.util.List;
import java.util.Objects;
import javax.faces.annotation.FacesConfig;

import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;

/**
 * Backing bean for Book entities.
 *
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
    private String[] selectedAuthors;

    // ======================================
    // =        Getters and Setters         =
    // ======================================

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Book getBook() {
        return this.book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setPageItems(List<Book> pageItems) {
        this.pageItems = pageItems;
    }

    public List<Book> getPageItems() {
        return this.pageItems;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public long getCount() {
        return this.count;
    }

    public List<Book> getAll() {
        return catalogService.getAll();
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

    public String[] getSelectedAuthors() {
        return selectedAuthors;
    }

    public void setSelectedAuthors(String[] selectedAuthors) {
        this.selectedAuthors = selectedAuthors;
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

                } catch (IOException e) {
                    FacesMessage message1 = new FacesMessage("Error upload no image file." + file.getFileName());
                    FacesContext.getCurrentInstance().addMessage(null, message1);
                    e.printStackTrace();
                }
            }
        }
    }

    public String create() {

        this.conversation.begin();
        this.conversation.setTimeout(1800000L);//less than 20 seconds
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
            try {
                this.book = catalogService.findById(getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Book findById(Long id) {
        try {
            return catalogService.findById(id);
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "No book found for this id " + id,
                           e.getMessage()));
            return null;
        }
    }

    @Inject
    BookServiceInterface catalogService;

    /*
     * Support updating and deleting Book entities
     */
    public String updatebis() {
        this.conversation.end();
        uploadImage();//image
        for (Author a : book.getAuthors()) { //authors
            book.addAuthor(a);
        }
        try {
            if (catalogService.createOrUpdateBook(book, id)) {
                return "search?faces-redirect=true";
            } else {
                return "view?faces-redirect=true&id=" + book.getId();
            }

        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public String deletebis() {
        this.conversation.end();
        try {
            Book deletableEntity = catalogService.findById(getId());

            this.catalogService.remove(deletableEntity);
            this.catalogService.refresh();
            return "search?faces-redirect=true";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(e.getMessage()));
            return null;
        }
    }

    public String search() {
        this.page = 0;
        return null;
    }

    /*
     * Support searching Book entities with pagination
     */
    public void paginate() {
        this.count = catalogService.getItemsCount(this.example.getTitle(), this.example.getDescription(),
                this.example.getIsbn(),
                this.example.getNbOfPage(), this.example.getLanguage());
        this.pageItems = catalogService.getPageItems(this.example.getTitle(), this.example.getDescription(),
                this.example.getIsbn(),
                this.example.getNbOfPage(), this.example.getLanguage(),
                this.page, getPageSize());
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
