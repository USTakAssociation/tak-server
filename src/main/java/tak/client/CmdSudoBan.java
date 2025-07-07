package tak;

import java.util.regex.Matcher;

class CmdSudoBan extends SudoCommand {

	public CmdSudoBan(Client client) {
		super("sudo ban ([a-zA-Z][a-zA-Z0-9_]{3,15}) ([^\n\n]{1,256})", client);
	}

	public void executeImpl(String message) {
		Matcher m = regexPattern.matcher(message);
		m.find();

		String name = m.group(1);
		String reason = m.group(2);
		Player p = Player.players.get(name);
		if(p == null) {
			client.sendSudoReply("No such player");
			return;
		}

		if(!client.moreRights(p)) {
			client.sendSudoReply("You don't have rights");
			return;
		}
		// check if player is already banned
		if(p.isBanned()) {
			client.sendSudoReply("Player is already banned");
			return;
		}

		p.setBan();
		p.setBanInDB(p.getName(), 1);
		// email player that they have been banned
		EMail.send(p.getEmail(), "PlayTak.com Ban Notice", p.getName() + ",\rYou have been banned from playtak.com!\rReason: " + reason);
		// kick player as well
		Client c = p.getClient();
		if(c == null) {
			client.sendSudoReply("Player not logged in");
			return;
		}
		c.disconnect();
		client.sendSudoReply(p.getName()+" kicked");
		client.sendSudoReply(p.getName()+" banned");

	}
}

