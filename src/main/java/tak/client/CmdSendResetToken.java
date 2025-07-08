package tak;

import java.util.regex.Matcher;

class CmdSendResetToken extends ClientCommand {
	public CmdSendResetToken(Client client) {
		super("^SendResetToken ([a-zA-Z][a-zA-Z0-9_]{3,15}) ([A-Za-z.0-9_+!#$%&'*^?=-]{1,30}@[A-Za-z.0-9-]{3,30})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		String tname = m.group(1).trim();
		String email = m.group(2).trim();
		if(Player.players.containsKey(tname)) {
			Player tplayer = Player.players.get(tname);
			if(email.equals(tplayer.getEmail())) {
				tplayer.sendResetToken();
				client.send("Reset token sent");
			} else {
				client.send("Reset Token Error: Email address does not match");
			}
		} else {
			client.send("Reset Token Error: No such player");
		}
	}
}
