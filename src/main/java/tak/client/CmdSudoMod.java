package tak;

import java.util.regex.Matcher;

class CmdSudoMod extends AdminCommand {
	public CmdSudoMod(Client client) {
		super("sudo mod ([a-zA-Z][a-zA-Z0-9_]{3,15})", client);
	}

	public void executeImpl(String msg) {
		Matcher m = regexPattern.matcher(msg);
		m.find();

		String name = m.group(1);
		System.out.println("here "+name+" "+msg);
		Player p = Player.players.get(name);
		if(p == null) {
			client.sendSudoReply("No such player");
			return;
		}
		p.setMod();
		p.setModInDB(name, 1);
		client.sendSudoReply("Added "+p.getName()+" as moderator");
	}
}
