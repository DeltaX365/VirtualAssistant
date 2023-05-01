package vn.sttechnology.virtualassistant.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class BitmapUtils {
    public static Bitmap generateBitmapByText(Context context, String text, float textSize, int bitmapWidth, int bitmapHeight) {
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth , bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

//        Random random = new Random();
//        int randomColor = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256));
        int randomColor = generateColorByText(text);
        paint.setColor(randomColor);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPaint(paint);

        String avatarText = generateTextForAvatar(text);
        paint.setTextSize(textSize * context.getResources().getDisplayMetrics().density);
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        float baseline = -paint.ascent(); // ascent() is negative
        float measureWidth = paint.measureText(avatarText); // round
//        float measureHeight = baseline + paint.descent();
        canvas.drawText(avatarText,(bitmapWidth - measureWidth)/2, (bitmapHeight - (paint.descent() + paint.ascent()))/2, paint);

        return bitmap;
    }

    private static String generateTextForAvatar(String text) {
        String avatarText = "";
        if (text != null) {
            String[] textSpited = text.split(" ");
            if (textSpited.length >= 2) {
                avatarText += textSpited[0].substring(0, 1) + textSpited[textSpited.length - 1].substring(0, 1);
            } else if (text.length() >= 2) {
                avatarText += text.substring(0, 2);
            }
            avatarText = avatarText.toUpperCase();
        }
        return avatarText;
    }

    private static int generateColorByText(String text) {
        int redColor = 255;
        int greenColor = 255;
        int blueColor = 255;

        if (text != null) {
            String redText = "";
            String greenText = "";
            String blueText = "";

            if (text.length() < 3) {
                redText = text;
            } else {
                int step = text.length() / 3;
                redText = text.substring(0, step);
                greenText = text.substring(step, 2 * step);
                blueText = text.substring(2 * step);
            }

            redColor = getNumericValue(redText) % 255;
            greenColor = getNumericValue(greenText) % 255;
            blueColor = getNumericValue(blueText) % 255;
        }

        int randomColor = Color.argb(255, redColor, greenColor, blueColor);
        return randomColor;
    }

    private static int getNumericValue(String text) {
        int total = 0;
        if (text != null) {
            for (int i = 0; i < text.length(); i++) {
                try {
                    int number = Character.getNumericValue(text.charAt(i));
                    if (number > 0) {
                        total += number;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return total;
    }
}
