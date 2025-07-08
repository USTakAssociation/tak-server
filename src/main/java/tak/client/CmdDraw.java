package tak;

import java.util.regex.Matcher;

class CmdDraw extends InGameCommand {
	public CmdDraw(Client client) {
		super("^Game#(\\d+) OfferDraw", client);
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
			game.draw(client.player);
			Player otherPlayer = (game.white==client.player)?game.black:game.white;

			if(game.gameState!=Game.gameS.NONE){
				Game.removeGame(game);
				client.player.removeGame();
				otherPlayer.removeGame();
			}
			client.sendWithoutLogging("OK");
		}
		finally{
			game.gameLock.unlock();
		}


	}
}
