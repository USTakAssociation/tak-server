package tak;

import java.util.regex.Matcher;

import tak.utils.BadWordFilter;

class CmdShout extends LoggedInCommand {
	public CmdShout(Client client) {
		super("^Shout ([^\n\r]{1,256})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		String msg = "<"+client.player.getName()+"> "+BadWordFilter.filterText(m.group(1));

		if(!client.player.isGagged()) {
			client.sendAllOnline("Shout "+msg);
			IRCBridge.send(msg);
		} else//send to only gagged player
			client.sendWithoutLogging("Shout <"+client.player.getName()+"> <Server: You have been muted for inappropriate chat behavior.>");
	}
}



