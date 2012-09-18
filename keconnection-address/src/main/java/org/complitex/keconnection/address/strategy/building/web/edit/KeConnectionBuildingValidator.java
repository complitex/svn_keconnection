/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.address.strategy.building.web.edit;

import java.util.Locale;
import org.apache.wicket.util.visit.IVisit;
import org.apache.wicket.util.visit.IVisitor;
import org.complitex.address.strategy.building.web.edit.BuildingValidator;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.strategy.web.DomainObjectEditPanel;
import org.complitex.dictionary.strategy.web.validate.IValidator;

/**
 *
 * @author Artem
 */
public class KeConnectionBuildingValidator implements IValidator {

    private final IValidator parentBuildingValidator;
    private KeConnectionBuildingEditComponent buildingEditComponent;

    public KeConnectionBuildingValidator(Locale systemLocale) {
        parentBuildingValidator = new BuildingValidator(systemLocale);
    }

    private KeConnectionBuildingEditComponent getEditComponent(DomainObjectEditPanel editPanel) {
        if (buildingEditComponent == null) {
            buildingEditComponent = editPanel.visitChildren(KeConnectionBuildingEditComponent.class,
                    new IVisitor<KeConnectionBuildingEditComponent, KeConnectionBuildingEditComponent>() {

                        @Override
                        public void component(KeConnectionBuildingEditComponent object,
                                IVisit<KeConnectionBuildingEditComponent> visit) {
                            visit.stop(object);
                        }
                    });
        }

        return buildingEditComponent;
    }

    @Override
    public boolean validate(DomainObject object, DomainObjectEditPanel editPanel) {
        boolean valid = parentBuildingValidator.validate(object, editPanel);
        if (valid) {
            KeConnectionBuildingEditComponent editComponent = getEditComponent(editPanel);

            if (editComponent.isBuildingOrganizationAssociationListEmpty()) {
                valid = false;
                editComponent.error(editComponent.getString("building_organization_associations_empty"));
            } else if (editComponent.isBuildingOrganizationAssociationListHasNulls()) {
                valid = false;
                editComponent.error(editComponent.getString("building_organization_associations_has_nulls"));
            }
        }
        return valid;
    }
}
