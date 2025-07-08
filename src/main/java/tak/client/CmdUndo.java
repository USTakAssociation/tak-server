package tak;

import java.util.regex.Matcher;

class CmdUndo extends InGameCommand {
	public CmdUndo(Client client) {
		super("^Game#(\\d+) RequestUndo", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		if (game.no != Integer.parseInt(m.group(1))) {
			client.sendNOK();
			return;
		}

		game.undo(client.player);
		client.sendWithoutLogging("OK");
	}
}



