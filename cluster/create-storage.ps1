# Create storage directory on all nodes
$nodes = kubectl get nodes -o jsonpath='{.items[*].metadata.name}'
$nodeArray = $nodes -split ' '

foreach ($node in $nodeArray) {
    Write-Host "Creating directory on node: $node"
    docker exec $node mkdir -p /tmp/postgres-data
    docker exec $node chmod 777 /tmp/postgres-data
}