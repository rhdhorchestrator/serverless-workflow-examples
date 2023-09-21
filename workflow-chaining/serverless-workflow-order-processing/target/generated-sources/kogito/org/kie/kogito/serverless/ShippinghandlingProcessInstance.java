package org.kie.kogito.serverless;

import org.kie.kogito.serverless.workflow.models.JsonNodeModel;

public class ShippinghandlingProcessInstance extends org.kie.kogito.process.impl.AbstractProcessInstance<JsonNodeModel> {

    public ShippinghandlingProcessInstance(org.kie.kogito.serverless.ShippinghandlingProcess process, JsonNodeModel value, org.kie.api.runtime.process.ProcessRuntime processRuntime) {
        super(process, value, processRuntime);
    }

    public ShippinghandlingProcessInstance(org.kie.kogito.serverless.ShippinghandlingProcess process, JsonNodeModel value, java.lang.String businessKey, org.kie.api.runtime.process.ProcessRuntime processRuntime) {
        super(process, value, businessKey, processRuntime);
    }

    public ShippinghandlingProcessInstance(org.kie.kogito.serverless.ShippinghandlingProcess process, JsonNodeModel value, org.kie.api.runtime.process.ProcessRuntime processRuntime, org.kie.api.runtime.process.WorkflowProcessInstance wpi) {
        super(process, value, processRuntime, wpi);
    }

    public ShippinghandlingProcessInstance(org.kie.kogito.serverless.ShippinghandlingProcess process, JsonNodeModel value, org.kie.api.runtime.process.WorkflowProcessInstance wpi) {
        super(process, value, wpi);
    }

    public ShippinghandlingProcessInstance(org.kie.kogito.serverless.ShippinghandlingProcess process, JsonNodeModel value, java.lang.String businessKey, org.kie.api.runtime.process.ProcessRuntime processRuntime, org.kie.kogito.correlation.CompositeCorrelation correlation) {
        super(process, value, businessKey, processRuntime, correlation);
    }

    protected java.util.Map<String, Object> bind(JsonNodeModel variables) {
        if (null != variables)
            return variables.toMap();
        else
            return new java.util.HashMap();
    }

    protected void unbind(JsonNodeModel variables, java.util.Map<String, Object> vmap) {
        variables.fromMap(this.id(), vmap);
    }
}
