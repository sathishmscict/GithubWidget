package com.nightonke.githubwidget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.image);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        imageView.setImageBitmap(get3DBitmap(
                this, getTestData(),
                Weekday.SUN,
                SettingsManager.getBaseColor(),
                Color.parseColor("#000000"),
                true,
                false));
    }

    private Bitmap get3DBitmap(
            Context context,
            ArrayList<Day> contributions,
            Weekday startWeekday,
            int baseColor,
            int textColor,
            boolean drawMonthText,
            boolean drawWeekdayText) {
        Bitmap bitmap;
        Canvas canvas;
        Paint blockPaint;
        Paint monthTextPaint;
        Paint weekdayTextPaint;
        Paint titlePaint;
        Paint numberPaint;
        Paint unitPaint;
        Paint remarkPaint;
        Paint dash;

        int bitmapWidth = Util.getScreenWidth(this);
        // Todo calculate the height
        int bitmapHeight = bitmapWidth * 3 / 4;
        int columnNumber = Util.getContributionsColumnNumber(TEST_DATA);
        int n = columnNumber - 2;
        float paddingLeft = 10f;
        float paddingRight = 10f;
        float paddingTop = 10f;
        float paddingBottom = 10f;
        float emptyHeight = 5f;
        float a1 = 38f;
        float a2 = 22f;
        float rate = 1 / 6f;
        float cosa1 = (float)Math.cos(Math.toRadians(a1));
        float cosa2 = (float)Math.cos(Math.toRadians(a2));
        float ls = (bitmapWidth - paddingLeft - paddingRight)
                /
                ((1 / (1 + rate)) + 7 * cosa1 + (n + 1) * cosa2);
        float lscosa1 = (float)Math.cos(Math.toRadians(a1)) * (ls);
        float lscosa2 = (float)Math.cos(Math.toRadians(a2)) * (ls);
        float lssina1 = (float)Math.sin(Math.toRadians(a1)) * (ls);
        float lssina2 = (float)Math.sin(Math.toRadians(a2)) * (ls);
        float l = ls / (1 + rate);
        float lcosa1 = (float)Math.cos(Math.toRadians(a1)) * l;
        float lcosa2 = (float)Math.cos(Math.toRadians(a2)) * l;
        float lsina1 = (float)Math.sin(Math.toRadians(a1)) * l;
        float lsina2 = (float)Math.sin(Math.toRadians(a2)) * l;
        float s = l * rate;
        float topFaceheight = lsina1 + lsina2;
        float x6 = paddingLeft + lcosa2;
        float x0 = x6 + 7 * lscosa1;
        float monthDashLength = 45f;
        float dashTextHeight = (drawMonthText || drawWeekdayText) ? monthDashLength : 0;
        float maxHeight = bitmapHeight - paddingTop - topFaceheight - lssina2 * (n + 1)
                - 6 * lssina1 - dashTextHeight - paddingBottom;
        float y0 = paddingTop + topFaceheight + maxHeight + 2 * emptyHeight;
        int currentWeekDay = Util.getWeekDayFromDate(
                contributions.get(0).year,
                contributions.get(0).month,
                contributions.get(0).day);
        float x = x0 - ((currentWeekDay - startWeekday.v + 7) % 7) * lscosa1;
        float y = y0 + ((currentWeekDay - startWeekday.v + 7) % 7) * lssina1;
        float textHeight = l;
        float maxData = 0;
        for (Day day : contributions) maxData = Math.max(maxData, day.data);
        int endWeekday = (startWeekday.v - 1 + 7) % 7;
        float monthDashAndTextPadding = 5f;
        float weekdayDashAndTextPadding = 5f;
        float lastMonthDashEndX = 0;
        float lastMonthDashEndY = 0;
        float maxY = 0;

        bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        blockPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        blockPaint.setStyle(Paint.Style.FILL);

        monthTextPaint = Util.getTextPaint(textHeight, textColor,
                Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf"));

        weekdayTextPaint = Util.getTextPaint(textHeight, textColor,
                Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf"));

        titlePaint = Util.getTextPaint(1.5f * textHeight, textColor,
                Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf"));

        numberPaint = Util.getTextPaint(4 * textHeight, Util.calculateLevelColor(baseColor, 4),
                Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf"));

        unitPaint = Util.getTextPaint(1.5f * textHeight, textColor,
                Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf"));

        remarkPaint = Util.getTextPaint(1.5f * textHeight, textColor,
                Typeface.createFromAsset(context.getAssets(), "fonts/Lato-Light.ttf"));

        dash = new Paint(Paint.ANTI_ALIAS_FLAG);
        dash.setARGB(255, 0, 0,0);
        dash.setStyle(Paint.Style.STROKE);
        dash.setColor(textColor);
        dash.setPathEffect(new DashPathEffect(new float[] {5, 5}, 0));

        int lastMonth = contributions.get(0).month;
        for (int i = 0; i < contributions.size(); i++) {
            Day day = contributions.get(i);
            float height;
            if (day.data == 0) height = emptyHeight;
            else height = maxHeight * day.data / maxData + 2 * emptyHeight;

            // face right-bottom corner
            blockPaint.setColor(
                    Util.calculateShadowColorRightBottom(
                            Util.calculateLevelColor(baseColor, day.level)));
            Path path = new Path();
            path.moveTo(x, y);
            path.lineTo(x + lcosa1, y - lsina1);
            path.lineTo(x + lcosa1, y - lsina1 - height);
            path.lineTo(x, y - height);
            path.lineTo(x, y);
            canvas.drawPath(path, blockPaint);

            blockPaint.setColor(
                    Util.calculateShadowColorLeftBottom(
                            Util.calculateLevelColor(baseColor, day.level)));
            path = new Path();
            path.moveTo(x, y);
            path.lineTo(x - lcosa2, y - lsina2);
            path.lineTo(x - lcosa2, y - lsina2 - height);
            path.lineTo(x, y - height);
            path.lineTo(x, y);
            canvas.drawPath(path, blockPaint);

            blockPaint.setColor(Util.calculateLevelColor(baseColor, day.level));
            path = new Path();
            path.moveTo(x, y - height);
            path.lineTo(x + lcosa1, y - lsina1 - height);
            path.lineTo(x + lcosa1 - lcosa2, y - lsina1 - height - lsina2);
            path.lineTo(x - lcosa2, y - lsina2 - height);
            path.lineTo(x, y - height);
            canvas.drawPath(path, blockPaint);

            if (i == contributions.size() - 1) break;

            currentWeekDay = (currentWeekDay + 1) % 7;
            if (currentWeekDay == startWeekday.v) {
                // another column
                x = x + 6 * lscosa1 + lscosa2;
                y = y - 6 * lssina1 + lssina2;
            } else {
                x -= lscosa1;
                y += lssina1;
                lastMonthDashEndX = x - lcosa2;
                lastMonthDashEndY = y - lsina2 + monthDashLength;
                if (currentWeekDay == endWeekday && day.month != lastMonth && drawMonthText) {
                    canvas.drawLine(x - lcosa2, y - lsina2,
                            lastMonthDashEndX, lastMonthDashEndY, dash);
                    canvas.drawText(
                            Util.getShortMonthName(day.year, day.month, day.day),
                            lastMonthDashEndX + monthDashAndTextPadding,
                            lastMonthDashEndY,
                            monthTextPaint);
                    lastMonth = day.month;
                }
            }

            maxY = Math.max(y, maxY);
        }
        if (drawMonthText || drawWeekdayText) maxY = lastMonthDashEndY;

        if (drawWeekdayText) {
            int lastWeekdaysNumber = Util.getLastWeekDaysNumber(contributions, startWeekday);
            x += (lastWeekdaysNumber - 1) * lscosa1;
            y -= (lastWeekdaysNumber - 1) * lssina1;
            float dashLength = lastMonthDashEndY - (y + 4 * lssina1);
            for (int i = 2; i >= 0; i--) {
                int drewWeekday = (startWeekday.v + 1 + 2 * i) % 7;
                String weekdayString = Util.getWeekdayFirstLetter(drewWeekday);
                canvas.drawText(
                        weekdayString,
                        x - 2 * i * lscosa1
                                - Util.getTextWidth(weekdayTextPaint, weekdayString)
                                - weekdayDashAndTextPadding,
                        y + (1 + 2 * i) * lssina1 + dashLength,
                        weekdayTextPaint);
                if (2 * i >= lastWeekdaysNumber) {
                    Path path = new Path();
                    path.moveTo(
                            x - 2 * i * lscosa1,
                            y + (1 + 2 * i) * lssina1 + dashLength);
                    path.lineTo(x - 2 * i * lscosa1, y + 2 * i * lssina1);
//                    path.lineTo(
//                            x - 2 * i * lscosa1 - lscosa2,
//                            y + 2 * i * lssina1 - lssina2);
                    canvas.drawPath(path, dash);
                } else {
                    canvas.drawLine(
                            x - 2 * i * lscosa1,
                            y + (1 + 2 * i) * lssina1 + dashLength,
                            x - 2 * i * lscosa1,
                            y + 2 * i * lssina1, dash);
                }
            }
        }

        float partPadding = 2 * textHeight;
        float titleAndNumberPadding = 8f;
        float numberAndRemarkPadding = 10f;
        float unitAndRemarkPadding = 5f;
        float avatarWidth = bitmapWidth / 5;

        // draw current streak text
        String[] currentStreaks = Util.getCurrentStreak(contributions);
        float numberHeight = Util.getTextHeight(numberPaint, currentStreaks[0]);
        float numberWidth = Util.getTextWidth(numberPaint, currentStreaks[0]);
        float titleWidth = Util.getTextWidth(titlePaint, Util.getString(R.string.current_streak));
        float remarkHeight = Util.getTextHeight(remarkPaint, currentStreaks[1]);
        canvas.drawText(Util.getString(R.string.current_streak),
                paddingLeft,
                maxY - numberHeight - titleAndNumberPadding,
                titlePaint);
        canvas.drawText(currentStreaks[0],
                paddingLeft + titleWidth - numberWidth,
                maxY,
                numberPaint);
        canvas.drawText(currentStreaks[1],
                paddingLeft + titleWidth + numberAndRemarkPadding,
                maxY,
                remarkPaint);
        canvas.drawText(currentStreaks[2],
                paddingLeft + titleWidth + numberAndRemarkPadding,
                maxY - remarkHeight - unitAndRemarkPadding,
                unitPaint);

        // draw longest streak text
        String[] longestStreaks = Util.getLongestStreak(contributions);
        numberHeight = Util.getTextHeight(numberPaint, longestStreaks[0]);
        numberWidth = Util.getTextWidth(numberPaint, longestStreaks[0]);
        titleWidth = Util.getTextWidth(titlePaint, Util.getString(R.string.longest_streak));
        remarkHeight = Util.getTextHeight(remarkPaint, longestStreaks[1]);
        canvas.drawText(Util.getString(R.string.longest_streak),
                paddingLeft,
                maxY - 2 * numberHeight - 2 * titleAndNumberPadding - partPadding,
                titlePaint);
        canvas.drawText(longestStreaks[0],
                paddingLeft + titleWidth - numberWidth,
                maxY - numberHeight - titleAndNumberPadding - partPadding,
                numberPaint);
        canvas.drawText(longestStreaks[1],
                paddingLeft + titleWidth + numberAndRemarkPadding,
                maxY - numberHeight - titleAndNumberPadding - partPadding,
                remarkPaint);
        canvas.drawText(longestStreaks[2],
                paddingLeft + titleWidth + numberAndRemarkPadding,
                maxY - remarkHeight - unitAndRemarkPadding
                        - numberHeight - titleAndNumberPadding - partPadding,
                unitPaint);

        // draw total text
        String[] totalStreaks = Util.getOneYearTotal(contributions);
        String[] busiestStreaks = Util.getBusiestDay(contributions);
        numberHeight = Util.getTextHeight(numberPaint, totalStreaks[0]);
        numberWidth = Util.getTextWidth(numberPaint, totalStreaks[0]);
        int titleHeight = Util.getTextHeight(titlePaint, Util.getString(R.string.one_year_total));
        titleWidth = Util.getTextWidth(titlePaint, Util.getString(R.string.one_year_total));
        remarkHeight = Util.getTextHeight(remarkPaint, totalStreaks[1]);
        int unitAndRemarkWidth = Util.getTextWidth(remarkPaint, totalStreaks[1]);
        unitAndRemarkWidth
                = Math.max(unitAndRemarkWidth, Util.getTextWidth(unitPaint, totalStreaks[2]));
        unitAndRemarkWidth
                = Math.max(unitAndRemarkWidth, Util.getTextWidth(remarkPaint, busiestStreaks[1]));
        unitAndRemarkWidth
                = Math.max(unitAndRemarkWidth, Util.getTextWidth(unitPaint, busiestStreaks[2]));
        canvas.drawText(Util.getString(R.string.one_year_total),
                bitmapWidth - paddingRight - unitAndRemarkWidth
                        - numberAndRemarkPadding - titleWidth - avatarWidth,
                paddingTop + titleHeight,
                titlePaint);
        canvas.drawText(totalStreaks[0],
                bitmapWidth - paddingRight - unitAndRemarkWidth
                        - numberAndRemarkPadding - numberWidth - avatarWidth,
                paddingTop + titleHeight + titleAndNumberPadding + numberHeight,
                numberPaint);
        canvas.drawText(totalStreaks[1],
                bitmapWidth - paddingRight - unitAndRemarkWidth - avatarWidth,
                paddingTop + titleHeight + titleAndNumberPadding + numberHeight,
                remarkPaint);
        canvas.drawText(totalStreaks[2],
                bitmapWidth - paddingRight - unitAndRemarkWidth - avatarWidth,
                paddingTop + titleHeight + titleAndNumberPadding + numberHeight
                        - remarkHeight - unitAndRemarkPadding,
                remarkPaint);
        
        // draw busiest day
        numberHeight = Util.getTextHeight(numberPaint, busiestStreaks[0]);
        numberWidth = Util.getTextWidth(numberPaint, busiestStreaks[0]);
        titleHeight = Util.getTextHeight(titlePaint, Util.getString(R.string.busiest_day));
        titleWidth = Util.getTextWidth(titlePaint, Util.getString(R.string.busiest_day));
        remarkHeight = Util.getTextHeight(remarkPaint, busiestStreaks[1]);
        canvas.drawText(Util.getString(R.string.busiest_day),
                bitmapWidth - paddingRight - unitAndRemarkWidth
                        - numberAndRemarkPadding - titleWidth - avatarWidth,
                paddingTop + titleHeight + titleAndNumberPadding + numberHeight + partPadding,
                titlePaint);
        canvas.drawText(busiestStreaks[0],
                bitmapWidth - paddingRight - unitAndRemarkWidth
                        - numberAndRemarkPadding - numberWidth - avatarWidth,
                paddingTop + titleHeight + 2 * titleAndNumberPadding
                        + 2 * numberHeight + partPadding,
                numberPaint);
        canvas.drawText(busiestStreaks[1],
                bitmapWidth - paddingRight - unitAndRemarkWidth - avatarWidth,
                paddingTop + titleHeight + 2 * titleAndNumberPadding
                        + 2 * numberHeight + partPadding,
                remarkPaint);
        canvas.drawText(busiestStreaks[2],
                bitmapWidth - paddingRight - unitAndRemarkWidth - avatarWidth,
                paddingTop + titleHeight + 2 * titleAndNumberPadding + 2 * numberHeight
                        - remarkHeight - unitAndRemarkPadding + partPadding,
                remarkPaint);

        return bitmap;
    }

    private ArrayList<Day> getTestData() {
        return Util.getContributionsFromString(TEST_DATA);
    }

    private final static String TEST_DATA = "\n" +
            "<svg width=\"721\" height=\"110\" class=\"js-calendar-graph-svg\">\n" +
            "  <g transform=\"translate(20, 20)\">\n" +
            "      <g transform=\"translate(0, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#1e6823\" data-count=\"46\" data-date=\"2015-04-26\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-04-27\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-04-28\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"11\" data-date=\"2015-04-29\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-04-30\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"12\" data-date=\"2015-05-01\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-05-02\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(13, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-05-03\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-05-04\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-05-05\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-05-06\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2015-05-07\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-05-08\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-05-09\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(26, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-05-10\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#44a340\" data-count=\"23\" data-date=\"2015-05-11\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-05-12\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-05-13\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-05-14\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-05-15\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-05-16\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(39, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-05-17\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-05-18\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-05-19\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-05-20\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-05-21\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-05-22\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-05-23\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(52, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-05-24\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"21\" data-date=\"2015-05-25\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-05-26\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-05-27\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-05-28\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-05-29\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-05-30\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(65, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-05-31\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-06-01\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-06-02\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"16\" data-date=\"2015-06-03\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2015-06-04\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-06-05\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-06-06\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(78, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-06-07\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2015-06-08\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#8cc665\" data-count=\"20\" data-date=\"2015-06-09\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-06-10\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-06-11\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-06-12\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-06-13\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(91, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-06-14\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"13\" data-date=\"2015-06-15\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#8cc665\" data-count=\"15\" data-date=\"2015-06-16\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#44a340\" data-count=\"23\" data-date=\"2015-06-17\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2015-06-18\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"17\" data-date=\"2015-06-19\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-06-20\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(104, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#8cc665\" data-count=\"11\" data-date=\"2015-06-21\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-06-22\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-06-23\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-06-24\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-06-25\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-06-26\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#1e6823\" data-count=\"34\" data-date=\"2015-06-27\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(117, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#8cc665\" data-count=\"20\" data-date=\"2015-06-28\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-06-29\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#1e6823\" data-count=\"33\" data-date=\"2015-06-30\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"12\" data-date=\"2015-07-01\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-07-02\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-07-03\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-07-04\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(130, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#8cc665\" data-count=\"18\" data-date=\"2015-07-05\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#44a340\" data-count=\"26\" data-date=\"2015-07-06\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-07-07\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2015-07-08\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#8cc665\" data-count=\"14\" data-date=\"2015-07-09\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-07-10\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-07-11\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(143, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-07-12\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-07-13\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#44a340\" data-count=\"24\" data-date=\"2015-07-14\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"16\" data-date=\"2015-07-15\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-07-16\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-07-17\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2015-07-18\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(156, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-07-19\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-07-20\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2015-07-21\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-07-22\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#8cc665\" data-count=\"20\" data-date=\"2015-07-23\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"12\" data-date=\"2015-07-24\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2015-07-25\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(169, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-07-26\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-07-27\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-07-28\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-07-29\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-07-30\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-07-31\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-08-01\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(182, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#1e6823\" data-count=\"45\" data-date=\"2015-08-02\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"18\" data-date=\"2015-08-03\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#44a340\" data-count=\"23\" data-date=\"2015-08-04\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#1e6823\" data-count=\"33\" data-date=\"2015-08-05\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2015-08-06\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2015-08-07\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#8cc665\" data-count=\"14\" data-date=\"2015-08-08\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(195, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#1e6823\" data-count=\"43\" data-date=\"2015-08-09\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"13\" data-date=\"2015-08-10\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-08-11\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-08-12\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-08-13\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"20\" data-date=\"2015-08-14\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#8cc665\" data-count=\"11\" data-date=\"2015-08-15\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(208, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-08-16\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-08-17\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-08-18\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"11\" data-date=\"2015-08-19\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-08-20\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"16\" data-date=\"2015-08-21\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#44a340\" data-count=\"27\" data-date=\"2015-08-22\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(221, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#44a340\" data-count=\"22\" data-date=\"2015-08-23\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2015-08-24\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-08-25\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-08-26\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-08-27\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-08-28\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2015-08-29\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(234, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#44a340\" data-count=\"22\" data-date=\"2015-08-30\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"21\" data-date=\"2015-08-31\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-09-01\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#44a340\" data-count=\"22\" data-date=\"2015-09-02\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2015-09-03\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-09-04\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-09-05\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(247, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-09-06\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2015-09-07\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#44a340\" data-count=\"28\" data-date=\"2015-09-08\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"15\" data-date=\"2015-09-09\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-09-10\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-09-11\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-09-12\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(260, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#8cc665\" data-count=\"16\" data-date=\"2015-09-13\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-09-14\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-09-15\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2015-09-16\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-09-17\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"15\" data-date=\"2015-09-18\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#44a340\" data-count=\"22\" data-date=\"2015-09-19\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(273, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#1e6823\" data-count=\"33\" data-date=\"2015-09-20\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"12\" data-date=\"2015-09-21\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2015-09-22\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-09-23\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-09-24\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"17\" data-date=\"2015-09-25\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#44a340\" data-count=\"28\" data-date=\"2015-09-26\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(286, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#44a340\" data-count=\"30\" data-date=\"2015-09-27\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"19\" data-date=\"2015-09-28\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2015-09-29\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"21\" data-date=\"2015-09-30\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#8cc665\" data-count=\"15\" data-date=\"2015-10-01\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"14\" data-date=\"2015-10-02\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-10-03\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(299, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-10-04\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-10-05\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#8cc665\" data-count=\"19\" data-date=\"2015-10-06\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#1e6823\" data-count=\"40\" data-date=\"2015-10-07\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#8cc665\" data-count=\"12\" data-date=\"2015-10-08\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#1e6823\" data-count=\"35\" data-date=\"2015-10-09\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#44a340\" data-count=\"23\" data-date=\"2015-10-10\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(312, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-10-11\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-10-12\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-10-13\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-10-14\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-10-15\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#44a340\" data-count=\"23\" data-date=\"2015-10-16\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2015-10-17\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(325, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-10-18\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"16\" data-date=\"2015-10-19\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#8cc665\" data-count=\"16\" data-date=\"2015-10-20\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-10-21\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#1e6823\" data-count=\"42\" data-date=\"2015-10-22\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-10-23\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-10-24\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(338, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-10-25\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-10-26\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-10-27\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-10-28\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-10-29\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-10-30\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-10-31\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(351, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#8cc665\" data-count=\"13\" data-date=\"2015-11-01\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-11-02\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-11-03\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-11-04\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-11-05\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-11-06\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-11-07\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(364, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-11-08\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-11-09\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-11-10\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"14\" data-date=\"2015-11-11\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-11-12\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"12\" data-date=\"2015-11-13\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2015-11-14\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(377, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-11-15\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"20\" data-date=\"2015-11-16\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-11-17\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-11-18\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2015-11-19\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"11\" data-date=\"2015-11-20\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-11-21\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(390, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2015-11-22\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"11\" data-date=\"2015-11-23\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#44a340\" data-count=\"23\" data-date=\"2015-11-24\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-11-25\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-11-26\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2015-11-27\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-11-28\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(403, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-11-29\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-11-30\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-12-01\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#44a340\" data-count=\"27\" data-date=\"2015-12-02\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-12-03\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2015-12-04\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-12-05\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(416, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-12-06\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2015-12-07\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2015-12-08\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#44a340\" data-count=\"26\" data-date=\"2015-12-09\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#8cc665\" data-count=\"14\" data-date=\"2015-12-10\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-12-11\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-12-12\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(429, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#8cc665\" data-count=\"13\" data-date=\"2015-12-13\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#44a340\" data-count=\"23\" data-date=\"2015-12-14\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#8cc665\" data-count=\"16\" data-date=\"2015-12-15\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-12-16\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#8cc665\" data-count=\"15\" data-date=\"2015-12-17\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-12-18\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2015-12-19\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(442, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2015-12-20\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#44a340\" data-count=\"27\" data-date=\"2015-12-21\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#44a340\" data-count=\"28\" data-date=\"2015-12-22\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2015-12-23\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2015-12-24\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"14\" data-date=\"2015-12-25\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#8cc665\" data-count=\"14\" data-date=\"2015-12-26\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(455, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#44a340\" data-count=\"25\" data-date=\"2015-12-27\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#44a340\" data-count=\"23\" data-date=\"2015-12-28\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#44a340\" data-count=\"28\" data-date=\"2015-12-29\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"17\" data-date=\"2015-12-30\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2015-12-31\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2016-01-01\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#44a340\" data-count=\"32\" data-date=\"2016-01-02\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(468, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#8cc665\" data-count=\"12\" data-date=\"2016-01-03\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#44a340\" data-count=\"26\" data-date=\"2016-01-04\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#44a340\" data-count=\"25\" data-date=\"2016-01-05\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2016-01-06\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2016-01-07\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2016-01-08\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2016-01-09\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(481, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2016-01-10\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2016-01-11\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-01-12\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2016-01-13\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2016-01-14\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"11\" data-date=\"2016-01-15\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2016-01-16\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(494, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2016-01-17\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2016-01-18\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2016-01-19\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2016-01-20\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2016-01-21\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-01-22\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2016-01-23\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(507, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2016-01-24\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"19\" data-date=\"2016-01-25\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#44a340\" data-count=\"24\" data-date=\"2016-01-26\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"21\" data-date=\"2016-01-27\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#8cc665\" data-count=\"16\" data-date=\"2016-01-28\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"11\" data-date=\"2016-01-29\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2016-01-30\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(520, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2016-01-31\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2016-02-01\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2016-02-02\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"15\" data-date=\"2016-02-03\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#44a340\" data-count=\"32\" data-date=\"2016-02-04\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2016-02-05\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2016-02-06\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(533, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2016-02-07\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"14\" data-date=\"2016-02-08\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#44a340\" data-count=\"27\" data-date=\"2016-02-09\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2016-02-10\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#1e6823\" data-count=\"33\" data-date=\"2016-02-11\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#1e6823\" data-count=\"46\" data-date=\"2016-02-12\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2016-02-13\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(546, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-02-14\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2016-02-15\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#44a340\" data-count=\"25\" data-date=\"2016-02-16\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2016-02-17\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2016-02-18\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"11\" data-date=\"2016-02-19\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#44a340\" data-count=\"27\" data-date=\"2016-02-20\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(559, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2016-02-21\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2016-02-22\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2016-02-23\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2016-02-24\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2016-02-25\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2016-02-26\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2016-02-27\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(572, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#8cc665\" data-count=\"13\" data-date=\"2016-02-28\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"17\" data-date=\"2016-02-29\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#44a340\" data-count=\"29\" data-date=\"2016-03-01\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2016-03-02\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2016-03-03\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-03-04\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-03-05\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(585, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2016-03-06\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"19\" data-date=\"2016-03-07\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2016-03-08\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"19\" data-date=\"2016-03-09\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2016-03-10\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2016-03-11\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2016-03-12\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(598, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-03-13\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"13\" data-date=\"2016-03-14\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2016-03-15\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"11\" data-date=\"2016-03-16\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2016-03-17\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2016-03-18\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-03-19\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(611, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2016-03-20\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2016-03-21\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2016-03-22\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2016-03-23\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-03-24\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-03-25\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2016-03-26\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(624, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"2\" data-date=\"2016-03-27\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2016-03-28\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2016-03-29\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#8cc665\" data-count=\"18\" data-date=\"2016-03-30\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#8cc665\" data-count=\"13\" data-date=\"2016-03-31\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2016-04-01\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"7\" data-date=\"2016-04-02\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(637, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#8cc665\" data-count=\"14\" data-date=\"2016-04-03\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"13\" data-date=\"2016-04-04\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2016-04-05\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2016-04-06\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-04-07\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"5\" data-date=\"2016-04-08\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2016-04-09\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(650, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"9\" data-date=\"2016-04-10\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-04-11\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2016-04-12\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"10\" data-date=\"2016-04-13\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#8cc665\" data-count=\"19\" data-date=\"2016-04-14\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2016-04-15\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#d6e685\" data-count=\"1\" data-date=\"2016-04-16\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(663, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#eeeeee\" data-count=\"0\" data-date=\"2016-04-17\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#d6e685\" data-count=\"3\" data-date=\"2016-04-18\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"4\" data-date=\"2016-04-19\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"39\" fill=\"#d6e685\" data-count=\"8\" data-date=\"2016-04-20\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"52\" fill=\"#8cc665\" data-count=\"11\" data-date=\"2016-04-21\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"65\" fill=\"#8cc665\" data-count=\"12\" data-date=\"2016-04-22\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"78\" fill=\"#44a340\" data-count=\"29\" data-date=\"2016-04-23\"/>\n" +
            "      </g>\n" +
            "      <g transform=\"translate(676, 0)\">\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"0\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2016-04-24\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"13\" fill=\"#8cc665\" data-count=\"14\" data-date=\"2016-04-25\"/>\n" +
            "          <rect class=\"day\" width=\"11\" height=\"11\" y=\"26\" fill=\"#d6e685\" data-count=\"6\" data-date=\"2016-04-26\"/>\n" +
            "      </g>\n" +
            "      <text x=\"13\" y=\"-5\" class=\"month\">May</text>\n" +
            "      <text x=\"78\" y=\"-5\" class=\"month\">Jun</text>\n" +
            "      <text x=\"130\" y=\"-5\" class=\"month\">Jul</text>\n" +
            "      <text x=\"182\" y=\"-5\" class=\"month\">Aug</text>\n" +
            "      <text x=\"247\" y=\"-5\" class=\"month\">Sep</text>\n" +
            "      <text x=\"299\" y=\"-5\" class=\"month\">Oct</text>\n" +
            "      <text x=\"351\" y=\"-5\" class=\"month\">Nov</text>\n" +
            "      <text x=\"416\" y=\"-5\" class=\"month\">Dec</text>\n" +
            "      <text x=\"468\" y=\"-5\" class=\"month\">Jan</text>\n" +
            "      <text x=\"533\" y=\"-5\" class=\"month\">Feb</text>\n" +
            "      <text x=\"585\" y=\"-5\" class=\"month\">Mar</text>\n" +
            "      <text x=\"637\" y=\"-5\" class=\"month\">Apr</text>\n" +
            "    <text text-anchor=\"middle\" class=\"wday\" dx=\"-10\" dy=\"9\" style=\"display: none;\">S</text>\n" +
            "    <text text-anchor=\"middle\" class=\"wday\" dx=\"-10\" dy=\"22\">M</text>\n" +
            "    <text text-anchor=\"middle\" class=\"wday\" dx=\"-10\" dy=\"35\" style=\"display: none;\">T</text>\n" +
            "    <text text-anchor=\"middle\" class=\"wday\" dx=\"-10\" dy=\"48\">W</text>\n" +
            "    <text text-anchor=\"middle\" class=\"wday\" dx=\"-10\" dy=\"61\" style=\"display: none;\">T</text>\n" +
            "    <text text-anchor=\"middle\" class=\"wday\" dx=\"-10\" dy=\"74\">F</text>\n" +
            "    <text text-anchor=\"middle\" class=\"wday\" dx=\"-10\" dy=\"87\" style=\"display: none;\">S</text>\n" +
            "  </g>\n" +
            "</svg>\n";
}