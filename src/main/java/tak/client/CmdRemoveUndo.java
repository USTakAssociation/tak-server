package tak;

import java.util.regex.Matcher;

class CmdRemoveUndo extends InGameCommand {
	public CmdRemoveUndo(Client client) {
		super("^Game#(\\d+) RemoveUndo", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		if (game.no != Integer.parseInt(m.group(1))) {
			client.sendNOK();
			return;
		}

		game.removeUndo(client.player);
		client.sendWithoutLogging("OK");

	}
}




