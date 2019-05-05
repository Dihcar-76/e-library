package org.rboug.application.elibrary.view.admin;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;
import org.rboug.application.elibrary.model.Item;

import javax.ejb.EJB;
import javax.enterprise.context.ConversationScoped;
import javax.faces.annotation.FacesConfig;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.faces.annotation.FacesConfig;
import static javax.faces.annotation.FacesConfig.Version.JSF_2_3;

@FacesConfig(
        // Activates CDI build-in beans
        version = JSF_2_3
)
@Named
@ConversationScoped
public class ItemLazyDataModel extends LazyDataModel<Item> implements Serializable {

@Inject
ItemService itemService;

    public ItemLazyDataModel(){
        System.out.println("--------- "+this+" -----------------");
    }

    @Override
    public List<Item> load(int first, int pageSize, String sortField,
                           SortOrder sortOrder, Map<String, Object> filters) {

        List<Item> list = itemService.getItemList(first, pageSize);
        this.setRowCount(itemService.getItemTotalCount());
        return null;
    }

}
