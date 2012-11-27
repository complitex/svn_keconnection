/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.importing.web;

import org.apache.wicket.event.IEvent;
import org.complitex.keconnection.heatmeter.entity.HeatmeterImportFile;
import com.google.common.collect.ImmutableList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.CheckBoxMultipleChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
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
import org.complitex.dictionary.web.component.AjaxFeedbackPanel;
import org.complitex.keconnection.heatmeter.service.HeatmeterImportService;
import org.complitex.keconnection.heatmeter.service.TablegramImportService;
import org.complitex.keconnection.importing.service.ImportService;
import org.complitex.keconnection.organization.entity.OrganizationImportFile;
import org.complitex.template.web.component.LocalePicker;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;
import java.util.*;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.event.Broadcast;
import org.complitex.dictionary.web.component.MonthDropDownChoice;

import org.complitex.dictionary.web.component.dateinput.MaskedDateInput;
import static org.complitex.dictionary.util.DateUtil.*;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public final class ImportPage extends TemplatePage {

    @EJB
    private ImportService importService;
    @EJB
    private TablegramImportService tablegramImportService;
    @EJB
    private HeatmeterImportService heatmeterImportService;
    @EJB
    private LocaleBean localeBean;
    private final IModel<List<IImportFile>> addressDataModel;
    private final IModel<List<IImportFile>> organizationDataModel;
    private final IModel<List<IImportFile>> heatmeterDataModel;
    private final IModel<List<IImportFile>> payloadDataModel;
    private final IModel<Locale> localeModel;
    private final IModel<List<String>> warningsModel;

    public ImportPage() {
        add(new Label("title", new ResourceModel("title")));

        final WebMarkupContainer container = new WebMarkupContainer("container");
        container.setOutputMarkupPlaceholderTag(true);
        add(container);

        organizationDataModel = new ListModel<>();
        addressDataModel = new ListModel<>();
        payloadDataModel = new ListModel<>();
        heatmeterDataModel = new ListModel<>();

        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        container.add(messages);

        warningsModel = new ListModel<>(new LinkedList<String>());
        container.add(new ListView<String>("warnings", warningsModel) {

            @Override
            protected void populateItem(ListItem<String> item) {
                final String warning = item.getModelObject();
                item.add(new Label("warning", warning));
            }
        }.setReuseItems(true));

        Form<Void> form = new Form<>("form");
        container.add(form);

        localeModel = new Model<>(localeBean.getSystemLocale());
        form.add(new LocalePicker("localePicker", localeModel, false));

        final IModel<Integer> beginOmModel = new Model<Integer>(getMonth(getCurrentDate()) + 1);
        final MonthDropDownChoice beginOm = new MonthDropDownChoice("beginOm", beginOmModel);
        beginOm.setRequired(true);
        form.add(beginOm);

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

        form.add(new CheckBoxMultipleChoice<>("organizationData",
                organizationDataModel,
                Arrays.asList(OrganizationImportFile.values()),
                renderer));

        form.add(new CheckBoxMultipleChoice<>("addressData",
                addressDataModel,
                Arrays.asList(AddressImportFile.values()),
                renderer));

        final List<HeatmeterImportFile> heatmeterImportFiles = heatmeterImportService.getHeatmeterImportFiles();
        final IModel<Date> beginDateModel = new Model<>();
        final WebMarkupContainer beginDateContainer = new WebMarkupContainer("beginDateContainer") {

            Integer beginOmValue;

            {
                setOutputMarkupPlaceholderTag(true);
                beginOmValue = beginOmModel.getObject();
            }

            @Override
            public void onEvent(IEvent<?> event) {
                if (event.getSource() == beginOm) {
                    this.beginOmValue = (Integer) event.getPayload();
                }
            }

            @Override
            protected void onBeforeRender() {
                if (beginOmValue != null) {
                    removeAll();

                    MaskedDateInput beginDate = new MaskedDateInput("beginDate", beginDateModel);
                    beginDate.setRequired(true);
                    add(beginDate);

                    int year = getYear(getCurrentDate());
                    Date firstDayOfMonth = getFirstDayOfMonth(year, beginOmValue);
                    beginDateModel.setObject(firstDayOfMonth);
                    beginDate.setMinDate(firstDayOfMonth);
                    beginDate.setMaxDate(getLastDayOfMonth(year, beginOmValue));
                }

                beginOmValue = null;
                super.onBeforeRender();
            }
        };
        beginDateContainer.setVisibilityAllowed(heatmeterImportFiles != null && !heatmeterImportFiles.isEmpty());
        form.add(beginDateContainer);

        beginOm.add(new AjaxFormComponentUpdatingBehavior("onchange") {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                if (beginDateContainer.isVisibilityAllowed()) {
                    beginOm.send(beginDateContainer, Broadcast.EXACT, beginOmModel.getObject());
                    target.add(beginDateContainer);
                }
            }
        });

        form.add(new CheckBoxMultipleChoice<>("heatmeterData", heatmeterDataModel, heatmeterImportFiles, renderer));

        form.add(new CheckBoxMultipleChoice<>("payloadData",
                payloadDataModel,
                tablegramImportService.getPayloadImportFiles(),
                renderer));

        //Кнопка импортировать
        Button process = new Button("process") {

            @Override
            public void onSubmit() {
                if (!importService.isProcessing()) {
                    messages.clean();

                    final int currentYear = getYear(getCurrentDate());
                    final int beginOm = beginOmModel.getObject();
                    final Date beginDate = beginDateModel.getObject();

                    //validation
                    boolean validated = true;
                    if (beginDateContainer.isVisibilityAllowed()) {
                        final int beginDateYear = getYear(beginDate);
                        final int beginDateMonth = getMonth(beginDate);

                        if (beginDateYear != currentYear) {
                            validated = false;
                            error(getString("error_wrong_year"));
                        }
                        if (beginDateMonth != beginOm - 1) {
                            validated = false;
                            error(getString("error_wrong_month"));
                        }
                    }

                    if (!validated) {
                        return;
                    }

                    warningsModel.getObject().clear();

                    final List<IImportFile> allImportFiles = ImmutableList.<IImportFile>builder().
                            addAll(organizationDataModel.getObject()).
                            addAll(addressDataModel.getObject()).
                            addAll(heatmeterDataModel.getObject()).
                            addAll(payloadDataModel.getObject()).
                            build();
                    importService.process(allImportFiles, localeBean.convert(localeModel.getObject()).getId(),
                            getFirstDayOfMonth(currentYear, beginOm), beginDate);
                    container.add(newTimer());
                }
            }

            @Override
            public void onError() {
                messages.clean();
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
                String warning;
                while ((warning = importService.getNextWarning()) != null) {
                    //warningsModel.getObject().add(warning);
                    error(warning);
                }

                if (!importService.isProcessing()) {
                    addressDataModel.setObject(null);
                    organizationDataModel.setObject(null);
                    heatmeterDataModel.setObject(null);
                    payloadDataModel.setObject(null);
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
                return " - " + getStringFormat("complete", im.getProcessed());
            } else {
                return " - " + getStringFormat("processing", im.getIndex(), im.getCount());
            }
        }
        return "";
    }
}
