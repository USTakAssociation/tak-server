package tak;

import java.util.regex.Matcher;

class CmdClient extends ClientCommand {
	public CmdClient(Client client) {
		super("^Client ([A-Za-z-.0-9]{1,60})", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		String clientversion=m.group(1);
		client.Log("Client !"+clientversion+"!");
		if(clientversion.equals("TreffnonX-08.09.16") || clientversion.equals("TakWeb-16.05.26")){
			client.sendWithoutLogging("Shout <Server> Your Playtak client is unfortunately no longer compatible. Please go to https://www.playtak.com in order to play.");
		}
		else{
			client.sendWithoutLogging("OK");
		}
	}
}
