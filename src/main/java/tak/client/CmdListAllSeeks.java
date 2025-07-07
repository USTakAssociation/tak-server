package tak;

import java.util.regex.Matcher;

class CmdListAllSeeks extends LoggedInCommand {
	public CmdListAllSeeks(Client client) {
		super("^List", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		Seek.sendListTo(client);
	}
}
