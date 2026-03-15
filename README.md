# Digicert Demo - Harness Deployment Setup

This repository contains Harness CI/CD configurations for deploying two applications (app-alpha and app-beta) using progressive canary deployment strategy across Dev, QA, and Prod environments.

## Repository Structure

```
.
├── .harness/
│   ├── pipelines/           # Pipeline definitions
│   ├── templates/           # Deployment templates
│   ├── services/            # Service definitions
│   ├── environments/        # Environment definitions
│   ├── infrastructures/     # Infrastructure definitions
│   └── connectors/          # Connector configurations
├── manifests/
│   ├── app-alpha/          # Kubernetes manifests for app-alpha
│   └── app-beta/           # Kubernetes manifests for app-beta
├── team_a/                 # Team A workspace (legacy)
└── team_b/                 # Team B workspace (legacy)
```

## Prerequisites

1. Harness account with appropriate permissions
2. GitHub account
3. Docker Hub account (or other container registry)
4. Kubernetes cluster(s) for Dev, QA, and Prod
5. Harness Delegate installed in your infrastructure

## Step 1: Push to GitHub

### 1.1 Create GitHub Repository

1. Go to https://github.com and create a new repository named `digicert_demo`
2. Do NOT initialize with README, .gitignore, or license

### 1.2 Push Code

```bash
# Add all files
git add .

# Commit
git commit -m "Initial commit: Harness deployment configuration

- Progressive canary deployment template
- App-alpha and app-beta pipeline definitions
- Service, environment, and infrastructure definitions
- Kubernetes manifests for both applications
- Connector configurations

Co-Authored-By: Claude Sonnet 4.5 <noreply@anthropic.com>"

# Add remote (replace <YOUR_USERNAME> with your GitHub username)
git remote add origin https://github.com/<YOUR_USERNAME>/digicert_demo.git

# Push to GitHub
git push -u origin main
```

## Step 2: Configure Harness Git Experience

### 2.1 Create GitHub Personal Access Token

1. Go to GitHub Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token with scopes: `repo`, `admin:repo_hook`, `user`
3. Copy the token

### 2.2 Add GitHub Token to Harness

1. In Harness, go to Account/Org/Project Settings → Secrets
2. Create new secret:
   - Name: `github_token`
   - Type: Text
   - Value: Paste your GitHub token

### 2.3 Create GitHub Connector in Harness

Option A: Via UI
1. Go to Project Settings → Connectors → New Connector → Code Repositories → GitHub
2. Configure:
   - Name: `GitHub Connector`
   - URL Type: Repository
   - Connection Type: HTTP
   - GitHub Repository URL: `https://github.com/<YOUR_USERNAME>/digicert_demo`
   - Username: Your GitHub username
   - Personal Access Token: Select `github_token` secret
   - Enable API Access: Yes
3. Test connection and save

Option B: Via Git Experience (after enabling)
1. The connector YAML is already in `.harness/connectors/github-connector.yaml`
2. Update placeholders:
   - `<YOUR_ORG>` → your GitHub username/org
   - `<YOUR_GITHUB_USERNAME>` → your GitHub username
   - `<YOUR_DELEGATE_SELECTOR>` → your delegate name

### 2.4 Enable Git Experience

1. Go to Project Settings → Git Experience
2. Click "Enable Git Experience"
3. Configure:
   - Git Connector: Select `GitHub Connector`
   - Repository: `digicert_demo`
   - Default Branch: `main`
   - Harness Folder: `.harness`
4. Import existing entities from Git

## Step 3: Configure Docker Registry

### 3.1 Add Docker Hub Credentials

1. Go to Account/Org/Project Settings → Secrets
2. Create secret:
   - Name: `dockerhub_password`
   - Type: Text
   - Value: Your Docker Hub password or access token

### 3.2 Create Docker Registry Connector

1. Go to Project Settings → Connectors → New Connector → Artifact Repositories → Docker Registry
2. Configure:
   - Name: `DockerHub Connector`
   - Provider Type: DockerHub
   - URL: `https://index.docker.io/v2/`
   - Username: Your Docker Hub username
   - Password: Select `dockerhub_password` secret
3. Test and save

Or update `.harness/connectors/dockerhub-connector.yaml` and push to Git.

## Step 4: Configure Kubernetes Infrastructure

### 4.1 Install Harness Delegate

For each environment (Dev, QA, Prod), install a Harness Delegate:

```bash
# Download delegate
curl -LO https://app.harness.io/storage/harness-download/delegate-helm-chart/harness-delegate-ng-<version>.tgz

# Install using Helm
helm install harness-delegate harness-delegate-ng-<version>.tgz \
  --namespace harness-delegate-ng --create-namespace \
  --set delegateName=<delegate-name> \
  --set accountId=<your-account-id> \
  --set delegateToken=<your-delegate-token>
```

### 4.2 Create Kubernetes Connectors

For each environment, create a Kubernetes connector:

1. Go to Project Settings → Connectors → New Connector → Cloud Providers → Kubernetes Cluster
2. Configure:
   - Name: `Kubernetes Dev Cluster` (or QA/Prod)
   - Details: Use the credentials of a specific Harness Delegate
   - Select Delegate: Choose your delegate

Or update the connector YAMLs in `.harness/connectors/` and sync via Git Experience.

## Step 5: Setup Environments and Infrastructure

Since Git Experience is enabled, all entities should be automatically imported from the `.harness/` directory:

- Environments: `dev`, `qa`, `prod`
- Infrastructure Definitions:
  - `infra-dev-us-east`
  - `infra-dev-us-west`
  - `infra-qa-us-east`
  - `infra-prod-us-east`

Verify in: Project → Environments

## Step 6: Deploy Your Application

### 6.1 Prepare Docker Image

For this demo, you can use a simple nginx image:

```bash
# Option 1: Use existing public image
# No action needed, use "nginx:latest" in pipeline

# Option 2: Build and push your own image
docker pull nginx:alpine
docker tag nginx:alpine <your-dockerhub-username>/app-alpha:v1.0.0
docker push <your-dockerhub-username>/app-alpha:v1.0.0

docker tag nginx:alpine <your-dockerhub-username>/app-beta:v1.0.0
docker push <your-dockerhub-username>/app-beta:v1.0.0
```

### 6.2 Run Pipeline

1. Go to Pipelines → Select `app-alpha` or `app-beta`
2. Click "Run"
3. Provide input values:
   - Service: Select service
   - Image Path: `nginx` or `<your-dockerhub-username>/app-alpha`
   - Tag: `latest` or `v1.0.0`
   - Dev Infrastructure 1: `infradevuseast`
   - Dev Infrastructure 2: `infradevuswest`
   - QA Infrastructure: `infraqauseast`
   - Prod Infrastructure: `infraproduseast`
   - Run Integration Tests: `no`
   - Run Load Tests: `no`
4. Click "Run Pipeline"

### 6.3 Monitor Deployment

The pipeline will:
1. Deploy to Dev (2 regions in parallel) with 50% canary
2. Run continuous verification
3. Complete rollout
4. Deploy to QA with 50% canary
5. Optionally run integration tests
6. Deploy to Prod with 25% canary
7. Optionally run load tests
8. Complete production rollout

## Pipeline Features

### Progressive Canary Deployment
- Dev: 50% canary → full rollout
- QA: 50% canary → integration tests → full rollout
- Prod: 25% canary → load tests → full rollout

### Continuous Verification
- Dev: 15 minutes, LOW sensitivity
- QA: 15 minutes, MEDIUM sensitivity
- Prod: 30 minutes, HIGH sensitivity

### Failure Handling
- Automatic rollback on errors
- Manual intervention on verification failures
- Stage rollback capabilities

## Customization

### Update Image References

Edit `.harness/services/app-alpha-service.yaml`:

```yaml
artifacts:
  primary:
    sources:
      - spec:
          imagePath: <your-dockerhub-username>/app-alpha  # Change this
```

### Modify Deployment Strategy

Edit `.harness/templates/progressive-canary-template.yaml` to adjust:
- Canary percentages
- Verification duration
- Test execution logic

### Add More Environments

1. Create environment YAML in `.harness/environments/`
2. Create infrastructure definition in `.harness/infrastructures/`
3. Add stage to pipeline template
4. Commit and push to GitHub

## Troubleshooting

### Delegate Not Connected
- Check delegate logs: `kubectl logs -n harness-delegate-ng <delegate-pod>`
- Verify delegate token and account ID
- Ensure network connectivity to Harness SaaS

### Git Sync Failures
- Verify GitHub token has correct permissions
- Check repository URL is correct
- Ensure `.harness` folder structure matches Harness requirements

### Deployment Failures
- Verify Kubernetes connector can access cluster
- Check namespace exists or will be created
- Ensure Docker image exists and is accessible
- Review Harness delegate logs

### Artifact Not Found
- Verify Docker registry connector is configured correctly
- Check image path and tag are correct
- Ensure Docker Hub credentials are valid

## Additional Resources

- [Harness Documentation](https://developer.harness.io/)
- [Harness Git Experience](https://developer.harness.io/docs/platform/git-experience/git-experience-overview/)
- [Kubernetes Deployments](https://developer.harness.io/docs/continuous-delivery/deploy-srv-diff-platforms/kubernetes/kubernetes-cd-quickstart/)
- [Canary Deployments](https://developer.harness.io/docs/continuous-delivery/manage-deployments/deployment-concepts)

## Support

For issues or questions:
1. Check Harness documentation
2. Review deployment logs in Harness UI
3. Inspect delegate logs
4. Contact Harness support

## License

This is a demonstration project for Digicert.
