package tak;

import java.util.regex.Matcher;

class CmdChangePassword extends LoggedInCommand {
	public CmdChangePassword(Client client) {
		super("^ChangePassword ([^\n\r\\s]{6,50}) ([^\n\r\\s]{6,50})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		String curPass = m.group(1);
		String newPass = m.group(2);

		if(client.player.authenticate(curPass)) {
			client.player.setPassword(newPass);
			client.send("Password changed");
		} else {
			client.send("Wrong password");
		}
		
	}
}

