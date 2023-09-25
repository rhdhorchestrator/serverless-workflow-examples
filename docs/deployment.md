# Deployments
This document aimed to gather all deployments steps for each external components (ie: DB) of this repo.

## Databases
Deployment steps for databases

### PostgreSQL

We are using PostgreSQL in a container running on a local `minikube` cluster.


You may need to configure/update the following properties in `application.properties`:
- quarkus.datasource.username=\<db username>
- quarkus.datasource.password=\<db password>
- quarkus.datasource.jdbc.url=\<db url>


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

Either port-forward the service to use `localhost` `kubectl port-forward --namespace default svc/postgres-postgresql 5432:5432 &` or use the DNS address given in the output (here `postgres-postgresql.default.svc.cluster.local`) in the `application.properties` of your application.

