package tak;

import java.util.regex.Matcher;

class CmdShoutRoom extends LoggedInCommand {
	public CmdShoutRoom(Client client) {
		super("^ShoutRoom ([^\n\r\\s]{1,64}) ([^\n\r]{1,256})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();
		ChatRoom.shout(m.group(1), client, m.group(2));
	}
}
