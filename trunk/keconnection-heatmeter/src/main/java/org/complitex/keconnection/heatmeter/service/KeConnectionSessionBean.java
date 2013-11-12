package org.complitex.keconnection.heatmeter.service;

import com.google.common.collect.Sets;
import org.complitex.dictionary.entity.DomainObject;
import org.complitex.dictionary.service.SessionBean;
import org.complitex.dictionary.strategy.StrategyFactory;
import org.complitex.dictionary.web.DictionaryFwSession;
import org.complitex.keconnection.heatmeter.entity.example.CorrectionExample;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 09.02.11 14:43
 */
@Stateless
@DeclareRoles(SessionBean.CHILD_ORGANIZATION_VIEW_ROLE)
public class KeConnectionSessionBean {

    @Resource
    private SessionContext sessionContext;
    @EJB
    private SessionBean sessionBean;
    @EJB
    protected StrategyFactory strategyFactory;

    @EJB
    protected KeConnectionOrganizationStrategy organizationStrategy;

    public boolean isAdmin() {
        return sessionBean.isAdmin();
    }

    public String getAllOuterOrganizationsString() {
        String s = "";
        String d = "";

        for (long id : getAllOuterOrganizationObjectIds()) {
            s += d + id;
            d = ",";
        }

        return "(" + s + ")";
    }

    private List<Long> getAllOuterOrganizationObjectIds() {
        List<Long> objectIds = new ArrayList<Long>();

        for (DomainObject o : organizationStrategy.getAllOuterOrganizations(null)) {
            objectIds.add(o.getId());
        }

        return objectIds;
    }

    private boolean hasOuterOrganization(Long objectId) {
        return getAllOuterOrganizationObjectIds().contains(objectId);
    }

    public boolean isAuthorized(Long outerOrganizationObjectId, Long userOrganizationId) {
        return isAdmin()
                || (hasOuterOrganization(outerOrganizationObjectId) && isUserOrganizationVisibleToCurrentUser(userOrganizationId));
    }

    /**
     * Returns main user's organization by means 
     *  of {@link SessionBean#getMainUserOrganization(DictionaryFwSession)}
     * 
     * @param session dictionary session.
     * @return 
     */
    public Long getCurrentUserOrganizationId(DictionaryFwSession session) {
        DomainObject mainUserOrganization = sessionBean.getMainUserOrganization(session);
        return mainUserOrganization != null && mainUserOrganization.getId() != null
                && mainUserOrganization.getId() > 0 ? mainUserOrganization.getId() : null;
    }

    public void prepareExampleForPermissionCheck(CorrectionExample example) {
        boolean isAdmin = sessionBean.isAdmin();
        example.setAdmin(isAdmin);
        if (!isAdmin) {
            example.setOuterOrganizationsString(getAllOuterOrganizationsString());
            example.setUserOrganizationsString(getCurrentUserOrganizationsString());
        }
    }

    public String getMainUserOrganizationForSearchCorrections(Long userOrganizationId) {
        if (sessionBean.isAdmin()) {
            return null;
        }
        return getMainUserOrganizationString(userOrganizationId);
    }

    private String getCurrentUserOrganizationsString() {
        return getUserOrganizationsString(getUserOrganizationIdsVisibleToCurrentUser());
    }

    private String getUserOrganizationsString(Set<Long> userOrganizationIds) {
        String s = "";
        String d = "";

        for (long p : userOrganizationIds) {
            s += d + p;
            d = ", ";
        }

        return "(" + s + ")";
    }

    private Set<Long> getUserOrganizationIdsVisibleToCurrentUser() {
        return sessionContext.isCallerInRole(SessionBean.CHILD_ORGANIZATION_VIEW_ROLE)
                ? Sets.newHashSet(sessionBean.getUserOrganizationTreeObjectIds())
                : Sets.newHashSet(sessionBean.getUserOrganizationObjectIds());
    }

    public String getMainUserOrganizationString(Long userOrganizationId) {
        return getUserOrganizationsString(Sets.newHashSet(userOrganizationId));
    }

    private boolean isUserOrganizationVisibleToCurrentUser(Long userOrganizationId) {
        return userOrganizationId == null || getUserOrganizationIdsVisibleToCurrentUser().contains(userOrganizationId);
    }
}
