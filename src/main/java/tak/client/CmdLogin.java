package tak;

import java.util.regex.Matcher;
import java.util.logging.Level;
import java.util.logging.Logger;


class CmdLogin extends ClientCommand {
	public CmdLogin(Client client) {
		super("^Login ([a-zA-Z][a-zA-Z0-9_]{3,15}) ([^\n\r\\s]{6,50})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		Player.loginLock.lock();
		try{
			String tname = m.group(1).trim();
			synchronized(Player.players) {
			if (Player.players.containsKey(tname)) {
				Player tplayer = Player.players.get(tname);
				String pass = m.group(2).trim();

				if(!tplayer.authenticate(pass)) {
					client.send("Authentication failure");
				} else {
					if(tplayer.isLoggedIn()) {
						Client oldClient = tplayer.getClient();
						tplayer.send("Message You've logged in from another window. Disconnecting");
						tplayer.logout();
						//Wait for other connection to close before logging in
						try {
							oldClient.join(1000);
						} catch (InterruptedException ex) {
							Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
						}
					}

					client.player = tplayer;

					client.send("Welcome "+client.player.getName()+"!");
					if(client.player.isAdmin() || client.player.isMod()){
						client.send("Is Mod");
					}
					client.player.login(client);

					Seek.registerListener(client);
					Game.registerGameListListener(client.player);

					client.sendAllOnline("Online "+client.onlineClients.incrementAndGet());
				}
			} else
				client.send("Authentication failure");
			}
		}
		finally{
			Player.loginLock.unlock();
		}
	}
}

