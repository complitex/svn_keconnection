/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.correction;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.dictionary.entity.FilterWrapper;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.keconnection.heatmeter.entity.HeatmeterCorrectionView;
import org.complitex.keconnection.heatmeter.service.HeatmeterCorrectionBean;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.TemplatePage;

import javax.ejb.EJB;

import static org.complitex.dictionary.util.PageUtil.*;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public final class HeatmeterCorrectionList extends TemplatePage {

    @EJB
    private HeatmeterCorrectionBean heatmeterCorrectionBean;

    private FilterWrapper<HeatmeterCorrectionView> newFilter() {
        return FilterWrapper.of(new HeatmeterCorrectionView());
    }

    public HeatmeterCorrectionList() {
        add(new Label("title", new ResourceModel("title")));

        final IModel<FilterWrapper<HeatmeterCorrectionView>> filterModel = new CompoundPropertyModel<>(newFilter());

        final Form<?> form = new Form<>("form", filterModel);
        form.setOutputMarkupId(true);
        add(form);

        AjaxLink<Void> reset = new AjaxLink<Void>("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterModel.setObject(newFilter());
                form.clearInput();
                target.add(form);
            }
        };
        form.add(reset);

        AjaxButton find = new AjaxButton("find") {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(form);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        form.add(find);

        form.add(newTextFields("object.", "ls", "externalHeatmeterId", "heatmeterNumber"));

        DataProvider<HeatmeterCorrectionView> dataProvider = new DataProvider<HeatmeterCorrectionView>() {

            @Override
            protected Iterable<HeatmeterCorrectionView> getData(long first, long count) {
                FilterWrapper<HeatmeterCorrectionView> filter = filterModel.getObject();

                filter.setFirst(first);
                filter.setCount(count);
                filter.setSortProperty(getSort().getProperty());
                filter.setAscending(getSort().isAscending());

                return heatmeterCorrectionBean.find(filter);
            }

            @Override
            protected int getSize() {
                return heatmeterCorrectionBean.count(filterModel.getObject());
            }

            @Override
            public IModel<HeatmeterCorrectionView> model(HeatmeterCorrectionView object) {
                return new CompoundPropertyModel<>(object);
            }
        };
        dataProvider.setSort("heatmeter_ls", SortOrder.DESCENDING);

        final WebMarkupContainer dataContainer = new WebMarkupContainer("dataContainer");
        form.add(dataContainer);

        DataView<HeatmeterCorrectionView> dataView = new DataView<HeatmeterCorrectionView>("dataView", dataProvider) {

            @Override
            protected void populateItem(Item<HeatmeterCorrectionView> item) {
                final HeatmeterCorrectionView correction = item.getModelObject();
                item.add(newTextLabels("ls", "externalHeatmeterId", "heatmeterNumber"));
                item.add(new Link<Void>("editLink") {

                    @Override
                    public void onClick() {
                        setResponsePage(HeatmeterCorrectionEdit.class,
                                new PageParameters().add(HeatmeterCorrectionEdit.CORRECTION_ID, correction.getId()));
                    }
                });
            }
        };
        dataContainer.add(dataView);

        final PagingNavigator paging = new PagingNavigator("paging", dataView, form);
        form.add(paging);

        form.add(newSorting("header.", dataProvider, dataView, form, "heatmeter_ls", "external_heatmeter_id",
                "heatmeter_number"));
    }
}
