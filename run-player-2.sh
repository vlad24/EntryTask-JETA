#! /bin/sh

set -e

mode=${1:?"Specify player 2 mode. Modes: AUTO_OPTIMAL | AUTO_RANDOM | AUTO_TRAVERSE | MANUAL"}

./gradlew clean player:build
docker build --no-cache -t "threegame-player:latest" ./player
docker run -it --rm --network host \
 -e player.type=2 \
 -e player.mode="${mode:-'AUTO_OPTIMAL'}" \
 -e player.auto.startBalanceFrom=50 \
 -e player.auto.startBalanceTo=100 \
 -e player.auto.moveDelaySec=1 \
 threegame-player
