package tak;

import java.util.regex.Matcher;

class CmdSudoGag extends SudoCommand {

	public CmdSudoGag(Client client) {
		super("sudo gag ([a-zA-Z][a-zA-Z0-9_]{3,15})", client);
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
			client.sendSudoReply("You don't have rights");
			return;
		}

		if(p.isGagged()) {
			client.sendSudoReply("Player is already gagged");
			return;
		}

		p.gag();
		p.setGagInDB(p.getName(), 1);
		client.sendSudoReply(p.getName()+" gagged");
	}
}
