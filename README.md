# Order Service

## Setup Instructions

1. Follow the instructions from the cluster folder

2. Create the Kubernetes objects from the k8s folder:
   ```bash
   kubectl apply -f k8s/.
   ```

3. Start port forwarding:
   ```bash
   kubectl port-forward svc/order-service -n order-service 8080:80
   ```

4. Access the application from your browser:
   - http://localhost:8080/product/1
   - http://localhost:8080/version