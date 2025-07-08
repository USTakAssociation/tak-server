package tak;

import java.util.regex.Matcher;

import tak.FlowMessages.GameUpdate;

class CmdAcceptSeek extends NotInGameCommand {
	public CmdAcceptSeek(Client client) {
		super("^Accept (\\d+)", client);
	}

	public void executeImpl(String command) {
		Matcher m = regexPattern.matcher(command);
		m.find();

		Seek.seekStuffLock.lock();
		try{
			Seek sk = Seek.seeks.get(Integer.parseInt(m.group(1)));
			if (sk != null && game == null && sk.client.player.getGame() == null && sk!=client.seek && (sk.opponent.toLowerCase().equals(client.player.getName().toLowerCase()) || sk.opponent.equals(""))) {
				Client otherClient = sk.client;
				int sz = sk.boardSize;
				int time = sk.time;
		
				client.removeSeeks();
				otherClient.removeSeeks();
				client.unspectateAll();
				otherClient.unspectateAll();
		
				game = new Game(client.player, otherClient.player, sz, time, sk.incr, sk.color, sk.komi, sk.pieces, sk.capstones, sk.unrated, sk.tournament, sk.triggerMove, sk.timeAmount, sk.pntId);
				client.notifySubscribers(GameUpdate.gameCreated(game.toDto()));
				for(var subscriber: client.subscribers) {
					game.subscribe(subscriber);
				}
		
				game.gameLock.lock();
				try{
					Game.addGame(game);
		
					client.player.setGame(game);
					otherClient.player.setGame(game);
		
					String msg = "Game Start " + game.no +" "+sz+" "+game.white.getName()+" vs "+game.black.getName();
					String msg2 = time + " " + sk.komi + " " + sk.pieces + " " + sk.capstones + " " + sk.triggerMove + " " + sk.timeAmount;
					client.send(msg+" "+((game.white==client.player)?"white":"black")+" "+msg2);
					otherClient.send(msg+" "+((game.white==otherClient.player)?"white":"black")+" "+msg2);
				}
				finally{
					game.gameLock.unlock();
				}
			} else {
				client.sendNOK();
			}
		}
		finally{
			Seek.seekStuffLock.unlock();
		}
	}
}


