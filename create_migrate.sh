#!/bin/bash

dateTime=$(date +"%Y%m%d%H%M%S")
description=$1

sep="/"

pattern=" "

description=${description//$pattern/_}
description=$(echo ${description} | tr '[:upper:]' '[:lower:]')

if [ "$1" == "" ];then
    echo "You must enter a description."
    echo "e.g. ./create_migrate \"create card table\" or ./create_migrate.sh create_card_table"
    exit 1
fi

fileName="V${dateTime}__${description}.sql"

echo "Creating file with name: ${fileName} ..."

BASEDIR=$(dirname "$0")

basePath="${BASEDIR}${sep}src${sep}main${sep}resources${sep}db${sep}migration"
filePath="${basePath}${sep}${fileName}"
echo "Path: ${filePath}"

touch ${filePath}