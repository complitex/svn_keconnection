package org.complitex.keconnection.heatmeter.web;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.image.Image;
import org.complitex.keconnection.heatmeter.service.HeatmeterBindService;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.complitex.dictionary.web.component.image.StaticImage;
import com.google.common.base.Joiner;
import com.google.common.io.ByteStreams;
import org.apache.wicket.ThreadContext;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
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
import org.complitex.dictionary.service.IProcessListener;
import org.complitex.dictionary.web.component.AjaxFeedbackPanel;
import org.complitex.dictionary.web.component.EnumDropDownChoice;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.keconnection.heatmeter.entity.*;
import org.complitex.keconnection.heatmeter.service.HeatmeterBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterBindingStatusRenderer;
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
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

import org.complitex.keconnection.heatmeter.web.component.heatmeter.bind.HeatmeterBindError;
import org.complitex.keconnection.heatmeter.web.component.heatmeter.bind.HeatmeterBindPanel;
import static org.complitex.dictionary.util.PageUtil.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 04.09.12 15:25
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class HeatmeterList extends TemplatePage {

    private final static Logger log = LoggerFactory.getLogger(HeatmeterList.class);
    private static final int IMPORT_AJAX_TIMER = 5;
    private static final int BIND_ALL_AJAX_TIMER = 10;
    @EJB
    private HeatmeterBean heatmeterBean;
    @EJB
    private HeatmeterImportService heatmeterImportService;
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;
    @EJB
    private HeatmeterBindingStatusRenderer heatmeterBindingStatusRenderer;
    @EJB
    private HeatmeterBindService heatmeterBindService;
    private Dialog importDialog;
    private final AtomicBoolean stopBindingAllCondition = new AtomicBoolean(true);

    public HeatmeterList() {
        //Title
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //Filter Model
        HeatmeterFilterWrapper filterWrapper = (HeatmeterFilterWrapper) getTemplateSession().getPreferenceFilter(HeatmeterList.class.getName(),
                new HeatmeterFilterWrapper(new Heatmeter()));
        final IModel<HeatmeterFilterWrapper> filterModel = new CompoundPropertyModel<>(filterWrapper);

        //Filter Form
        final Form<?> filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        //Filter Reset Button
        AjaxButton filterReset = new AjaxButton("filter_reset") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                filterModel.setObject(new HeatmeterFilterWrapper(new Heatmeter()));
                filterForm.clearInput();
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
        filterForm.add(newTextFields("object.", "ls"));
        filterForm.add(new EnumDropDownChoice<>("object.type", HeatmeterType.class));
        filterForm.add(new EnumDropDownChoice<>("object.status", HeatmeterPeriodType.class));
        filterForm.add(new DropDownChoice<>("object.bindingStatus",
                Arrays.asList(HeatmeterBindingStatus.class.getEnumConstants()), new IChoiceRenderer<HeatmeterBindingStatus>() {

            @Override
            public String getDisplayValue(HeatmeterBindingStatus status) {
                return heatmeterBindingStatusRenderer.render(status, getLocale());
            }

            @Override
            public String getIdValue(HeatmeterBindingStatus object, int index) {
                return String.valueOf(object.ordinal());
            }
        }).setNullValid(true));

        //Selected Heatmeaters Id Map
        final Map<String, Long> selectedIds = new HashMap<>();

        //Data Provider
        DataProvider<Heatmeter> dataProvider = new DataProvider<Heatmeter>() {

            @Override
            protected Iterable<Heatmeter> getData(int first, int count) {
                HeatmeterFilterWrapper filterWrapper = filterModel.getObject();

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

        final HeatmeterBindPanel heatmeterBindPanel = new HeatmeterBindPanel("heatmeterBindPanel") {

            @Override
            protected void onBind(Heatmeter heatmeter, AjaxRequestTarget target) {
                if (stopBindingAllCondition.get()) {
                    target.add(dataContainer);
                }
            }
        };
        add(heatmeterBindPanel);

        //Data View
        DataView<Heatmeter> dataView = new DataView<Heatmeter>("data_view", dataProvider) {

            @Override
            protected void populateItem(Item<Heatmeter> item) {
                final Heatmeter heatmeter = item.getModelObject();

                item.add(newTextLabels("ls"));

                //building
                List<String> building = new ArrayList<>();
                for (HeatmeterCode hc : heatmeter.getHeatmeterCodes()) {
                    building.add(addressRendererBean.displayBuildingSimple(hc.getBuildingId(), getLocale()));
                }
                final String buildingLabel = Joiner.on("; ").join(building);
                item.add(new Label("buildingId", buildingLabel));

                //organization
                List<String> organization = new ArrayList<>();
                for (HeatmeterCode hc : heatmeter.getHeatmeterCodes()) {
                    organization.add(organizationStrategy.displayShortName(hc.getOrganizationId(), getLocale()));
                }
                item.add(new Label("organizationId", Joiner.on("; ").join(organization)));

                item.add(new Label("type", getStringOrKey(heatmeter.getType())));

                item.add(new Label("status", getStringOrKey(heatmeter.getStatus())));

                item.add(new Label("bindingStatus", heatmeterBindingStatusRenderer.render(heatmeter.getBindingStatus(), getLocale())));

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

                item.add(new AjaxLink<Void>("bindHeatmeterLink") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        heatmeterBindPanel.open(heatmeter, buildingLabel, target);
                    }

                    @Override
                    public boolean isEnabled() {
                        return stopBindingAllCondition.get();
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
        filterForm.add(newSorting("header.", dataProvider, dataView, filterForm, "ls", "type_id", "status"));

        //Import Dialog
        final WebMarkupContainer importDialogContainer = new WebMarkupContainer("import_dialog_container");
        importDialogContainer.setOutputMarkupId(true);
        add(importDialogContainer);

        importDialog = new Dialog("import_dialog");
        importDialog.setTitle(getString("import_dialog_title"));
        importDialog.setWidth(420);
        importDialog.setHeight(80);
        importDialogContainer.add(importDialog);

        Form<?> uploadForm = new Form<>("upload_form");
        importDialog.add(uploadForm);

        final FileUploadField fileUploadField = new FileUploadField("file_upload_field");
        uploadForm.add(fileUploadField);

        uploadForm.add(new AjaxButton("upload") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form form) {
                FileUpload fileUpload = fileUploadField.getFileUpload();

                if (fileUpload == null || fileUpload.getClientFileName() == null) {
                    return;
                }

                importDialog.close(target);

                final AtomicBoolean stopTimer = new AtomicBoolean(false);

                try {
                    InputStream is = fileUpload.getInputStream();
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(ByteStreams.toByteArray(is));

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
                            getSession().info(getStringFormat("info_skipped", object.getLs(), object.getAddress()));
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

                        private void stopTimer() {
                            stopTimer.set(true);
                        }
                    };

                    dataContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(IMPORT_AJAX_TIMER)) {

                        @Override
                        protected void onPostProcessTarget(AjaxRequestTarget target) {
                            target.add(messages);
                            target.add(paging);

                            if (stopTimer.get()) {
                                stop();
                            }
                        }
                    });
                    target.add(dataContainer);

                    heatmeterImportService.asyncUploadHeatmeters(fileUpload.getClientFileName(), inputStream, listener);
                } catch (IOException e) {
                    log.error("Ошибка чтения файла", e);
                }
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                //no errors
            }
        });

        //Bind all section
        {
            final WebMarkupContainer bindAllIndicator = new WebMarkupContainer("bindAllIndicator");
            bindAllIndicator.setOutputMarkupId(true);
            final Image bindAllIndicatorImage = new StaticImage("bindAllIndicatorImage",
                    AbstractDefaultAjaxBehavior.INDICATOR);
            bindAllIndicatorImage.setVisible(false);
            bindAllIndicator.add(bindAllIndicatorImage);
            filterForm.add(bindAllIndicator);

            class BindAllTimerBehavior extends AjaxSelfUpdatingTimerBehavior {

                final AtomicBoolean stopCondition;
                final Component bindAll;

                BindAllTimerBehavior(AtomicBoolean stopCondition, Component bindAll) {
                    super(Duration.seconds(BIND_ALL_AJAX_TIMER));
                    this.stopCondition = stopCondition;
                    this.bindAll = bindAll;
                }

                @Override
                protected void onPostProcessTarget(AjaxRequestTarget target) {
                    target.add(messages);
                    target.add(paging);

                    if (stopCondition.get()) {
                        stop();
                        getComponent().remove(this);
                        bindAllIndicatorImage.setVisible(false);
                        bindAll.setEnabled(true);
                        target.add(bindAll);
                        target.add(bindAllIndicator);
                    }
                }
            }

            AjaxLink<Void> bindAll = new AjaxLink<Void>("bindAll") {

                @Override
                public void onClick(AjaxRequestTarget target) {
                    if (heatmeterBindService.isProcessing()) {
                        return;
                    }

                    bindAllIndicatorImage.setVisible(true);
                    this.setEnabled(false);
                    target.add(bindAllIndicator);
                    target.add(this);

                    stopBindingAllCondition.getAndSet(false);

                    heatmeterBindService.bindAll(new IProcessListener<Heatmeter>() {

                        private int processedCount;
                        private int errorCount;
                        private ThreadContext threadContext = ThreadContext.get(false);

                        @Override
                        public void processed(Heatmeter object) {
                            processedCount++;
                        }

                        @Override
                        public void skip(Heatmeter object) {
                            throw new UnsupportedOperationException("Not supported yet.");
                        }

                        @Override
                        public void error(Heatmeter object, Exception ex) {
                            ThreadContext.restore(threadContext);
                            errorCount++;

                            getSession().error(HeatmeterBindError.message(object, ex, getLocale()));
                        }

                        @Override
                        public void done() {
                            ThreadContext.restore(threadContext);
                            getSession().info(getStringFormat("heatmeter_bind_done", processedCount, errorCount));
                            stopTimer();
                        }

                        private void stopTimer() {
                            stopBindingAllCondition.getAndSet(true);
                        }
                    });

                    dataContainer.add(new BindAllTimerBehavior(stopBindingAllCondition, this));
                    target.add(dataContainer);
                }
            };
            bindAll.setOutputMarkupId(true);
            filterForm.add(bindAll);
        }
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList(
                new AddItemButton(id) {

                    @Override
                    protected void onClick() {
                        setResponsePage(HeatmeterEdit.class);
                    }
                },
                new UploadButton(id, true) {

                    @Override
                    protected void onClick(AjaxRequestTarget target) {
                        importDialog.open(target);
                    }
                });
    }
}
