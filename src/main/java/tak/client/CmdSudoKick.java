package tak;

import java.util.regex.Matcher;

class CmdSudoKick extends SudoCommand {

	public CmdSudoKick(Client client) {
		super("sudo kick ([a-zA-Z][a-zA-Z0-9_]{3,15})", client);
	}

	public void executeImpl(String message) {
		Matcher m = regexPattern.matcher(message);
		m.find();

		String name = m.group(1);
		Player p = Player.players.get(name);
		if(p == null) {
			client.sendSudoReply("No such player");
			return;
		}

		if(!client.moreRights(p)) {
			client.sendSudoReply("You dont have rights");
			return;
		}

		Client c = p.getClient();
		if(c == null) {
			client.sendSudoReply("Player not logged in");
			return;
		}

		c.disconnect();
		client.sendSudoReply(p.getName()+" kicked");


	}
}
