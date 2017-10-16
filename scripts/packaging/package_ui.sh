#!/usr/bin/env bash

ROOT_WORKING_DIR="target/packaging"
WORKING_DIR="ui/target/packaging"
DOCKER_DIR="$WORKING_DIR/docker"
TAR_NAME="buymythings_ui.tar"

BINARY_REPO="/var/jenkins_home/binary_repository/BuyMyThings"

echo "[$(date +"%Y-%m-%d_%Hh%M")] cleaning packaging directories"
rm -f ${ROOT_WORKING_DIR}/${TAR_NAME}
rm -rf ${WORKING_DIR}

echo "[$(date +"%Y-%m-%d_%Hh%M")] creating packaging directories"
mkdir -p ${ROOT_WORKING_DIR}
mkdir -p ${WORKING_DIR}
mkdir -p ${WORKING_DIR}/init.d
mkdir -p ${DOCKER_DIR}
mkdir -p ${DOCKER_DIR}/libs

echo "[$(date +"%Y-%m-%d_%Hh%M")] packaging BuyMyThings/UI"
sbt -Dsbt.log.noformat=true UI/stage

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] packaging failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] copying files to packaging directory"
cp ui/UI ${WORKING_DIR}/init.d/
cp ui/target/universal/stage/lib/* ${DOCKER_DIR}/libs/
cp ui/conf/logback.xml ${DOCKER_DIR}/
cp ui/Dockerfile ${DOCKER_DIR}/

echo "[$(date +"%Y-%m-%d_%Hh%M")] creating packaged tar"
(cd ${WORKING_DIR}; tar cf ${TAR_NAME} init.d docker)

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] tar creation failed"
  exit 1;
fi
mv "$WORKING_DIR/$TAR_NAME" "$ROOT_WORKING_DIR/${TAR_NAME}"

echo "[$(date +"%Y-%m-%d_%Hh%M")] moving packaged tar to binary repository"
mkdir -p ${BINARY_REPO}
mv ${ROOT_WORKING_DIR}/${TAR_NAME} ${BINARY_REPO}/

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] tar move failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] packaging done"
