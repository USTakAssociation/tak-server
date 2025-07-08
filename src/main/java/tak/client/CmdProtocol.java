package tak;

import java.util.regex.Matcher;

class CmdProtocol extends ClientCommand {
	public CmdProtocol(Client client) {
		super("^Protocol ([1-9][0-9]{0,8})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		client.protocolVersion=Integer.parseInt(m.group(1));

		client.sendWithoutLogging("OK");
	}
}

