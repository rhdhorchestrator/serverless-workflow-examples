package org.kie.kogito.app;

import org.kie.kogito.event.cloudevents.CloudEventMeta;

public class ProcessCloudEventMetaFactory {

    @javax.enterprise.inject.Produces()
    public CloudEventMeta buildCloudEventMeta_CONSUMED_orderEvent() {
        return new CloudEventMeta("orderEvent", "", org.kie.kogito.event.EventKind.CONSUMED);
    }

    @javax.enterprise.inject.Produces()
    public CloudEventMeta buildCloudEventMeta_PRODUCED_domesticShipping() {
        return new CloudEventMeta("process.shippinghandling.domesticshipping", "/process/shippinghandling", org.kie.kogito.event.EventKind.PRODUCED);
    }

    @javax.enterprise.inject.Produces()
    public CloudEventMeta buildCloudEventMeta_PRODUCED_fraudEvaluation() {
        return new CloudEventMeta("process.fraudhandling.fraudevaluation", "/process/fraudhandling", org.kie.kogito.event.EventKind.PRODUCED);
    }

    @javax.enterprise.inject.Produces()
    public CloudEventMeta buildCloudEventMeta_PRODUCED_internationalShipping() {
        return new CloudEventMeta("process.shippinghandling.internationalshipping", "/process/shippinghandling", org.kie.kogito.event.EventKind.PRODUCED);
    }
}
