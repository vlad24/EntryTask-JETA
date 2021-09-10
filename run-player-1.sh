#! /bin/sh

set -e

mode=${1:?"Specify player 1 mode. Modes: AUTO_OPTIMAL | AUTO_RANDOM | AUTO_TRAVERSE | MANUAL"}

./gradlew clean player:build
docker build --no-cache -t "threegame-player:latest" ./player
docker run -it --rm --network host \
 -e player.type=1 \
 -e player.mode="${mode:-'AUTO_OPTIMAL'}" \
 -e player.startBalanceFrom=50000 \
 -e player.startBalanceTo=100000 \
 -e player.moveDelaySec=1 \
 threegame-player
