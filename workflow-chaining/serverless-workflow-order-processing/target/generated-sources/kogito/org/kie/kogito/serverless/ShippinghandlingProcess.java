package org.kie.kogito.serverless;

import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.kie.api.definition.process.Process;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.drools.core.util.KieFunctions;

@javax.enterprise.context.ApplicationScoped()
@javax.inject.Named("shippinghandling")
@io.quarkus.runtime.Startup()
public class ShippinghandlingProcess extends org.kie.kogito.process.impl.AbstractProcess<org.kie.kogito.serverless.workflow.models.JsonNodeModel> {

    @javax.inject.Inject()
    org.kie.kogito.serverless.ShippinghandlingMessageProducer_2 producer_2;

    @javax.inject.Inject()
    org.kie.kogito.serverless.ShippinghandlingMessageProducer_3 producer_3;

    @javax.inject.Inject()
    public ShippinghandlingProcess(org.kie.kogito.app.Application app, org.kie.kogito.correlation.CorrelationService correlations) {
        super(app, java.util.Arrays.asList(), correlations);
        activate();
    }

    public ShippinghandlingProcess() {
    }

    @Override()
    public org.kie.kogito.serverless.ShippinghandlingProcessInstance createInstance(org.kie.kogito.serverless.workflow.models.JsonNodeModel value) {
        return new org.kie.kogito.serverless.ShippinghandlingProcessInstance(this, value, this.createProcessRuntime());
    }

    public org.kie.kogito.serverless.ShippinghandlingProcessInstance createInstance(java.lang.String businessKey, org.kie.kogito.serverless.workflow.models.JsonNodeModel value) {
        return new org.kie.kogito.serverless.ShippinghandlingProcessInstance(this, value, businessKey, this.createProcessRuntime());
    }

    public org.kie.kogito.serverless.ShippinghandlingProcessInstance createInstance(java.lang.String businessKey, org.kie.kogito.correlation.CompositeCorrelation correlation, org.kie.kogito.serverless.workflow.models.JsonNodeModel value) {
        return new org.kie.kogito.serverless.ShippinghandlingProcessInstance(this, value, businessKey, this.createProcessRuntime(), correlation);
    }

    @Override()
    public org.kie.kogito.serverless.workflow.models.JsonNodeModel createModel() {
        return new org.kie.kogito.serverless.workflow.models.JsonNodeModel();
    }

    public org.kie.kogito.serverless.ShippinghandlingProcessInstance createInstance(org.kie.kogito.Model value) {
        return this.createInstance((org.kie.kogito.serverless.workflow.models.JsonNodeModel) value);
    }

    public org.kie.kogito.serverless.ShippinghandlingProcessInstance createInstance(java.lang.String businessKey, org.kie.kogito.Model value) {
        return this.createInstance(businessKey, (org.kie.kogito.serverless.workflow.models.JsonNodeModel) value);
    }

    public org.kie.kogito.serverless.ShippinghandlingProcessInstance createInstance(org.kie.api.runtime.process.WorkflowProcessInstance wpi) {
        return new org.kie.kogito.serverless.ShippinghandlingProcessInstance(this, this.createModel(), this.createProcessRuntime(), wpi);
    }

    public org.kie.kogito.serverless.ShippinghandlingProcessInstance createReadOnlyInstance(org.kie.api.runtime.process.WorkflowProcessInstance wpi) {
        return new org.kie.kogito.serverless.ShippinghandlingProcessInstance(this, this.createModel(), wpi);
    }

    protected org.kie.api.definition.process.Process process() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("shippinghandling", true);
        factory.variable("workflowdata", org.jbpm.process.core.datatype.DataTypeResolver.fromClass(com.fasterxml.jackson.databind.JsonNode.class), "{}", "customTags", null);
        factory.name("Shipping Handling");
        factory.packageName("org.kie.kogito.serverless");
        factory.dynamic(false);
        factory.version("1.0");
        factory.type("SW");
        factory.visibility("Public");
        org.jbpm.ruleflow.core.factory.StartNodeFactory<?> startNode1 = factory.startNode(1);
        startNode1.name("Start");
        startNode1.interrupting(false);
        startNode1.metaData("UniqueId", "_jbpm-unique-58");
        startNode1.metaData("state", "ShippingHandling");
        startNode1.done();
        org.jbpm.ruleflow.core.factory.EndNodeFactory<?> endNode2 = factory.endNode(2);
        endNode2.name("End");
        endNode2.terminate(false);
        endNode2.action(new org.jbpm.process.instance.impl.actions.ProduceEventAction<com.fasterxml.jackson.databind.JsonNode>("domesticShipping", "workflowdata", () -> producer_2));
        endNode2.metaData("UniqueId", "_jbpm-unique-59");
        endNode2.metaData("TriggerType", "ProduceMessage");
        endNode2.metaData("EventType", "message");
        endNode2.metaData("state", "DomesticShipping");
        endNode2.metaData("TriggerRef", "domesticShipping");
        endNode2.metaData("MappingVariable", "workflowdata");
        endNode2.metaData("MessageType", "com.fasterxml.jackson.databind.JsonNode");
        endNode2.done();
        org.jbpm.ruleflow.core.factory.EndNodeFactory<?> endNode3 = factory.endNode(3);
        endNode3.name("End");
        endNode3.terminate(false);
        endNode3.action(new org.jbpm.process.instance.impl.actions.ProduceEventAction<com.fasterxml.jackson.databind.JsonNode>("internationalShipping", "workflowdata", () -> producer_3));
        endNode3.metaData("UniqueId", "_jbpm-unique-60");
        endNode3.metaData("TriggerType", "ProduceMessage");
        endNode3.metaData("EventType", "message");
        endNode3.metaData("state", "InternationalShipping");
        endNode3.metaData("TriggerRef", "internationalShipping");
        endNode3.metaData("MappingVariable", "workflowdata");
        endNode3.metaData("MessageType", "com.fasterxml.jackson.databind.JsonNode");
        endNode3.done();
        org.jbpm.ruleflow.core.factory.SplitFactory<?> splitNode4 = factory.splitNode(4);
        splitNode4.name("ShippingHandling");
        splitNode4.type(2);
        splitNode4.metaData("UniqueId", "4");
        splitNode4.metaData("Variable", "workflowdata");
        splitNode4.metaData("state", "ShippingHandling");
        splitNode4.constraint(5, "4_5", "DROOLS_DEFAULT", "jsonpath", new org.jbpm.process.instance.impl.ExpressionReturnValueEvaluator("jsonpath", "$.[?(@.country == 'US')]", "workflowdata"), 0, false);
        splitNode4.constraint(6, "4_6", "DROOLS_DEFAULT", "jsonpath", new org.jbpm.process.instance.impl.ExpressionReturnValueEvaluator("jsonpath", "$.[?(@.country != 'US')]", "workflowdata"), 0, false);
        splitNode4.done();
        org.jbpm.ruleflow.core.factory.ActionNodeFactory<?> actionNode5 = factory.actionNode(5);
        actionNode5.name("DomesticShipping");
        actionNode5.action(new org.kie.kogito.serverless.workflow.actions.InjectAction("{\"shipping\":\"domestic\"}"));
        actionNode5.metaData("UniqueId", "_jbpm-unique-62");
        actionNode5.metaData("state", "DomesticShipping");
        actionNode5.done();
        org.jbpm.ruleflow.core.factory.ActionNodeFactory<?> actionNode6 = factory.actionNode(6);
        actionNode6.name("InternationalShipping");
        actionNode6.action(new org.kie.kogito.serverless.workflow.actions.InjectAction("{\"shipping\":\"international\"}"));
        actionNode6.metaData("UniqueId", "_jbpm-unique-63");
        actionNode6.metaData("state", "InternationalShipping");
        actionNode6.done();
        factory.connection(5, 2, "5_2");
        factory.connection(6, 3, "6_3");
        factory.connection(1, 4, "1_4");
        factory.connection(4, 5, "4_5");
        factory.connection(4, 6, "4_6");
        factory.validate();
        return factory.getProcess();
    }
}
