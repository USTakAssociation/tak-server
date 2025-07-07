package tak;

import tak.utils.BadWordFilter;

import java.util.regex.Matcher;

class CmdRegistration extends ClientCommand {
	public CmdRegistration(Client client) {
		super("^Register ([a-zA-Z][a-zA-Z0-9_]{3,15}) ([A-Za-z.0-9_+!#$%&'*^?=-]{1,30}@[A-Za-z.0-9-]{3,30})", client);
	}

	public boolean validate(String command) {
		boolean ok = super.validate(command);
		if (!ok)
			client.send("Registration Error: Unknown format for username/email. Only [a-z][A-Z][0-9][_] allowed for username, it should be 4-16 characters and should start with letter");
		return ok;
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		String tname = m.group(1).trim();

		if(tname.toLowerCase().contains("guest")) {
			client.send("Registration Error: Can't register with guest in the name");
		}
		else if(BadWordFilter.containsBadWord(tname)){
			client.send("Registration Error: Username cannot contain profanity");
		}
		else {
			synchronized(Player.players) {
				if (Player.isNameTaken(tname)) {
					client.send("Registration Error: Username is already taken");
				}
				else {
					String email = m.group(2).trim();
					Player tplayer = Player.createPlayer(tname, email);
					client.send("Registered "+tplayer.getName()+". Check your email for password");
				}
			}
		}
		if(client.player!=null){
			client.player.lastActivity=System.nanoTime();
		}
		client.sendWithoutLogging("OK");
	}
}
