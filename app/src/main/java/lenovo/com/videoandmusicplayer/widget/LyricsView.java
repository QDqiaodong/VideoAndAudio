package lenovo.com.videoandmusicplayer.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

import lenovo.com.videoandmusicplayer.R;
import lenovo.com.videoandmusicplayer.bean.LyricItem;
import lenovo.com.videoandmusicplayer.utils.LogUtil;

/**
 * Created by Administrator on 2016/3/24.
 */
public class LyricsView extends View {

    /**
     * 默认歌词的大小和 颜色
     */
    private int defaultTextSize;
    private int defaultTextColor;

    /**
     * 高亮歌词的大小和 颜色
     */
    private int highLightSize;
    private int highLightColor;

    /**
     * 歌词
     */
    private ArrayList<LyricItem> lyricItems = new ArrayList<>();
    /**
     * 高亮歌词的索引
     */
    private int highLightIndex = 0;
    /**
     * 高亮歌词在屏幕的纵坐标
     */
    private int highLightY;
    /**
     * 每行歌词的高度
     */
    private int rowHeight;
    /**
     * 当前播放的位置（歌曲的进度）
     */
    private long currentPosition;
    /**
     * 画笔
     */
    private Paint paint;


    public LyricsView(Context context) {
        super(context);
        init();
    }

    public LyricsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        defaultTextSize = getResources().getDimensionPixelSize(R.dimen.lyrics_default_size);
        highLightSize = getResources().getDimensionPixelSize(R.dimen.lyrics_high_light_size);

        defaultTextColor = Color.WHITE;
        highLightColor = Color.GREEN;

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(defaultTextSize);
        paint.setColor(defaultTextSize);

        highLightIndex = 0;

        String textHeight = "哈哈";
        rowHeight = getTextHeight(textHeight) + 10;
    }

    /**
     * 获取文本的高度
     * @param textHeight
     * @return
     */
    private int getTextHeight(String textHeight) {
        Rect rect = new Rect();
        paint.getTextBounds(textHeight,0,textHeight.length(),rect);
        LogUtil.i("qd","LyricsView 默认文本的行高=="+(rect.height()+10));
        return rect.height();
    }

    /**
     * 获取文本的宽度
     * @param textHeight
     * @return
     */
    private int getTextWidth(String textHeight) {
        Rect rect = new Rect();
        paint.getTextBounds(textHeight,0,textHeight.length(),rect);
        LogUtil.i("qd", "LyricsView 默认文本的行高==" + (rect.width()));
        return rect.width();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        /**
         * 如果没有歌词的话，友好的提示用户
         */
        if(lyricItems == null || lyricItems.isEmpty()){
            paint.setTextSize(highLightSize);
            drawHighLightText(canvas, "正在检索歌词。。。。");
            return;
        }

        //取出高亮的歌词
        LyricItem lyricItem = lyricItems.get(highLightIndex);
        //判断是否需要滚动歌词，非最后一句歌词则滚动
        if(highLightIndex != lyricItems.size() - 1){
            //当前高亮歌词的开始时间
            long startPosition = lyricItem.getStartPosition();
            //下一句高亮歌词的开始时间
            long nextStartPostion = lyricItems.get(highLightIndex + 1).getStartPosition();
            //歌词滚动比例
            float scale = (float)(currentPosition-startPosition) / (nextStartPostion - startPosition);
            //歌词滚动的距离
            float distanceY = scale * rowHeight;
            //画布向上移动
            canvas.translate(0,-distanceY);
        }

        //画出高亮歌词
        paint.setTextSize(highLightSize);
        drawHighLightText(canvas,lyricItem.getText_lyric());

        //画出高亮歌词上面的歌词
        for(int i = 0; i < highLightIndex;i++){
            LyricItem preItem = lyricItems.get(i); //上面的歌词
            int result = highLightIndex - i; //距离高亮文本的间隔数
            int y = highLightY - result * rowHeight;
            paint.setTextSize(defaultTextSize);
            drawHorizontalText(canvas,preItem.getText_lyric(),false,getTextX(preItem.getText_lyric()),y);
        }


        //画出高亮歌词下面的歌词
        for(int i = highLightIndex + 1; i < lyricItems.size();i++){
            LyricItem preItem = lyricItems.get(i); //下面的歌词
            int result = i - highLightIndex; //距离高亮文本的间隔数
            int y = highLightY + result * rowHeight;
            paint.setTextSize(defaultTextSize);
            drawHorizontalText(canvas, preItem.getText_lyric(), false, getTextX(preItem.getText_lyric()),y);
        }


        super.onDraw(canvas);
    }

    /**
     * 画出高亮歌词
     * @param canvas
     * @param s
     */
    private void drawHighLightText(Canvas canvas, String s) {
        drawHorizontalText(canvas, s, true, getTextX(s), getHighLightTextY(s));
    }

    /**
     * 画出水平歌词
     */
    private void drawHorizontalText(Canvas canvas,String text,boolean isHighLight,float x,float y){
        paint.setColor(isHighLight ? highLightColor : defaultTextColor);
        paint.setTextSize(isHighLight ? highLightSize : defaultTextSize);
        canvas.drawText(text, 0, text.length(), x,y, paint);
    }

    /**
     * 获取文本的x 轴的坐标
     * @return
     */
    public int getTextX(String text){
        int x = getWidth() / 2 - getTextWidth(text) / 2;
        return x;
    }

    /**
     * 获取高亮歌词Y轴坐标
     * @return
     */
    public int getHighLightTextY(String text){
        highLightY = getHeight() / 2 - getTextHeight(text) / 2;
        return highLightY;
    }

    /**
     * 设置歌词
     * @param lists
     */
    public void setLyricLists(ArrayList<LyricItem> lists){
        lyricItems = lists;
        highLightIndex = 0;
       // LogUtil.i("qd","lyriView lists.size()="+lists.size());
    }

    /**
     * 更新当前高亮歌词的位置
     * @param
     */
    public void updateCurrentPosition(long position){
        //如果没有歌词，当然不需要更新
        if(lyricItems == null || lyricItems.isEmpty()){
            invalidate();
            return;
        }

        //当前的播放位置
        currentPosition = position;

        //当前的高亮文本行号
        for(int i = 0;i < lyricItems.size(); i++){
            LyricItem lyricItem = lyricItems.get(i);

            //最后一行
            if(i == lyricItems.size()-1){
                if(currentPosition >= lyricItem.getStartPosition()){
                    highLightIndex = i;
                    break;
                }
                /**
                 * 如果当前播放时间 > 当前歌词的开始时间
                 * 并且
                 * 如果当前播放时间 < 下一歌词的开始时间
                 * 当前歌词即为 高亮行
                 */
            }else if(currentPosition >= lyricItem.getStartPosition() && currentPosition <  lyricItems.get(i+1).getStartPosition()){
                highLightIndex = i;
                break;
            }
        }
       invalidate();
    }
}
