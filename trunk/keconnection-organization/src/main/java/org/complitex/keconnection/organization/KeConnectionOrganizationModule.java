package org.complitex.keconnection.organization;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;
import org.complitex.organization.DefaultOrganizationModule;
import org.complitex.organization.IOrganizationModule;

import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;

@Singleton(name = DefaultOrganizationModule.CUSTOM_ORGANIZATION_MODULE_BEAN_NAME)
@Startup
public class KeConnectionOrganizationModule implements IOrganizationModule {

    @EJB
    private KeConnectionOrganizationStrategy keConnectionOrganizationStrategy;

    public static final String NAME = "org.complitex.keconnection.organization";

    @Override
    public Class<? extends WebPage> getEditPage() {
        return keConnectionOrganizationStrategy.getEditPage();
    }

    @Override
    public PageParameters getEditPageParams() {
        return keConnectionOrganizationStrategy.getEditPageParams(null, null, null);
    }
}
