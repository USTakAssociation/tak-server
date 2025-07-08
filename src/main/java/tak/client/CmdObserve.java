package tak;

import java.util.regex.Matcher;

class CmdObserve extends LoggedInCommand {
	public CmdObserve(Client client) {
		super("^Observe (\\d+)", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		game = Game.games.get(Integer.parseInt(m.group(1)));
		if(game!=null){
			game.gameLock.lock();
			try{
				client.spectating.add(game);
				game.newSpectator(client.player);
			}
			finally{
				game.gameLock.unlock();
			}
		} else
			client.sendNOK();
	}
}


