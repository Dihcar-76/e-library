package org.rboug.application.elibrary.dao;

import javax.ejb.Local;
import javax.ejb.Stateful;
import javax.enterprise.context.ConversationScoped;
import javax.faces.annotation.FacesConfig;
import javax.inject.Named;


public interface ElibraryDao<ID_TYPE, ENTITY_TYPE> {
    ENTITY_TYPE create(ENTITY_TYPE entity);

    ENTITY_TYPE retrieve(ID_TYPE entityId);

    ENTITY_TYPE update(ENTITY_TYPE entity);

    void delete(ENTITY_TYPE entity);

    ENTITY_TYPE getNew();
}
