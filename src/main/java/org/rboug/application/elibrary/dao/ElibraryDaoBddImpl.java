package org.rboug.application.elibrary.dao;

public class ElibraryDaoBddImpl<ID_TYPE, ENTITY_TYPE> implements ElibraryDao<ID_TYPE, ENTITY_TYPE>  {


    /**
     * Base pour assurer la persistance des entités dans une Bdd
     *
     * @param <ID_TYPE> type de l'identifiant de l'entité
     * @param <ENTITY_TYPE> type de l'entité
     */

    @Override
    public ENTITY_TYPE create(ENTITY_TYPE entity) {
        return entity;
    }

    @Override
    public ENTITY_TYPE retrieve(ID_TYPE entityId) {
        return null;
    }

    @Override
    public ENTITY_TYPE update(ENTITY_TYPE entity) {
        return entity;
    }

    @Override
    public void delete(ENTITY_TYPE entity) {

    }

    @Override
    public ENTITY_TYPE getNew() {
        return null;
    }
}
