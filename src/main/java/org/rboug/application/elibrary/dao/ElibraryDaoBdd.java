package org.rboug.application.elibrary.dao;

public interface ElibraryDaoBdd<ID_TYPE, ENTITY_TYPE> {


    /**
     * Base pour assurer la persistance des entités dans une Bdd
     *
     * @param <ID_TYPE> type de l'identifiant de l'entité
     * @param <ENTITY_TYPE> type de l'entité
     */

    public ENTITY_TYPE create(ENTITY_TYPE entity);

    public ENTITY_TYPE retrieve(ID_TYPE entityId);
    public ENTITY_TYPE update(ENTITY_TYPE entity);

    public void delete(ENTITY_TYPE entity);

    public ENTITY_TYPE getNew();
}
