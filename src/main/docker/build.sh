#!/bin/bash

cp ../../../target/music-service*.jar .

docker build -t docker.io/luckysahaf/music-service:latest .

rm music-service*.jar