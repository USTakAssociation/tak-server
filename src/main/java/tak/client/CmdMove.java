package tak;

import java.util.regex.Matcher;

class CmdMove extends InGameCommand {
	public CmdMove(Client client) {
		super("^Game#(\\d+) M ([A-Z])(\\d) ([A-Z])(\\d)(( \\d)+)", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		if (game.no != Integer.parseInt(m.group(1))) {
			client.sendNOK();
			return;
		}

		String args[] = m.group(6).split(" ");
		int argsint[] = new int[args.length-1];
		for(int i=1;i<args.length;i++)
			argsint[i-1] = Integer.parseInt(args[i]);
		game.gameLock.lock();
		try{
			Status st = game.moveMove(client.player, m.group(2).charAt(0), Integer.parseInt(m.group(3)), m.group(4).charAt(0), Integer.parseInt(m.group(5)), argsint);
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


