package tak;

import java.util.regex.Matcher;

class CmdResign extends InGameCommand {
	public CmdResign(Client client) {
		super("^Game#(\\d+) Resign", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		if (game.no != Integer.parseInt(m.group(1))) {
			client.sendNOK();
			return;
		}

		game.gameLock.lock();
		try{
			game.resign(client.player);
			Player otherPlayer = (game.white==client.player)?game.black:game.white;

			Game.removeGame(game);
			client.player.removeGame();
			otherPlayer.removeGame();
		}
		finally{
			game.gameLock.unlock();
		}

	}
}
