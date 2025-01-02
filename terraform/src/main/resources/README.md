# terraform
This workflow takes terraform configurations and creates a run on Terraform Cloud / Terraform Enterprise on a designated Project and Workspace. After approval, the run is applied and the infrastructure is provisioned.   
Notifications are sent to notify for success or failure upon completion

The following inputs are required:
- for Terraform Cloud
    - Terraform Cloud URL
    - Terraform Access Token
- for Terraform Configurations:
    - A URL to a raw github file containing a tar of terraform configuration files (*.tf). 

An example for such tar file can be seen [here.](https://github.com/ElaiShalevRH/TerraformDemo)

## Prerequisites
* A running instance of Terraform Cloud / Enterprise, with a Project and a Workspace created. A Workspace ID is issued and will be used in the workflows input schema. 
* A running instance of Backstage with notification plugin configured.

## Workflow application configuration
Application properties can be initialized from environment variables before running the application:

| Environment variable  | Description | Mandatory |
|-----------------------|-------------|-----------|
| `TERRAFORM_URL`       | The Terraform Cloud instance URL | ✅ |
| `TERRAFORM_TOKEN`      | The Access Token for Terraform Cloud | ✅ |


## Run instructions
1. Set up a Terraform Cloud instance with a Project, a Workspace, and procure a WorkspaceID. 
2. Set up a publically accessible tar of your terraform configurations.
3. Make sure to configure any Terraform Providers Credentials on the Terraform instance. 
4. Run the Workflow and enter the URL to the tar and the WorkspaceID as the input params.
5. Accept the Terraform Plan on the cloud instance, if not configured to be automatic. 
6. Recieve notification of success/failure.  