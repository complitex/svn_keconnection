/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.complitex.keconnection.heatmeter.web.component.heatmeter.list;

import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.*;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import javax.ejb.EJB;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.IHeaderResponse;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.apache.wicket.util.string.Strings;
import org.complitex.address.service.AddressRendererBean;
import static org.complitex.dictionary.util.DateUtil.*;
import org.complitex.dictionary.web.component.css.CssAttributeBehavior;
import org.complitex.keconnection.heatmeter.entity.Heatmeter;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConnection;
import org.complitex.keconnection.heatmeter.entity.HeatmeterConsumption;
import org.complitex.keconnection.heatmeter.entity.HeatmeterPayload;
import org.complitex.keconnection.heatmeter.entity.HeatmeterStatus;
import org.complitex.keconnection.heatmeter.entity.HeatmeterType;
import org.complitex.keconnection.heatmeter.entity.HeatmeterValidate;
import org.complitex.keconnection.heatmeter.entity.HeatmeterValidateStatus;
import org.complitex.keconnection.heatmeter.service.HeatmeterBindingStatusRenderer;
import org.complitex.keconnection.heatmeter.service.HeatmeterConsumptionBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterPayloadBean;
import org.complitex.keconnection.heatmeter.service.HeatmeterService;
import org.complitex.keconnection.heatmeter.web.HeatmeterEdit;
import org.complitex.keconnection.heatmeter.web.HeatmeterList.HeatmeterListWrapper;
import org.complitex.keconnection.organization.strategy.IKeConnectionOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Artem
 */
public abstract class HeatmeterItemPanel extends Panel {

    private static final Logger log = LoggerFactory.getLogger(HeatmeterItemPanel.class);

    private static abstract class UpdatableContainer extends WebMarkupContainer {

        UpdatableContainer(String id) {
            super(id);
            setOutputMarkupPlaceholderTag(true);
        }

        @Override
        protected void onBeforeRender() {
            removeAll();
            onPopulate();

            super.onBeforeRender();
        }

        protected abstract void onPopulate();
    }

    private static class LastRowBehavior extends CssAttributeBehavior {

        LastRowBehavior() {
            super("lastRow");
        }
    }
    @EJB
    private AddressRendererBean addressRendererBean;
    @EJB(name = IKeConnectionOrganizationStrategy.KECONNECTION_ORGANIZATION_STRATEGY_NAME)
    private IKeConnectionOrganizationStrategy organizationStrategy;
    @EJB
    private HeatmeterBindingStatusRenderer heatmeterBindingStatusRenderer;
    @EJB
    private HeatmeterService heatmeterService;
    @EJB
    private HeatmeterPayloadBean heatmeterPayloadBean;
    @EJB
    private HeatmeterConsumptionBean heatmeterConsumptionBean;
    private final UpdatableContainer primaryRow;
    private final UpdatableContainer secondaryRow;
    private final UpdatableContainer errorStatusRow;
    private final IModel<String> savePayloadStatusModel = new Model<>();
    private final IModel<String> saveConsumptionStatusModel = new Model<>();
    private final IModel<Boolean> errorStatusRowVisibleModel = new AbstractReadOnlyModel<Boolean>() {

        @Override
        public Boolean getObject() {
            return !Strings.isEmpty(savePayloadStatusModel.getObject())
                    || !Strings.isEmpty(saveConsumptionStatusModel.getObject());
        }
    };
    private final IModel<Boolean> secondaryRowVisibleModel;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.renderCSSReference(new PackageResourceReference(HeatmeterItemPanel.class,
                HeatmeterItemPanel.class.getSimpleName() + ".css"));
    }

    public HeatmeterItemPanel(String id, final HeatmeterListWrapper heatmeterListWrapper) {
        super(id);

        final Heatmeter heatmeter = heatmeterListWrapper.getHeatmeter();

        setRenderBodyOnly(true);

        secondaryRowVisibleModel = new AbstractReadOnlyModel<Boolean>() {

            @Override
            public Boolean getObject() {
                return (heatmeter.getPayloads().size() > 1 || heatmeter.getConsumptions().size() > 1)
                        && heatmeterListWrapper.getOperatingMonthDate() != null;
            }
        };

        add(primaryRow = new UpdatableContainer("primaryRow") {

            @Override
            protected void onPopulate() {
                //organization
                {
                    String organization = null;
                    if (!heatmeter.getConnections().isEmpty()) {
                        HeatmeterConnection c = heatmeter.getConnections().get(0);
                        if (c.getOrganizationId() != null && c.getOrganizationId() > 0) {
                            organization = organizationStrategy.getUniqueCode(c.getOrganizationId());
                        }
                    }
                    add(new Label("organization", organization));
                }

                //address
                {
                    RepeatingView addresses = new RepeatingView("addresses");
                    add(addresses);
                    for (HeatmeterConnection c : heatmeter.getConnections()) {
                        String address = addressRendererBean.displayBuildingSimple(c.getBuildingId(), getLocale());
                        addresses.add(new Label(addresses.newChildId(), address));
                    }
                }

                //building code
                {
                    RepeatingView buildingCodes = new RepeatingView("buildingCodes");
                    add(buildingCodes);
                    for (HeatmeterConnection c : heatmeter.getConnections()) {
                        buildingCodes.add(new Label(buildingCodes.newChildId(), String.valueOf(c.getCode())));
                    }
                }

                //ls and hyperlink to edit heatmeter page
                {
                    PageParameters editParameters = new PageParameters().add("id", heatmeter.getId());
                    Link<Void> heatmeterEditLink = new BookmarkablePageLink<>("heatmeterEditLink",
                            HeatmeterEdit.class, editParameters);
                    heatmeterEditLink.add(new Label("ls", String.valueOf(heatmeter.getLs())));
                    add(heatmeterEditLink);
                }

                //type
                {
                    HeatmeterType type = heatmeter.getType();
                    add(new Label("type", type != null ? getString(type.getShortName()) : null));
                }

                //payload
                {
                    boolean isNewPayload = heatmeter.getPayloads().size() == 1;
                    HeatmeterPayload payload = isNewPayload ? heatmeter.getPayloads().get(0)
                            : heatmeter.getPayloads().get(heatmeter.getPayloads().size() - 2);
                    addPayload(this, heatmeterListWrapper,
                            payload, isNewPayload && heatmeterListWrapper.getOperatingMonthDate() != null, true);
                }

                //consumption
                {
                    boolean isNewConsumption = heatmeter.getConsumptions().size() == 1;
                    HeatmeterConsumption consumption = isNewConsumption ? heatmeter.getConsumptions().get(0)
                            : heatmeter.getConsumptions().get(heatmeter.getConsumptions().size() - 2);
                    addConsumption(this, heatmeterListWrapper,
                            consumption, isNewConsumption && heatmeterListWrapper.getOperatingMonthDate() != null, true);
                }

                //status
                {
                    HeatmeterStatus status = heatmeter.getStatus();
                    add(new Label("status", status != null ? getString(status.name()) : null));
                }

                //bind status
                {
                    add(new Label("bindingStatus",
                            heatmeterBindingStatusRenderer.render(heatmeter.getBindingStatus(), getLocale())));
                }

                //bind heatmeter action
                {
                    AjaxLink<Void> bindHeatmeterLink = new AjaxLink<Void>("bindHeatmeterLink") {

                        @Override
                        public void onClick(AjaxRequestTarget target) {
                            HeatmeterItemPanel.this.onBindHeatmeter(heatmeter, target);
                        }
                    };
                    bindHeatmeterLink.setEnabled(HeatmeterItemPanel.this.isEditable());
                    add(bindHeatmeterLink);
                }
            }
        });

        add(secondaryRow = new UpdatableContainer("secondaryRow") {

            @Override
            protected void onPopulate() {
                //payload
                boolean payloadDataVisible = heatmeter.getPayloads().size() > 1;
                HeatmeterPayload payload = heatmeter.getPayloads().get(heatmeter.getPayloads().size() - 1);
                addPayload(this, heatmeterListWrapper, payload, true, payloadDataVisible);

                //consumption
                boolean consumptionDataVisible = heatmeter.getConsumptions().size() > 1;
                HeatmeterConsumption consumption = heatmeter.getConsumptions().get(heatmeter.getConsumptions().size() - 1);
                addConsumption(this, heatmeterListWrapper, consumption, true, consumptionDataVisible);
            }

            @Override
            public boolean isVisible() {
                return secondaryRowVisibleModel.getObject();
            }
        });

        add(errorStatusRow = new UpdatableContainer("errorStatusRow") {

            @Override
            protected void onPopulate() {
                add(new Label("payloadErrorStatus", savePayloadStatusModel));
                add(new Label("consumptionErrorStatus", saveConsumptionStatusModel));
            }

            @Override
            public boolean isVisible() {
                return errorStatusRowVisibleModel.getObject();
            }
        });

        initializeCss();
    }

    private void addPayload(MarkupContainer container, final HeatmeterListWrapper heatmeterListWrapper,
            final HeatmeterPayload payload, boolean editable, boolean payloadDataVisible) {

        container.add(
                new HeatmeterPayloadItem("tg1", new PropertyModel<BigDecimal>(payload, "payload1"),
                editable).setVisible(payloadDataVisible));
        container.add(new HeatmeterPayloadItem("tg2",
                new PropertyModel<BigDecimal>(payload, "payload2"),
                editable).setVisible(payloadDataVisible));
        container.add(new HeatmeterPayloadItem("tg3",
                new PropertyModel<BigDecimal>(payload, "payload3"),
                editable).setVisible(payloadDataVisible));

        container.add(new HeatmeterDateItem("beginDate",
                new PropertyModel<Date>(payload, "beginDate"),
                editable).setVisible(payloadDataVisible));

        AjaxLink<Void> savePayload = new AjaxLink<Void>("savePayload") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                log.info("Payload: {},{},{}, {}",
                        new Object[]{payload.getPayload1(), payload.getPayload2(), payload.getPayload3(),
                            payload.getBeginDate()});


                //validate 
                HeatmeterValidate validate = heatmeterService.validatePayloads(heatmeterListWrapper.getHeatmeter());
                if (HeatmeterValidateStatus.VALID != validate.getStatus()) {
                    savePayloadStatusModel.setObject(
                            MessageFormat.format(getString(validate.getStatus().name().toLowerCase()), validate));
                } else {
                    savePayloadStatusModel.setObject(null);

                    try {

                        //update end date of previous payload
                        HeatmeterPayload previousPayload = getPreviousPayload(heatmeterListWrapper.getHeatmeter());
                        if (previousPayload != null) {
                            previousPayload.setEndDate(previousDay(payload.getBeginDate()));
                            heatmeterPayloadBean.save(previousPayload);
                        }

                        //save new payload
                        heatmeterPayloadBean.save(payload);

                        //add new payload
                        addNewPayload(heatmeterListWrapper);

                        target.add(primaryRow);
                        target.add(secondaryRow);
                    } catch (Exception e) {
                        log.error("Db error.", e);
                        savePayloadStatusModel.setObject(getString("db_save_error"));
                    }
                }
                target.add(errorStatusRow);
                initializeCss(target);
            }
        };
        savePayload.setEnabled(HeatmeterItemPanel.this.isEditable());
        savePayload.setVisible(payloadDataVisible && editable);
        container.add(savePayload);
    }

    private void addConsumption(MarkupContainer container, final HeatmeterListWrapper heatmeterListWrapper,
            final HeatmeterConsumption consumption, boolean editable, boolean consumptionDataVisible) {

        container.add(new HeatmeterConsumptionItem("consumption",
                new PropertyModel<BigDecimal>(consumption, "consumption"),
                editable).setVisible(consumptionDataVisible));
        container.add(new HeatmeterConsumptionItem("consumption1",
                new PropertyModel<BigDecimal>(consumption, "consumption1"),
                false).setVisible(consumptionDataVisible));
        container.add(new HeatmeterDateItem("readoutDate",
                new PropertyModel<Date>(consumption, "readoutDate"),
                editable).setVisible(consumptionDataVisible));

        AjaxLink<Void> saveConsumption = new AjaxLink<Void>("saveConsumption") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                log.info("Consumption: {}, {}",
                        new Object[]{consumption.getConsumption(), consumption.getReadoutDate()});


                //validate 
                HeatmeterValidate validate = heatmeterService.validateConsumptions(heatmeterListWrapper.getHeatmeter());
                if (HeatmeterValidateStatus.VALID != validate.getStatus()) {
                    saveConsumptionStatusModel.setObject(
                            MessageFormat.format(getString(validate.getStatus().name().toLowerCase()), validate));
                } else {
                    saveConsumptionStatusModel.setObject(null);

                    try {
                        heatmeterConsumptionBean.save(consumption);

                        //add new consumption
                        addNewConsumption(heatmeterListWrapper);

                        target.add(primaryRow);
                        target.add(secondaryRow);
                    } catch (Exception e) {
                        log.error("Db error.", e);
                        savePayloadStatusModel.setObject(getString("db_save_error"));
                    }
                }
                target.add(errorStatusRow);
                initializeCss(target);
            }
        };
        saveConsumption.setEnabled(HeatmeterItemPanel.this.isEditable());
        saveConsumption.setVisible(consumptionDataVisible && editable);
        container.add(saveConsumption);
    }

    private HeatmeterPayload getPreviousPayload(Heatmeter heatmeter) {
        int size = heatmeter.getPayloads().size();
        if (size > 1) {
            return heatmeter.getPayloads().get(size - 2);
        }
        return null;
    }

    private void addNewPayload(HeatmeterListWrapper heatmeterListWrapper) {
        HeatmeterPayload p = new HeatmeterPayload(heatmeterListWrapper.getOperatingMonthDate());
        p.setHeatmeterId(heatmeterListWrapper.getHeatmeter().getId());
        heatmeterListWrapper.getHeatmeter().getPayloads().add(p);
    }

    private void addNewConsumption(HeatmeterListWrapper heatmeterListWrapper) {
        HeatmeterConsumption c = new HeatmeterConsumption(heatmeterListWrapper.getOperatingMonthDate());
        c.setHeatmeterId(heatmeterListWrapper.getHeatmeter().getId());
        heatmeterListWrapper.getHeatmeter().getConsumptions().add(c);
    }

    private Component determineLastRow() {
        return errorStatusRowVisibleModel.getObject() ? errorStatusRow
                : (secondaryRowVisibleModel.getObject() ? secondaryRow : primaryRow);
    }

    private void initializeCss() {
        initializeCss(null);
    }

    private void initializeCss(AjaxRequestTarget target) {
        Component lastRow = determineLastRow();
        Collection<Component> wereLastRows = new ArrayList<>();
        for (Component c : ImmutableList.of(primaryRow, secondaryRow, errorStatusRow)) {
            if (wasLastRow(c)) {
                wereLastRows.add(c);
            }
            removeLastRowBehavior(c);
        }
        addLastRowBehavior(lastRow);

        if (target != null) {
            target.add(lastRow);
            target.add(toArray(wereLastRows, Component.class));
        }
    }

    private void removeLastRowBehavior(Component component) {
        component.remove(toArray(component.getBehaviors(LastRowBehavior.class), Behavior.class));
    }

    private boolean wasLastRow(Component component) {
        return !component.getBehaviors(LastRowBehavior.class).isEmpty();
    }

    private void addLastRowBehavior(Component component) {
        removeLastRowBehavior(component);
        component.add(new LastRowBehavior());
    }

    protected abstract boolean isEditable();

    protected abstract void onBindHeatmeter(Heatmeter heatmeter, AjaxRequestTarget target);
}
