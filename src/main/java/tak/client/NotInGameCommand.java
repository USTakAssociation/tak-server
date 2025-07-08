package tak;

public abstract class NotInGameCommand extends LoggedInCommand {
	public NotInGameCommand(String regex, Client client) {
		super(regex, client);
	}

	@Override
	public boolean validate(String command) {
		boolean superOk = super.validate(command);
		if (superOk && (client.player.getGame() != null)) {
			return false;
		}
		return superOk;
	}
}
