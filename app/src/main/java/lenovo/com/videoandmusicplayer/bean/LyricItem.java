package lenovo.com.videoandmusicplayer.bean;

/**
 * 歌词的bean
 */
public class LyricItem {
	private long startPosition;
	private String text_lyric;
	
	public LyricItem(long start,String text){
		this.startPosition = start;
		this.text_lyric = text;
	}

	public long getStartPosition() {
		return startPosition;
	}
	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}
	public String getText_lyric() {
		return text_lyric;
	}
	public void setText_lyric(String text_lyric) {
		this.text_lyric = text_lyric;
	}

	public void myToString() {
		System.out.println("LyricItem [startPosition=" + startPosition + ", text_lyric="
				+ text_lyric + "]"); 
	}
	
	
	
}
