package tak;

public abstract class SudoCommand extends LoggedInCommand {
	public SudoCommand(String regex, Client client) {
		super(regex, client);
	}

	@Override
	public void execute(String message) {
		client.sendSudoReply("> "+message);
		super.execute(message);
	}

	@Override
	public boolean validate(String command) {
		if(!client.player.isMod() && !client.player.isAdmin()) {
			return false;
		}

		return super.validate(command);
	}
}

