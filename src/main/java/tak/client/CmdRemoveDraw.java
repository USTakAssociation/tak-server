package tak;

import java.util.regex.Matcher;

class CmdRemoveDraw extends InGameCommand {
	public CmdRemoveDraw(Client client) {
		super("^Game#(\\d+) RemoveDraw", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		if (game.no != Integer.parseInt(m.group(1))) {
			client.sendNOK();
			return;
		}

		game.removeDraw(client.player);
		client.sendWithoutLogging("OK");
	}
}




