package tak;

import java.util.regex.Matcher;

class CmdPing extends ClientCommand {
	public CmdPing(Client client) {
		super("^PING$", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();
		if(client.player!=null){
			client.player.lastActivity=System.nanoTime();
		}
		client.sendWithoutLogging("OK");
	}
}
