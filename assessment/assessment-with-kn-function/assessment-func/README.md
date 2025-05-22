> **🚨 Deprecation Notice: 🚨**  
> From Orchestrator release version 1.7, Workflow Types will be retired. All workflows will act as infrastructure workflows, and no workflow will act as an assesment workflow. <br>
> This workflow, being an assessment workflow, will be obsolete and irrelevant.

# Assessment kn func

A simple function `assessment-func` written in Java using Quarkus to expose a REST API.

## Prerequisite

- Have a Kubernetes cluster running with the Knative Serving component
  installed. For more information, see the
  [Knative installation instructions](https://knative.dev/docs/install/).

- Have `docker`, `maven`, `kubeclt` installed.

- Have an account on [quay.io](https://quay.io/).

**Note**: Replace `docker` with `podman`, if preferred.


## Testing locally

Run the following command:

```bash
mvn clean quarkus:dev
```

Execute the command:

```bash
curl -XPOST -H "Content-Type: application/json" http://localhost:9090/execute -d '{"inputText": "_YOUR_DUMMY_TEXT_"}'
```

Response:
```
{
    "currentVersion": {
        "id": "dummy-infra-workflow-option",
        "name": "Dummy infra workflow option"
    },
    "upgradeOptions": [],
    "migrationOptions": [],
    "newOptions": [],
    "continuationOptions": [],
    "otherOptions": []
}
```

## Building and deploying to cluster

1. Build and push the image to Quay by running these commands while replacing `{username}` with your Quay username:

   ```bash
   # Build and push the container on your local machine.
   docker buildx build --platform linux/arm64,linux/amd64 -t "quay.io/{username}/assessment-func" --push .

   # (OR)
   # Build and push the container on your local machine. - Quarkus native mode
   docker buildx build --platform linux/arm64,linux/amd64 -t "quay.io/{username}/assessment-func" --push . -f Dockerfile.native
   ```

1. Deploy the function into the cluster by applying
   the configuration in `k8s/service.yaml`using `kubectl`. Ensure that the container image value matches the container built in the previous step.

   ```bash
   kubectl apply -f k8s/service.yaml
   ```

1. Now that the service is created, Knative will perform the following steps:

   - Create a new immutable revision for this version of the function.
   - Network programming to create a route, ingress, service, and load balancer for the function.
   - Automatically scale pods up and down (including to zero active pods).

1. Find the URL for the service using the following command:

   ```bash
   kubectl get ksvc assessment-func

   NAME                     URL
   assessment-func          http://assessment-func.default.127.0.0.1.sslip.io
   ```

1. Try to make a request to the function `assessment-func` and see the result. Replace the URL below with the URL returned in the previous command.

```bash
curl -XPOST -H "Content-Type: application/json" http://assessment-func.default.127.0.0.1.sslip.io:9090/execute -d '{"inputText": "_YOUR_DUMMY_TEXT_"}'
```

Response:
```
{
    "currentVersion": {
        "id": "dummy-infra-workflow-option",
        "name": "Dummy infra workflow option"
    },
    "upgradeOptions": [],
    "migrationOptions": [],
    "newOptions": [],
    "continuationOptions": [],
    "otherOptions": []
}
```

## Removing from cluster
Run the following command to delete the service from the cluster:

```bash
kubectl delete -f k8s/service.yaml
```
