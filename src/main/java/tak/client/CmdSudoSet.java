package tak;

import java.util.regex.Matcher;

class CmdSudoSet extends AdminCommand {
	public CmdSudoSet(Client client) {
		super("sudo set ([a-zA-Z]{3,15}) ([a-zA-Z][a-zA-Z0-9_]{3,15}) ([^\n\r\\s]{6,100})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		String param = m.group(1);
		String name = m.group(2);
		String value = m.group(3);

		Player p = Player.players.get(name);
		if(p == null) {
			client.sendSudoReply("No such player");
			return;
		}
		if(param.equals("password")) {
			p.setPassword(value);
			client.sendSudoReply("Password set");
		}

	}
}


