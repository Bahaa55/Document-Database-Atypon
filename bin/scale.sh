#!/bin/bash

port=${1}
token=${2}

cd ..
cd nodes
mkdir ${port}
cd ${port}
git init
git pull https://${token}:@github.com/Bahaa55/Document-Database-Atypon
cd read
nohup mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=${port} &
curl http://localhost:2000/add-node?port=${port}

