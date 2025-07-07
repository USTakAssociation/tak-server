package tak;

public abstract class AdminCommand extends SudoCommand {
	public AdminCommand(String regex, Client client) {
		super(regex, client);
	}

	@Override
	public boolean validate(String command) {
		if(!client.player.isAdmin()) {
			client.sendSudoReply("command not found");
			return false;
		}
		
		return super.validate(command);
	}
}
