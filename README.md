## Installation and usage:
1. Clone the repo
2. Launch the server and game client. Replace 'holo' with your desired username.
```
./mvnw clean compile exec:java@server
./mvnw clean compile exec:java@client -Dexec.args="holo"
```

## During the game: 

There are 5 basic commands: `inventory`, `look`, `get`, `goto`, `drop`
```
“inventory” (or “inv” for short): lists all of the artefacts currently being carried by the player
“look”: describes the entities in the current location and lists the paths to other locations
“get”: picks up a specified artefact from the current location and adds it into player’s inventory
“goto”: moves the player to a new location (if there is a path to that location)
“drop”: puts down an artefact from player’s inventory and places it into the current location
```

For example:
```
holo:> look

> You are in forest - An eerie forest

> You can see artefacts:
> pickaxe - A sturdy iron pickaxe

> You can access from here:
> river
> cabin
> castle

holo:> get pickaxe

> You picked up the pickaxe.

holo:> goto river

> You moved to the river

> You are in river - A raging river
```
