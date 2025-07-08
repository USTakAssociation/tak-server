package tak;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


class CmdSeek extends NotInGameCommand {
	Pattern seekV3Pattern;
	Pattern seekV2Pattern;
	Pattern seekV1Pattern;


	public CmdSeek(Client client) {
		super("^Seek", client);

		seekV3Pattern = Pattern.compile("^Seek (\\d) (\\d+) (\\d+) ([WBA]) (\\d+) (\\d+) (\\d+) (0|1) (0|1) (\\d+) (\\d+) ([A-Za-z0-9_]*)");
		seekV2Pattern = Pattern.compile("^Seek (\\d) (\\d+) (\\d+) ([WBA]) (\\d+) (\\d+) (\\d+) (0|1) (0|1) ([A-Za-z0-9_]*)");
		seekV1Pattern = Pattern.compile("^Seek (\\d) (\\d+) (\\d+)( [WB])?");

	}

	public void executeImpl(String command) {
		Matcher sv3m = seekV3Pattern.matcher(command);
		Matcher sv2m = seekV2Pattern.matcher(command);
		Matcher sv1m = seekV1Pattern.matcher(command);
		
		if (sv3m.find()) {
			seekV3(sv3m);
		} else if (sv2m.find()) {
			seekV2(sv2m);
		} else if (sv1m.find()) {
			seekV1(sv1m);
		} else {
			client.sendNOK();
		}
	}

	public void seekV3(Matcher m) {
		Seek.seekStuffLock.lock();
		try{
			if (client.seek != null) {
				Seek.removeSeek(client.seek.no);
			}
			int no = Integer.parseInt(m.group(1));
			if(no == 0) {
				client.Log("Seek remove");
				client.seek = null;
			} else {
				Seek.COLOR clr = Seek.COLOR.ANY;

				if("W".equals(m.group(4)))
					clr = Seek.COLOR.WHITE;
				else if("B".equals(m.group(4)))
					clr = Seek.COLOR.BLACK;
				client.seek = Seek.newSeek(
						client,
						Integer.parseInt(m.group(1)),
						Integer.parseInt(m.group(2)),
						Integer.parseInt(m.group(3)),
						clr,
						Integer.parseInt(m.group(5)),
						Integer.parseInt(m.group(6)),
						Integer.parseInt(m.group(7)),
						Integer.parseInt(m.group(8)),
						Integer.parseInt(m.group(9)),
						Integer.parseInt(m.group(10)),
						Integer.parseInt(m.group(11)),
						m.group(12),
						null
				);
				client.Log("Seek "+client.seek.boardSize);
			}
		}
		finally{
			Seek.seekStuffLock.unlock();
		}
	}

	public void seekV2(Matcher m) {
		Seek.seekStuffLock.lock();
		try{
			if (client.seek != null) {
				Seek.removeSeek(client.seek.no);
			}
			int no = Integer.parseInt(m.group(1));
			if(no == 0) {
				client.Log("Seek remove");
				client.seek = null;
			} else {
				Seek.COLOR clr = Seek.COLOR.ANY;

				if("W".equals(m.group(4)))
					clr = Seek.COLOR.WHITE;
				else if("B".equals(m.group(4)))
					clr = Seek.COLOR.BLACK;
				client.seek = Seek.newSeek(
					client,
					Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)),
					Integer.parseInt(m.group(3)),
					clr,
					Integer.parseInt(m.group(5)),
					Integer.parseInt(m.group(6)),
					Integer.parseInt(m.group(7)),
					Integer.parseInt(m.group(8)),
					Integer.parseInt(m.group(9)),
					0,
					0,
					m.group(10),
					null
				);
				client.Log("Seek "+client.seek.boardSize);
			}
		}
		finally{
			Seek.seekStuffLock.unlock();
		}
	}

	public void seekV1(Matcher m) {
		Seek.seekStuffLock.lock();
		try{
			if (client.seek != null) {
				Seek.removeSeek(client.seek.no);
			}
			int no = Integer.parseInt(m.group(1));
			if(no == 0) {
				client.Log("Seek remove");
				client.seek = null;
			} else {
				Seek.COLOR clr = Seek.COLOR.ANY;

				if(" W".equals(m.group(4)))
					clr = Seek.COLOR.WHITE;
				else if(" B".equals(m.group(4)))
					clr = Seek.COLOR.BLACK;

				int capstonesCount=0;
				int tilesCount=0;
				switch(Integer.parseInt(m.group(1))) {
					case 3: capstonesCount = 0; tilesCount = 10; break;
					case 4: capstonesCount = 0; tilesCount = 15; break;
					case 5: capstonesCount = 1; tilesCount = 21; break;
					case 6: capstonesCount = 1; tilesCount = 30; break;
					case 7: capstonesCount = 2; tilesCount = 40; break;
					case 8: capstonesCount = 2; tilesCount = 50; break;
				}

				client.seek = Seek.newSeek(
					client,
					Integer.parseInt(m.group(1)),
					Integer.parseInt(m.group(2)),
					Integer.parseInt(m.group(3)),
					clr,
					0,
					tilesCount,
					capstonesCount,
					0,
					0,
					0,
					0,
					"",
					null
				);
				client.Log("Seek "+client.seek.boardSize);
			}
		}
		finally{
			Seek.seekStuffLock.unlock();
		}
	}
}

