#!/bin/bash
set -x

source pre-start-monitoring.sh

sudo docker compose --profile monitoring --profile mongo --profile hello-service --verbose up -d
