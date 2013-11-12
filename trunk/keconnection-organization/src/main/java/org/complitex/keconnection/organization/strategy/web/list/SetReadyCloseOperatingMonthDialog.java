/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.organization.strategy.web.list;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.Model;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;
import org.complitex.keconnection.organization.strategy.entity.Organization;
import org.odlabs.wiquery.ui.dialog.Dialog;

import javax.ejb.EJB;
import java.text.MessageFormat;

/**
 *
 * @author Artem
 */
public abstract class SetReadyCloseOperatingMonthDialog extends Panel {
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeConnectionOrganizationStrategy organizationStrategy;

    private final Dialog dialog;
    private final Label caption;
    private Organization organization;

    public SetReadyCloseOperatingMonthDialog(String id) {
        super(id);

        dialog = new Dialog("dialog") {

            {
                getOptions().putLiteral("width", "auto");
                setMinHeight(1);
            }
        };
        dialog.setModal(true);
        add(dialog);

        caption = new Label("caption", new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                if (organization != null) {
                    return MessageFormat.format(getString("caption"),
                            organizationStrategy.displayDomainObject(organization, getLocale()));
                }
                return null;
            }
        });
        caption.setOutputMarkupId(true);
        dialog.add(caption);

        AjaxCheckBox readyCloseOperatingMonthFlag = new AjaxCheckBox("readyCloseOperatingMonthFlag", new Model<Boolean>()) {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                organizationStrategy.setReadyCloseOperatingMonthFlag(organization);
                close(target);
                onSet(organization, target);
            }
        };
        dialog.add(readyCloseOperatingMonthFlag);
    }

    protected abstract void onSet(Organization organization, AjaxRequestTarget target);

    private void close(AjaxRequestTarget target) {
        dialog.close(target);
    }

    public void open(AjaxRequestTarget target, Organization organization) {
        this.organization = organization;
        target.add(caption);
        dialog.open(target);
    }
}
