package tak;

import java.util.regex.Matcher;

class CmdSudoBroadcast extends AdminCommand {
	public CmdSudoBroadcast(Client client) {
		super("sudo broadcast ([^\n\r]{1,256})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		String bmsg = m.group(1);
		Client.sendAllOnline(bmsg);
	}
}
