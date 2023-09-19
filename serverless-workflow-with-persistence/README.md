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

We are using PostgreSQL in a container running on a local `kind` cluster.

You can used the following steps to install one or you can use any existing one.

You may need to configure/update the following properties in `application.properties`:
- quarkus.datasource.username=\<db username>
- quarkus.datasource.password=\<db password>
- quarkus.datasource.jdbc.url=\<db url>

In the configured example, we are using the admin `postgres` account.

#### Install with Helm
We will install PostgreSQL using helm charts ; make sure you have helm installed (https://helm.sh/docs/intro/install/)

1. First, we have to add the `bitnami` to helm:

`$ helm repo add bitnami https://charts.bitnami.com/bitnami`

2. Then we can install the chart, here we are using `toto` as password, you can change it at your will but remember to update  `application.propreties`:
```
$ helm install postgres bitnami/postgresql --set global.postgresql.auth.postgresPassword=toto 
NAME: postgres
LAST DEPLOYED: Fri Sep 15 15:55:24 2023
NAMESPACE: default
STATUS: deployed
REVISION: 1
TEST SUITE: None
NOTES:
CHART NAME: postgresql
CHART VERSION: 12.11.2
APP VERSION: 15.4.0

** Please be patient while the chart is being deployed **

PostgreSQL can be accessed via port 5432 on the following DNS names from within your cluster:

    postgres-postgresql.default.svc.cluster.local - Read/Write connection

To get the password for "postgres" run:

    export POSTGRES_PASSWORD=$(kubectl get secret --namespace default postgres-postgresql -o jsonpath="{.data.postgres-password}" | base64 -d)

To connect to your database run the following command:

    kubectl run postgres-postgresql-client --rm --tty -i --restart='Never' --namespace default --image docker.io/bitnami/postgresql:15.4.0-debian-11-r10 --env="PGPASSWORD=$POSTGRES_PASSWORD" \
      --command -- psql --host postgres-postgresql -U postgres -d postgres -p 5432

    > NOTE: If you access the container using bash, make sure that you execute "/opt/bitnami/scripts/postgresql/entrypoint.sh /bin/bash" in order to avoid the error "psql: local user with ID 1001} does not exist"

To connect to your database from outside the cluster execute the following commands:

    kubectl port-forward --namespace default svc/postgres-postgresql 5432:5432 &
    PGPASSWORD="$POSTGRES_PASSWORD" psql --host 127.0.0.1 -U postgres -d postgres -p 5432

WARNING: The configured password will be ignored on new installation in case when previous PostgreSQL release was deleted through the helm command. In that case, old PVC will have an old password, and setting it through helm won't take effect. Deleting persistent volumes (PVs) will solve the issue.
```

Either port-forward the service to use `localhost` `kubectl port-forward --namespace default svc/postgres-postgresql 5432:5432 &` or use the DNS address given in the output (here `postgres-postgresql.default.svc.cluster.local`) in the `application.properties`

## Run it!
To run the workflow, execute the following command:
```bash
$ mvn clean quarkus:dev
```
Go to http://localhost:8080/q/dev/ to see the UI or to http://localhost:8080/q/dev/org.kie.kogito.kogito-quarkus-serverless-workflow-devui/workflowInstances to see the workflow tools from which you can trigger new workflows and send cloud events, see https://sonataflow.org/serverlessworkflow/latest/testing-and-troubleshooting/quarkus-dev-ui-extension/quarkus-dev-ui-workflow-instances-page.html#_sending_cloud_events_to_active_workflow_instances for more details.

### Run a workflow
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