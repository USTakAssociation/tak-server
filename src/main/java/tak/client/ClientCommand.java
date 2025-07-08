package tak;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class ClientCommand {
	Pattern regexPattern;
	Client client;

	abstract void executeImpl(String command);

	public void execute(String command) {
		executeImpl(command);
	}

	public ClientCommand(String regex, Client client) {
		this.regexPattern = Pattern.compile(regex);
		this.client = client;
	}

	public boolean validate(String command) {
		return regexPattern.matcher(command).find();
	}
}
