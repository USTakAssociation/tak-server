package tak;

import java.util.regex.Matcher;

class CmdResetPassword extends ClientCommand {
	public CmdResetPassword(Client client) {
		super("^ResetPassword ([a-zA-Z][a-zA-Z0-9_]{3,15}) ([^\n\r\\s]{6,50}) ([^\n\r\\s]{6,50})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		String tname = m.group(1);
		String token = m.group(2);
		String pass = m.group(3);
		if(Player.players.containsKey(tname)) {
			Player tplayer = Player.players.get(tname);
			if(tplayer.resetPassword(token, pass)) {
				client.send("Password is changed");
			} else {
				client.send("Wrong token");
			}
		} else {
			client.send("No such player");
		}
	}
}
