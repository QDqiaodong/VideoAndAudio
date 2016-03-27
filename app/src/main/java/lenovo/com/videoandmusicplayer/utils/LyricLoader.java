package lenovo.com.videoandmusicplayer.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import lenovo.com.videoandmusicplayer.bean.LyricItem;

/**
 * 歌词的加载器
 * @author Administrator
 *
 */
public class LyricLoader {
	/**
	 * 加载歌词
	 * @param path
	 * @return
	 */
	public static ArrayList<LyricItem> loadLyric(String path) {
		if(path == null){
			return null;
		}
		ArrayList<LyricItem> lists;
		String fileName = path.substring(0,path.lastIndexOf('.'));
		if(new File(fileName+".txt").exists()){
			lists = parseFile(fileName+".txt");
		}else if(new File(fileName+".lrc").exists()){
			lists = parseFile(fileName+".lrc");
		}else{
			return null;
		}
		
		if(lists != null && !lists.isEmpty()){
			Collections.sort(lists, new Comparator<LyricItem>() {
				@Override
				public int compare(LyricItem o1, LyricItem o2) {
					return (int) (o1.getStartPosition() - o2.getStartPosition());
				}
			});
		}
		
		return  lists;
	}

	private static ArrayList<LyricItem> parseFile(String string) {
		ArrayList<LyricItem> lists = new ArrayList<LyricItem>();
		BufferedReader read = null;
		try {
			read = new BufferedReader(new InputStreamReader(new FileInputStream(new File(string)),"GBK"));
			String line = null;
			while((line = read.readLine())!=null ){
				lists.addAll(parseLine(line));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			if(read != null){
				try {
					read.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return lists;
	}

	/**
	 * ����ÿһ�и��
	 * @param line
	 * @return
	 */
	private static ArrayList<LyricItem> parseLine(String line) {
		if("".equals(line)){
			return null;
		}
		ArrayList<LyricItem> lists = new ArrayList<LyricItem>();
		
		String[] lineItems = line.split("]");
		if(lineItems.length >= 2){
			String text = lineItems[lineItems.length - 1];

			for(int i = 0; i<lineItems.length - 1; i++){
				String timeItem = lineItems[i].substring(1,lineItems[i].length());
				
				String[] times = timeItem.split(":");
				long minuteMill = Integer.valueOf(times[0]) * 60 * 1000;
				String[] times2 = times[1].split("\\.");
				long secondMill = Integer.valueOf(times2[0]) * 1000;
				long millisecond = Integer.valueOf(times2[1]) * 10;
				
				long startPosition = minuteMill + secondMill + millisecond;
				LyricItem lyricItem = new LyricItem(startPosition,text);
				lists.add(lyricItem);
			}
		}
		
		return lists;
	}
}
