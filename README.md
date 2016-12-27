## Rendering PlantUML diagrams with AWS Lambda

Serverless Framework project to render [PlantUML](http://plantuml.com) diagrams with an AWS API Gateway + Lambda
function backend in such a way that the URLs could be used directly in HTML IMG tags, for example.

This is work in progress, only a simple proof of concept for now.

### Demo

#### PNG Diagram

![Rendered PNG diagram should be here](https://393sqs3x53.execute-api.eu-west-1.amazonaws.com/plantuml/png/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0)

####SVG Diagram 

![Rendered SVG diagram should be here](https://393sqs3x53.execute-api.eu-west-1.amazonaws.com/plantuml/svg/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0)

####TXT Diagram

https://393sqs3x53.execute-api.eu-west-1.amazonaws.com/plantuml/txt/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0

### Issues
- Serverless does not support specifying binary response MIME types for API Gateway. So this has to be done manually after serverless deployment.
    - Set `*/*` as binary type.
