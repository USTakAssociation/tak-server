package tak;

public abstract class InGameCommand extends LoggedInCommand {
	public InGameCommand(String regex, Client client) {
		super(regex, client);
	}

	@Override
	public boolean validate(String command) {
		boolean superOk = super.validate(command);
		if (superOk && client.player.getGame() == null) {
			return false;
		}
		return superOk;
	}
}
