# Event Driven Escalation workflow
An escalation workflow integrated with Atlassian JIRA using [SonataFlow](https://sonataflow.org/serverlessworkflow/latest/index.html)
and Event Driven Architecture (EDA).

Email service is using [MailTrap Send email API](https://api-docs.mailtrap.io/docs/mailtrap-api-docs/bcf61cdc1547e-send-email-early-access) API

## Prerequisite
* Access to a Jira server (URL, user and [API token](https://support.atlassian.com/atlassian-account/docs/manage-api-tokens-for-your-atlassian-account/))
* Access to an OpenShift cluster with `admin` Role
* An account to [MailTrap](https://mailtrap.io/home) with a [testing Inbox](https://mailtrap.io/inboxes) and an [API token](https://mailtrap.io/api-tokens)

## Escalation flow
![SWF VIZ](./src/main/resources/ticketEscalation.svg)

**Note**:
The value of the `.jiraIssue.fields.status.statusCategory.key` field is the one to be used to identify when the `done` status is reached, all the other
similar fields are subject to translation to the configured language and cannot be used for a consistent check.

## Application configuration
Application properties can be initialized from environment variables before running the application:

| Environment variable  | Description | Mandatory | Default value |
|-----------------------|-------------|-----------|---------------|
| `JIRA_URL`            | The Jira server URL | ✅ | |
| `JIRA_USERNAME`       | The Jira server username | ✅ | |
| `JIRA_API_TOKEN`      | The Jira API Token | ✅ | |
| `JIRA_PROJECT`        | The key of the Jira project where the escalation issue is created | ❌ | `TEST` |
| `JIRA_ISSUE_TYPE`     | The ID of the Jira issue type to be created | ✅ | |
| `JIRA_ISSUE_TYPE`     | The ID of the Jira issue type to be created | ✅ | |
| `JIRA_WORKFLOW_INSTANCE_ID_LABEL` | The name part of the Jira ticket label that contains the ID of the related SWF instance (e.g. `workflowInstanceId=123`)  | `workflowInstanceId` |
| `JIRA_WORKFLOW_NAME_LABEL` | The whole Jira ticket label that contains the name of the SWF (e.g. `workflowName=escalation`)  | `workflowName=escalation` |
| `MAILTRAP_URL`        | The MailTrail API Token| ❌ | `https://sandbox.api.mailtrap.io` |
| `MAILTRAP_API_TOKEN`  | The MailTrail API Token| ✅ | |
| `MAILTRAP_INBOX_ID`   | The ID of the MailTrap inbox | ✅ | |
| `MAILTRAP_SENDER_EMAIL` | The email address of the mail sender | ❌ | `escalation@company.com` |
| `OCP_API_SERVER_URL`  | The OpensShift API Server URL | ✅ | |
| `OCP_API_SERVER_TOKEN`| The OpensShift API Server Token | ✅ | |
| `ESCALATION_TIMEOUT_SECONDS` | The number of seconds to wait before triggering the escalation request, after the issue has been created | ❌ | `60` |

## How to run
You can run it locally as a Quarkus application or using the deployment instructions at [escalation-eda chart chart](../README.md#escalation-eda-chart):
```bash
mvn clean quarkus:dev
```

Initialize the environment before running some test command.

For local runtime:
```bash
ESCALATION_SWF_URL="http://localhost:8080"
```
Otherwise, in case of Knative environment:
```bash
ESCALATION_SWF_URL=$(oc get ksvc -n escalation escalation-swf -oyaml | yq '.status.url')
ESCALATION_SWF_URL="${ESCALATION_SWF_URL//\"/}"
```

Example of POST to trigger the flow (see input schema in [ticket-escalation-schema.json](./src/main/resources/ticket-escalation-schema.json)):
```bash
NAMESPACE=new-namespace
MANAGER=manager@company.com
SWF_INSTANCE_ID=$(curl -k -XPOST -H "Content-Type: application/json" "${ESCALATION_SWF_URL}/ticket-escalation" -d "{\"namespace\": \"${NAMESPACE}\", \"manager\": \"${MANAGER}\"}" | jq '.id')
SWF_INSTANCE_ID="${SWF_INSTANCE_ID//\"/}"
echo $SWF_INSTANCE_ID
```

To resume the pending instance, send a CloudEvent with:

```bash
curl -k -X POST -H "Content-Type: application/cloudevents+json" -d "{ \
  \"specversion\": \"1.0\", \
  \"type\": \"dev.parodos.escalation\", \
  \"source\": \"jira.listener\", \
  \"id\": \"123456\", \
  \"time\": \"2023-10-10T12:00:00Z\", \
  \"kogitoprocrefid\": \"$SWF_INSTANCE_ID\", \
  \"data\": { \
    \"ticketId\": \"ES-6\", \
    \"workFlowInstanceId\":\"$SWF_INSTANCE_ID\", \
    \"workflowName\": \"escalation\", \
    \"status\": \"done\" \
    } \
  }" ${ESCALATION_SWF_URL}
```
Tips:
* Visit [Workflow Instances](http://localhost:8080/q/dev/org.kie.kogito.kogito-quarkus-serverless-workflow-devui/workflowInstances)
* Visit (Data Index Query Service)[http://localhost:8080/q/graphql-ui/]

## Building the containerized image
The application runs from a containerized image already avaliable as `quay.io/orchestrator/escalation-swf:1.0`.
You can build and publish your own image using:
```bash
mvn clean install -Pknative
docker tag quay.io/orchestrator/escalation-swf:1.0 quay.io/_YOUR_QUAY_ID_/jira-listener-jvm
docker push quay.io/_YOUR_QUAY_ID_/escalation-swf:1.0
```
