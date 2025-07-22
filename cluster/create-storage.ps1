# Create storage directory on all nodes
$nodes = kubectl get nodes -o jsonpath='{.items[*].metadata.name}'
$nodeArray = $nodes -split ' '

foreach ($node in $nodeArray) {
    Write-Host "Creating directory on node: $node"
    docker exec $node powershell -Command "New-Item -ItemType Directory -Path C:\tmp\postgres-data -Force"
    docker exec $node powershell -Command "icacls C:\tmp\postgres-data /grant Everyone:F"
}