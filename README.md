# Three-Game Demo Implementation

## Overview

Simple implementation of game where two players are 
ping-ponging a whole number (called *balance*) which each of the players can modify according to some rules.
On each turn, a player can choose to either increment, decrement or keep the balance as is. 
After the balance is modified, it is divided by 3 if it can be done with 0 remainder.
The first player to bump balance to 1 wins.

## Solution
### Stack
The solution is written in Kotlin 1.5 utilizing Spring Boot 2.5.4.
Project is built using Gradle 7. Tests are implemented using MockK framework.

### Architecture
Each of the player is run as a separate application not knowing anything about another player.
To support dynamic online state of players,
the system relies on event processing using Apache Kafka.
Kafka is used as a source of truth for both players, and they do not utilize any service-side persistence.
Players produce/consume to/from one partitioned topic called `game`.
Kafka transactions are used to have `exactly-once` event-handling semantics.

### Distribution

#### Kafka
To run kafka there is kafka docker-compose file under `kafka-platfrom` directory.

#### Player process
Each of the player can be run in a docker container.
Corresponding `Dockerfile` can be found under `player` module.

Dockerfile assumes that project is already built. To build the player run one of `gradle` scripts in the root.

PS: for convenience, all that operations are gathered under `run-*` bash scripts in the project root.

### Player logic
Each of the 2 players can be either a real human or computer which plays according to some predefined strategy.
This is referred as `mode` and can be configured on the player process launch (see next section for launch details). 
Modes supported:
* `AUTO_OPTIMAL`: from every X gets to the closest number divisible by 3
* `AUTO_TRAVERSE`: just applies increment/decrement/keep moves sequentially regardless of the current balance
* `AUTO_RANDOM`: randomly chooses the balance update operation
* `MANUAL`: player will always ask human for further actions

When player is configured to run in some **AUTO_** mode, then:
* player 1 will always trigger new game with random initial balance from *50_000* to *100_000*
  * to customize this range override `player.startBalanceFrom` and `player.startBalanceTo` properties in run script
  * NB: this also assumes that if player 1 is restarted during ongoing game, an extra game will be launched after player 1 restarts. So to play with player connection/disconnection it is better to experiment with player 2
* on each turn player will think for some amount of seconds for demo purposes which is 1 by default
  * to customize delay set `player.moveDelaySec` property. Set to 0 or negative to have no delay
* after getting to balance == 1, player just claims the victory. Loosing player is informed and no more games are initiated.
* if some player gets to zero balance, then it is considered an instant victory (though 0 should not squeeze in on normal flow)

When player is started in **MANUAL** mode:
* initial balance is asked from the user. It must be greater than 1
* looser-player can initiate a new revenge game, if human wishes so

## Run instructions

First, for players to be able to communicate event-platform should be running. To do this run `run-kafka.sh` script which does not require any arguments.

Then each of the players can be started/stopped. To start first players use `run-player-[1|2].sh` scripts. Those scripts expect one argument specifying the playing mode for the player listed in previouse section.

### TL;DR
```bash
./run-kafka.sh
./run-player-1.sh MANUAL
./run-player-2.sh AUTO_OPTIMAL
```


## Possible improvements
* Support indefinite amount of players with search-for-opponent logic
* For 2-player-only game could add sync mechanisms(e.g. via persistence) not to start games when some already in-progress 
