package tak;

import java.util.regex.Matcher;

class CmdPlace extends InGameCommand {
	public CmdPlace(Client client) {
		super("^Game#(\\d+) P ([A-Z])(\\d)( C)?( W)?", client);
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
			Status st = game.placeMove(client.player, m.group(2).charAt(0), Integer.parseInt(m.group(3)), m.group(4) != null, m.group(5)!=null);
			if(st.isOk()){

				if(game.gameState!=Game.gameS.NONE){
					Player otherPlayer = (game.white==client.player)?game.black:game.white;
					Game.removeGame(game);
					client.player.removeGame();
					otherPlayer.removeGame();
				}
				client.sendWithoutLogging("OK");
			} else {
				client.sendNOK();
				client.send("Error:"+st.msg());
			}
		}
		finally{
			game.gameLock.unlock();
		}

	}
}



