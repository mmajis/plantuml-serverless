# Render PlantUML diagrams with AWS Lambda

A serverless UI + API to render [PlantUML](http://plantuml.com) diagrams.

## Drop in replacement for official PlantUML server

This can be used as a drop in replacement for scenarios
where http://www.plantuml.com/plantuml is used a a rendering endpoint. You can avoid sending the diagram source to a server outside your control and use an encrypted HTTPS endpoint for the diagram traffic.

This doesn't support everything the official PlantUML server does but should be good for most intents and purposes (PNG, SVG and TXT rendering).

For example, to have Visual Studio Code PlantUML plugin render using your own serverless deployment, set the following properties in vscode for the plugin: 

```json
    "plantuml.render": "PlantUMLServer",
    "plantuml.server": "https://your-endpoint-here"
```

Use `"plantuml.server": "https://plantuml.nitorio.us"` if you'd like to try before you deploy your own.

## Available in AWS Serverless Application Repository

See the pre-packaged AWS SAM application [here in the AWS Serverless Application Repository](https://serverlessrepo.aws.amazon.com/applications/arn:aws:serverlessrepo:us-east-1:293246570391:applications~plantuml-render) and deploy it easily to your AWS account!

## Demo

### PNG Diagram

![Rendered PNG diagram should be here](https://393sqs3x53.execute-api.eu-west-1.amazonaws.com/plantuml/png/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0)

### SVG Diagram 

![Rendered SVG diagram should be here](https://393sqs3x53.execute-api.eu-west-1.amazonaws.com/plantuml/svg/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0)

### TXT Diagram

https://393sqs3x53.execute-api.eu-west-1.amazonaws.com/plantuml/txt/Kt8goYylJYrIKj2rKr1o3F1KS4yiIIrFh5IoKWZ8ALOeIirBIIrIACd8B5Oeo4dCAodDpU52KGVMw9EOcvIIgE1McfTSafcVfwI0JpU6Of09C6czhCGYlDgnwBVHrSKq80YiEJL58IKpCRqeCHVDrM0zM9oDgGqUGc0jg464hXe0

## Build

- `mvn clean package`

## Deploy

You can deploy with Serverless framework or AWS SAM. Also note that if you just want to run a released version, it's available from AWS Serverless Application Repository (see link above).
### Serverless framework:
- `sls deploy`

(Will have to remove or edit custom domains for this to work)

### AWS SAM

- Edit `sam-deploy.sh` to replace the deployment bucket with your own.
- Run `sam-deploy.sh`
