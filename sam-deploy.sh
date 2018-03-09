#!/bin/bash

aws cloudformation package \
--s3-bucket plantuml-serverless-dev-serverlessdeploymentbucke-1etumhykwyac9 \
--output-template-file target/serverless-output.yaml \
--template-file /Users/mika/code/plantuml-serverless/sam-template.yml

aws cloudformation deploy \
--template-file /Users/mika/code/plantuml-serverless/target/serverless-output.yaml \
--stack-name sam-plantuml \
--capabilities CAPABILITY_IAM