#!/bin/bash

# Note! This needs an ECR repo created like this:
# aws ecr create-repository --repository-name plantuml-sam \
# --image-tag-mutability IMMUTABLE --image-scanning-configuration scanOnPush=true
#
# After repo creation, edit the script to replace the repo URL in the commands below

sam build --cached

# Edit the AWS account id and region in the repo URL here
aws ecr get-login-password | docker login --username AWS \
--password-stdin 293246570391.dkr.ecr.eu-west-1.amazonaws.com

# Replace image-repository and s3-bucket values with your own
sam deploy \
--stack-name plantuml-sam \
--capabilities CAPABILITY_IAM \
--image-repository 293246570391.dkr.ecr.eu-west-1.amazonaws.com/plantuml-sam \
--s3-bucket plantuml-serverlessrepo

aws s3 cp LICENSE s3://plantuml-serverlessrepo/LICENSE
aws s3 cp serverlessrepo/README.md s3://plantuml-serverlessrepo/README.md
