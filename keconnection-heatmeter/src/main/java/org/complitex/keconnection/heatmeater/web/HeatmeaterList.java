package org.complitex.keconnection.heatmeater.web;

import org.apache.wicket.Session;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
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
import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.service.IProcessListener;
import org.complitex.dictionary.web.component.AjaxFeedbackPanel;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.keconnection.heatmeater.entity.Heatmeater;
import org.complitex.keconnection.heatmeater.service.HeatmeaterBean;
import org.complitex.keconnection.heatmeater.service.HeatmeaterService;
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
public class HeatmeaterList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(HeatmeaterList.class);

    @EJB
    private HeatmeaterBean heatmeaterBean;

    @EJB
    private HeatmeaterService heatmeaterService;

    private final String[] properties = {"ls", "typeId", "buildingCodeId"};

    private Dialog importDialog;

    public HeatmeaterList() {
        //Title
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //Filter Model
        FilterWrapper<Heatmeater> filterWrapper = getTemplateSession().getPreferenceFilter(HeatmeaterList.class.getName(),
                FilterWrapper.of(new Heatmeater()));
        final IModel<FilterWrapper<Heatmeater>> filterModel = new CompoundPropertyModel<>(filterWrapper);

        //Filter Form
        final Form filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        //Filter Reset Button
        AjaxButton filterReset = new AjaxButton("filter_reset") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                filterModel.setObject(FilterWrapper.of(new Heatmeater()));
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
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //skip
            }
        };
        filterForm.add(filterFind);

        //Filter Fields
        filterForm.add(newTextFields("object.", properties));

        //Selected Heatmeaters Id Map
        final Map<String, Long> selectedIds = new HashMap<>();

        //Data Provider
        DataProvider<Heatmeater> dataProvider = new DataProvider<Heatmeater>() {
            @Override
            protected Iterable<Heatmeater> getData(int first, int count) {
                FilterWrapper<Heatmeater> filterWrapper = filterModel.getObject();

                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.setAscending(getSort().isAscending());

                return heatmeaterBean.getHeatmeaters(filterWrapper);
            }

            @Override
            protected int getSize() {
                return heatmeaterBean.getHeatmeaterCount(filterModel.getObject());
            }

            @Override
            public IModel<Heatmeater> model(Heatmeater object) {
                return new CompoundPropertyModel<>(object);
            }
        };
        dataProvider.setSort("id", SortOrder.DESCENDING);

        //Data Container
        final WebMarkupContainer dataContainer = new WebMarkupContainer("data_container");
        dataContainer.setOutputMarkupId(true);
        filterForm.add(dataContainer);

        //Data View
        DataView dataView = new DataView<Heatmeater>("data_view", dataProvider) {
            @Override
            protected void populateItem(Item<Heatmeater> item) {
                final Long id = item.getModelObject().getId();

                item.add(newTextLabels(properties));

                PageParameters pageParameters = new PageParameters();
                pageParameters.add("id", id);
                item.add(new BookmarkablePageLink<>("edit", HeatmeaterEdit.class, pageParameters));

                item.add(new Link("delete") {
                    @Override
                    public void onClick() {
                        info(getString("info_deleted"));
                        heatmeaterBean.delete(id);
                    }
                });

                //todo add date updated
            }
        };
        dataContainer.add(dataView);

        //Paging Navigator
        filterForm.add(new PagingNavigator("paging", dataView, HeatmeaterList.class.getName(), filterForm));

        //Sorting
        filterForm.add(newSorting("header.", dataProvider, dataView, filterForm, properties));

        //Import Dialog
        importDialog = new Dialog("import_dialog");
        importDialog.setTitle(getString("import_dialog_title"));
        importDialog.setWidth(420);
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

                    IProcessListener<Heatmeater> listener = new IProcessListener<Heatmeater>() {
                        private int processedCount = 0;
                        private int skippedCount = 0;
                        private ThreadContext threadContext = ThreadContext.get(false);

                        @Override
                        public void processed(Heatmeater object) {
                            processedCount++;
                        }

                        @Override
                        public void skip(Heatmeater object) {
                            ThreadContext.restore(threadContext);
                            getSession().info(getStringFormat("info_skipped", object.getLs()));
                            skippedCount++;
                        }

                        @Override
                        public void error(Heatmeater object, Exception e) {
                            ThreadContext.restore(threadContext);
                            getSession().error(getStringFormat("error_upload", e.getMessage()));
                            stopTimer();
                        }

                        @Override
                        public void done() {
                            ThreadContext.restore(threadContext);
                            getSession().info(getStringFormat("info_done", processedCount, skippedCount));
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

                            if (stopTimer.get()){
                                stop();
                            }
                        }
                    });
                    target.add(dataContainer);

                    heatmeaterService.uploadHeatmeaters(inputStream, listener);
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
                setResponsePage(HeatmeaterEdit.class);
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
