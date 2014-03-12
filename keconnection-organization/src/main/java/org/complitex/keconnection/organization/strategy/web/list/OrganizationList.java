package org.complitex.keconnection.organization.strategy.web.list;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.SortOrder;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.Strings;
import org.complitex.dictionary.entity.example.AttributeExample;
import org.complitex.dictionary.entity.example.DomainObjectExample;
import org.complitex.dictionary.service.LocaleBean;
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.strategy.web.DomainObjectAccessUtil;
import org.complitex.dictionary.util.AttributeUtil;
import org.complitex.dictionary.util.StringUtil;
import org.complitex.dictionary.web.component.ShowMode;
import org.complitex.dictionary.web.component.datatable.ArrowOrderByBorder;
import org.complitex.dictionary.web.component.datatable.DataProvider;
import org.complitex.dictionary.web.component.paging.PagingNavigator;
import org.complitex.dictionary.web.component.scroll.ScrollBookmarkablePageLink;
import org.complitex.dictionary.web.component.search.CollapsibleSearchPanel;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;
import org.complitex.keconnection.organization.strategy.entity.Organization;
import org.complitex.template.web.component.toolbar.AddItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.component.toolbar.search.CollapsibleSearchToolbarButton;
import org.complitex.template.web.pages.DomainObjectList;
import org.complitex.template.web.pages.ScrollListPage;
import org.complitex.template.web.security.SecurityRole;

import javax.ejb.EJB;
import java.util.List;

/**
 *
 * @author Artem
 */
@AuthorizeInstantiation(SecurityRole.ADDRESS_MODULE_VIEW)
public class OrganizationList extends ScrollListPage {
    @EJB
    private LocaleBean localeBean;
    private DomainObjectExample example;
    private WebMarkupContainer content;
    private DataView<Organization> dataView;
    private CollapsibleSearchPanel searchPanel;

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeConnectionOrganizationStrategy organizationStrategy;

    public OrganizationList() {
        init();
    }

    public OrganizationList(PageParameters params) {
        super(params);
        init();
    }

    public DomainObjectExample getExample() {
        return example;
    }

    public void refreshContent(AjaxRequestTarget target) {
        content.setVisible(true);
        if (target != null) {
            dataView.setCurrentPage(0);
            target.add(content);
        }
    }

    private DomainObjectExample newExample() {
        DomainObjectExample e = new DomainObjectExample();
        e.addAttributeExample(new AttributeExample(KeConnectionOrganizationStrategy.NAME));
        e.addAttributeExample(new AttributeExample(KeConnectionOrganizationStrategy.CODE));
        e.addAttributeExample(new AttributeExample(KeConnectionOrganizationStrategy.SHORT_NAME));
        return e;
    }

    private void init() {
        IModel<String> labelModel = new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return organizationStrategy.getPluralEntityLabel(getLocale());
            }
        };

        add(new Label("title", labelModel));
        add(new Label("label", labelModel));

        content = new WebMarkupContainer("content");
        content.setOutputMarkupPlaceholderTag(true);

        //Example
        example = (DomainObjectExample) getFilterObject(newExample());

        //Search
        final List<String> searchFilters = organizationStrategy.getSearchFilters();
        content.setVisible(searchFilters == null || searchFilters.isEmpty());
        add(content);

        final IModel<ShowMode> showModeModel = new Model<ShowMode>(ShowMode.ACTIVE);
        searchPanel = new CollapsibleSearchPanel("searchPanel", getTemplateSession().getGlobalSearchComponentState(),
                searchFilters, organizationStrategy.getSearchCallback(), ShowMode.ALL, true, showModeModel);
        add(searchPanel);
        searchPanel.initialize();

        //Form
        final Form<Void> filterForm = new Form<Void>("filterForm");
        content.add(filterForm);

        //Data Provider
        final DataProvider<Organization> dataProvider = new DataProvider<Organization>() {

            @Override
            protected Iterable<? extends Organization> getData(long first, long count) {
                //store preference, but before clear data order related properties.
                {
                    example.setAsc(false);
                    example.setOrderByAttributeTypeId(null);
                    setFilterObject(example);
                }

                //store state
                getTemplateSession().storeGlobalSearchComponentState();

                boolean asc = getSort().isAscending();
                String sortProperty = getSort().getProperty();

                if (!Strings.isEmpty(sortProperty)) {
                    example.setOrderByAttributeTypeId(Long.valueOf(sortProperty));
                }
                example.setStatus(showModeModel.getObject().name());
                example.setLocaleId(localeBean.convert(getLocale()).getId());
                example.setAsc(asc);
                example.setStart(first);
                example.setSize(count);
                return organizationStrategy.find(example);
            }

            @Override
            protected int getSize() {
                example.setStatus(showModeModel.getObject().name());
                example.setLocaleId(localeBean.convert(getLocale()).getId());
                return organizationStrategy.count(example);
            }
        };
        dataProvider.setSort(String.valueOf(organizationStrategy.getDefaultSortAttributeTypeId()), SortOrder.ASCENDING);

        //Filters
        filterForm.add(new TextField<String>("nameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAttributeExample(KeConnectionOrganizationStrategy.NAME).getValue();
            }

            @Override
            public void setObject(String name) {
                example.getAttributeExample(KeConnectionOrganizationStrategy.NAME).setValue(name);
            }
        }));
        filterForm.add(new TextField<String>("codeFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAttributeExample(KeConnectionOrganizationStrategy.CODE).getValue();
            }

            @Override
            public void setObject(String code) {
                example.getAttributeExample(KeConnectionOrganizationStrategy.CODE).setValue(code);
            }
        }));
        filterForm.add(new TextField<String>("shortNameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAttributeExample(KeConnectionOrganizationStrategy.SHORT_NAME).getValue();
            }

            @Override
            public void setObject(String shortName) {
                example.getAttributeExample(KeConnectionOrganizationStrategy.SHORT_NAME).setValue(shortName);
            }
        }));
        filterForm.add(new TextField<String>("parentShortNameFilter", new Model<String>() {

            @Override
            public String getObject() {
                return example.getAdditionalParam(KeConnectionOrganizationStrategy.PARENT_SHORT_NAME_FILTER);
            }

            @Override
            public void setObject(String parentShortName) {
                example.addAdditionalParam(KeConnectionOrganizationStrategy.PARENT_SHORT_NAME_FILTER, parentShortName);
            }
        }));

        final SetReadyCloseOperatingMonthDialog setReadyCloseOperatingMonthDialog =
                new SetReadyCloseOperatingMonthDialog("setReadyCloseOperatingMonthDialog") {

                    @Override
                    protected void onSet(Organization organization, AjaxRequestTarget target) {
                        target.add(content);
                    }
                };
        add(setReadyCloseOperatingMonthDialog);

        //Data View
        dataView = new DataView<Organization>("data", dataProvider) {

            @Override
            protected void populateItem(Item<Organization> item) {
                final Organization organization = item.getModelObject();

                item.add(new Label("order", StringUtil.valueOf(getFirstItemOffset() + item.getIndex() + 1)));
                item.add(new Label("name", AttributeUtil.getStringCultureValue(organization,
                        KeConnectionOrganizationStrategy.NAME, getLocale())));
                item.add(new Label("code", organizationStrategy.getCode(organization)));
                item.add(new Label("shortName", AttributeUtil.getStringCultureValue(organization,
                        KeConnectionOrganizationStrategy.SHORT_NAME, getLocale())));
                item.add(new Label("parentShortName", organization.getParentShortName()));
                item.add(new Label("om", organization.getOperatingMonth(getLocale())));

                ScrollBookmarkablePageLink<WebPage> detailsLink = new ScrollBookmarkablePageLink<WebPage>("detailsLink",
                        organizationStrategy.getEditPage(),
                        organizationStrategy.getEditPageParams(organization.getId(), null, null),
                        String.valueOf(organization.getId()));
                detailsLink.add(new Label("editMessage", new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        if (DomainObjectAccessUtil.canAddNew(organizationStrategy, "organization")) {
                            return getString("edit");
                        } else {
                            return getString("view");
                        }
                    }
                }));
                item.add(detailsLink);

                //ready close operating month flag
                {
                    final Boolean readyCloseFlag = organization.isReadyCloseOperatingMonth();
                    AjaxLink<Void> setReadyCloseOperatingMonthLink = new AjaxLink<Void>("setReadyCloseOperatingMonthLink") {

                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            setReadyCloseOperatingMonthDialog.open(target, organization);
                        }
                    };
                    setReadyCloseOperatingMonthLink.setVisibilityAllowed(readyCloseFlag != null && !readyCloseFlag);
                    item.add(setReadyCloseOperatingMonthLink);
                }

                //close operating month
                {
                    final Boolean readyCloseFlag = organization.isReadyCloseOperatingMonth();
                    AjaxLink<Void> closeOperatingMonthLink = new AjaxLink<Void>("closeOperatingMonthLink") {

                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            organizationStrategy.closeOperatingMonth(organization);
                            target.add(content);
                        }
                    };
                    closeOperatingMonthLink.setVisibilityAllowed(readyCloseFlag != null && readyCloseFlag);
                    item.add(closeOperatingMonthLink);
                }
            }
        };
        filterForm.add(dataView);

        filterForm.add(new ArrowOrderByBorder("nameHeader",
                String.valueOf(KeConnectionOrganizationStrategy.NAME), dataProvider, dataView, content));
        filterForm.add(new ArrowOrderByBorder("codeHeader",
                String.valueOf(KeConnectionOrganizationStrategy.CODE), dataProvider, dataView, content));
        filterForm.add(new ArrowOrderByBorder("shortNameHeader",
                String.valueOf(KeConnectionOrganizationStrategy.SHORT_NAME), dataProvider, dataView, content));

        //Reset Action
        AjaxLink<Void> reset = new AjaxLink<Void>("reset") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                filterForm.clearInput();
                example.setId(null);
                example.getAttributeExample(KeConnectionOrganizationStrategy.NAME).setValue(null);
                example.getAttributeExample(KeConnectionOrganizationStrategy.CODE).setValue(null);
                example.getAttributeExample(KeConnectionOrganizationStrategy.SHORT_NAME).setValue(null);
                example.addAdditionalParam(KeConnectionOrganizationStrategy.PARENT_SHORT_NAME_FILTER, null);
                target.add(content);
            }
        };
        filterForm.add(reset);

        //Submit Action
        AjaxButton submit = new AjaxButton("submit", filterForm) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                target.add(content);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
            }
        };
        filterForm.add(submit);

        //Navigator
        content.add(new PagingNavigator("navigator", dataView, getPreferencesPage(), content));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return ImmutableList.of(new AddItemButton(id) {

            @Override
            protected void onClick() {
                DomainObjectList.onAddObject(this.getPage(), organizationStrategy, getTemplateSession());
            }

            @Override
            protected void onBeforeRender() {
                if (!DomainObjectAccessUtil.canAddNew(organizationStrategy, "organization")) {
                    setVisible(false);
                }
                super.onBeforeRender();
            }
        }, new CollapsibleSearchToolbarButton(id, searchPanel));
    }
}
