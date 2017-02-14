#!/bin/bash

source /etc/profile
which java || sdk install java
which mvn || sdk install maven
source /etc/profile
# remove any previously deployed artifact
rm -rf /home/user/.m2/repository/org/projectsforge/
mkdir -p ~/.ssh
ssh-keyscan -p 2223 static.projectsforge.org >> ~/.ssh/known_hosts
eval $(ssh-agent -s)
echo "$SSH_PRIVATE_KEY" | ssh-add - 
which doxygen || sudo apt-get update && sudo apt-get install -y doxygen

# build of master
find . -name "target" -exec rm -rf {} \; || true
mvn -P\!linux-amd64,\!linux-amd64-support,linux-i386-support,linux-i386 install deploy

find . -iname "target" -exec rm -rf {} \; || true
mvn -P\!linux-amd64,\!linux-amd64-support,windows-i386-support,windows-i386 install deploy

find . -iname "target" -exec rm -rf {} \; || true
mvn -P\!linux-amd64,\!linux-amd64-support,windows-amd64-support,windows-amd64 install deploy

find . -iname "target" -exec rm -rf {} \; || true
mvn -Plinux-i386-support,linux-amd64-support,windows-i386-support,windows-amd64-support,doxygen-support,test-support,javadoc-support,alldeps-support,linux-amd64 install deploy 



