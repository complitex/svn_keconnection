package org.complitex.keconnection.tarif;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LogManager;
import org.complitex.template.web.pages.DomainObjectEdit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import org.complitex.keconnection.tarif.strategy.TarifGroupStrategy;
import org.complitex.template.strategy.TemplateStrategy;

@Singleton(name = "TarifGroupModule")
@Startup
public class Module {

    public static final String NAME = "org.complitex.keconnection.tarifgroup";
    @EJB
    private TarifGroupStrategy tarifGroupStrategy;

    @PostConstruct
    public void init() {
        LogManager.get().registerLink(DomainObject.class.getName(), tarifGroupStrategy.getEntityTable(), DomainObjectEdit.class,
                tarifGroupStrategy.getEditPageParams(null, null, null), TemplateStrategy.OBJECT_ID);
    }
}
