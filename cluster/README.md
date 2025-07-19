# Kind Kubernetes Cluster Setup

This directory contains configuration files for setting up a local Kubernetes cluster using Kind (Kubernetes in Docker) with PostgreSQL database.

## Prerequisites

- Docker installed and running
- [Kind](https://kind.sigs.k8s.io/docs/user/quick-start/#installation) installed
- kubectl installed

## Setup Instructions

### 1. Create the Kind Cluster

Create a multi-node cluster with ingress support:

```bash
kind create cluster --name devops-cluster --config kind-config.yml
```

This creates:
- 1 control-plane node with ingress ready
- 1 worker node
- Port mappings for HTTP (80) and HTTPS (443)

### 2. Create Storage Directory

Deploy the storage provisioner DaemonSet to automatically create the directory on all nodes:

```bash
kubectl apply -f storage-provisioner.yaml
```

Wait for the DaemonSet to complete on all nodes:
```bash
kubectl -n storage-provisioner get pods -w
```

Verify storage directory was created:
```bash
kubectl -n storage-provisioner logs -l app=storage-provisioner
```

Run verification job:
```bash
kubectl -n storage-provisioner get job verify-storage
kubectl -n storage-provisioner logs job/verify-storage
```

This creates the directory for:
- PostgreSQL data (`/tmp/postgres-data`)

### 3. Apply Storage Configuration

Create the storage class and persistent volume:

```bash
kubectl apply -f local-storage.yaml
```

This creates:
- A `local-storage` StorageClass
- Persistent volume for PostgreSQL

### 4. Deploy PostgreSQL

Deploy PostgreSQL with persistent storage:

```bash
kubectl apply -f postgres.yml
```

This deploys:
- PostgreSQL 17 with 2Gi persistent storage
- Secret with credentials (username: `postgres`, password: `postgres123`)
- Default database: `demoapp`
- Service exposed on port 5432

## Connecting to PostgreSQL

To connect to the PostgreSQL pod and create/manage databases:

```bash
kubectl exec -it <pod-name> -- psql -U postgres
```

Replace `<pod-name>` with the actual pod name. To get the pod name:

```bash
kubectl get pods -l app=postgres
```

Example:
```bash
kubectl exec -it postgres-74fc4b9579-xxxxx -- psql -U postgres
```

### Common PostgreSQL Commands

Once connected to PostgreSQL:

```sql
-- List all databases
\l

-- Connect to a database
\c demoapp

-- Create a new database
CREATE DATABASE myapp;

-- List tables in current database
\dt

-- Exit PostgreSQL
\q
```

## Cluster Management

### View cluster info
```bash
kubectl cluster-info --context kind-devops-cluster
```

### Delete the cluster
```bash
kind delete cluster --name devops-cluster
```

### Check node status
```bash
kubectl get nodes
```

### View all resources
```bash
kubectl get all -A
```

## Troubleshooting

### If storage directory is not accessible
Redeploy the DaemonSet:
```bash
kubectl delete -f storage-provisioner.yaml
kubectl apply -f storage-provisioner.yaml
```

### If PostgreSQL pod is not starting
Check pod events:
```bash
kubectl describe pod -l app=postgres
```

Check logs:
```bash
kubectl logs -l app=postgres
```

### Connection details for applications
- **Host**: `postgres-service` (within cluster)
- **Port**: `5432`
- **Username**: `postgres`
- **Password**: `postgres123`
- **Database**: `demoapp`

## Alternative: Manual Storage Setup (Legacy)

The `create-storage.sh` script is provided as an example of manual directory creation using Docker exec. However, the Kubernetes-native DaemonSet approach is recommended as it:
- Is declarative and GitOps-friendly
- Automatically runs on new nodes
- Doesn't require external script execution
- Works with any Kubernetes environment (not just Kind)