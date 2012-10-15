package org.complitex.keconnection.heatmeter.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.time.Duration;
import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.service.ContextProcessListener;
import org.complitex.dictionary.web.component.AjaxFeedbackPanel;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.keconnection.heatmeter.entity.Tablegram;
import org.complitex.keconnection.heatmeter.service.TablegramBean;
import org.complitex.keconnection.heatmeter.service.TablegramService;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.complitex.dictionary.util.PageUtil.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 10.10.12 16:38
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class TablegramList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(TablegramList.class);

    @EJB
    private TablegramBean tablegramBean;

    @EJB
    private TablegramService tablegramService;

    public TablegramList() {
        //Title
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //Filter Model
        FilterWrapper<Tablegram> filterWrapper = getTemplateSession().getPreferenceFilter(TablegramList.class.getName(),
                FilterWrapper.of(new Tablegram()));
        final IModel<FilterWrapper<Tablegram>> filterModel = new CompoundPropertyModel<>(filterWrapper);

        //Filter Form
        final Form filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        //Filter Reset Button
        AjaxButton filterReset = new AjaxButton("filter_reset") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                filterModel.setObject(FilterWrapper.of(new Tablegram()));
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

        final String[] properties = new String[]{"id", "fileName", "operatingMonth", "uploaded", "count", "linkedCount",
                "processedCount"};

        //Filter Fields
        filterForm.add(newTextFields("object.", properties));

        //Data Provider
        DataProvider<Tablegram> dataProvider = new DataProvider<Tablegram>() {
            @Override
            protected Iterable<Tablegram> getData(int first, int count) {
                FilterWrapper<Tablegram> filterWrapper = filterModel.getObject();

                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.setAscending(getSort().isAscending());

                return tablegramBean.getTablegrams(filterWrapper);
            }

            @Override
            protected int getSize() {
                return tablegramBean.getTablegramsCount(filterModel.getObject());
            }

            @Override
            public IModel<Tablegram> model(Tablegram object) {
                return new CompoundPropertyModel<>(object);
            }
        };
        dataProvider.setSort("id", SortOrder.DESCENDING);

        //Data Container
        final WebMarkupContainer dataContainer = new WebMarkupContainer("data_container");
        dataContainer.setOutputMarkupId(true);
        filterForm.add(dataContainer);

        //Data View
        DataView dataView = new DataView<Tablegram>("data_view", dataProvider) {
            @Override
            protected void populateItem(Item<Tablegram> item) {
                final Tablegram tablegram = item.getModelObject();

                item.add(newTextLabels(properties));

                PageParameters pageParameters = new PageParameters();
                pageParameters.add("t_id", tablegram.getId());
                BookmarkablePageLink payloadLink = new BookmarkablePageLink<Void>("tablegram_record_link",
                        TablegramRecordList.class, pageParameters);
                payloadLink.add(item.get("fileName"));
                item.add(payloadLink);

                item.add(new AjaxButton("process") {
                    @Override
                    protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                        final AtomicBoolean stopTimer = new AtomicBoolean(false);

                        ContextProcessListener<Tablegram> listener = new ContextProcessListener<Tablegram>() {
                            @Override
                            public void onProcessed(Tablegram tablegram) {
                                getSession().info(getStringFormat("info_processed", tablegram.getFileName()));
                            }

                            @Override
                            public void onSkip(Tablegram tablegram) {
                                //nothing
                            }

                            @Override
                            public void onError(Tablegram tablegram, Exception e) {
                                getSession().error(getStringFormat("error_link", e.getMessage()));
                            }

                            @Override
                            public void onDone() {
                                stopTimer.set(true);
                            }
                        };

                        dataContainer.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(2)){

                            @Override
                            protected void onPostProcessTarget(AjaxRequestTarget target) {
                                target.add(messages);

                                if (stopTimer.get()){
                                    stop();
                                }
                            }
                        });

                        tablegramService.asyncLink(Arrays.asList(tablegram), listener);

                        target.add(dataContainer);
                        target.add(messages);
                    }

                    @Override
                    protected void onError(AjaxRequestTarget target, Form<?> form) {
                        //no errors
                    }
                });
            }
        };
        dataContainer.add(dataView);

        //Paging Navigator
        final PagingNavigator paging = new PagingNavigator("paging", dataView, TablegramList.class.getName(), filterForm);
        filterForm.add(paging);

        //Sorting
        filterForm.add(newSorting("header.", dataProvider, dataView, filterForm, "id", "file_name",
                "operating_month", "uploaded", "count", "linked_count", "processed_count"));
    }
}
