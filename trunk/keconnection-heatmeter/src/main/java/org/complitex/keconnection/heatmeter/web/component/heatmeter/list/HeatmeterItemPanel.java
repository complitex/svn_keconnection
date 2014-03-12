package org.complitex.keconnection.heatmeter.web.component.heatmeter.list;

import com.google.common.collect.ImmutableList;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
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
import org.complitex.dictionary.strategy.organization.IOrganizationStrategy;
import org.complitex.dictionary.web.component.css.CssAttributeBehavior;
import org.complitex.keconnection.heatmeter.entity.*;
import org.complitex.keconnection.heatmeter.service.*;
import org.complitex.keconnection.heatmeter.web.HeatmeterEdit;
import org.complitex.keconnection.organization.strategy.KeConnectionOrganizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import static com.google.common.collect.Iterables.toArray;
import static org.complitex.dictionary.util.DateUtil.*;

/**
 *
 * @author Artem
 */
public abstract class HeatmeterItemPanel extends Panel {

    private final Logger log = LoggerFactory.getLogger(HeatmeterItemPanel.class);

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

    @EJB(name = IOrganizationStrategy.BEAN_NAME, beanInterface = IOrganizationStrategy.class)
    private KeConnectionOrganizationStrategy organizationStrategy;

    @EJB
    private HeatmeterBindingStatusRenderer heatmeterBindingStatusRenderer;

    @EJB
    private HeatmeterService heatmeterService;

    @EJB
    private HeatmeterPayloadBean heatmeterPayloadBean;

    @EJB
    private HeatmeterInputBean heatmeterInputBean;

    @EJB
    private HeatmeterBean heatmeterBean;

    private final UpdatableContainer primaryRow;
    private final UpdatableContainer secondaryRow;
    private final UpdatableContainer errorStatusRow;
    private final IModel<String> savePayloadStatusModel = new Model<>();
    private final IModel<String> saveInputStatusModel = new Model<>();
    private final IModel<Boolean> errorStatusRowVisibleModel = new AbstractReadOnlyModel<Boolean>() {

        @Override
        public Boolean getObject() {
            return !Strings.isEmpty(savePayloadStatusModel.getObject())
                    || !Strings.isEmpty(saveInputStatusModel.getObject());
        }
    };
    private final IModel<Boolean> secondaryRowVisibleModel;

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new PackageResourceReference(HeatmeterItemPanel.class,
                HeatmeterItemPanel.class.getSimpleName() + ".css")));
    }

    public HeatmeterItemPanel(String id, final Heatmeter heatmeter) {
        super(id);

        setRenderBodyOnly(true);

        secondaryRowVisibleModel = new AbstractReadOnlyModel<Boolean>() {

            @Override
            public Boolean getObject() {
                return (heatmeter.getPayloads().size() > 1 || heatmeter.getInputs().size() > 1)
                        && heatmeter.getOm() != null;
            }
        };

        add(primaryRow = new UpdatableContainer("primaryRow") {

            @Override
            protected void onPopulate() {
                //organization
                String organization = null;
                if (!heatmeter.getConnections().isEmpty()) {
                    HeatmeterConnection c = heatmeter.getConnections().get(0);
                    if (c.getOrganizationId() != null && c.getOrganizationId() > 0) {
                        organization = organizationStrategy.getCode(c.getOrganizationId());
                    }
                }
                add(new Label("organization", organization));

                //address
                RepeatingView addresses = new RepeatingView("addresses");
                add(addresses);
                for (HeatmeterConnection c : heatmeter.getConnections()) {
                    String address = addressRendererBean.displayBuildingSimple(c.getBuildingId(), getLocale());
                    addresses.add(new Label(addresses.newChildId(), address));
                }

                //building code
                RepeatingView buildingCodes = new RepeatingView("buildingCodes");
                add(buildingCodes);
                for (HeatmeterConnection c : heatmeter.getConnections()) {
                    buildingCodes.add(new Label(buildingCodes.newChildId(), String.valueOf(c.getCode())));
                }

                //ls and hyperlink to edit heatmeter page
                PageParameters editParameters = new PageParameters().add("id", heatmeter.getId());
                Link<Void> heatmeterEditLink = new BookmarkablePageLink<>("heatmeterEditLink",
                        HeatmeterEdit.class, editParameters);
                heatmeterEditLink.add(new Label("ls", String.valueOf(heatmeter.getLs())));
                add(heatmeterEditLink);

                //type
                HeatmeterType type = heatmeter.getType();
                add(new Label("type", type != null ? getString(type.getShortName()) : null));

                //payload
                HeatmeterPayload payload = heatmeter.getPayloads().size() > 1
                        ? heatmeter.getPayloads().get(heatmeter.getPayloads().size() - 2)
                        : heatmeter.getPayloads().get(0);

                addPayload(this, heatmeter, payload, true);

                //input and consumption
                HeatmeterInput input =  heatmeter.getInputs().size() > 1
                        ? heatmeter.getInputs().get(heatmeter.getInputs().size() - 2)
                        : heatmeter.getInputs().get(0);

                addInputConsumption(this, heatmeter, input, true);

                //status
                HeatmeterStatus status = heatmeter.getStatus();
                add(new Label("status", status != null ? getString(status.name()) : null));

                //bind status
                add(new Label("bindingStatus",
                        heatmeterBindingStatusRenderer.render(heatmeter.getBindingStatus(), getLocale())));

                //calculating
                Boolean calculating = heatmeter.getCalculating();

                add(new Label("calculating", calculating != null
                        ? getString(Boolean.class.getSimpleName() + "." + calculating.toString().toUpperCase())
                        : null));

                //bind heatmeter action
                AjaxLink<Void> bindHeatmeterLink = new AjaxLink<Void>("bindHeatmeterLink") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        HeatmeterItemPanel.this.onBindHeatmeter(heatmeter, target);
                    }
                };
                bindHeatmeterLink.setEnabled(HeatmeterItemPanel.this.isEditable());
                add(bindHeatmeterLink);

                //activate heatmeter
                AjaxLink<Void> activateHeatmeter = new AjaxLink<Void>("activateHeatmeter") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        HeatmeterItemPanel.this.onActivateHeatmeter(heatmeter, target);
                    }
                };
                activateHeatmeter.setEnabled(HeatmeterItemPanel.this.isEditable());
                activateHeatmeter.setVisible(heatmeter.getStatus() == HeatmeterStatus.OFF
                        || heatmeter.getStatus() == HeatmeterStatus.ADJUSTMENT);
                add(activateHeatmeter);

                //deactivate heatmeter
                AjaxLink<Void> deactivateHeatmeter = new AjaxLink<Void>("deactivateHeatmeter") {

                    @Override
                    public void onClick(AjaxRequestTarget target) {
                        HeatmeterItemPanel.this.onDeactivateHeatmeter(heatmeter, target);
                    }
                };
                deactivateHeatmeter.setEnabled(HeatmeterItemPanel.this.isEditable());
                deactivateHeatmeter.setVisible(heatmeter.getStatus() == HeatmeterStatus.OPERATION);
                add(deactivateHeatmeter);
            }
        });

        add(secondaryRow = new UpdatableContainer("secondaryRow") {

            @Override
            protected void onPopulate() {
                //payload
                boolean payloadDataVisible = heatmeter.getPayloads().size() > 1;
                HeatmeterPayload payload = heatmeter.getPayloads().get(heatmeter.getPayloads().size() - 1);
                addPayload(this, heatmeter, payload, payloadDataVisible);

                //input and consumption
                HeatmeterInput input = heatmeter.getInputs().get(heatmeter.getInputs().size() - 1);
                addInputConsumption(this, heatmeter, input, heatmeter.getInputs().size() > 1);
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
                add(new Label("inputErrorStatus", saveInputStatusModel));
            }

            @Override
            public boolean isVisible() {
                return errorStatusRowVisibleModel.getObject();
            }
        });

        initializeCss();
    }

    private void addPayload(MarkupContainer container, final Heatmeter heatmeter,
                            final HeatmeterPayload payload, boolean payloadDataVisible) {
        boolean editable = payload.getId() == null;

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
                //update end date and end om of previous payload if begin date of new payload is filled
                if (payload.getBeginDate() != null) {
                    HeatmeterPayload previousPayload = getPreviousPayload(heatmeter);
                    if (previousPayload != null) {
                        HeatmeterPeriod previousPeriod = previousPayload;
                        previousPeriod.setEndDate(previousDay(payload.getBeginDate()));
//                        previousPeriod.setEndOm(heatmeter.getOm());
                        heatmeterPayloadBean.save(previousPayload, heatmeter.getOm());
                    }
                }

                //set empty payload as zero
                if (payload.getPayload1() == null){
                    payload.setPayload1(new BigDecimal(0));
                }
                if (payload.getPayload2() == null){
                    payload.setPayload2(new BigDecimal(0));
                }
                if (payload.getPayload3() == null){
                    payload.setPayload3(new BigDecimal(0));
                }

                //validate
                HeatmeterValidate validate = heatmeterService.validatePayloads(heatmeter);
                if (HeatmeterValidateStatus.VALID != validate.getStatus()) {
                    savePayloadStatusModel.setObject(
                            MessageFormat.format(getString(validate.getStatus().name().toLowerCase()), validate));
                } else {
                    savePayloadStatusModel.setObject(null);

                    try {
                        //save new payload
                        heatmeterPayloadBean.save(payload, heatmeter.getOm());

                        //calculate consumptions
                        Heatmeter h = heatmeterService.recalculateConsumptions(heatmeter.getId());
                        heatmeter.setInputs(h.getInputs());

                        //add new payload
                        addNewPayload(heatmeter);

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

    private void addInputConsumption(MarkupContainer container, final Heatmeter heatmeter,
                                     final HeatmeterInput input, boolean inputDataVisible) {
        boolean editable = input.getId() == null;

        container.add(new HeatmeterInputItem("input", new PropertyModel<BigDecimal>(input, "value"),
                editable).setVisible(inputDataVisible));
        container.add(new HeatmeterInputItem("consumption1",
                new PropertyModel<BigDecimal>(input, "sumConsumption.consumption1"),
                false).setVisible(inputDataVisible));
        container.add(new HeatmeterDateItem("readoutDate",
                new PropertyModel<Date>(input, "endDate"),
                editable).setVisible(inputDataVisible));

        AjaxLink<Void> saveConsumption = new AjaxLink<Void>("saveInput") {

            @Override
            public void onClick(AjaxRequestTarget target) {
                final HeatmeterInput previousInput = getPreviousInput(heatmeter);

                //update begin date and begin om of new input
                if (previousInput != null) {
                    input.setBeginDate(nextDay(previousInput.getEndDate()));
                    input.setBeginOm(heatmeter.getOm());
                } else {
                    input.setBeginDate(heatmeter.getOm());
                    input.setBeginOm(heatmeter.getOm());
                }

                //update end om of input
                input.setEndOm(heatmeter.getOm());

                //validate
                HeatmeterValidate validate = heatmeterService.validateInputs(heatmeter);
                if (HeatmeterValidateStatus.VALID != validate.getStatus()) {
                    saveInputStatusModel.setObject(
                            MessageFormat.format(getString(validate.getStatus().name().toLowerCase()), validate));
                } else {
                    saveInputStatusModel.setObject(null);

                    try {
                        //save
                        for (HeatmeterInput i : heatmeter.getInputs()){
                            if (i.getId() == null){
                                heatmeterInputBean.save(i, heatmeter.getOm());
                            }
                        }

                        //recalculate consumptions
                        Heatmeter h = heatmeterService.recalculateConsumptions(heatmeter.getId());
                        heatmeter.setInputs(h.getInputs());

                        //add new input
                        addNewInput(heatmeter);

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
        saveConsumption.setVisible(inputDataVisible && editable);
        container.add(saveConsumption);
    }

    private HeatmeterPayload getPreviousPayload(Heatmeter heatmeter) {
        int size = heatmeter.getPayloads().size();
        if (size > 1) {
            return heatmeter.getPayloads().get(size - 2);
        }
        return null;
    }

    private HeatmeterInput getPreviousInput(Heatmeter heatmeter) {
        int size = heatmeter.getInputs().size();
        if (size > 1) {
            return heatmeter.getInputs().get(size - 2);
        }
        return null;
    }

    private void addNewPayload(Heatmeter heatmeter) {
        HeatmeterPayload p = new HeatmeterPayload(heatmeter.getId(), heatmeter.getOm());
        p.setBeginDate(null);
        heatmeter.getPayloads().add(p);
    }

    private void addNewInput(Heatmeter heatmeter) {
        HeatmeterInput i = new HeatmeterInput(heatmeter.getId(), heatmeter.getOm());
        if (heatmeter.getOm() != null) {
            Date om = heatmeter.getOm();
            i.setEndDate(getLastDayOfMonth(getYear(om), getMonth(om) + 1));
        }
        heatmeter.getInputs().add(i);
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

    protected abstract void onDeactivateHeatmeter(Heatmeter heatmeter, AjaxRequestTarget target);

    protected abstract void onActivateHeatmeter(Heatmeter heatmeter, AjaxRequestTarget target);
}
