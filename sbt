#!/bin/bash

java -Xms512M -Xmx700M -Xss1M -XX:+CMSClassUnloadingEnabled -XX:MaxPermSize=384M -jar '.\sbt-lib\sbt-launch.jar' "$@"