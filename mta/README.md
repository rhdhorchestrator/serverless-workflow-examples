# MTA - migration analysis workflow

# Synopsis
This workflow is an assessment workflow type, that invokes an application analysis workflow using [MTA][1]
and returns the [move2kube][3] workflow reference to run next and creates a migration wave in the Jira instance configured, if the analysis is considered to be successful.

Largely the steps for this workflow are as follows:
- User initiates the workflow by providing the Git Hub repo link to analyze.
- The workflow submits the repo for analysis to MTA.
- Upon successful completion of the analysis, the workflow queries MTA for details to create migration wave such as Jira instance projects, issue types and others.
- The workflow creates migration wave in Jira with the details gathered from the step above.

The unique differentiator about this workflow is that it supports two major MTA versions v6.2.2 and v7.0.2 and integrates with configured Jira instance for creation of the migration wave.

Users are encouraged to use this workflow as self-service alternative for interacting with the MTA UI. Instead of running
a mass-migration of project from a managed place, the project stakeholders can use this (or automation) to regularly check
the cloud-readiness compatibility of their code.

- MTA v6.2.2 [instructions](./v6.2.2/openshift-local-setup/readme.md)
- MTA v7.0.2 [instructions](./v7.0.2/openshift-local-setup/readme.md)

# Prerequisites
* An OpenShift cluster available with MTA Operator 7.x installed
* The MTA Hub instance with configured [Jira Tracker](https://access.redhat.com/documentation/en-us/migration_toolkit_for_applications/7.0/html/user_interface_guide/creating-configuring-jira-connection#doc-wrapper)

# Inputs
- `repositoryUrl` [mandatory] - the git repo url to examine
- `exportToIssueManager` [mandatory] - if true creates migration wave in Jira instance
- `migrationStartDatetime` [conditional] - Must be provided, if `exportToIssueManager="true"`. e.g. "2024-07-01T00:00:00Z"
- `migrationEndDatetime` [conditional] - Must be provided, if `exportToIssueManager="true"`. e.g. "2024-07-31T00:00:00Z"
- `backstageUser` [optional] - the backstage user to send backstage notification with the analysis results
- `backstageGroup` [optional] - the backstage group to send backstage notification with the analysis results

# Runtime configuration

| key                                                             | example                                                                                      | description                               |
|-----------------------------------------------------------------|----------------------------------------------------------------------------------------------|-------------------------------------------|
| mta.url                                                         | http://mta-ui.openshift-mta.svc.cluster.local:8080                                           | Endpoint (with protocol and port) for MTA |
| quarkus.rest-client.mta_json.url                                | ${mta.url}                                                                                   | MTA hub api                               |
| quarkus.rest-client.notifications.url                           | ${BACKSTAGE_NOTIFICATIONS_URL:http://backstage-backstage.rhdh-operator/api/notifications/}   | Backstage notification url                |
| quarkus.openapi-generator.mta_json.auth.bearerAuth.bearer-token |                                                                                              | Bearer Token for MTA api                  |

All the configuration items are on [./application.properties]

# Running the workflow on a local machine
* Run the workflow instance locally on a machine, it takes a few minutes for the workflow to start, so please be patient!
```shell
mvn quarkus:dev
```

* Trigger the MTA workflow example
```shell
curl --location 'http://localhost:8080/mta-analysis' \
--header 'Accept: application/json, text/plain, */*' \
--header 'Content-Type: application/json' \
--data '{
    "repositoryURL": "https://github.com/rhkp/mock-service.git",
    "exportToIssueManager": "true",
    "migrationStartDatetime" : "2024-07-01T00:00:00Z",
    "migrationEndDatetime" : "2024-07-31T00:00:00Z"
}'
```

# Output
1. On completion the workflow returns an [options structure][2] in the exit state of the workflow (also named variables in SonataFlow)
linking to the [move2kube][3] workflow that will generate k8s manifests for container deployment.
1. When the workflow completes there should be a report link on the exit state of the workflow (also named variables in SonataFlow)

# Workflow Diagram
![mta workflow diagram](https://github.com/rhdhorchestrator/serverless-workflows/blob/main/mta/mta.svg?raw=true)

[1]: https://developers.redhat.com/products/mta/download
[2]: https://github.com/rhdhorchestrator/serverless-workflows/blob/main/assessment/schema/workflow-options-output-schema.json
[3]: https://github.com/rhdhorchestrator/serverless-workflows/tree/main/move2kube