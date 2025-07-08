package tak;

import java.util.regex.Matcher;

class CmdSudoUnban extends SudoCommand {

	public CmdSudoUnban(Client client) {
		super("sudo unban ([a-zA-Z][a-zA-Z0-9_]{3,15})", client);
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
		if(!p.isBanned()){
			client.sendSudoReply("Player is not banned");
			return;
		}

		p.unBan();
		p.setBanInDB(p.getName(), 0);
		client.sendSudoReply(p.getName()+" unbanned");
	}
}
