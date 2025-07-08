package tak;

import java.util.regex.Matcher;

class CmdLeaveRoom extends LoggedInCommand {
	public CmdLeaveRoom(Client client) {
		super("^LeaveRoom ([^\n\r\\s]{1,64})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();
		client.removeFromRoom(m.group(1));
		client.sendWithoutLogging("OK");
	}
}


