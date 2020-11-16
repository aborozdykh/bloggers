#!/usr/bin/env bash

pgrep java | xargs kill -9

java -jar target/bloggers-1.0.jar --spring.profiles.active=pre-prod -Xcom -Xmx1024m > logs/log.txt &