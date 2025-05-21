> **🚨 Deprecation Notice: 🚨**  
> From Orchestrator release version 1.7, Workflow Types will be retired. All workflows will act as infrastructure workflows, and no workflow will act as an assesment workflow. <br>
> This workflow, being an assessment workflow, will be obsolete and irrelevant.

# Assessment
The assessment common use case is to assess inputs (ie: a link to their project code and/or an application identifying code), and based on a logic determined by the enterprise returns a list of infrastructure workflows.

A workflow of type assessment is then a workflow that performs some checks on user's input(s) and then recommends suitable workflows of type infrastucture (aka workflow options) for the next step.

## Goal
The goal in these examples is to show how an assessment workflow can be implemented:
- using **jq expression** convenient for simple use cases:
  - where the assessment logic in order to return suitable workflow options is implemented in the workflow definition.
- using **custom java code** convenient for complex use cases:
  - where the assessment logic in order to return suitable workflow options is implemented java classes.
- using **knative function** convenient for complex use cases where efficiency and scalability matter more:
  - where the assessment logic in order to return suitable workflow options is implemented a kn functions invoked via REST call.

## Flow
An assessment flow usually consists of:
- **Start**
  - get the user's input(s)
- **Assessment**
  - perform the desired check or evaluation against the user's input(s)
  - return suitable infrastructure workflow options
- **Precheck**
  - validate whether the workflows in the returned assessment options exist
  - if there are non-existed workflows in the options, then remove them from the options and output the remaining valid ones
- **End**

**Note:** the workflow options must be an object with six fields: _currentVersion, upgradeOptions, migrateOptions, newOptions, continuationOptions, otherOptions_. See `workflow-option-output-schema.json` file definied the data output schema of an assessment workflow in each example.
