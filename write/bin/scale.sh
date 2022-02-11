#!/bin/bash

port=${1}

cd ..
cd nodes
mkdir ${port}
cd ${port}
git init
git pull https://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:@github.com/Bahaa55/Document-Database-Atypon
cd read
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=${port}
curl http://localhost:2000/add-node?port=${port}

