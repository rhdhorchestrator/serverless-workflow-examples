package org.kie.kogito.serverless;

import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.kie.api.definition.process.Process;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.drools.core.util.KieFunctions;

@javax.enterprise.context.ApplicationScoped()
@javax.inject.Named("orderworkflow")
@io.quarkus.runtime.Startup()
public class OrderworkflowProcess extends org.kie.kogito.process.impl.AbstractProcess<org.kie.kogito.serverless.workflow.models.JsonNodeModel> {

    @javax.inject.Inject()
    @javax.inject.Named("shippinghandling")
    org.kie.kogito.process.Process<org.kie.kogito.serverless.workflow.models.JsonNodeModel> processshippinghandling;

    @javax.inject.Inject()
    @javax.inject.Named("fraudhandling")
    org.kie.kogito.process.Process<org.kie.kogito.serverless.workflow.models.JsonNodeModel> processfraudhandling;

    @javax.inject.Inject()
    public OrderworkflowProcess(org.kie.kogito.app.Application app, org.kie.kogito.correlation.CorrelationService correlations) {
        super(app, java.util.Arrays.asList(), correlations);
        activate();
    }

    public OrderworkflowProcess() {
    }

    @Override()
    public org.kie.kogito.serverless.OrderworkflowProcessInstance createInstance(org.kie.kogito.serverless.workflow.models.JsonNodeModel value) {
        return new org.kie.kogito.serverless.OrderworkflowProcessInstance(this, value, this.createProcessRuntime());
    }

    public org.kie.kogito.serverless.OrderworkflowProcessInstance createInstance(java.lang.String businessKey, org.kie.kogito.serverless.workflow.models.JsonNodeModel value) {
        return new org.kie.kogito.serverless.OrderworkflowProcessInstance(this, value, businessKey, this.createProcessRuntime());
    }

    public org.kie.kogito.serverless.OrderworkflowProcessInstance createInstance(java.lang.String businessKey, org.kie.kogito.correlation.CompositeCorrelation correlation, org.kie.kogito.serverless.workflow.models.JsonNodeModel value) {
        return new org.kie.kogito.serverless.OrderworkflowProcessInstance(this, value, businessKey, this.createProcessRuntime(), correlation);
    }

    @Override()
    public org.kie.kogito.serverless.workflow.models.JsonNodeModel createModel() {
        return new org.kie.kogito.serverless.workflow.models.JsonNodeModel();
    }

    public org.kie.kogito.serverless.OrderworkflowProcessInstance createInstance(org.kie.kogito.Model value) {
        return this.createInstance((org.kie.kogito.serverless.workflow.models.JsonNodeModel) value);
    }

    public org.kie.kogito.serverless.OrderworkflowProcessInstance createInstance(java.lang.String businessKey, org.kie.kogito.Model value) {
        return this.createInstance(businessKey, (org.kie.kogito.serverless.workflow.models.JsonNodeModel) value);
    }

    public org.kie.kogito.serverless.OrderworkflowProcessInstance createInstance(org.kie.api.runtime.process.WorkflowProcessInstance wpi) {
        return new org.kie.kogito.serverless.OrderworkflowProcessInstance(this, this.createModel(), this.createProcessRuntime(), wpi);
    }

    public org.kie.kogito.serverless.OrderworkflowProcessInstance createReadOnlyInstance(org.kie.api.runtime.process.WorkflowProcessInstance wpi) {
        return new org.kie.kogito.serverless.OrderworkflowProcessInstance(this, this.createModel(), wpi);
    }

    protected org.kie.api.definition.process.Process process() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("orderworkflow", true);
        factory.variable("workflowdata", org.jbpm.process.core.datatype.DataTypeResolver.fromClass(com.fasterxml.jackson.databind.JsonNode.class), "{}", "customTags", null);
        factory.variable("ProcessOrder_11", org.jbpm.process.core.datatype.DataTypeResolver.fromClass(com.fasterxml.jackson.databind.JsonNode.class), null, "customTags", "internal");
        factory.variable("ProcessOrder_17", org.jbpm.process.core.datatype.DataTypeResolver.fromClass(com.fasterxml.jackson.databind.JsonNode.class), null, "customTags", "internal");
        factory.name("Order Workflow");
        factory.packageName("org.kie.kogito.serverless");
        factory.dynamic(false);
        factory.version("1.0");
        factory.type("SW");
        factory.visibility("Public");
        factory.metaData("Description", "Workflow for processing Orders and produce Logistics Events");
        org.jbpm.ruleflow.core.factory.EndNodeFactory<?> endNode1 = factory.endNode(1);
        endNode1.name("End");
        endNode1.terminate(false);
        endNode1.metaData("UniqueId", "_jbpm-unique-40");
        endNode1.metaData("state", "ProcessOrder");
        endNode1.done();
        org.jbpm.ruleflow.core.factory.StartNodeFactory<?> startNode2 = factory.startNode(2);
        startNode2.name("OrderEvent");
        startNode2.interrupting(false);
        startNode2.metaData("TriggerMapping", "workflowdata");
        startNode2.metaData("UniqueId", "_jbpm-unique-41");
        startNode2.metaData("TriggerType", "ConsumeMessage");
        startNode2.metaData("EventType", "message");
        startNode2.metaData("state", "ReceiveOrder");
        startNode2.metaData("TriggerRef", "orderEvent");
        startNode2.metaData("DataOnly", true);
        startNode2.metaData("MessageType", "com.fasterxml.jackson.databind.JsonNode");
        startNode2.done();
        startNode2.trigger("orderEvent", java.util.Arrays.asList());
        org.jbpm.ruleflow.core.factory.ActionNodeFactory<?> actionNode3 = factory.actionNode(3);
        actionNode3.name("Script");
        actionNode3.action(new org.kie.kogito.serverless.workflow.actions.MergeAction("workflowdata", "workflowdata"));
        actionNode3.metaData("UniqueId", "_jbpm-unique-42");
        actionNode3.done();
        org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory<?> compositeContextNode4 = factory.compositeContextNode(4);
        compositeContextNode4.name("ReceiveOrder");
        compositeContextNode4.metaData("UniqueId", "_jbpm-unique-43");
        compositeContextNode4.metaData("state", "ReceiveOrder");
        compositeContextNode4.autoComplete(true);
        org.jbpm.ruleflow.core.factory.StartNodeFactory<?> startNode5 = compositeContextNode4.startNode(5);
        startNode5.name("EmbeddedStart");
        startNode5.interrupting(false);
        startNode5.metaData("UniqueId", "_jbpm-unique-44");
        startNode5.done();
        org.jbpm.ruleflow.core.factory.EndNodeFactory<?> endNode6 = compositeContextNode4.endNode(6);
        endNode6.name("EmbeddedEnd");
        endNode6.terminate(true);
        endNode6.metaData("UniqueId", "_jbpm-unique-45");
        endNode6.done();
        compositeContextNode4.connection(5, 6, "5_6");
        compositeContextNode4.done();
        org.jbpm.ruleflow.core.factory.SplitFactory<?> splitNode7 = factory.splitNode(7);
        splitNode7.name("ProcessOrderStart");
        splitNode7.type(1);
        splitNode7.metaData("UniqueId", "_jbpm-unique-46");
        splitNode7.metaData("state", "ProcessOrder");
        splitNode7.done();
        org.jbpm.ruleflow.core.factory.JoinFactory<?> joinNode8 = factory.joinNode(8);
        joinNode8.name("ProcessOrderEnd");
        joinNode8.type(1);
        joinNode8.metaData("UniqueId", "_jbpm-unique-47");
        joinNode8.metaData("state", "ProcessOrder");
        joinNode8.done();
        org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory<?> compositeContextNode9 = factory.compositeContextNode(9);
        compositeContextNode9.name("ProcessOrder-HandleFraudEvaluation");
        compositeContextNode9.metaData("UniqueId", "_jbpm-unique-48");
        compositeContextNode9.variable("ProcessOrder_11", org.jbpm.process.core.datatype.DataTypeResolver.fromClass(com.fasterxml.jackson.databind.JsonNode.class), null, "customTags", "internal");
        compositeContextNode9.autoComplete(true);
        org.jbpm.ruleflow.core.factory.StartNodeFactory<?> startNode10 = compositeContextNode9.startNode(10);
        startNode10.name("EmbeddedStart");
        startNode10.interrupting(false);
        startNode10.metaData("UniqueId", "_jbpm-unique-49");
        startNode10.done();
        org.jbpm.ruleflow.core.factory.SubProcessNodeFactory<?> subProcessNode12 = compositeContextNode9.subProcessNode(12);
        subProcessNode12.name("fraudhandling");
        subProcessNode12.processId("fraudhandling");
        subProcessNode12.processName("");
        subProcessNode12.waitForCompletion(true);
        subProcessNode12.independent(true);
        subProcessNode12.subProcessNode(new org.jbpm.workflow.core.node.SubProcessFactory<JsonNodeModel>() {

            public JsonNodeModel bind(org.kie.api.runtime.process.ProcessContext kcontext) {
                org.kie.kogito.serverless.workflow.models.JsonNodeModel model = new org.kie.kogito.serverless.workflow.models.JsonNodeModel();
                java.util.Map<java.lang.String, java.lang.Object> inputs = org.jbpm.workflow.core.impl.NodeIoHelper.processInputs((org.jbpm.workflow.instance.impl.NodeInstanceImpl) kcontext.getNodeInstance(), (java.lang.String name) -> {
                    return kcontext.getVariable(name);
                });
                model.update(inputs);
                return model;
            }

            public org.kie.kogito.process.ProcessInstance<JsonNodeModel> createInstance(JsonNodeModel model) {
                return processfraudhandling.createInstance(model);
            }

            public void unbind(org.kie.api.runtime.process.ProcessContext kcontext, JsonNodeModel model) {
                java.util.Map<java.lang.String, java.lang.Object> outputs = new java.util.HashMap<java.lang.String, java.lang.Object>();
                outputs.put("workflowdata", model.getWorkflowdata());
                org.jbpm.workflow.core.impl.NodeIoHelper.processOutputs((org.jbpm.workflow.instance.impl.NodeInstanceImpl) kcontext.getNodeInstance(), (java.lang.String name) -> {
                    return outputs.get(name);
                }, (java.lang.String name) -> {
                    return kcontext.getVariable(name);
                });
            }
        });
        subProcessNode12.mapDataInputAssociation(new org.jbpm.workflow.core.impl.DataAssociation(java.util.Arrays.asList(new org.jbpm.workflow.core.impl.DataDefinition("workflowdata", "workflowdata", "com.fasterxml.jackson.databind.JsonNode", null)), new org.jbpm.workflow.core.impl.DataDefinition("workflowdata", "workflowdata", "com.fasterxml.jackson.databind.JsonNode", null), null, null));
        subProcessNode12.mapDataOutputAssociation(new org.jbpm.workflow.core.impl.DataAssociation(java.util.Arrays.asList(new org.jbpm.workflow.core.impl.DataDefinition("workflowdata", "workflowdata", "com.fasterxml.jackson.databind.JsonNode", null)), new org.jbpm.workflow.core.impl.DataDefinition("ProcessOrder_11", "ProcessOrder_11", "com.fasterxml.jackson.databind.JsonNode", null), null, null));
        subProcessNode12.metaData("UniqueId", "_jbpm-unique-50");
        subProcessNode12.metaData("state", "ProcessOrder");
        subProcessNode12.metaData("branch", "HandleFraudEvaluation");
        subProcessNode12.done();
        org.jbpm.ruleflow.core.factory.ActionNodeFactory<?> actionNode13 = compositeContextNode9.actionNode(13);
        actionNode13.name("Script");
        actionNode13.action(new org.kie.kogito.serverless.workflow.actions.MergeAction("ProcessOrder_11", "workflowdata"));
        actionNode13.metaData("UniqueId", "_jbpm-unique-51");
        actionNode13.done();
        org.jbpm.ruleflow.core.factory.EndNodeFactory<?> endNode14 = compositeContextNode9.endNode(14);
        endNode14.name("EmbeddedEnd");
        endNode14.terminate(true);
        endNode14.metaData("UniqueId", "_jbpm-unique-52");
        endNode14.done();
        compositeContextNode9.connection(10, 12, "10_12");
        compositeContextNode9.connection(12, 13, "12_13");
        compositeContextNode9.connection(13, 14, "13_14");
        compositeContextNode9.done();
        org.jbpm.ruleflow.core.factory.CompositeContextNodeFactory<?> compositeContextNode15 = factory.compositeContextNode(15);
        compositeContextNode15.name("ProcessOrder-HandleShippingType");
        compositeContextNode15.metaData("UniqueId", "_jbpm-unique-53");
        compositeContextNode15.variable("ProcessOrder_17", org.jbpm.process.core.datatype.DataTypeResolver.fromClass(com.fasterxml.jackson.databind.JsonNode.class), null, "customTags", "internal");
        compositeContextNode15.autoComplete(true);
        org.jbpm.ruleflow.core.factory.StartNodeFactory<?> startNode16 = compositeContextNode15.startNode(16);
        startNode16.name("EmbeddedStart");
        startNode16.interrupting(false);
        startNode16.metaData("UniqueId", "_jbpm-unique-54");
        startNode16.done();
        org.jbpm.ruleflow.core.factory.SubProcessNodeFactory<?> subProcessNode18 = compositeContextNode15.subProcessNode(18);
        subProcessNode18.name("shippinghandling");
        subProcessNode18.processId("shippinghandling");
        subProcessNode18.processName("");
        subProcessNode18.waitForCompletion(true);
        subProcessNode18.independent(true);
        subProcessNode18.subProcessNode(new org.jbpm.workflow.core.node.SubProcessFactory<JsonNodeModel>() {

            public JsonNodeModel bind(org.kie.api.runtime.process.ProcessContext kcontext) {
                org.kie.kogito.serverless.workflow.models.JsonNodeModel model = new org.kie.kogito.serverless.workflow.models.JsonNodeModel();
                java.util.Map<java.lang.String, java.lang.Object> inputs = org.jbpm.workflow.core.impl.NodeIoHelper.processInputs((org.jbpm.workflow.instance.impl.NodeInstanceImpl) kcontext.getNodeInstance(), (java.lang.String name) -> {
                    return kcontext.getVariable(name);
                });
                model.update(inputs);
                return model;
            }

            public org.kie.kogito.process.ProcessInstance<JsonNodeModel> createInstance(JsonNodeModel model) {
                return processshippinghandling.createInstance(model);
            }

            public void unbind(org.kie.api.runtime.process.ProcessContext kcontext, JsonNodeModel model) {
                java.util.Map<java.lang.String, java.lang.Object> outputs = new java.util.HashMap<java.lang.String, java.lang.Object>();
                outputs.put("workflowdata", model.getWorkflowdata());
                org.jbpm.workflow.core.impl.NodeIoHelper.processOutputs((org.jbpm.workflow.instance.impl.NodeInstanceImpl) kcontext.getNodeInstance(), (java.lang.String name) -> {
                    return outputs.get(name);
                }, (java.lang.String name) -> {
                    return kcontext.getVariable(name);
                });
            }
        });
        subProcessNode18.mapDataInputAssociation(new org.jbpm.workflow.core.impl.DataAssociation(java.util.Arrays.asList(new org.jbpm.workflow.core.impl.DataDefinition("workflowdata", "workflowdata", "com.fasterxml.jackson.databind.JsonNode", null)), new org.jbpm.workflow.core.impl.DataDefinition("workflowdata", "workflowdata", "com.fasterxml.jackson.databind.JsonNode", null), null, null));
        subProcessNode18.mapDataOutputAssociation(new org.jbpm.workflow.core.impl.DataAssociation(java.util.Arrays.asList(new org.jbpm.workflow.core.impl.DataDefinition("workflowdata", "workflowdata", "com.fasterxml.jackson.databind.JsonNode", null)), new org.jbpm.workflow.core.impl.DataDefinition("ProcessOrder_17", "ProcessOrder_17", "com.fasterxml.jackson.databind.JsonNode", null), null, null));
        subProcessNode18.metaData("UniqueId", "_jbpm-unique-55");
        subProcessNode18.metaData("state", "ProcessOrder");
        subProcessNode18.metaData("branch", "HandleShippingType");
        subProcessNode18.done();
        org.jbpm.ruleflow.core.factory.ActionNodeFactory<?> actionNode19 = compositeContextNode15.actionNode(19);
        actionNode19.name("Script");
        actionNode19.action(new org.kie.kogito.serverless.workflow.actions.MergeAction("ProcessOrder_17", "workflowdata"));
        actionNode19.metaData("UniqueId", "_jbpm-unique-56");
        actionNode19.done();
        org.jbpm.ruleflow.core.factory.EndNodeFactory<?> endNode20 = compositeContextNode15.endNode(20);
        endNode20.name("EmbeddedEnd");
        endNode20.terminate(true);
        endNode20.metaData("UniqueId", "_jbpm-unique-57");
        endNode20.done();
        compositeContextNode15.connection(16, 18, "16_18");
        compositeContextNode15.connection(18, 19, "18_19");
        compositeContextNode15.connection(19, 20, "19_20");
        compositeContextNode15.done();
        factory.connection(8, 1, "8_1");
        factory.connection(2, 3, "2_3");
        factory.connection(3, 4, "3_4");
        factory.connection(4, 7, "4_7");
        factory.connection(9, 8, "9_8");
        factory.connection(15, 8, "15_8");
        factory.connection(7, 9, "7_9");
        factory.connection(7, 15, "7_15");
        factory.validate();
        return factory.getProcess();
    }

    protected void registerListeners() {
        services.getSignalManager().addEventListener("shippinghandling", completionEventListener);
        services.getSignalManager().addEventListener("fraudhandling", completionEventListener);
    }
}
