package tak;

import java.util.regex.Matcher;

class CmdUnobserve extends LoggedInCommand {
	public CmdUnobserve(Client client) {
		super("^Unobserve (\\d+)", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		game = Game.games.get(Integer.parseInt(m.group(1)));
		if(game!=null){
			game.gameLock.lock();
			try{
				client.spectating.remove(game);
				game.unSpectate(client.player);
				client.sendWithoutLogging("OK");
			}
			finally{
				game.gameLock.unlock();
			}
		} else
			client.sendNOK();
		
	}
}


