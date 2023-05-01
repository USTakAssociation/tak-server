package tak.DTOs;

import java.util.UUID;

import lombok.Builder;
import lombok.Value;
import lombok.extern.jackson.Jacksonized;
import tak.Seek;

@Value
@Builder
@Jacksonized
public class SeekDto {
	public int id;
	public UUID uid;
	public String creator;
	public String opponent;
	/** Seconds */
	public int timeContingent;
	/** Seconds */
	public int timeIncrement;
	public int extraTimeTriggerMove;
	/** Seconds */
	public int extraTimeAmount;
	public float komi;

	public int boardSize;
	public int capstones;
	public int pieces;
	public boolean unrated;
	public boolean tournament;
	public Seek.COLOR color;

	public int tournamentInt() {
		return tournament ? 1 : 0;
	}

	public int unratedInt() {
		return unrated ? 1 : 0;
	}

	public int komiInt() {
		return Math.round(komi * 2);
	}
}
