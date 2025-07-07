package tak;

import java.util.regex.Matcher;

class CmdJoinRoom extends LoggedInCommand {
	public CmdJoinRoom(Client client) {
		super("^JoinRoom ([^\n\r\\s]{1,64})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();
		client.addToRoom(m.group(1));
	}
}

