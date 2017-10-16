#!/usr/bin/env bash

ROOT_WORKING_DIR="target/packaging"
WORKING_DIR="ui/target/packaging"
DOCKER_DIR="$WORKING_DIR/docker"
TAR_NAME="buymythings_ui.tar"

BINARY_SERVER_USER="menelgil"
BINARY_SERVER_HOST="remyradix.fr"
BINARY_SERVER_DIR="BuyMyThings/packaging"

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
sbt UI/stage

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
(cd ${WORKING_DIR}; tar cvf ${TAR_NAME} init.d docker)

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] tar creation failed"
  exit 1;
fi
mv "$WORKING_DIR/$TAR_NAME" "$ROOT_WORKING_DIR/${TAR_NAME}"

echo "[$(date +"%Y-%m-%d_%Hh%M")] sending packaged tar to binary server"
scp "$ROOT_WORKING_DIR/$TAR_NAME" ${BINARY_SERVER_USER}@${BINARY_SERVER_HOST}:./${BINARY_SERVER_DIR}/${TAR_NAME}

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] tar upload failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] packaging done"
