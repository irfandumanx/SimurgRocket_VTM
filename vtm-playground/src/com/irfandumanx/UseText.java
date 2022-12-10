package com.irfandumanx;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Color;
import org.oscim.backend.canvas.Paint;
import org.oscim.core.MapPosition;
import org.oscim.map.Map;

import java.util.Random;

public class UseText extends Text {
    private static final int BITMAP_HEIGHT = 80;
    private static final int BITMAP_WIDTH = 650;
    private static final Random random = new Random();

    private final Paint paintScaleText;
    private final Paint paintScaleTextStroke;

    public UseText(Map map) {
        this(map, CanvasAdapter.getScale());
    }

    public UseText(Map map, float scale) {
        super(map, (int) (BITMAP_WIDTH * scale), (int) (BITMAP_HEIGHT * scale), scale);

        this.paintScaleText = createTextPaint(Color.BLACK, 0, Paint.Style.FILL);
        this.paintScaleTextStroke = createTextPaint(Color.WHITE, 2, Paint.Style.STROKE);
    }

    public void setColor(int color) {
        this.paintScaleText.setColor(color);
    }

    private Paint createTextPaint(int color, float strokeWidth, Paint.Style style) {
        Paint paint = CanvasAdapter.newPaint();
        paint.setColor(color);
        paint.setStrokeWidth(strokeWidth * this.scale);
        paint.setStyle(style);
        paint.setTypeface(Paint.FontFamily.DEFAULT, Paint.FontStyle.BOLD);
        paint.setTextSize(16 * this.scale);
        return paint;
    }

    @Override
    protected void redraw(Canvas canvas, MapPosition position) {
        canvas.fillColor(Color.TRANSPARENT);
        StringBuilder sb = new StringBuilder();

        String latitude = "" + position.getLatitude();
        String longitude = "   " + position.getLongitude();

        sb.append(latitude).append(longitude);
        setColor(Color.GRAY);
        drawScaleText(sb.toString(), this.paintScaleText, this.scale);
    }

    private void drawScaleText(String scaleText1, Paint paint, float scale) {
        this.mapScaleCanvas.drawText(scaleText1, 100f, 20f, paint);
    }

}
