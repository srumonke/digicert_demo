# Quick Start Guide

This guide will get you deploying in under 15 minutes.

## Prerequisites Checklist

- [ ] Harness account (free trial available at https://harness.io)
- [ ] GitHub account
- [ ] Docker Hub account
- [ ] Kubernetes cluster access (or use minikube/kind for testing)

## 5-Minute Setup

### 1. Push to GitHub (2 min)

```bash
# Create repo on GitHub: https://github.com/new
# Name: digicert_demo

# Push code
git add .
git commit -m "Initial Harness setup"
git remote add origin https://github.com/<YOUR_USERNAME>/digicert_demo.git
git push -u origin main
```

### 2. Setup Harness Project (1 min)

1. Login to Harness: https://app.harness.io
2. Create Organization: `default` (if not exists)
3. Create Project: `Digicert`

### 3. Configure GitHub Integration (3 min)

**Create GitHub Token:**
1. GitHub → Settings → Developer settings → Personal access tokens → Generate new token (classic)
2. Select scopes: `repo`, `admin:repo_hook`, `user`
3. Copy token

**Add to Harness:**
1. Harness → Project Digicert → Project Settings → Secrets → New Secret
2. Name: `github_token`, Value: your token

**Create Connector:**
1. Project Settings → Connectors → New → GitHub
2. URL: `https://github.com/<YOUR_USERNAME>/digicert_demo`
3. Authentication: HTTP → Username + Token
4. Username: your GitHub username
5. Token: select `github_token`
6. Enable API Access
7. Test & Save

### 4. Enable Git Experience (2 min)

1. Project Settings → Git Experience → Enable
2. Connector: `GitHub Connector`
3. Repository: `digicert_demo`
4. Branch: `main`
5. Harness Folder: `.harness`
6. Click "Import from Git"

All pipelines, services, environments, and infrastructure will be imported automatically!

### 5. Setup Docker Registry (2 min)

**Create Secret:**
1. Project Settings → Secrets → New Secret
2. Name: `dockerhub_password`
3. Value: your Docker Hub password

**Create Connector:**
1. Connectors → New → Docker Registry
2. Provider: DockerHub
3. URL: `https://index.docker.io/v2/`
4. Username: your Docker Hub username
5. Password: select `dockerhub_password`
6. Test & Save

### 6. Setup Kubernetes (3 min)

**Option A: Use Existing K8s Cluster**

Install Harness Delegate:
```bash
# Get delegate installation command from:
# Harness → Project Settings → Delegates → New Delegate → Kubernetes

# Example:
helm repo add harness-delegate https://app.harness.io/storage/harness-download/delegate-helm-chart/
helm repo update
helm install harness-delegate harness-delegate/harness-delegate-ng \
  --namespace harness-delegate-ng --create-namespace \
  --set delegateName=dev-delegate \
  --set accountId=YOUR_ACCOUNT_ID \
  --set delegateToken=YOUR_DELEGATE_TOKEN
```

**Option B: Local Testing with Minikube**

```bash
# Start minikube
minikube start --cpus 4 --memory 8192

# Install delegate
kubectl create namespace harness-delegate-ng
# Follow delegate installation from Harness UI
```

**Create K8s Connector:**
1. Connectors → New → Kubernetes Cluster
2. Name: `Kubernetes Dev Cluster`
3. Use credentials from Harness Delegate
4. Select your delegate
5. Test & Save

Repeat for QA and Prod (or use same for demo).

### 7. Create Namespaces (1 min)

```bash
# Create namespaces on your cluster
kubectl create namespace dev-us-east
kubectl create namespace dev-us-west
kubectl create namespace qa-us-east
kubectl create namespace prod-us-east
```

## Deploy Your First App

### Option 1: Quick Test with Nginx

1. Go to Pipelines → `app-alpha`
2. Click "Run"
3. Fill inputs:
   - **Service**: `app-alpha-service`
   - **Image Path**: `nginx`
   - **Tag**: `alpine`
   - **Dev Infrastructure 1**: `infradevuseast`
   - **Dev Infrastructure 2**: `infradevuswest`
   - **QA Infrastructure**: `infraqauseast`
   - **Prod Infrastructure**: `infraproduseast`
   - **Run Integration Tests**: `no`
   - **Run Load Tests**: `no`
4. Click "Run Pipeline"

### Option 2: Deploy Your Own App

1. Build and push your Docker image:
```bash
docker build -t <your-dockerhub-username>/myapp:v1.0.0 .
docker push <your-dockerhub-username>/myapp:v1.0.0
```

2. Update service definition to point to your image
3. Run pipeline with your image details

## What Happens During Deployment

1. **Dev Stage** (Parallel to 2 regions):
   - 50% canary deployment
   - 15-minute continuous verification
   - Canary cleanup
   - Full rolling deployment

2. **QA Stage**:
   - 50% canary deployment
   - 15-minute continuous verification
   - Integration tests (if enabled)
   - Full rolling deployment

3. **Prod Stage**:
   - 25% canary deployment
   - 30-minute continuous verification
   - Load tests (if enabled)
   - Full rolling deployment

## Verify Deployment

```bash
# Check deployments
kubectl get deployments -n dev-us-east
kubectl get pods -n dev-us-east
kubectl get svc -n dev-us-east

# Access the application (if using minikube)
minikube service app-alpha-service -n dev-us-east

# Port forward to access locally
kubectl port-forward -n dev-us-east svc/app-alpha-service 8080:80
# Then open http://localhost:8080
```

## Next Steps

1. **Customize the deployment**:
   - Edit manifests in `manifests/app-alpha/`
   - Modify canary percentages in `.harness/templates/progressive-canary-template.yaml`
   - Add actual integration/load tests

2. **Add your application**:
   - Replace nginx with your application container
   - Update manifest files with your app configuration
   - Configure health check endpoints

3. **Setup monitoring**:
   - Connect Prometheus/Datadog/etc for continuous verification
   - Configure health sources in pipeline

4. **Automate triggers**:
   - Add GitHub webhook triggers
   - Setup automated deployments on merge to main

## Common Issues

**Delegate not connecting?**
- Check logs: `kubectl logs -n harness-delegate-ng -l app=harness-delegate`
- Verify account ID and token

**Pipeline fails at deployment?**
- Ensure namespaces exist in k8s cluster
- Check k8s connector can access cluster
- Verify delegate is running

**Can't find service/environment?**
- Ensure Git Experience sync completed
- Check `.harness/` folder structure in Git
- Manually import from Git if needed

**Image pull errors?**
- Verify Docker registry connector credentials
- Check image path and tag are correct
- Ensure image exists in registry

## Demo Script

For a live demo:

1. Show GitHub repo structure
2. Show Harness Git Experience sync
3. Run `app-alpha` pipeline
4. Monitor canary deployment in real-time
5. Show automatic verification
6. Display running pods in k8s
7. Make a change, push to Git, show auto-sync
8. Run pipeline with new version

Total demo time: 5-10 minutes

## Resources

- Full setup: See [README.md](README.md)
- Harness Docs: https://developer.harness.io
- Kubernetes: https://kubernetes.io/docs/
- Git Experience: https://developer.harness.io/docs/platform/git-experience/

## Support

- Harness Community: https://community.harness.io
- Harness University: https://university.harness.io
- Harness Support: support@harness.io
