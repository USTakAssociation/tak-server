package tak;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tak.FlowMessages.GameUpdate;
import tak.utils.BadWordFilter;
import tak.utils.ConcurrentHashSet;
import tak.*;


/**
 *
 * @author chaitu
 */
public class Client extends Thread implements Publisher<GameUpdate> {

	Websocket websocket;
	Player player = null;
	int clientNo;
	public int protocolVersion=0;

	static AtomicInteger totalClients = new AtomicInteger(0);
	static AtomicInteger onlineClients = new AtomicInteger(0);

	static ConcurrentHashSet<Client> clientConnections = new ConcurrentHashSet<>();
	protected ConcurrentHashSet<Subscriber<? super GameUpdate>> subscribers = new ConcurrentHashSet<>();

	Seek seek = null;
	Set<Game> spectating;
	Set<String> chatRooms;

	String commandString = "^(Game#(\\d+))? ?(Login Guest|sudo \\w+|\\w+)";
	Pattern commandPattern;

	String broadcastString = "sudo broadcast ([^\n\r]{1,256})";
	Pattern broadcastPattern;

	HashMap<String, ClientCommand> commands;

	Client(Websocket socket) {
		websocket = socket;
		this.clientNo = totalClients.incrementAndGet();

		commands = new HashMap<>();
		commands.put("Client", new CmdClient(this));
		commands.put("PING", new CmdPing(this));
		commands.put("Login", new CmdLogin(this));
		commands.put("Login Guest", new CmdLoginGuest(this));
		commands.put("Register", new CmdRegistration(this));
		commands.put("SendResetToken", new CmdSendResetToken(this));
		commands.put("ResetPassword", new CmdResetPassword(this));
		commands.put("ChangePassword", new CmdChangePassword(this));
		commands.put("Protocol", new CmdProtocol(this));
		commands.put("List", new CmdListAllSeeks(this));
		commands.put("Seek", new CmdSeek(this));
		commands.put("Accept", new CmdAcceptSeek(this));
		commands.put("P", new CmdPlace(this));
		commands.put("M", new CmdMove(this));
		commands.put("RequestUndo", new CmdUndo(this));
		commands.put("RemoveUndo", new CmdRemoveUndo(this));
		commands.put("OfferDraw", new CmdDraw(this));
		commands.put("RemoveDraw", new CmdRemoveDraw(this));
		commands.put("Resign", new CmdResign(this));
		commands.put("Show", new CmdGameState(this));
		commands.put("GameList", new CmdGameList(this));
		commands.put("Observe", new CmdObserve(this));
		commands.put("Unobserve", new CmdUnobserve(this));
		commands.put("Shout", new CmdShout(this));
		commands.put("ShoutRoom", new CmdShoutRoom(this));
		commands.put("JoinRoom", new CmdJoinRoom(this));
		commands.put("LeaveRoom", new CmdLeaveRoom(this));
		commands.put("Tell", new CmdTell(this));
		commands.put("sudo gag", new CmdSudoGag(this));
		commands.put("sudo ungag", new CmdSudoUngag(this));
		commands.put("sudo ban", new CmdSudoBan(this));
		commands.put("sudo unban", new CmdSudoUnban(this));
		commands.put("sudo kick", new CmdSudoKick(this));
		commands.put("sudo list", new CmdSudoList(this));
		commands.put("sudo reload", new CmdSudoReload(this));
		commands.put("sudo mod", new CmdSudoMod(this));
		commands.put("sudo unmod", new CmdSudoUnmod(this));
		commands.put("sudo set", new CmdSudoSet(this));
		commands.put("sudo broadcast", new CmdSudoBroadcast(this));

		commandPattern = Pattern.compile(commandString);

		clientConnections.add(this);
		spectating = new HashSet<>();
		chatRooms = new HashSet<>();
	}

	void sendNOK() {
		send("NOK");
	}

	void send(String st) {
		Log("Send:"+st);
		sendWithoutLogging(st);
	}

	void sendCmdReply(String st) {
		sendWithoutLogging("CmdReply "+st);
	}

	void sendSudoReply(String st) {
		sendWithoutLogging("sudoReply "+st);
	}

	void sendWithoutLogging(String st) {
		websocket.send(st);
	}

	void removeSeeks() {
		Seek.seekStuffLock.lock();
		try{
			if (seek != null) {
				Log("Removing seek " + seek.no + " from player " + this.player.getName());
				Seek.removeSeek(seek.no);
				seek = null;
			}
		}
		finally{
			Seek.seekStuffLock.unlock();
		}
	}

	void unspectateAll() {
		for(Game g: spectating)
			g.unSpectate(player);
		spectating.clear();
	}

	static void sendAll(final String msg) {
		for(Client c: clientConnections){
			c.sendWithoutLogging(msg);
		}
	}

	static void sendAllOnline(final String msg) {
		for(Client c: clientConnections){
			if(c.player!=null){
				c.sendWithoutLogging(msg);
			}
		}
	}

	void clientQuit() throws IOException {
		clientConnections.remove(this);

		if (player != null) {
			Game game = player.getGame();
			if(game!=null){
				game.playerDisconnected(player);
			}

			Seek.unregisterListener(this);
			Game.unregisterGameListListener(player);
			removeSeeks();
			removeFromAllRooms();
			unspectateAll();

			player.loggedOut();
			sendAllOnline("Online "+onlineClients.decrementAndGet());
		}

		websocket.kill(201);
		Log("disconnected");
	}

	void Log(Object obj) {
		TakServer.Log(clientNo+":"+((player!=null)?player.getName():"")+":"+obj);
	}

	void disconnect() {
		websocket.kill(202);
	}

    public void handleMessage(String message) {
		Log("handling message: " + message);
        Matcher m = commandPattern.matcher(message);
		m.find();

		String activeGame = m.group(2);
		String commandString = m.group(3);

		Log("Extracted command: " + commandString + " Active Game: " + activeGame);

		ClientCommand command = commands.get(commandString);
		if (command == null) {
			sendNOK();
			return;
		}

		if (!command.validate(message)) {
			sendNOK();
			return;
		}

		command.execute(message);
    }

	@Override
	public void run() {
		String temp=null;
		try {
			while(!websocket.headerended && !websocket.streamended){
				temp=websocket.recieve(true);
			}
			websocket.send("Welcome!");
			websocket.send("Login or Register");
			Log("Welcome sent");
			mainloop:
			while (!websocket.streamended) {
				while(temp==null){
					temp=websocket.recieve(true);
					if(websocket.streamended){
						break mainloop;
					}
				}
				temp = temp.replaceAll("[\\n\\r]+$","");

				if(temp.equals("quit")){
					break;
				}

				handleMessage(temp);

				temp=websocket.recieve(true);
			}
		} finally {
			try {
				clientQuit();
			} catch (IOException ex) {
				Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public void addToRoom(String room) {
		chatRooms.add(room);
		ChatRoom.joinRoom(room,this);
		send("Joined room "+room);
	}

	public void removeFromRoom(String room) {
		chatRooms.remove(room);
		ChatRoom.leaveRoom(room,this);
	}

	public void removeFromAllRooms() {
		for(String room : chatRooms){
			ChatRoom.leaveRoom(room,this);
		}
		chatRooms.clear();
	}

	//this has more rights than p
	boolean moreRights(Player p) {
		//if i am mod and other is not mod
		if(player.isMod() && !p.isMod() || player.isAdmin())
			return true;

		return false;
	}

	static void sigterm() {
		TakServer.Log("Sigterm!");
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("message")));
			String msg = br.readLine();
			sendAll("Message "+msg);

			int sleep=Integer.parseInt(br.readLine());
			TakServer.Log("sleeping "+sleep+" milliseconds");
			Thread.sleep(sleep);
			sendAll("Message "+br.readLine());

			TakServer.Log("Exiting");
		} catch (IOException | NumberFormatException | InterruptedException ex) {
			TakServer.Log(ex);
		}

	}

	@Override
	public void subscribe(Subscriber<? super GameUpdate> subscriber) {
		subscribers.add(subscriber);
	}
	protected void notifySubscribers(GameUpdate update) {
		for (var subscriber: subscribers) {
			subscriber.onNext(update);
		}
	}
}
