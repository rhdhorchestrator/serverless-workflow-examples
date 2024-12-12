# Assessment workflow with jq expression
This assessment workflow uses jq expression to evaluate a user's input text (`inputText`) in order to provide recommendation of the suitable infrastructure workflow options.

For simplicity sake, the assessment here consists of checking if `inputText` contains the keyword `dummy` and returns the `dummy-infra-workflow-option` as infrastructure workflow options.

**Note**: this example assumes that the dummy infrastructure workflow options `dummy-infra-workflow-option` provided with the core workflow is deployed and up-and-running otherwise it will be filtered out from the precheck subflow upon assessment execution.

## Workflow diagram
![Assessment workflow diagram](https://github.com/rhdhorchestrator/serverless-workflow-examples/blob/main/assessment/assessment-with-jq-expression/assessment-with-jq-expression.svg?raw=true)

## Assessment logic
The assessment logic is implemented in the workflow definition in `assessment-with-jq-expression.sw.yaml` file as follows:
```yaml
- name: AssessState
  type: switch
  dataConditions:
  - condition: "${ .inputText | ascii_downcase | contains(\"dummy\") }" # basic check for infrastructure workflow options recommendation
    transition: JavaWorkflowOptions
  defaultCondition:
    transition: NoWorkflowOptions
```

## How to run

```bash
mvn clean quarkus:dev
```

Example of POST to trigger the flow:
```bash
curl -XPOST -H "Content-Type: application/json" http://localhost:8080/assessment-with-jq-expression -d '{"inputText": "_YOUR_DUMMY_TEXT_"}'
```

Response:
```
{
  "workflowdata": {
    "result": {...},
    "preCheck": {...},
    "inputText": "_YOUR_DUMMY_TEXT_",
    "workflowOptions": {
      "newOptions": [],
      "otherOptions": [],
      "currentVersion": {
        "id": "dummy-infra-workflow-option",
        "name": "Dummy infra workflow option"
      },
      "upgradeOptions": [],
      "migrationOptions": [],
      "continuationOptions": []
    }
  }
}
```