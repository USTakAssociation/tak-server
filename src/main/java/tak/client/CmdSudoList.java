package tak;

import java.util.regex.Matcher;

class CmdSudoList extends AdminCommand {
	public CmdSudoList(Client client) {
		super("sudo list ([a-zA-Z]{3,15})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		// return gag list
		if("gag".equals(m.group(1))) {
			String res="[";
			for(Player p: Player.gagList)
				res += p.getName()+", ";

			client.sendSudoReply(res+"]");
		}
		// return mod list
		else if ("mod".equals(m.group(1))) {
			String res = "[";
			for(Player p: Player.modList)
				res += p.getName()+", ";

			client.sendSudoReply(res+"]");
		}
		// return mod list
		else if ("ban".equals(m.group(1))) {
			String res = "[";
			for(Player p: Player.banList)
				res += p.getName()+", ";

			client.sendSudoReply(res+"]");
		}
		else if("online".equals(m.group(1))) {
				String res = "[";
				for(Client c: client.clientConnections) {
					if(c.player != null)
						res += c.player.getName()+", ";
				}
				client.sendSudoReply(res+"]");
		} else {
			client.sendSudoReply("command not found");
			// privileged commands - only for admins
		}
	}
}
