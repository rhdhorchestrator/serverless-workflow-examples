# serverless-workflow-with-persistence
This project defines a simple workflow with persistence.

The database used to store the persisted data is PostgreSQL. See https://sonataflow.org/serverlessworkflow/main/persistence/persistence-with-postgresql.html for more details.

The workflow is waiting for 2 events in a sequential manner: first `event1_event_type` then  `event2_event_type`.
A timeout of 30sec is configured for each wait state, it means that the workflow is completed after 1 minutes maximum ; either the events were received or one or all states timed out (2 * 30sec). 
A message stating which events were received is displayed in the logs, below no events were received or not in the expected order:
```bash
INFO  [org.kie.kog.ser.wor.act.SysoutAction] (kogito-event-executor-1) event-state-timeouts: f85c9da5-ed4a-4387-b19d-ece69df5bd75 has finalized. The event1 was received. -- The event2 was received.
```
![event_state_timeouts_devprofile.svg](src%2Fmain%2Fresources%2Fevent_state_timeouts_devprofile.svg)
## Dependencies

- PostgreSQL
- Quarkus add-ons:
  - kogito-addons-quarkus-persistence-jdbc: enables persistence
  - quarkus-jdbc-postgresql: jdbc add-on
  - quarkus-agroal: data source add-on

### PostgreSQL
See [PostgreSQL deployements steps](../docs/deployment.md#PostgreSQL) to deploy a local PostgreSQL instance


In the configured example, we are using the admin `postgres` account.

## Run it!
### In a K8S cluster
/!\ With `Kind` local cluster, the JobService is not able to work properly; there is a network issue and its pod cannot reach the workflow service.
/!\ Do not create a Minikube cluster yet, one will be created using Knative CLI /!\

Install quickstart plugin for Knative CLI: https://knative.dev/docs/getting-started/quickstart-install/#install-the-knative-quickstart-plugin

Update [application-knative.properties](application-knative.properties) and  [pom file](pom.xml) files with your namespace (default is `workflow-with-persistence`)

Update the field `quarkus.knative.env.vars.postgres_host` in [application-knative.properties](application-knative.properties) with your PostgreSQL service

Create the namespace the associated namespace
```bash
$ kubectl create ns workflow-with-persistence
namespace/workflow-with-persistence created
```

#### Deploy the JobService
To have the JobService run in standalone mode, you need to install quickstart plugin for Knative CLI: https://knative.dev/docs/getting-started/quickstart-install/#install-the-knative-quickstart-plugin first.

To deploy the JobService in a local K8S/OCP cluster, you need to run the following command:
```bash
$ kubectl -n workflow-with-persistence apply -f k8s/data-index-service-postgresql.yml 
service/data-index-service-postgresql created
deployment.apps/data-index-service-postgresql created
trigger.eventing.knative.dev/data-index-service-postgresql-processes-trigger created
trigger.eventing.knative.dev/data-index-service-postgresql-jobs-trigger created
```

#### Deploy the DataIndex
To have the JobService run in standalone mode, you need to install quickstart plugin for Knative CLI: https://knative.dev/docs/getting-started/quickstart-install/#install-the-knative-quickstart-plugin first.

To deploy the DataIndex in a local K8S/OCP cluster, you need to run the following command:
```bash
$ kubectl -n workflow-with-persistence apply -f k8s/jobs-service-postgresql.yml 
broker.eventing.knative.dev/default created
sinkbinding.sources.knative.dev/jobs-service-postgresql-sb created
service/jobs-service-postgresql created
deployment.apps/jobs-service-postgresql created
trigger.eventing.knative.dev/jobs-service-postgresql-create-job-trigger created
trigger.eventing.knative.dev/jobs-service-postgresql-cancel-job-trigger created
```
Then run `kubectl port-forward --namespace  workflow-with-persistence svc/data-index-service-postgresql 8888:80 &` to access the [GraphQL UI](http://localhost:8888/graphiql/)
Deploy [DataIndex](../docs/deployment.md#DataIndex) and [JobService](../docs/deployment.md#JobService) described in the [deployment file](../docs/deployment.md)

#### Deploy the workflow
In order to have the image create available in the minikube cluster, make sure to run
```bash
eval $(minikube docker-env)
```

Generates the image containing the workflow:
```bash
$ mvn clean package -Pknative
```

Deploy the Knative resources to serve the workflow:
```bash
$ kubectl -n workflow-with-persistence apply -f target/kubernetes/knative.yml 
Warning: Kubernetes default value is insecure, Knative may default this to secure in a future release: spec.template.spec.containers[0].securityContext.allowPrivilegeEscalation, spec.template.spec.containers[0].securityContext.capabilities, spec.template.spec.containers[0].securityContext.runAsNonRoot, spec.template.spec.containers[0].securityContext.seccompProfile
service.serving.knative.dev/workflow-with-persistence created

$ kubectl -n workflow-with-persistence apply -f target/kubernetes/kogito.yml 
trigger.eventing.knative.dev/event2-event-type-trigger-workflow-with-persistence created
trigger.eventing.knative.dev/event1-event-type-trigger-workflow-with-persistence created
sinkbinding.sources.knative.dev/sb-workflow-with-persistence created
broker.eventing.knative.dev/default created
```

See that your workflow is running:
```bash
$ kubectl  -n workflow-with-persistence get services.serving.knative.dev 
NAME                        URL                                                                             LATESTCREATED                     LATESTREADY                       READY   REASON
workflow-with-persistence   http://workflow-with-persistence.workflow-with-persistence.127.0.0.1.sslip.io   workflow-with-persistence-00001   workflow-with-persistence-00001   True    

$ kn service list -n workflow-with-persistence
NAME                        URL                                                                             LATEST                            AGE   CONDITIONS   READY   REASON
workflow-with-persistence   http://workflow-with-persistence.workflow-with-persistence.127.0.0.1.sslip.io   workflow-with-persistence-00001   11m   3 OK / 3     True  

$ kubectl  -n workflow-with-persistence get pods 
NAME                                                          READY   STATUS    RESTARTS   AGE
data-index-service-postgresql-68f5fdc846-grmc7                1/1     Running   0          14m
jobs-service-postgresql-5bb66b865b-wvshw                      0/1     Running   0          14m
workflow-with-persistence-00001-deployment-548474dd7d-lpf95   2/2     Running   0          4m11s
```

### Locally
To run the workflow, execute the following command:
```bash
$ mvn clean quarkus:dev
```
Go to http://localhost:8080/q/dev/ to see the UI or to http://localhost:8080/q/dev/org.kie.kogito.kogito-quarkus-serverless-workflow-devui/workflowInstances to see the workflow tools from which you can trigger new workflows and send cloud events, see https://sonataflow.org/serverlessworkflow/latest/testing-and-troubleshooting/quarkus-dev-ui-extension/quarkus-dev-ui-workflow-instances-page.html#_sending_cloud_events_to_active_workflow_instances for more details.


### Run a workflow
In this step, we consider the workflow runtime URL to be `http://localhost:8080`, if you ran it in K8S/OCP you should use the URL displayed when get the knative service:
```bash
$ kn service list -n workflow-with-persistence
NAME                        URL                                                                             LATEST                            AGE   CONDITIONS   READY   REASON
workflow-with-persistence   http://workflow-with-persistence.workflow-with-persistence.127.0.0.1.sslip.io   workflow-with-persistence-00001   11m   3 OK / 3     True
```

Either start a new workflow from the UI or execute:
```bash
$ curl -X POST      -H 'Content-Type: application/json'  http://localhost:8080/event-timeout-with-persitence
{"id":"185fd483-e765-420d-91c6-5ff3fefa0b05","workflowdata":{}}
```

Now, you can send `event1` then `event2` to complete the workflow either from the UI or by executing:
```bash
$ curl -i -X POST \
     -H 'Content-Type: application/cloudevents+json'  \
     -d '{"datacontenttype": "application/json", "specversion":"1.0","id":"<any UUID>","source":"/local/curl","type":"event1_event_type","data": "{\"eventData\":\"Event1 sent from UI\"}", "kogitoprocrefid": "<workflow_id>" }  ' \
http://localhost:8080
HTTP/1.1 202 Accepted
content-length: 0

$ curl -i -X POST \
     -H 'Content-Type: application/cloudevents+json'  \
     -d '{"datacontenttype": "application/json", "specversion":"1.0","id":"<any UUID>","source":"/local/curl","type":"event2_event_type","data": "{\"eventData\":\"Event1 sent from UI\"}", "kogitoprocrefid": "<workflow_id>" }  ' \
http://localhost:8080
HTTP/1.1 202 Accepted
content-length: 0

```
The `id` is any UUID, it can be the same UUID as the workflow ID.

YOu should see the following log in your terminal:
```bash
INFO  [org.kie.kog.ser.wor.act.SysoutAction] (kogito-event-executor-1) event-state-timeouts: f85c9da5-ed4a-4387-b19d-ece69df5bd75 has finalized. The event1 was received. -- The event2 was received.
```

If you were to not send any events or only `event2`, after 30sec the workflow will be terminated and the printed message would be:
```bash
INFO  [org.kie.kog.ser.wor.act.SysoutAction] (pool-16-thread-1) event-state-timeouts: aef745d7-9d2b-48b7-b1f8-3ccb5937b57a has finalized. The event state did not receive event1, and the timeout has overdue -- The event state did not receive event2, and the timeout has overdue
```

If you only send `event1`, it should be:
```bash
INFO  [org.kie.kog.ser.wor.act.SysoutAction] (pool-16-thread-1) event-state-timeouts: aef745d7-9d2b-48b7-b1f8-3ccb5937b57a has finalized. The event1 was received. -- The event state did not receive event2, and the timeout has overdue
```

### Check the DB
As every workflow is persisted until it is completed or terminated in the DB, if you log in the DB you should be able to see the running workflows.
In the output below, a workflow is running, if you have none, the last request will show an empty result:
```bash
$ PGPASSWORD="toto" psql --host 127.0.0.1 -U postgres -d postgres -p 5432
postgres=# \dt
                 List of relations
 Schema |         Name          | Type  |  Owner   
--------+-----------------------+-------+----------
 public | correlation_instances | table | postgres
 public | flyway_schema_history | table | postgres
 public | process_instances     | table | postgres
(3 rows)

postgres=# \d+ process_instances
                                              Table "public.process_instances"
     Column      |       Type        | Collation | Nullable | Default | Storage  | Compression | Stats target | Description 
-----------------+-------------------+-----------+----------+---------+----------+-------------+--------------+-------------
 id              | character(36)     |           | not null |         | extended |             |              | 
 payload         | bytea             |           | not null |         | extended |             |              | 
 process_id      | character varying |           | not null |         | extended |             |              | 
 version         | bigint            |           |          |         | plain    |             |              | 
 process_version | character varying |           |          |         | extended |             |              | 
Indexes:
    "process_instances_pkey" PRIMARY KEY, btree (id)
    "idx_process_instances_process_id" btree (process_id, id, process_version)
Access method: heap

postgres=# select id, process_id from process_instances ;
                  id                  |          process_id           
--------------------------------------+-------------------------------
 0e577e3d-b9c5-48e8-abc1-5dbcca5f0813 | event-timeout-with-persitence 
```

The ID matches the ID of the workflow.

Once the workflow has completed, the entry is removed from the DB.