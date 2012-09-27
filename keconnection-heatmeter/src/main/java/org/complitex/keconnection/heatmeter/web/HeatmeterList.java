package org.complitex.keconnection.heatmeter.web;

import org.apache.wicket.ThreadContext;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.upload.FileUpload;
import org.apache.wicket.markup.html.form.upload.FileUploadField;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;
import org.complitex.address.service.AddressRendererBean;
import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.service.IProcessListener;
import org.complitex.dictionary.web.component.AjaxFeedbackPanel;
import org.complitex.dictionary.web.component.EnumDropDownChoice;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPeriodType;
import org.complitex.keconnection.heatmeter.entity.HeatmeterWrapper;
import org.complitex.keconnection.heatmeter.service.HeatmeterBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterImportService;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.complitex.template.web.component.toolbar.AddItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.component.toolbar.UploadButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;
import org.odlabs.wiquery.ui.dialog.Dialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.complitex.dictionary.util.PageUtil.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.09.12 15:25
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class HeatmeterList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(HeatmeterList.class);

    @EJB
    private HeatmeterBean heatmeterBean;

    @EJB
    private HeatmeterImportService heatmeterImportService;

    @EJB
    private AddressRendererBean addressRendererBean;

    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;

    private Dialog importDialog;

    public HeatmeterList() {
        //Title
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //Filter Model
        FilterWrapper<Heatmeter> filterWrapper = getTemplateSession().getPreferenceFilter(HeatmeterList.class.getName(),
                FilterWrapper.of(new Heatmeter()));
        final IModel<FilterWrapper<Heatmeter>> filterModel = new CompoundPropertyModel<>(filterWrapper);

        //Filter Form
        final Form filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        //Filter Reset Button
        AjaxButton filterReset = new AjaxButton("filter_reset") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                filterModel.setObject(FilterWrapper.of(new Heatmeter()));
                target.add(filterForm);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //skip
            }
        };
        filterReset.setDefaultFormProcessing(false);
        filterForm.add(filterReset);

        //Filter Find
        AjaxButton filterFind = new AjaxButton("filter_find") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(filterForm);
                target.add(messages);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(messages);
            }
        };
        filterForm.add(filterFind);

        //Filter Fields
        filterForm.add(newTextFields("object.", "ls", "type", "buildingId", "organizationId"));
        filterForm.add(new EnumDropDownChoice<>("object.status", HeatmeterPeriodType.class));

        //Selected Heatmeaters Id Map
        final Map<String, Long> selectedIds = new HashMap<>();

        //Data Provider
        DataProvider<Heatmeter> dataProvider = new DataProvider<Heatmeter>() {
            @Override
            protected Iterable<Heatmeter> getData(int first, int count) {
                FilterWrapper<Heatmeter> filterWrapper = filterModel.getObject();

                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.setAscending(getSort().isAscending());

                return heatmeterBean.getHeatmeters(filterWrapper);
            }

            @Override
            protected int getSize() {
                return heatmeterBean.getHeatmeterCount(filterModel.getObject());
            }

            @Override
            public IModel<Heatmeter> model(Heatmeter object) {
                return new CompoundPropertyModel<>(object);
            }
        };
        dataProvider.setSort("id", SortOrder.DESCENDING);

        //Data Container
        final WebMarkupContainer dataContainer = new WebMarkupContainer("data_container");
        dataContainer.setOutputMarkupId(true);
        filterForm.add(dataContainer);

        //Data View
        DataView dataView = new DataView<Heatmeter>("data_view", dataProvider) {
            @Override
            protected void populateItem(Item<Heatmeter> item) {
                final Heatmeter heatmeter = item.getModelObject();

                item.add(newTextLabels("ls"));

                item.add(new Label("buildingId", addressRendererBean.displayBuildingSimple(heatmeter.getBuildingId(), getLocale())));

                item.add(new Label("organizationId", organizationStrategy.displayShortName(heatmeter.getOrganizationId(), getLocale())));

                item.add(new Label("type", getStringOrKey(heatmeter.getType())));

                item.add(new Label("status", getStringOrKey(heatmeter.getStatus())));

                PageParameters pageParameters = new PageParameters();
                pageParameters.add("id", heatmeter.getId());
                item.add(new BookmarkablePageLink<>("edit", HeatmeterEdit.class, pageParameters));

                item.add(new Link("delete") {
                    @Override
                    public void onClick() {
                        info(getString("info_deleted"));
                        heatmeterBean.delete(heatmeter.getId());
                    }
                });

                //todo add date updated
            }
        };
        dataContainer.add(dataView);

        //Paging Navigator
        final PagingNavigator paging = new PagingNavigator("paging", dataView, HeatmeterList.class.getName(), filterForm);
        filterForm.add(paging);

        //Sorting
        filterForm.add(newSorting("header.", dataProvider, dataView, filterForm, "ls", "type", "buildingId", "organizationId", "status"));

        //Import Dialog
        importDialog = new Dialog("import_dialog");
        importDialog.setTitle(getString("import_dialog_title"));
        importDialog.setWidth(420);
        importDialog.setHeight(80);
        add(importDialog);

        Form uploadForm = new Form("upload_form");
        importDialog.add(uploadForm);

        final FileUploadField fileUploadField = new FileUploadField("file_upload_field"){
            @Override
            protected boolean forceCloseStreamsOnDetach() {
                return false;
            }
        };
        uploadForm.add(fileUploadField);

        uploadForm.add(new AjaxButton("upload") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                FileUpload fileUpload = fileUploadField.getFileUpload();

                if (fileUpload == null || fileUpload.getClientFileName() == null){
                    return;
                }

                importDialog.close(target);

                final AtomicBoolean stopTimer = new AtomicBoolean(false);

                try {
                    InputStream inputStream = fileUpload.getInputStream();

                    IProcessListener<HeatmeterWrapper> listener = new IProcessListener<HeatmeterWrapper>() {
                        private int processedCount = 0;
                        private int skippedCount = 0;
                        private int errorCount = 0;
                        private ThreadContext threadContext = ThreadContext.get(false);

                        @Override
                        public void processed(HeatmeterWrapper object) {
                            processedCount++;
                        }

                        @Override
                        public void skip(HeatmeterWrapper object) {
                            ThreadContext.restore(threadContext);
                            getSession().info(getStringFormat("info_skipped", object.getLs()));
                            skippedCount++;
                        }

                        @Override
                        public void error(HeatmeterWrapper object, Exception e) {
                            ThreadContext.restore(threadContext);
                            getSession().error(getStringFormat("error_upload", e.getMessage()));
                            errorCount++;
                        }

                        @Override
                        public void done() {
                            ThreadContext.restore(threadContext);
                            getSession().info(getStringFormat("info_done", processedCount, skippedCount, errorCount));
                            stopTimer();
                        }

                        private void stopTimer(){
                            stopTimer.set(true);
                        }
                    };

                    dataContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.ONE_SECOND){
                        @Override
                        protected void onPostProcessTarget(AjaxRequestTarget target) {
                            target.add(messages);
                            target.add(paging);

                            if (stopTimer.get()){
                                stop();
                            }
                        }
                    });
                    target.add(dataContainer);

                    heatmeterImportService.asyncUploadHeatmeters(inputStream, listener);
                } catch (IOException e) {
                    log.error("Ошибка чтения файла", e);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //no errors
            }
        });
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList(new AddItemButton(id) {
            @Override
            protected void onClick() {
                setResponsePage(HeatmeterEdit.class);
            }
        },
        new UploadButton(id, true){
            @Override
            protected void onClick(AjaxRequestTarget target) {
                importDialog.open(target);
            }
        });
    }
}
