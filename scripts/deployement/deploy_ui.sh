#!/usr/bin/env bash

TAR_NAME="buymythings_ui.tar"
BINARY_REPO="/var/jenkins_home/binary_repository/BuyMyThings"

DOCKER_IMAGE="com.radix/buymythings_ui"

echo "[$(date +"%Y-%m-%d_%Hh%M")] retrieving packaged binary"
cp ${BINARY_REPO}/${TAR_NAME} .

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] retrieving packaged binary failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] extracting packaged binary"
tar xf ${TAR_NAME}
sudo chmod +x ./init.d/UI

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] packaged binary extraction failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] removing packaged binary"
rm -f ${TAR_NAME}

echo "[$(date +"%Y-%m-%d_%Hh%M")] stopping service"
sudo ./init.d/UI stop

echo "[$(date +"%Y-%m-%d_%Hh%M")] building docker image"
(cd docker; sudo docker build -t ${DOCKER_IMAGE} .)

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] building docker image failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] starting service"
sudo ./init.d/UI start

if [ "$?" -ne 0 ]; then
  echo "[$(date +"%Y-%m-%d_%Hh%M")] starting service failed"
  exit 1;
fi

echo "[$(date +"%Y-%m-%d_%Hh%M")] removing stopped containers"
sudo docker rm $(sudo docker ps -a -f status=exited -q) &> /dev/null
echo "[$(date +"%Y-%m-%d_%Hh%M")] removing unused images"
sudo docker rmi $(sudo docker images -a -f dangling=true -q) &> /dev/null

echo "[$(date +"%Y-%m-%d_%Hh%M")] deployement done"
