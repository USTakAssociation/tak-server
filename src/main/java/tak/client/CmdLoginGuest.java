package tak;

import java.util.regex.Matcher;
import java.util.logging.Level;
import java.util.logging.Logger;

class CmdLoginGuest extends ClientCommand {
	public CmdLoginGuest(Client client) {
		super("^Login Guest ?(([a-z]{20})?)", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();
		
		Player.loginLock.lock();
		try{
			Player.cleanUpGuests();
			String token=m.group(1);
			if(token!=""){
				client.player=Player.guestsByToken.get(token);
				if(client.player==null){
					client.player = new Player(token);
				}
				else if(client.player.isLoggedIn()){
					Client oldClient = client.player.getClient();
					client.player.send("Message You've logged in from another window. Disconnecting");
					client.player.logout();
					try {
						oldClient.join(1000);
					} catch (InterruptedException ex) {
						Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}
			else{
				client.player = new Player();
			}
			client.player.login(client);
			client.player.lastActivity=System.nanoTime();

			client.send("Welcome "+client.player.getName()+"!");
			client.Log("Player logged in");

			Seek.registerListener(client);
			Game.registerGameListListener(client.player);

			client.sendAllOnline("Online "+client.onlineClients.incrementAndGet());
		}
		finally{
			Player.loginLock.unlock();
		}
	}
}

