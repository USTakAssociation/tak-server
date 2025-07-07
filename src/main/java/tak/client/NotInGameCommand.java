package tak;

public abstract class NotInGameCommand extends LoggedInCommand {
	public NotInGameCommand(String regex, Client client) {
		super(regex, client);
	}

	@Override
	public boolean validate(String command) {
		if (client.player.getGame() != null) {
			return false;
		}
		return super.validate(command);
	}
}
