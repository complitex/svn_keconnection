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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.web.component.AjaxFeedbackPanel;
import org.complitex.dictionary.web.component.EnumDropDownChoice;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.keconnection.heatmeter.entity.Payload;
import org.complitex.keconnection.heatmeter.entity.PayloadStatus;
import org.complitex.keconnection.heatmeter.service.PayloadBean;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.util.HashMap;
import java.util.Map;

import static org.complitex.dictionary.util.PageUtil.*;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.09.12 18:48
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class PayloadList extends TemplatePage{
    private final static Logger log = LoggerFactory.getLogger(PayloadList.class);

    @EJB
    private PayloadBean payloadBean;

    public PayloadList(PageParameters pageParameters) {
        Long tablegramId = pageParameters.get("t_id").toLongObject();

        //Title
        add(new Label("title", new ResourceModel("title")));

        //Feedback Panel
        final AjaxFeedbackPanel messages = new AjaxFeedbackPanel("messages");
        add(messages);

        //Filter Model
        FilterWrapper<Payload> filterWrapper = getTemplateSession().getPreferenceFilter(PayloadList.class.getName(),
                FilterWrapper.of(new Payload(tablegramId)));
        final IModel<FilterWrapper<Payload>> filterModel = new CompoundPropertyModel<>(filterWrapper);

        //Filter Form
        final Form filterForm = new Form<>("filter_form", filterModel);
        filterForm.setOutputMarkupId(true);
        add(filterForm);

        //Filter Reset Button
        AjaxButton filterReset = new AjaxButton("filter_reset") {
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                filterModel.setObject(FilterWrapper.of(new Payload()));
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

        //properties
        final String[] properties = new String[]{"ls", "name", "address", "beginDate", "endDate", "operatingMonth",
                "payload1", "payload2", "payload3", "status"};

        //Filter Fields
        filterForm.add(newTextFields("object.", properties));

        filterForm.replace(new EnumDropDownChoice<>("object.status", PayloadStatus.class));

        //Selected Heatmeaters Id Map
        final Map<String, Long> selectedIds = new HashMap<>();

        //Data Provider
        DataProvider<Payload> dataProvider = new DataProvider<Payload>() {
            @Override
            protected Iterable<Payload> getData(int first, int count) {
                FilterWrapper<Payload> filterWrapper = filterModel.getObject();

                filterWrapper.setFirst(first);
                filterWrapper.setCount(count);
                filterWrapper.setSortProperty(getSort().getProperty());
                filterWrapper.setAscending(getSort().isAscending());

                return payloadBean.getPayloads(filterWrapper);
            }

            @Override
            protected int getSize() {
                return payloadBean.getPayloadsCount(filterModel.getObject());
            }

            @Override
            public IModel<Payload> model(Payload object) {
                return new CompoundPropertyModel<>(object);
            }
        };
        dataProvider.setSort("id", SortOrder.DESCENDING);

        //Data Container
        final WebMarkupContainer dataContainer = new WebMarkupContainer("data_container");
        dataContainer.setOutputMarkupId(true);
        filterForm.add(dataContainer);

        //Data View
        DataView dataView = new DataView<Payload>("data_view", dataProvider) {
            @Override
            protected void populateItem(Item<Payload> item) {
                final Payload heatmeter = item.getModelObject();

                item.add(newTextLabels(properties));

            }
        };
        dataContainer.add(dataView);

        //Paging Navigator
        final PagingNavigator paging = new PagingNavigator("paging", dataView, PayloadList.class.getName(), filterForm);
        filterForm.add(paging);

        //Sorting
        filterForm.add(newSorting("header.", dataProvider, dataView, filterForm, true, properties));
    }
}