#!/bin/sh
mvn install:install-file -Dfile=zap-2.4.2.jar -DgroupId=org.zaproxy -DartifactId=zaproxy -Dversion=2.4.2 -Dpackaging=jar
