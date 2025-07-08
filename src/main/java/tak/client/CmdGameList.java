package tak;

class CmdGameList extends LoggedInCommand {
	public CmdGameList(Client client) {
		super("^GameList", client);
	}

	public void executeImpl(String command) {
		Game.sendGameListTo(client.player);		
	}
}


