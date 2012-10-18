package org.complitex.keconnection.heatmeter.web;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.complitex.dictionary.web.component.BookmarkablePageLinkPanel;
import org.complitex.keconnection.heatmeter.entity.Payload;
import org.complitex.keconnection.heatmeter.service.PayloadBean;
import org.complitex.template.web.component.toolbar.AddItemButton;
import org.complitex.template.web.component.toolbar.ToolbarButton;
import org.complitex.template.web.security.SecurityRole;
import org.complitex.template.web.template.ListTemplatePage;

import java.util.Arrays;
import java.util.List;

import static org.complitex.dictionary.util.PageUtil.newPageParameters;

/**
 * @author Anatoly A. Ivanov java@inheaven.ru
 *         Date: 24.09.12 18:48
 */
@AuthorizeInstantiation(SecurityRole.ADMIN_MODULE_EDIT)
public class PayloadList  extends ListTemplatePage<Payload> {

    public PayloadList(PageParameters pageParameters) {
        super(pageParameters, PayloadBean.class, "ls", "tablegramRecordId", "beginDate", "endDate", "operatingMonth",
                "payload1", "payload2", "payload3");
    }

    @Override
    protected Payload newFilterObject(PageParameters pageParameters) {
        return new Payload();
    }

    @Override
    protected List<? extends Component> getActionComponents(String id, Payload object) {
        return Arrays.asList(new BookmarkablePageLinkPanel<>(id, getString("edit"), PayloadEdit.class,
                newPageParameters("id", object.getId())));
    }

    @Override
    protected List<? extends ToolbarButton> getToolbarButtons(String id) {
        return Arrays.asList(new AddItemButton(id) {

            @Override
            protected void onClick() {
                setResponsePage(PayloadEdit.class);
            }
        });
    }
}