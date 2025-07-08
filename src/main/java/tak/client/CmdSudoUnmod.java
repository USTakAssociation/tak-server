package tak;

import java.util.regex.Matcher;

class CmdSudoUnmod extends AdminCommand {
	public CmdSudoUnmod(Client client) {
		super("sudo unmod ([a-zA-Z][a-zA-Z0-9_]{3,15})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		String name = m.group(1);
		Player p = Player.players.get(name);
		if(p == null) {
			client.sendSudoReply("No such player");
			return;
		}
		p.unMod();
		p.setModInDB(name, 0);
		client.sendSudoReply("Removed "+p.getName()+" as moderator");

	}
}

