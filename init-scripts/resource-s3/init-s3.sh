#!/bin/bash
echo "----> Creating S3 bucket: ${AWS_S3_BUCKET_NAME} in region ${DEFAULT_REGION}"

awslocal s3 mb "s3://${AWS_S3_BUCKET_NAME}" --region "${DEFAULT_REGION}"

echo "----> S3 bucket created."