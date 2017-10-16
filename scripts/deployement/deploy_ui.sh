#!/usr/bin/env bash

TAR_NAME="buymythings_ui.tar"
BINARY_SERVER_USER="menelgil"
BINARY_SERVER_HOST="remyradix.fr"
BINARY_SERVER_DIR="BuyMyThings/packaging"

DOCKER_IMAGE="com.radix/buymythings_ui"

echo "[$(date +"%Y-%m-%d_%Hh%M")] retrieving packaged binary"
scp ${BINARY_SERVER_USER}@${BINARY_SERVER_HOST}:./${BINARY_SERVER_DIR}/${TAR_NAME} ./${TAR_NAME}

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] retrieving package binary failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] extracting binary"
tar xvf ${TAR_NAME}

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] binary extraction failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] removing binary"
rm -f ${TAR_NAME}

echo "[$(date +"%Y-%m-%d_%Hh%M")] building docker image"
(cd docker; docker build -t ${DOCKER_IMAGE} .)

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] building docker image failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] stopping service"
sudo /etc/init.d/UI stop

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] stopping service failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] updating service init.d file"
sudo cp init.d/UI /etc/init.d/UI
sudo chmod +x /etc/init.d/UI


echo "[$(date +"%Y-%m-%d_%Hh%M")] starting service"
sudo /etc/init.d/UI start

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] starting service failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] deployement done"
