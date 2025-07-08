package tak;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tak.utils.BadWordFilter;

class CmdSudoReload extends AdminCommand {
	Pattern wordPattern;

	public CmdSudoReload(Client client) {
		super("^sudo reload", client);

		wordPattern = Pattern.compile("sudo reload wordconfig");
	}

	public void executeImpl(String command) {
		Matcher wpm = wordPattern.matcher(command);
		if (wpm.find()) {
			BadWordFilter.loadConfigs();
		} else {
			client.sendSudoReply("command not found");
		}
	}
}

