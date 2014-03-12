/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.correction;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.web.component.DisableAwareDropDownChoice;
import org.complitex.dictionary.web.component.DomainObjectDisableAwareRenderer;
import org.complitex.dictionary.web.component.datatable.ArrowOrderByBorder;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.dictionary.web.component.scroll.ScrollBookmarkablePageLink;
import org.complitex.keconnection.heatmeter.entity.Correction;
import org.complitex.keconnection.heatmeter.entity.example.CorrectionExample;
import org.complitex.keconnection.heatmeter.service.CorrectionBean;
import org.complitex.keconnection.heatmeter.web.correction.component.model.OrganizationModel;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;
import org.complitex.template.web.component.toolbar.AddItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.pages.ScrollListPage;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.ArrayList;
import java.util.List;

/**
 * Абстрактный класс для списка коррекций.
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.AUTHORIZED)
public abstract class AbstractCorrectionList extends ScrollListPage {

    @EJB
    private CorrectionBean correctionBean;
    @EJB
    private LocaleBean localeBean;
    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeConnectionOrganizationStrategy organizationStrategy;
    private String entity;
    private IModel<CorrectionExample> example;

    public AbstractCorrectionList(String entity) {
        this.entity = entity;
        setPreferencesPage(getClass().getName() + "#" + entity);
        init();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(AbstractCorrectionList.class,
                AbstractCorrectionList.class.getSimpleName() + ".css")));
    }

    protected String getEntity() {
        return entity;
    }

    protected void clearExample() {
        example.setObject(newExample());
    }

    protected CorrectionExample newExample() {
        CorrectionExample correctionExample = new CorrectionExample();
        correctionExample.setEntity(entity);
        return correctionExample;
    }

    protected List<? extends Correction> find(CorrectionExample example) {
        return correctionBean.find(example);
    }

    protected int count(CorrectionExample example) {
        return correctionBean.count(example);
    }

    protected String displayCorrection(Correction correction) {
        return correction.getCorrection();
    }

    protected String displayInternalObject(Correction correction) {
        return correction.getDisplayObject();
    }

    protected abstract Class<? extends WebPage> getEditPage();

    protected abstract PageParameters getEditPageParams(Long objectCorrectionId);

    protected abstract IModel<String> getTitleModel();

    protected void init() {
        IModel<String> titleModel = getTitleModel();
        add(new Label("title", titleModel));
        add(new Label("label", titleModel));

        final WebMarkupContainer content = new WebMarkupContainer("content");
        content.setOutputMarkupId(true);
        add(content);
        final Form filterForm = new Form("filterForm");
        content.add(filterForm);

        example = new Model<CorrectionExample>((CorrectionExample) getFilterObject(newExample()));

        final DataProvider<Correction> dataProvider = new DataProvider<Correction>() {

            @Override
            protected Iterable<? extends Correction> getData(long first, long count) {
                final CorrectionExample exampleObject = example.getObject();

                //store preference, but before clear data order related properties.
                {
                    exampleObject.setAsc(false);
                    exampleObject.setOrderByClause(null);
                    setFilterObject(exampleObject);
                }

                exampleObject.setAsc(getSort().isAscending());
                if (!Strings.isEmpty(getSort().getProperty())) {
                    exampleObject.setOrderByClause(getSort().getProperty());
                }
                exampleObject.setStart(first);
                exampleObject.setSize(count);
                exampleObject.setLocaleId(localeBean.convert(getLocale()).getId());
                return find(exampleObject);
            }

            @Override
            protected int getSize() {
                example.getObject().setAsc(getSort().isAscending());
                return count(example.getObject());
            }
        };
        dataProvider.setSort("", SortOrder.ASCENDING);

        final IModel<List<DomainObject>> allOuterOrganizationsModel = new LoadableDetachableModel<List<DomainObject>>() {

            @Override
            protected List<DomainObject> load() {
                return new ArrayList<DomainObject>(organizationStrategy.getAllOuterOrganizations(getLocale()));
            }
        };
        final IModel<DomainObject> outerOrganizationModel = new OrganizationModel() {

            @Override
            public Long getOrganizationId() {
                return example.getObject().getOrganizationId();
            }

            @Override
            public void setOrganizationId(Long organizationId) {
                example.getObject().setOrganizationId(organizationId);
            }

            @Override
            public List<DomainObject> getOrganizations() {
                return allOuterOrganizationsModel.getObject();
            }
        };
        final DomainObjectDisableAwareRenderer organizationRenderer = new DomainObjectDisableAwareRenderer() {

            @Override
            public Object getDisplayValue(DomainObject object) {
                return organizationStrategy.displayDomainObject(object, getLocale());
            }
        };

        filterForm.add(new DisableAwareDropDownChoice<DomainObject>("organizationFilter",
                outerOrganizationModel, allOuterOrganizationsModel, organizationRenderer).setNullValid(true));

        final IModel<List<DomainObject>> allUserOrganizationsModel = new LoadableDetachableModel<List<DomainObject>>() {

            @Override
            protected List<DomainObject> load() {
                return (List<DomainObject>) organizationStrategy.getUserOrganizations(getLocale());
            }
        };
        final IModel<DomainObject> userOrganizationModel = new OrganizationModel() {

            @Override
            public Long getOrganizationId() {
                return example.getObject().getUserOrganizationId();
            }

            @Override
            public void setOrganizationId(Long userOrganizationId) {
                example.getObject().setUserOrganizationId(userOrganizationId);
            }

            @Override
            public List<DomainObject> getOrganizations() {
                return allUserOrganizationsModel.getObject();
            }
        };

        filterForm.add(new DisableAwareDropDownChoice<DomainObject>("userOrganizationFilter",
                userOrganizationModel, allUserOrganizationsModel, organizationRenderer).setNullValid(true));

        filterForm.add(new TextField<String>("correctionFilter", new PropertyModel<String>(example, "correction")));
        filterForm.add(new TextField<String>("codeFilter", new PropertyModel<String>(example, "code")));
        filterForm.add(new TextField<String>("internalObjectFilter", new PropertyModel<String>(example, "internalObject")));

        final List<DomainObject> internalOrganizations = Lists.newArrayList(organizationStrategy.getItselfOrganization());
        IModel<DomainObject> internalOrganizationModel = new OrganizationModel() {

            @Override
            public Long getOrganizationId() {
                return example.getObject().getInternalOrganizationId();
            }

            @Override
            public void setOrganizationId(Long organizationId) {
                example.getObject().setInternalOrganizationId(organizationId);
            }

            @Override
            public List<DomainObject> getOrganizations() {
                return internalOrganizations;
            }
        };

        filterForm.add(new DisableAwareDropDownChoice<DomainObject>("internalOrganizationFilter",
                internalOrganizationModel, internalOrganizations, organizationRenderer).setNullValid(true));

        AjaxLink<Void> reset = new IndicatingAjaxLink<Void>("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                clearExample();
                target.add(content);
            }
        };
        filterForm.add(reset);
        AjaxButton submit = new IndicatingAjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(content);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        filterForm.add(submit);

        DataView<Correction> data = new DataView<Correction>("data", dataProvider, 1) {

            @Override
            protected void populateItem(Item<Correction> item) {
                Correction correction = item.getModelObject();

                item.add(new Label("organization", correction.getOrganization()));
                item.add(new Label("correction", displayCorrection(correction)));

                String code = correction.getCode();
                if (code == null) {
                    code = "";
                }
                item.add(new Label("code", code));

                item.add(new Label("internalObject", displayInternalObject(correction)));

                //user organization
                item.add(new Label("userOrganization", correction.getUserOrganization()));

                item.add(new Label("internalOrganization", correction.getInternalOrganization()));

                ScrollBookmarkablePageLink link = new ScrollBookmarkablePageLink<WebPage>("edit", getEditPage(),
                        getEditPageParams(correction.getId()), String.valueOf(correction.getId()));
                link.setVisible(correction.isEditable());

                item.add(link);
            }
        };
        filterForm.add(data);

        filterForm.add(new ArrowOrderByBorder("organizationHeader", CorrectionBean.OrderBy.ORGANIZATION.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("correctionHeader", CorrectionBean.OrderBy.CORRECTION.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("codeHeader", CorrectionBean.OrderBy.CODE.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("internalObjectHeader", CorrectionBean.OrderBy.OBJECT.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("userOrganizationHeader", CorrectionBean.OrderBy.USER_ORGANIZATION.getOrderBy(), dataProvider, data, content));
        filterForm.add(new ArrowOrderByBorder("internalOrganizationHeader", CorrectionBean.OrderBy.INTERNAL_ORGANIZATION.getOrderBy(), dataProvider,
                data, content));

        content.add(new PagingNavigator("navigator", data, getPreferencesPage() + "#" + entity, content));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(new AddItemButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(getEditPage(), getEditPageParams(null));
            }
        });
    }
}
