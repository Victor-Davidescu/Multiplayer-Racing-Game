# Multiplayer-Racing-Game

Requires:
- IDE: IntelliJ IDEA 2021.1
- Java SE Development Kit 15.0.2 (64-bit)


- Arguments required:
		> For runnning as server (4 arguments):
			- Argument 0: Running mode
			- Argument 1: Server's port
			- Argument 2: Number of clients that can connect to server, range is [2-4].
			- Argument 3: Number of laps for the race, range is [1-4].
			
		> For running as client (3 arguments):
			- Argument 0: Running mode
			- Argument 1: Server's IP address.
			- Argument 2: Server's port.
	
	- Running using IntelliJ:
		Open project using RacingGame_Part3.iml to load the project with its configurations.
		There are 7 run configurations:
			1) Server (2 clients), arguments: server 5000 2 1
			2) Server (3 clients), arguments: server 5000 3 1
			3) Server (4 clients), arguments: server 5000 4 1
			4) Client-1, arguments: client localhost 5000
			5) Client-2, arguments: client localhost 5000
			6) Client-3, arguments: client localhost 5000
			7) Client-4, arguments: client localhost 5000
			
			! RUN ONLY ONE SERVER !
			
	- Running without IntelliJ:
		Open a Command Prompt at the project's current location.
		
		Example of run command for server: 
		"java -classpath "out\production\RacingGame_Part3" Main server 5000 2 1"
		
		Example of run command for client:
		"java -classpath "out\production\RacingGame_Part3" Main client localhost 5000"
		
	- Kart keybinds:
		> Client's kart: arrow keys

	- How program ends?
		Drive any kart, go through all checkpoints (including finish line) that are matching the kart's colour.
		After a kart passes a finish line checkpoint, the program ends. To play again, restart the program.
