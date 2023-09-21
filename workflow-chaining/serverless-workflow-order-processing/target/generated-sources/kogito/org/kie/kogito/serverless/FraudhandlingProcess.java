package org.kie.kogito.serverless;

import org.kie.kogito.serverless.workflow.models.JsonNodeModel;
import org.kie.api.definition.process.Process;
import org.jbpm.ruleflow.core.RuleFlowProcessFactory;
import org.jbpm.process.core.datatype.impl.type.ObjectDataType;
import org.drools.core.util.KieFunctions;

@javax.enterprise.context.ApplicationScoped()
@javax.inject.Named("fraudhandling")
@io.quarkus.runtime.Startup()
public class FraudhandlingProcess extends org.kie.kogito.process.impl.AbstractProcess<org.kie.kogito.serverless.workflow.models.JsonNodeModel> {

    @javax.inject.Inject()
    org.kie.kogito.serverless.FraudhandlingMessageProducer_2 producer_2;

    @javax.inject.Inject()
    public FraudhandlingProcess(org.kie.kogito.app.Application app, org.kie.kogito.correlation.CorrelationService correlations) {
        super(app, java.util.Arrays.asList(), correlations);
        activate();
    }

    public FraudhandlingProcess() {
    }

    @Override()
    public org.kie.kogito.serverless.FraudhandlingProcessInstance createInstance(org.kie.kogito.serverless.workflow.models.JsonNodeModel value) {
        return new org.kie.kogito.serverless.FraudhandlingProcessInstance(this, value, this.createProcessRuntime());
    }

    public org.kie.kogito.serverless.FraudhandlingProcessInstance createInstance(java.lang.String businessKey, org.kie.kogito.serverless.workflow.models.JsonNodeModel value) {
        return new org.kie.kogito.serverless.FraudhandlingProcessInstance(this, value, businessKey, this.createProcessRuntime());
    }

    public org.kie.kogito.serverless.FraudhandlingProcessInstance createInstance(java.lang.String businessKey, org.kie.kogito.correlation.CompositeCorrelation correlation, org.kie.kogito.serverless.workflow.models.JsonNodeModel value) {
        return new org.kie.kogito.serverless.FraudhandlingProcessInstance(this, value, businessKey, this.createProcessRuntime(), correlation);
    }

    @Override()
    public org.kie.kogito.serverless.workflow.models.JsonNodeModel createModel() {
        return new org.kie.kogito.serverless.workflow.models.JsonNodeModel();
    }

    public org.kie.kogito.serverless.FraudhandlingProcessInstance createInstance(org.kie.kogito.Model value) {
        return this.createInstance((org.kie.kogito.serverless.workflow.models.JsonNodeModel) value);
    }

    public org.kie.kogito.serverless.FraudhandlingProcessInstance createInstance(java.lang.String businessKey, org.kie.kogito.Model value) {
        return this.createInstance(businessKey, (org.kie.kogito.serverless.workflow.models.JsonNodeModel) value);
    }

    public org.kie.kogito.serverless.FraudhandlingProcessInstance createInstance(org.kie.api.runtime.process.WorkflowProcessInstance wpi) {
        return new org.kie.kogito.serverless.FraudhandlingProcessInstance(this, this.createModel(), this.createProcessRuntime(), wpi);
    }

    public org.kie.kogito.serverless.FraudhandlingProcessInstance createReadOnlyInstance(org.kie.api.runtime.process.WorkflowProcessInstance wpi) {
        return new org.kie.kogito.serverless.FraudhandlingProcessInstance(this, this.createModel(), wpi);
    }

    protected org.kie.api.definition.process.Process process() {
        RuleFlowProcessFactory factory = RuleFlowProcessFactory.createProcess("fraudhandling", true);
        factory.variable("workflowdata", org.jbpm.process.core.datatype.DataTypeResolver.fromClass(com.fasterxml.jackson.databind.JsonNode.class), "{}", "customTags", null);
        factory.name("Fraud Handling");
        factory.packageName("org.kie.kogito.serverless");
        factory.dynamic(false);
        factory.version("1.0");
        factory.type("SW");
        factory.visibility("Public");
        org.jbpm.ruleflow.core.factory.StartNodeFactory<?> startNode1 = factory.startNode(1);
        startNode1.name("Start");
        startNode1.interrupting(false);
        startNode1.metaData("UniqueId", "_jbpm-unique-64");
        startNode1.metaData("state", "FraudHandling");
        startNode1.done();
        org.jbpm.ruleflow.core.factory.EndNodeFactory<?> endNode2 = factory.endNode(2);
        endNode2.name("End");
        endNode2.terminate(false);
        endNode2.action(new org.jbpm.process.instance.impl.actions.ProduceEventAction<com.fasterxml.jackson.databind.JsonNode>("fraudEvaluation", "workflowdata", () -> producer_2));
        endNode2.metaData("UniqueId", "_jbpm-unique-65");
        endNode2.metaData("TriggerType", "ProduceMessage");
        endNode2.metaData("EventType", "message");
        endNode2.metaData("state", "FraudVerificationNeeded");
        endNode2.metaData("TriggerRef", "fraudEvaluation");
        endNode2.metaData("MappingVariable", "workflowdata");
        endNode2.metaData("MessageType", "com.fasterxml.jackson.databind.JsonNode");
        endNode2.done();
        org.jbpm.ruleflow.core.factory.SplitFactory<?> splitNode3 = factory.splitNode(3);
        splitNode3.name("FraudHandling");
        splitNode3.type(2);
        splitNode3.metaData("UniqueId", "3");
        splitNode3.metaData("Variable", "workflowdata");
        splitNode3.metaData("state", "FraudHandling");
        splitNode3.constraint(5, "3_5", "DROOLS_DEFAULT", "jsonpath", new org.jbpm.process.instance.impl.ExpressionReturnValueEvaluator("jsonpath", "$.[?(@.total <= 1000)]", "workflowdata"), 0, false);
        splitNode3.constraint(4, "3_4", "DROOLS_DEFAULT", "jsonpath", new org.jbpm.process.instance.impl.ExpressionReturnValueEvaluator("jsonpath", "$.[?(@.total > 1000)]", "workflowdata"), 0, false);
        splitNode3.done();
        org.jbpm.ruleflow.core.factory.ActionNodeFactory<?> actionNode4 = factory.actionNode(4);
        actionNode4.name("FraudVerificationNeeded");
        actionNode4.action(new org.kie.kogito.serverless.workflow.actions.InjectAction("{\"fraudEvaluation\":true}"));
        actionNode4.metaData("UniqueId", "_jbpm-unique-67");
        actionNode4.metaData("state", "FraudVerificationNeeded");
        actionNode4.done();
        org.jbpm.ruleflow.core.factory.EndNodeFactory<?> endNode5 = factory.endNode(5);
        endNode5.name("End");
        endNode5.terminate(false);
        endNode5.metaData("UniqueId", "_jbpm-unique-68");
        endNode5.done();
        factory.connection(4, 2, "4_2");
        factory.connection(1, 3, "1_3");
        factory.connection(3, 4, "3_4");
        factory.connection(3, 5, "3_5");
        factory.validate();
        return factory.getProcess();
    }
}
