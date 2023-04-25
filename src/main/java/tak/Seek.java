/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tak;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import static tak.Game.DEFAULT_SIZE;
import tak.utils.ConcurrentHashSet;
import java.util.concurrent.locks.*;

/**
 *
 * @author chaitu
 */
public class Seek {
	public static enum COLOR {WHITE, BLACK, ANY};
	Client client;
	int boardSize;
	int no;
	int time;//time in seconds for each side
	int incr;//increment in seconds
	int komi;
	int pieces;
	int capstones;
	int unrated;
	int tournament;
	int triggerMove;
	int timeAmount;
	String opponent;
	COLOR color;

	public static Lock seekStuffLock = new ReentrantLock(); // this locks out all other threads
	
	static AtomicInteger seekNo = new AtomicInteger(0);
	
	static Map<Integer, Seek> seeks = new ConcurrentHashMap<>();
	static ConcurrentHashSet<Client> seekListeners = new ConcurrentHashSet<>();
	
	/** Creates a new seek with the same settings as the given seek, but with a new `no` */
	public static Seek newSeek(Client client, SeekDto seek) {
		return newSeek(client,
				seek.boardSize, seek.timeContingent, seek.timeIncrement, seek.color, seek.komiInt(), seek.pieces,
				seek.capstones, seek.unratedInt(), seek.tournamentInt(), seek.extraTimeTriggerMove, seek.extraTimeAmount,
				seek.opponent);
	}

	public static Seek newSeek(Client client, int boardSize, int timeContingent, int timeIncrement, COLOR clr, int komi, int pieces, int capstones, int unrated, int tournament, int triggerMove, int timeAmount, String opponent) {
		opponent = opponent == null ? "" : opponent;

		seekStuffLock.lock();
		try{
			Seek sk = new Seek(client, boardSize, timeContingent, timeIncrement, clr, komi, pieces, capstones, unrated, tournament, triggerMove, timeAmount, opponent);
			System.out.println("Print Seek " + sk.toString());
			addSeek(sk);
			return sk;
		}
		finally{
			seekStuffLock.unlock();
		}
	}
	
	Seek(Client client, int boardSize, int timeContingent, int timeIncrement, COLOR clr, int komi, int pieces, int capstones, int unrated, int tournament, int triggerMove, int timeAmount, String opponent) {
		seekStuffLock.lock();
		try{
			this.client = client;
			no = seekNo.incrementAndGet();
			time = timeContingent;
			incr = timeIncrement;
			color = clr;
			this.komi = Math.min(komi,8);
			this.pieces = Math.max(Math.min(pieces,80),10);
			this.capstones = Math.min(capstones,5);
			this.unrated = unrated;
			this.tournament = tournament;
			this.triggerMove = triggerMove;
			this.timeAmount = timeAmount;
			this.opponent = opponent;

			if (boardSize < 3 || boardSize > 8)
				boardSize = DEFAULT_SIZE;
			this.boardSize = boardSize;
		}
		finally{
			seekStuffLock.unlock();
		}
	}

	static void removeSeek(int b) {
		seekStuffLock.lock();
		try{
			Seek sk=Seek.seeks.get(b);
			Seek.seeks.remove(b);
			updateListeners("remove "+sk.toString());
		}
		finally{
			seekStuffLock.unlock();
		}
	}
	
	static void addSeek(Seek sk) {
		seekStuffLock.lock();
		try{
			Seek.seeks.put(sk.no, sk);
			updateListeners("new " + sk.toString());
		}
		finally{
			seekStuffLock.unlock();
		}
	}
		
	static void sendListTo(Client c) {
		seekStuffLock.lock();
		try{
			for (Integer no : Seek.seeks.keySet()) {
				c.send("Seek new "+Seek.seeks.get(no));
			}
		}
		finally{
			seekStuffLock.unlock();
		}
	}
		
	public static List<SeekDto> getList() {
		seekStuffLock.lock();
		try{
			return Seek.seeks.values().stream().map(s -> s.toDto()).toList();
		}
		finally{
			seekStuffLock.unlock();
		}
	}

	public SeekDto toDto() {
		seekStuffLock.lock();
		try{
			return new SeekDto(
				no,
				client.player.getName(),
				opponent == "" ? null : opponent,
				time,
				incr,
				triggerMove,
				timeAmount,
				komi,
				boardSize,
				pieces,
				capstones,
				unrated > 0,
				tournament > 0,
				color
			);
		}
		finally{
			seekStuffLock.unlock();
		}
	}
	
	static void registerListener(Client c) {
		seekStuffLock.lock();
		try{
			seekListeners.add(c);
			sendListTo(c);
		}
		finally{
			seekStuffLock.unlock();
		}
	}
	
	static void updateListeners(final String st) {
		seekStuffLock.lock();
		try{
			for (Client cc : seekListeners) {
				cc.sendWithoutLogging("Seek " + st);
			}
		}
		finally{
			seekStuffLock.unlock();
		}
	}
	
	static void unregisterListener(Client c) {
		seekStuffLock.lock();
		try{
			seekListeners.remove(c);
		}
		finally{
			seekStuffLock.unlock();
		}
	}

	@Override
	public String toString() {
		seekStuffLock.lock();
		try{
			String clr = "A";
			if(color == COLOR.WHITE)
				clr = "W";
			else if(color == COLOR.BLACK)
				clr = "B";
			String playerName = client.player.getName();

			return String.join(" ", new String[]{
				Integer.toString(no),
				playerName,
				Integer.toString(boardSize),
				Integer.toString(time),
				Integer.toString(incr),
				clr,
				Integer.toString(komi),
				Integer.toString(pieces),
				Integer.toString(capstones),
				Integer.toString(unrated),
				Integer.toString(tournament),
				Integer.toString(triggerMove),
				Integer.toString(timeAmount),
				opponent
		});
		}
		finally{
			seekStuffLock.unlock();
		}
	}
}
