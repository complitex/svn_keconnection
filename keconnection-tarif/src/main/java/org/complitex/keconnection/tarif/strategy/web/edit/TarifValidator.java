/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.tarif.strategy.web.edit;

import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.web.DomainObjectEditPanel;
import org.complitex.dictionary.strategy.web.validate.CodeValidator;
import org.complitex.dictionary.strategy.web.validate.DefaultValidator;
import org.complitex.dictionary.strategy.web.validate.IValidator;
import org.complitex.dictionary.util.EjbBeanLocator;
import org.complitex.keconnection.tarif.strategy.TarifStrategy;

/**
 *
 * @author Artem
 */
public class TarifValidator implements IValidator {

    private static class TarifCodeValidator extends CodeValidator {

        TarifCodeValidator() {
            super("tarif", TarifStrategy.CODE);
        }

        @Override
        protected Long validateCode(Long id, String code) {
            TarifStrategy tarifStrategy = EjbBeanLocator.getBean(TarifStrategy.class);
            return tarifStrategy.validateCode(id, code);
        }
    }
    private final IValidator defaultValidator;
    private final IValidator codeValidator;

    public TarifValidator() {
        defaultValidator = new DefaultValidator("tarif");
        codeValidator = new TarifCodeValidator();
    }

    @Override
    public boolean validate(DomainObject object, DomainObjectEditPanel editPanel) {
        return defaultValidator.validate(object, editPanel) && codeValidator.validate(object, editPanel);
    }
}
