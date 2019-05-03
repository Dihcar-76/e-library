package org.rboug.application.elibrary.view.admin;

import org.rboug.application.elibrary.model.Item;

import javax.ejb.Stateful;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;
import javax.persistence.Query;
import java.util.List;

@Stateless
public class ItemService {

    @PersistenceContext(unitName = "applicationCDBookStorePU", type = PersistenceContextType.EXTENDED)
    private EntityManager entityManager;

    public List<Item> getItemList(int start, int size){
        Query query = entityManager.createQuery("SELECT i FROM Item i");
        query.setFirstResult(start);
        query.setMaxResults(size);
        List<Item> list = query.getResultList();
        return list;
    }

    public int getItemTotalCount() {
        Query query = entityManager.createQuery("SELECT COUNT(i.id) FROM Item i");
        return ((Long)query.getSingleResult()).intValue();
    }

}
