#!/bin/bash

# Create storage directory on all nodes
for node in $(kubectl get nodes -o jsonpath='{.items[*].metadata.name}'); do
  echo "Creating directory on node: $node"
  docker exec $node mkdir -p /tmp/postgres-data
  docker exec $node chmod 777 /tmp/postgres-data
done
