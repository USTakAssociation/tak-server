package tak;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class LoggedInCommand extends ClientCommand {
	Game game;

	public LoggedInCommand(String regex, Client client) {
		super(regex, client);
	}

	@Override
	public void execute(String command) {
		this.game = client.player.getGame();
		executeImpl(command);
	}

	@Override
	public boolean validate(String command) {
		if (client.player == null) {
			return false;
		}
		return super.validate(command);
	}
}
