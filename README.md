# TakServer

This repo is not longer active and has been moved here
https://github.com/USTakAssociation/playtak-api

*Last updated on June 17th 2024*

Server to handle online TAK games

## Requirements
- Java 21
- Maven
- sqlite

## Getting Started
Pull the source by cloning the repository, then cd in to the project
```
git clone git@github.com:USTakAssociation/tak-server.git
cd tak-server
```

to run locally run the following commands:
Get the dependencies
```
mvn dependency:resolve
```
```
mvn compile
```
```
mvn package
```

Create the sqlite databases
```
sh ./create_databases.sh
```
This creates the players and games sqlite dbs

Optionally, you can then use the script `scripts/development/add_user.sh` to add users to the local players database with a password of "password".

```
./scripts/development/add_user.sh mylocalacct ./players.db
# See scripts/development/add_user.sh comments for more options.
```

copy the properties and message to the target
```
cp properties.xml ./target
cp message ./target
```

Configure `porthttp`, `portws`, `db-path` and `event-subscriber-url` in `./target/properties.xml` to your liking.

Finaly run the app
```
cd ./target
java -jar ./takserver-jar-with-dependencies.jar
```

If you want to run the app from `./` via `java -jar ./target/takserver-jar-with-dependencies.jar` then `./properties.xml` will be used and you may need to set `<db-path>./target/</db-path>`.

## Rest API for PNT
A http server is running on `porthttp`, allowing the creation of tournament seeks.

Details need to be filled in here. Until then, please see [TakServer.java](./src/main/java/tak/TakServer.java) for details.

### HTTP API
||URL|Body|Return|Comment|
|-|-|-|-|-|
|PUT|/api/v1/seeks|[SeekDto](./src/main/java/tak/SeekDto.java) without `id`|[SeekDto](./src/main/java/tak/SeekDto.java)|Creates a seek|
|GET|/api/v1/seeks|n/a|[List\[SeekDto\]](./src/main/java/tak/SeekDto.java)|Returns list of existing seeks|

## GameUpdateBroadcaster
The [GameUpdateBroadcaster](./src/main/java/tak/GameUpdateBroadcaster.java) reads an URL from `server-settings.event-subscriber-url` (in `properties.xml`)
and sends `POST` requests there containing
```typescript
{
	type: "game.created"|"game.ended",
	game: GameDto
}
```
(see [GameDto](./src/main/java/tak/DTOs/GameDto.java)). If the URL could not be parsed, a `SEVERE` log message will warn about it but the Tak server will still start.

## Server API

|Environment|Telnet `port`|Websocket `portws`|HTTP `porthttp`|
|-|-|-|-|
|Production | 10000 | 9999 | 9998|
|Beta | 10002 | 10001 | 10003 |
|Local dev | 10000 | 9999 | 9998 |

Stand alone clients can connect directly to playtak.com or by running locally on localhost via a websocket on `portws` for encrypted communication or telnet on `port` which will not be encrypted.
<br>
You can find the playtak UI client github here [playtak-ui](https://github.com/USTakAssociation/playtak-ui)

**You can telnet on port `10000` to test the commands.**

Typical communication is like below
* Connect to server via a websocket. Server gives welcome message
* Server sends "Login or Register"
* Client replies with login information or registers (If Client registers, password is sent to the mail and it can login with the password)
* Server accepts name or asks for another name if the one provided is invalid or already taken
* Server sends list of seeks with "Seek new" messages and games in progress with "GameList Add" messages
* Client posts seek or accepts existing seek
* If seek is accepted, server removes existing seeks for both the players (sends "Seek remove" for all) and starts game
* Client sends moves, server validates moves and sends the move to other client. If invalid, server sends a "NOK" message.
* Game progresses and ends either in a win/lose/draw or abandonment.


### Client to Server Communication
The input/output of server is all text.

The client to server commands and their format is as below
(format of all squares is [Capital letter][digit]. e.g., A2, B5, C4, (row numbers start from 1)

Komi is given in half flats as an integer from 0 to 8, denoting komis from +0.0 to +4.0

Currently two protocol versions are supported, 0 and 1. The only difference being that version 1 sends time in milliseconds instead of seconds.

| Commands to server                                                                                                                                             | Description                                                                                                                                                                                                                                                                                                                                                                 |
|----------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Client **client name**                                                                                                                                         | Informs the server of the client being connected from                                                                                                                                                                                                                                                                                                                       |
| Protocol **version**                                                                                                                                           | Changes protocol version, do not send to stay on version 0, may only be sent before logging in                                                                                                                                                                                                                                                                              |
| Register **username email**                                                                                                                                    | Register with the given username and email                                                                                                                                                                                                                                                                                                                                  |
| Login **username password**                                                                                                                                    | Login with the username and password                                                                                                                                                                                                                                                                                                                                        |
| Login Guest                                                                                                                                                    | Login as a guest                                                                                                                                                                                                                                                                                                                                                            |
| Login Guest **token**                                                                                                                                          | Login as a guest and set a token for rejoining, use the same token within 4 hours of last activity to continue using the same guest account. The token must be a cryptographically random composition of 20 lower-case letters                                                                                                                                              |
| Seek **no** **time** **incr** **W/B**                                                                                                                          | V1 Seeks a game of board size **no** with time per player **time** specified in seconds, increment per move **incr** specified in seconds and an optional choice of color **W** for white, **B** for black                                                                                                                                                                  |
| Seek **no** **time** **incr** **W/B/A** **komi** **pieces** **capstones** **unrated** **tournament** **opponent**                                              | V2 Seeks a game of board size **no** with time per player **time** specified in seconds, increment per move **incr** specified in seconds, **opponent** is the name of the opponent allowed to join, blank to allow anyone to join                                                                                                                                          |
| Seek **no** **time** **incr** **W/B/A** **komi** **pieces** **capstones** **unrated** **tournament** **extra_time_amount** **extra_time_trigger** **opponent** | V3 Seeks a game of board size **no** with time per player **time** specified in seconds, increment per move **incr** specified in seconds, **extra_time_amount** extra time to add when move trigger is hit, **extra_time_trigger** Move number on when to add extra time to clock, **opponent** is the name of the opponent allowed to join, blank to allow anyone to join |
| Accept **no**                                                                                                                                                  | Accepts the seek with the number **no**                                                                                                                                                                                                                                                                                                                                     |
| Game#**no** P **Sq** C\                                                                                                                                        | W                                                                                                                                                                                                                                                                                                                                                                           |Sends a 'Place' move to the specified game no. The optional suffix 'C' or 'W' denote if it is a capstone or a wall (standing stone)|
| Game#**no** M **Sq1** **Sq2** **no1** **no2**...                                                                                                               | Sends a 'Move' move to the specified game no. **Sq1** is beginning square, **Sq2** is ending square, **no1**, **no2**, **no3**.. are the no. of pieces dropped in the in-between squares (including the last square)                                                                                                                                                        |
| Game#**no** OfferDraw                                                                                                                                          | Offers the opponent draw or accepts the opponent's draw offer                                                                                                                                                                                                                                                                                                               |
| Game#**no** RemoveDraw                                                                                                                                         | Removes your draw offer                                                                                                                                                                                                                                                                                                                                                     |
| Game#**no** Resign                                                                                                                                             | Resign the game                                                                                                                                                                                                                                                                                                                                                             |
| Game#**no** Show                                                                                                                                               | Prints a somewhat human readable game position of the game number **no**                                                                                                                                                                                                                                                                                                    |
| Game#**no** RequestUndo                                                                                                                                        | Requests the other player to undo the last move or accept the other player's undo request                                                                                                                                                                                                                                                                                   |
| Game#**no** RemoveUndo                                                                                                                                         | Removes your undo request                                                                                                                                                                                                                                                                                                                                                   |
| List                                                                                                                                                           | Send list of seeks                                                                                                                                                                                                                                                                                                                                                          |
| GameList                                                                                                                                                       | Send list of games in progress                                                                                                                                                                                                                                                                                                                                              |
| Observe **no**                                                                                                                                                 | Observe the specified game. Server sends the game moves and clock info                                                                                                                                                                                                                                                                                                      |
| Unobserve **no**                                                                                                                                               | Unobserve the specified game                                                                                                                                                                                                                                                                                                                                                |
| Game#**no** Show **Sq**                                                                                                                                        | Prints the position in the specified square (this is used mainly to convert server notation to PTN notation)                                                                                                                                                                                                                                                                |
| Shout **text**                                                                                                                                                 | Send message **text** to all logged in players                                                                                                                                                                                                                                                                                                                              |
| JoinRoom **room**                                                                                                                                              | Join the room **room**                                                                                                                                                                                                                                                                                                                                                      |
| ShoutRoom **room** **text**                                                                                                                                    | Send test to players in room **room**                                                                                                                                                                                                                                                                                                                                       |
| LeaveRoom **room**                                                                                                                                             | Leave the room **room**                                                                                                                                                                                                                                                                                                                                                     |
| Tell **player** **text**                                                                                                                                       | Send private message **text** to **player**                                                                                                                                                                                                                                                                                                                                 |
| PING                                                                                                                                                           | Pings to inform server that the client is alive. Recommended ping spacing is 30 seconds. Server may disconnect clients if pings are not received                                                                                                                                                                                                                            |
| quit                                                                                                                                                           | Sent by client to indicate it is going to quit. Server removes all seeks, abandons (which loses) game if any                                                                                                                                                                                                                                                                |

The *Client*, *Login* and *Register* are the only three commands which work while not logged in.

### Server to Client Communication

The server to client messages and their format is as below.
The list does not include error messages, you're free to poke around and figure out the error messages on your own or look at the code.

|Messages from server|Description|
|--------------------|-----------|
|Welcome! |Just a welcome message when connected to server|
|Login or Register |Login with username/password or login as guest or register after this message|
|Welcome **name**! |A welcome message indicating that you've logged in as **name**|
|GameList Add **no** **player_white** **player_black** **size** **original_time** **incr** **komi** **pieces** **capstones** **unrated** **tournament** |Notifies client that a game has started (which the client can observe if it wants)|
|GameList Remove **no** **player_white** **player_black** **size** **original_time** **incr** **komi** **pieces** **capstones** **unrated** **tournament** |Notifies client that the game has ended|
|Game Start **no** **size** **player_white** vs **player_black** **your color** **time** **komi** **pieces** **capstones** |Notifies client to start a game. The game no. being **no**, players' names being **white_player**, **black_player** and **your_color** being your color which could be either "white" or "black"|
|Game#**no** P **Sq** C\|W|The 'Place' move played by the other player in game number **no**. The format is same as the command from client to server|
|Game#**no** M **Sq1** **Sq2** **no1** **no2**...|The 'Move' move played by the other player in game number **no**. The format is same as the command from client to server|
|Game#**no** Time **whitetime** **blacktime** |Update the clock with the time specified for white and black players, time given in seconds|
|Game#**no** Timems **whitetime** **blacktime** |Update the clock with the time specified for white and black players, time given in milliseconds, only sent if the client has opted in to protocol version 1 or later|
|Game#**no** Over **result**|Game number **no** is over. **result** is one of *R-0*, *0-R*, *F-0*, *0-F*, *1/2-1/2*|
|Game#**no** OfferDraw |Indicates the opponent has offered a draw|
|Game#**no** RemoveDraw |Indicates your opponent has taken back his offer to draw|
|Game#**no** RequestUndo |Request from opponent to undo the last move|
|Game#**no** RemoveUndo |Opponent removes his undo request|
|Game#**no** Undo |Undo the last move. Client is supposed to keep track of previous board states and undo to the last state.|
|Game#**no** Abandoned. **player** quit|Game number **no** is abandoned by **player** as he quit. Clients can treat this as resign.|
|Seek new **no** **name** **boardsize** **time** **increment** **W/B/A** **komi** **pieces** **capstones** **unrated** **tournament** **opponent** |There is a new seek with seek no. **no** posted by **name** with board size **boardsize** with **time** seconds for each player. W, B or A denotes the color of the seeker, **opponent** is the name of the player allowed to join, blank to let anyone join |
|Seek remove **no** **name** **boardsize** **time** **increment** **W/B/A** **komi** **pieces** **capstones** **unrated** **tournament** **opponent** |Existing seek no. **no** is removed (either the client has joined another game or has changed his seek or has quit)|
|Observe **no** **player_white** **player_black** **size** **original_time** **incr** **komi** **pieces** **capstones** **unrated** **tournament** | Start observing the game number **no** of board size **size** with original time setting of **origin_time** seconds|
|Shout \<**player**\> **text** |Chat message **text** from **player**|
|Joined room **room** |Indicates you've joined the room **room**|
|Left room **room** |Indicates you've left the room **room**|
|ShoutRoom **room** \<**player**\> **text** |Message **text** from **player** to chat room **room**|
|Tell \<**player**\> **text** |Private chat message **text** from **player**|
|Told \<**player**\> **text** |Confirmation that your message is sent to **player**. You'll receive this even if **player** is not logged in|
|Message **text** |A message from server. Might be used to indicate announcements like name accepted/server going down, etc|
|Error **text** |An error message|
|Online **no** |**no** players are connected to server|
|NOK |Indicates the command client send is invalid or unrecognized|
|OK  |Indicates previous command is ok. Clients can ignore this. *I might remove this message altogether in future as it serves no real purpose*|
