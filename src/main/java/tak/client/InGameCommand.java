package tak;

public abstract class InGameCommand extends LoggedInCommand {
	public InGameCommand(String regex, Client client) {
		super(regex, client);
	}

	@Override
	public boolean validate(String command) {
		if (client.player.getGame() == null) {
			return false;
		}
		return super.validate(command);
	}
}
