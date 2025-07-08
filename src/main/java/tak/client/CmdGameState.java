package tak;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CmdGameState extends InGameCommand {
	Pattern fullGameStatePattern;
	Pattern squareStatePattern;

	public CmdGameState(Client client) {
		super("^Game#(\\d+) Show", client);

		fullGameStatePattern = Pattern.compile("^Game#(\\d+) Show$");
		squareStatePattern = Pattern.compile("^Game#(\\d+) Show ([A-Z])(\\d)");
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		if (game.no != Integer.parseInt(m.group(1))) {
			client.sendNOK();
			return;
		}

		Matcher fgsm = fullGameStatePattern.matcher(command);
		Matcher ssm = squareStatePattern.matcher(command);

		if (fgsm.find()) {
			client.send(game.toString());
		} else if (ssm.find()) {
			squareState(ssm);
		} else {
			client.sendNOK();
		}
	}

	public void squareState(Matcher m) {
		client.send("Game#"+game.no+" Show Sq "+game.sqState(m.group(2).charAt(0), Integer.parseInt(m.group(3))));
	}
}
