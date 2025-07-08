package tak;

import java.util.regex.Matcher;

import tak.utils.BadWordFilter;

class CmdTell extends LoggedInCommand {
	public CmdTell(Client client) {
		super("^Tell ([a-zA-Z][a-zA-Z0-9_]{3,15}) ([^\n\r]{1,256})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		if(Player.players.containsKey(m.group(1))) {
			Player tplayer = Player.players.get(m.group(1));
			if(!client.player.isGagged()){
				tplayer.send("Tell "+"<"+client.player.getName()+"> "+ BadWordFilter.filterText(m.group(2)));
				client.send("Told "+"<"+tplayer.getName()+"> " + BadWordFilter.filterText(m.group(2)));
			} else {
				client.send("Told "+"<"+tplayer.getName()+"> <Server: You have been muted for inappropriate chat behavior.>");
			}
		} else {
			client.send("No such player");
		}

	}
}


