#!/bin/bash

docker pull dockcross/linux-x86
docker run --rm dockcross/linux-x86 > ./dockcross-i386-linux
chmod +x dockcross-*

