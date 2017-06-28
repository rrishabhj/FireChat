package com.google.firebase.udacity.friendlychat.view;

/**
 * Created by DMI on 28-06-2017.
 */


        import android.content.Context;
        import android.content.res.TypedArray;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.graphics.Path;
        import android.graphics.Point;
        import android.graphics.RectF;
        import android.text.DynamicLayout;
        import android.text.Layout;
        import android.text.TextPaint;
        import android.text.format.DateUtils;
        import android.util.AttributeSet;
        import android.util.DisplayMetrics;
        import android.util.TypedValue;
        import android.view.View;

        import com.google.firebase.udacity.friendlychat.R;

        import java.text.ParseException;
        import java.text.SimpleDateFormat;
        import java.util.Date;
        import java.util.Locale;

/**
 * Created by Rushil on 6/20/2015.
 */
public class ChatBubbleView extends View {

    private final int dp5, dp7, dp18, dp20, dp35, dp70, dp80;
    private String message, sender, date;
    private Date dateTime;
    private boolean isUser;
    private int bubbleColor, textColor, accentColor;
    private Paint bubblePaint;
    private float bubbleWidth, textWidth, scale;
    private int deviceWidth;
    private RectF bubble;
    private DynamicLayout messageLayout, dateLayout;
    private TextPaint textPaint, accentPaint;
    private DisplayMetrics display;
    private Context context;

    public ChatBubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ChatBubble, 0, 0);
        boolean lighterAccent;
        try {
            message = typedArray.getString(R.styleable.ChatBubble_messageText);
            sender = typedArray.getString(R.styleable.ChatBubble_senderText);
            bubbleColor = typedArray.getColor(R.styleable.ChatBubble_bubbleColor, getResources().getColor(R.color
                    .primary_redd));
            textColor = typedArray.getColor(R.styleable.ChatBubble_textColor, determineTextColor(bubbleColor));
            isUser = typedArray.getBoolean(R.styleable.ChatBubble_isUser, true);
            lighterAccent = typedArray.getBoolean(R.styleable.ChatBubble_lighterAccent, false);
            if (typedArray.getString(R.styleable.ChatBubble_dateTime) != null)
                dateTime = new SimpleDateFormat("EEEE, MMMM d, y HH:mm", Locale.CANADA).parse(typedArray.getString(R
                        .styleable
                        .ChatBubble_dateTime));
        } catch (ParseException e) {
            throw new RuntimeException("Couldn't parse!", e);
        } finally {
            typedArray.recycle();
        }

        display = getResources().getDisplayMetrics();
        deviceWidth = display.widthPixels;
        scale = display.density;
        dp5 = getPixelsFromDp(5);
        dp7 = getPixelsFromDp(7);
        dp18 = getPixelsFromDp(18);
        dp20 = getPixelsFromDp(20);
        dp35 = getPixelsFromDp(35);
        dp70 = getPixelsFromDp(70);
        dp80 = getPixelsFromDp(80);
        accentColor = lighterAccent ? lighten(bubbleColor) : (determineTextColor(bubbleColor) == Color.BLACK ? darken
                (bubbleColor) : lighten(bubbleColor));

        if (message == null)
            message = "";
    }

    //method required as not to make custom view sluggish
    private void initViews() {
        float dateWidth;
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(textColor);
        textPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15.0f, display));

        accentPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        accentPaint.setColor(accentColor);
        accentPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 13.0f, display));

        if (dateTime != null)
            date = (String) DateUtils.getRelativeDateTimeString(context, dateTime.getTime(), DateUtils.SECOND_IN_MILLIS,
                    DateUtils.WEEK_IN_MILLIS, 0);

        if (date != null)
            dateWidth = accentPaint.measureText(date);
        else
            dateWidth = -1;

        float messageWidth = textPaint.measureText(message);

        float measuredWidth = dateWidth == -1 ? messageWidth : (messageWidth > dateWidth ? messageWidth : dateWidth);

        if (isUser)
            if (measuredWidth < deviceWidth - dp70)
                textWidth = measuredWidth;
            else
                textWidth = deviceWidth - dp70;
        else {
            if (measuredWidth < deviceWidth - dp80)
                textWidth = measuredWidth;
            else
                textWidth = deviceWidth - dp80;
        }

        bubblePaint = new Paint();
        bubblePaint.setColor(bubbleColor);
        bubblePaint.setStyle(Paint.Style.FILL);

        messageLayout = new DynamicLayout(message, textPaint, (int) TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_PX, textWidth, display), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        dateLayout = new DynamicLayout(date, accentPaint, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,
                textWidth, display), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);

        // Added 20dp to "textWidth" to allow for a 10dp padding on either side of the text
        if (isUser) {
            if (message.equals(""))
                bubbleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 100.0f, display);
            else
                bubbleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, getLongestLineWidth() + dp35,
                        display);

            bubble = new RectF(dp20, 0.0f, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, bubbleWidth,
                    display), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, messageLayout.getHeight() +
                    dateLayout.getHeight() + dp20, display));
        } else {
            if (message.equals(""))
                bubbleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 100.0f, display);
            else
                bubbleWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, getLongestLineWidth() + dp20,
                        display);

            bubble = new RectF(0.0f, 0.0f, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, bubbleWidth,
                    display), TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, messageLayout.getHeight() +
                    dateLayout.getHeight() + dp20, display));
        }
    }

    private void drawTriangle(Canvas canvas) {
        if (isUser) {
            Point a = new Point(0, 0);
            Point b = new Point(dp20, 0);
            Point c = new Point(dp20, dp20);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.lineTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, b.x, display), TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_PX, b.y, display));
            path.lineTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, c.x, display), TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_PX, c.y, display));
            path.lineTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, a.x, display), TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_PX, a.y, display));
            path.close();

            canvas.drawPath(path, bubblePaint);
        } else {
            Point a = new Point((int) bubbleWidth + dp20, 0);
            Point b = new Point((int) (bubbleWidth), 0);
            Point c = new Point((int) (bubbleWidth), dp20);

            Path path = new Path();
            path.setFillType(Path.FillType.EVEN_ODD);
            path.lineTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, b.x, display), TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_PX, b.y, display));
            path.lineTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, c.x, display), TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_PX, c.y, display));
            path.lineTo(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, a.x, display), TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_PX, a.y, display));
            path.close();

            canvas.drawPath(path, bubblePaint);
        }
    }

    private float getLongestLineWidth() {
        float longestWidth = 0;

        for (int i = 0; i < messageLayout.getLineCount(); i++) {
            float lineWidth = messageLayout.getLineMax(i);
            if (longestWidth < lineWidth)
                longestWidth = lineWidth;
        }
        for (int i = 0; i < dateLayout.getLineCount(); i++) {
            float lineWidth = dateLayout.getLineMax(i);
            if (longestWidth < lineWidth)
                longestWidth = lineWidth;
        }
        return longestWidth;
    }

    private void drawMessage(Canvas canvas) {
        messageLayout = new DynamicLayout(message, textPaint, (int) TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_PX, textWidth, display), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        if (isUser)
            canvas.translate(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27.5f, display), TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, display));
        else
            canvas.translate(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, display), TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, display));
        messageLayout.draw(canvas);
        canvas.restore();
    }

    private void drawDate(Canvas canvas) {
        dateLayout = new DynamicLayout(date, accentPaint, (int) TypedValue.applyDimension(TypedValue
                .COMPLEX_UNIT_PX, textWidth, display), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        canvas.save();
        if (isUser)
            canvas.translate(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 27.5f, display), TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_PX, messageLayout.getHeight() + dp5 + dp7, display));
        else
            canvas.translate(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10.0f, display), TypedValue
                    .applyDimension(TypedValue.COMPLEX_UNIT_PX, messageLayout.getHeight() + dp5 + dp7, display));
        dateLayout.draw(canvas);
        canvas.restore();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Since the text is translated 10dp right and 10dp down, we add 20dp to the height of the
        // bubble so that there will be 10dp above and 10dp below
        // We do not alter bubbleWidth because we already added its 20dp padding when we
        // first calculated it (see above)

        // Additionally, changed the input from COMPLEX_UNIT_DIP to PX because when you retrieved
        // bubbleWidth and bounds, they were given values in pixels. If you give them back to Android
        // as DIP, the areas on screen are going to be bigger than you want, because pixels are smaller
        // than dp. Now, you get the values in pixels, you give them back in pixels, and any alterations
        // you need to make along the way, you convert from dp to px.
        initViews();
        canvas.drawRoundRect(bubble, dp5, dp5, bubblePaint);
        if (isUser) {
            canvas.drawRect(dp20, 0, dp20 + dp5, dp18, bubblePaint);
            canvas.drawRect(dp20, 0, dp20 + dp20, dp7, bubblePaint);
        } else {
            canvas.drawRect(bubbleWidth - dp20, 0, bubbleWidth, dp5, bubblePaint);
            canvas.drawRect(bubbleWidth - dp7, 0, bubbleWidth, dp18, bubblePaint);
        }
        drawMessage(canvas);
        drawDate(canvas);
        drawTriangle(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initViews();
        if (isUser)
            setMeasuredDimension((int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, bubbleWidth, display)),
                    (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, messageLayout.getHeight() +
                            dateLayout.getHeight() + dp20, display)));
        else
            setMeasuredDimension((int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, bubbleWidth + dp20,
                    display)), (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, messageLayout.getHeight()
                    + dateLayout.getHeight() + dp20, display)));
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
        requestLayout();
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
        requestLayout();
    }

    public int getBubbleColor() {
        return bubbleColor;
    }

    public void setBubbleColor(int bubbleColor) {
        this.bubbleColor = bubbleColor;
        requestLayout();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        requestLayout();
    }

    public boolean isUser() {
        return isUser;
    }

    public void setUserBoolean(boolean isUser) {
        this.isUser = isUser;
        requestLayout();
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    /**
     * Converts dp to pixels, according to http://stackoverflow.com/questions/6656540
     *
     * @param dp value to convert
     * @return {@code dp} in pixels
     */
    private int getPixelsFromDp(int dp) {
        return (int) (scale * dp + 0.5f);
    }

    /*
     * Formula found at http://stackoverflow.com/a/3943023/2038087
     */
    private int determineTextColor(int bubbleColor) {
        double r = bubbleColor & 255, g = (bubbleColor >> 8) & 255, b = (bubbleColor >> 16) & 255;
        double luminance = 0.2126 * r + 0.7152 * g + 0.0722 * b;
        if (luminance > 0.179)
            return Color.BLACK;
        return Color.WHITE;
    }

    private int lighten(int accentColor) {
        int red = (int) ((Color.red(accentColor) * (1 - 0.7) / 255 + 0.7) * 255);
        int green = (int) ((Color.green(accentColor) * (1 - 0.7) / 255 + 0.7) * 255);
        int blue = (int) ((Color.blue(accentColor) * (1 - 0.7) / 255 + 0.7) * 255);
        return Color.argb(Color.alpha(accentColor), red, green, blue);
    }

    private int darken(int accentColor) {
        int a = Color.alpha(accentColor);
        int r = Color.red(accentColor);
        int g = Color.green(accentColor);
        int b = Color.blue(accentColor);

        return Color.argb(a, Math.max((int) (r * 0.7), 0), Math.max((int) (g * 0.7), 0), Math.max((int) (b * 0.7), 0));
    }
}