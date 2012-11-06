/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.tarif.strategy.web.edit;

import org.complitex.dictionary.strategy.web.validate.CodeValidator;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.web.DomainObjectEditPanel;
import org.complitex.dictionary.strategy.web.validate.DefaultValidator;
import org.complitex.dictionary.strategy.web.validate.IValidator;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.keconnection.tarif.strategy.TarifGroupStrategy;

/**
 *
 * @author Artem
 */
public class TarifGroupValidator implements IValidator {

    private static class TarifGroupCodeValidator extends CodeValidator {

        TarifGroupCodeValidator() {
            super("tarif_group", TarifGroupStrategy.CODE);
        }

        @Override
        protected Long validateCode(Long id, String code) {
            TarifGroupStrategy tarifGroupStrategy = EjbBeanLocator.getBean(TarifGroupStrategy.class);
            return tarifGroupStrategy.validateCode(id, code);
        }
    }
    private final IValidator defaultValidator;
    private final IValidator codeValidator;

    public TarifGroupValidator() {
        defaultValidator = new DefaultValidator("tarif_group");
        codeValidator = new TarifGroupCodeValidator();
    }

    @Override
    public boolean validate(DomainObject object, DomainObjectEditPanel editPanel) {
        return defaultValidator.validate(object, editPanel) && codeValidator.validate(object, editPanel);
    }
}
