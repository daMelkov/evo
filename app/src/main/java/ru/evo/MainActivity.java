package ru.evo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import java.util.Collection;
import ru.evo.model.Gear;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new DrawView(this));
    }

    static class DrawView extends View {

        private float sizeX;
        private float sizeY;
        private float radius;
        private enum LineStyle {solid, dotted}
        private int OFFSET = 20;

        public DrawView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            sizeX = getWidth();
            sizeY = getHeight();
            radius = (float) (0.4 * (sizeX <= sizeY ? sizeX: sizeY));

            canvas.drawColor(0xfff0f0f0);

            drawAxis(canvas);

            Gear gear = new Gear(12, radius);
            gear.placeTo(new Point((int)sizeX/2, (int)sizeY/2));
            drawGear(canvas, gear);
        }

        private void drawGear(Canvas canvas, Gear gear) {
            Paint paint = getPaint(4, 0xff404040, MainActivity.DrawView.LineStyle.solid);
            //Collection<Point> points = gear.getPoints();


            for(Gear.Tooth tooth: gear.getTooths()){
                Point prevPoint = null;
                for(Point point: tooth.getPoints()) {
                    if (prevPoint != null) {
                        canvas.drawLine(prevPoint.x, prevPoint.y, point.x, point.y, paint);
                    }

                    prevPoint = point;
                }

                //canvas.drawPoint(point.x, point.y, paint);
            }
        }

        private void drawAxis(final Canvas canvas) {
            Paint paint = getPaint(1, 0xfff01010, LineStyle.dotted);
            Path line = new Path();

            line.moveTo(sizeX/2, sizeY/2 - radius - OFFSET);
            line.lineTo(sizeX/2, sizeY/2 + radius + OFFSET);
            canvas.drawPath(line, paint);

            line.moveTo(sizeX/2 - radius - OFFSET, sizeY/2);
            line.lineTo(sizeX/2 + radius + OFFSET, sizeY/2);
            canvas.drawPath(line, paint);
        }

        @NonNull
        private Paint getPaint(final int width, final int color, LineStyle style) {
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(width);
            paint.setColor(color);

            paint.setPathEffect(null);
            if(style == LineStyle.dotted){
                paint.setPathEffect(new DashPathEffect(new float[] {80, 10, 5, 10}, 0));
            }

            return paint;
        }
    }
}
