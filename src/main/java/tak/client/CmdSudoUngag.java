package tak;

import java.util.regex.Matcher;

class CmdSudoUngag extends SudoCommand {

	public CmdSudoUngag(Client client) {
		super("sudo ungag ([a-zA-Z][a-zA-Z0-9_]{3,15})", client);
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

		if(!p.isGagged()) {
			client.sendSudoReply("Player is not gagged");
			return;
		}

		p.unGag();
		p.setGagInDB(p.getName(), 0);
		client.sendSudoReply(p.getName()+" ungagged");
	}
}

