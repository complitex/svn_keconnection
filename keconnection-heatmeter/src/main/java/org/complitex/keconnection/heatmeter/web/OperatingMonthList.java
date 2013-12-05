package org.complitex.keconnection.heatmeter.web;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.web.component.AjaxFeedbackPanel;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.keconnection.heatmeter.entity.OperatingMonth;
import org.complitex.keconnection.heatmeter.service.OperatingMonthBean;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;

import static org.complitex.dictionary.util.PageUtil.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 01.10.12 18:32
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class OperatingMonthList extends TemplatePage{
    private final Logger log = LoggerFactory.getLogger(OperatingMonthList.class);

    @EJB
    private OperatingMonthBean operatingMonthBean;

    public OperatingMonthList() {
        //Title
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //Filter Model
        FilterWrapper<OperatingMonth> filterWrapper = getTemplateSession().getPreferenceFilter(OperatingMonthList.class.getName(),
                FilterWrapper.of(new OperatingMonth()));
        final IModel<FilterWrapper<OperatingMonth>> filterModel = new CompoundPropertyModel<>(filterWrapper);

        //Filter Form
        final Form filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        //Filter Reset Button
        AjaxButton filterReset = new AjaxButton("filter_reset") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                filterModel.setObject(FilterWrapper.of(new OperatingMonth()));
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

        final String[] properties = new String[]{"id", "organizationId", "beginOm", "updated"};

        //Filter Fields
        filterForm.add(newTextFields("object.", properties));

        //Data Provider
        DataProvider<OperatingMonth> dataProvider = new DataProvider<OperatingMonth>() {
            @Override
            protected Iterable<OperatingMonth> getData(int first, int count) {
                FilterWrapper<OperatingMonth> filterWrapper = filterModel.getObject();

                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.setAscending(getSort().isAscending());

                return operatingMonthBean.getOperatingMonths(filterWrapper);
            }

            @Override
            protected int getSize() {
                return operatingMonthBean.getOperatingMonthsCount(filterModel.getObject());
            }

            @Override
            public IModel<OperatingMonth> model(OperatingMonth object) {
                return new CompoundPropertyModel<>(object);
            }
        };
        dataProvider.setSort("id", SortOrder.DESCENDING);

        //Data Container
        final WebMarkupContainer dataContainer = new WebMarkupContainer("data_container");
        dataContainer.setOutputMarkupId(true);
        filterForm.add(dataContainer);

        //Data View
        DataView dataView = new DataView<OperatingMonth>("data_view", dataProvider) {
            @Override
            protected void populateItem(Item<OperatingMonth> item) {
                item.add(newTextLabels(properties));
            }
        };
        dataContainer.add(dataView);

        //Paging Navigator
        final PagingNavigator paging = new PagingNavigator("paging", dataView, OperatingMonthList.class.getName(), filterForm);
        filterForm.add(paging);

        //Sorting
        filterForm.add(newSorting("header.", dataProvider, dataView, filterForm, "id", "organization_id",
                "begin_om", "updated"));
    }
}
