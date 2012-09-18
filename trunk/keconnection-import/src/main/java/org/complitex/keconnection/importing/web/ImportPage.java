/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.importing.web;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.ejb.EJB;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.model.util.ListModel;
import org.apache.wicket.util.time.Duration;
import org.complitex.address.entity.AddressImportFile;
import org.complitex.dictionary.entity.IImportFile;
import org.complitex.dictionary.entity.ImportMessage;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.keconnection.importing.service.ImportService;
import org.complitex.keconnection.organization.enity.OrganizationImportFile;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public final class ImportPage extends TemplatePage {

    @EJB
    private ImportService importService;
    @EJB
    private LocaleBean localeBean;
    private final IModel<List<IImportFile>> addressDataModel;
    private final IModel<List<IImportFile>> organizationDataModel;
    private final IModel<Locale> localeModel;

    public ImportPage() {
        add(new Label("title", new ResourceModel("title")));

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupPlaceholderTag(true);
        add(container);

        organizationDataModel = new ListModel<IImportFile>();
        addressDataModel = new ListModel<IImportFile>();

        container.add(new FeedbackPanel("messages"));

        Form<Void> form = new Form<Void>("form");
        container.add(form);

        //Организации
        final List<IImportFile> organizationDataList = Lists.newArrayList();
        Collections.addAll(organizationDataList, OrganizationImportFile.values());

        //Адреса
        final List<IImportFile> addressDataList = new ArrayList<>();
        Collections.addAll(addressDataList, AddressImportFile.values());

        final IChoiceRenderer<IImportFile> renderer = new IChoiceRenderer<IImportFile>() {

            @Override
            public Object getDisplayValue(IImportFile importFile) {
                return importFile.getFileName() + getStatus(importService.getMessage(importFile));
            }

            @Override
            public String getIdValue(IImportFile importFile, int index) {
                return importFile.name();
            }
        };

        form.add(new CheckBoxMultipleChoice<IImportFile>("organizationData", organizationDataModel, organizationDataList, renderer));

        form.add(new CheckBoxMultipleChoice<IImportFile>("addressData", addressDataModel, addressDataList, renderer));

        localeModel = new Model<Locale>(localeBean.getSystemLocale());

        //Кнопка импортировать
        Button process = new Button("process") {

            @Override
            public void onSubmit() {
                if (!importService.isProcessing()) {
                    final Set<IImportFile> allImportFiles = ImmutableSet.<IImportFile>builder().
                            addAll(organizationDataModel.getObject()).addAll(addressDataModel.getObject()).build();
                    importService.process(allImportFiles, localeBean.convert(localeModel.getObject()).getId());
                    container.add(newTimer());
                }
            }

            @Override
            public boolean isVisible() {
                return !importService.isProcessing();
            }
        };
        form.add(process);

        //Ошибки
        container.add(new Label("error", new LoadableDetachableModel<Object>() {

            @Override
            protected Object load() {
                return importService.getErrorMessage();
            }
        }) {

            @Override
            public boolean isVisible() {
                return importService.isError();
            }
        });
    }

    private AjaxSelfUpdatingTimerBehavior newTimer() {
        return new AjaxSelfUpdatingTimerBehavior(Duration.seconds(3)) {

            long stopTimer = 0;

            @Override
            protected void onPostProcessTarget(AjaxRequestTarget target) {
                if (!importService.isProcessing()) {

                    addressDataModel.setObject(null);
                    organizationDataModel.setObject(null);

                    stopTimer++;
                }

                if (stopTimer > 2) {
                    if (importService.isSuccess()) {
                        info(getString("success"));
                    }
                    stop();
                }
            }
        };
    }

    private String getStatus(ImportMessage im) {
        if (im != null) {
            if (!im.isCompleted() && !importService.isProcessing()) {
                return " - " + getStringOrKey("error");
            } else if (im.isCompleted()) {
                return " - " + getStringFormat("complete", im.getIndex());
            } else {
                return " - " + getStringFormat("processing", im.getIndex(), im.getCount());
            }
        }
        return "";
    }
}
