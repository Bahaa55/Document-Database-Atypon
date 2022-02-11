#!/bin/bash

for (( i=2001; i< (2004); i++ ))
do
	cd ..
	cd nodes
	mkdir ${i}
	cd ${i}
	git init
	git pull https://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:@github.com/Bahaa55/Document-Database-Atypon
	cd read
	nohup mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=${i} &
	curl http://localhost:2000/add-node?port=${i}
	cd ..
done
