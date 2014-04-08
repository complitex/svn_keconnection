/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.tarif.strategy.web.edit;

import java.util.List;
import javax.ejb.EJB;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.entity.description.EntityAttributeType;
import org.complitex.dictionary.strategy.web.AbstractComplexAttributesPanel;
import org.complitex.dictionary.strategy.web.DomainObjectAccessUtil;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectComponentUtil;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.keconnection.tarif.strategy.TarifGroupStrategy;
import org.complitex.keconnection.tarif.strategy.TarifStrategy;

/**
 *
 * @author Artem
 */
public final class TarifEditComponent extends AbstractComplexAttributesPanel {

    @EJB
    private TarifGroupStrategy tarifGroupStrategy;
    @EJB
    private TarifStrategy tarifStrategy;

    public TarifEditComponent(String id, boolean disabled) {
        super(id, disabled);
    }

    @Override
    protected void init() {
        final DomainObject tarif = getDomainObject();
        final boolean enabled = !isDisabled() && DomainObjectAccessUtil.canEdit(null, "tarif", tarif);

        final EntityAttributeType tarifGroupAttributeType =
                tarifStrategy.getEntity().getAttributeType(TarifStrategy.TARIF_GROUP);
        final IModel<String> tarifGroupLabelModel =
                DomainObjectComponentUtil.labelModel(tarifGroupAttributeType.getAttributeNames(), getLocale());
        add(new Label("tarifGroupLabel", tarifGroupLabelModel));

        WebMarkupContainer tarifGroupRequiredContainer = new WebMarkupContainer("tarifGroupRequiredContainer");
        tarifGroupRequiredContainer.setVisible(tarifGroupAttributeType.isMandatory());
        add(tarifGroupRequiredContainer);

        final List<DomainObject> allTarifGroups = tarifGroupStrategy.getAll();

        final DomainObjectDisableAwareRenderer tarifGroupRenderer = new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return tarifGroupStrategy.displayDomainObject(object, getLocale());
            }
        };

        IModel<DomainObject> model = new Model<DomainObject>() {

            @Override
            public void setObject(DomainObject tarifGroup) {
                Long tarifGroupId = tarifGroup != null ? tarifGroup.getId() : null;
                tarif.getAttribute(TarifStrategy.TARIF_GROUP).setValueId(tarifGroupId);
            }

            @Override
            public DomainObject getObject() {
                Long tarifGroupId = tarif.getAttribute(TarifStrategy.TARIF_GROUP).getValueId();
                if (tarifGroupId != null) {
                    for (DomainObject tarifGroup : allTarifGroups) {
                        if (tarifGroup.getId().equals(tarifGroupId)) {
                            return tarifGroup;
                        }
                    }
                }
                return null;
            }
        };

        DisableAwareDropDownChoice<DomainObject> tarifGroup =
                new DisableAwareDropDownChoice<DomainObject>("tarifGroup", model, allTarifGroups, tarifGroupRenderer);
        tarifGroup.setRequired(true);
        tarifGroup.setEnabled(enabled);
        tarifGroup.setLabel(tarifGroupLabelModel);
        add(tarifGroup);
    }
}
