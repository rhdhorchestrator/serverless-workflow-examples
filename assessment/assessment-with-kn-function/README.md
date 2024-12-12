# Assessment workflow with kn function
This assessment workflow uses kn function to evaluate a user's input text (`inputText`) in order to provide recommendation of the suitable infrastructure workflow options.

For simplicity sake, the assessment here consists of checking if `inputText` contains the keyword `dummy` and returns the `dummy-infra-workflow-option` as infrastructure workflow options.

**Note**: this example assumes that the dummy infrastructure workflow options `dummy-infra-workflow-option` provided with the core workflow is deployed and up-and-running otherwise it will be filtered out from the precheck subflow upon assessment execution.

## Workflow diagram
![Assessment workflow diagram](https://github.com/rhdhorchestrator/serverless-workflow-examples/blob/main/assessment/assessment-with-kn-function/assessment-with-kn-function.svg?raw=true)

## Assessment logic
The assessment logic is implemented in the workflow definition in `assessment-with-jq-expression.sw.yaml` file as follows:
```java
if (null != input &&
    null != input.getInputText() &&
    input.getInputText().toLowerCase().contains("dummy")) { // basic check for infrastructure workflow options recommendation
    workflowOptions.setCurrentVersion(new WorkflowOption("dummy-infra-workflow-option", "Dummy infra workflow option"));
    return workflowOptions;
}
```

## How to run

```bash
mvn clean quarkus:dev
```

Example of POST to trigger the flow:
```bash
curl -XPOST -H "Content-Type: application/json" http://localhost:8080/assessment-with-kn-function -d '{"inputText": "_YOUR_DUMMY_TEXT_"}'
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