package ru.evo.model;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Stack;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class Gear {
    private float _m; // модуль зубчатого колеса
    private float _r1; // радиус впадин зубьев
    private float _r2; // радиус вершин зубьев
    private float _rd; // радиус делительной окружности
    private float _h1; // высота ножки зуба
    private float _h2; // высота головки зуба
    private int _z;  // число зубьев

    private Point mZeroPoint; // точка привязки колеса

    public Collection<Tooth> getTooths() {
        return mTooths;
    }

    private Collection<Tooth> mTooths; // зубья

    public Gear(int z, float r2) {
        _z = z;
        _r2 = r2;
        _m = 2f * _r2/(_z + 2f);
        _h1 = 1.25f*_m;
        _h2 = _m;
        _rd = _r2 - _h2;
        _r1 = _rd - _h1;

        mZeroPoint = new Point(0,0);

        mTooths = new ArrayList<>();
        for(int i=1; i<=_z; i++){
            float toothAngle = (float) (2*Math.PI/_z*i); // угол поворота зуба согласно его положению на зубчатом колесе

            Tooth tooth = new Tooth(toothAngle);
            //Tooth tooth = new Tooth(0);
            mTooths.add(tooth);
        }
    }

    public void moveTo(Point newZeroPoint){
        int dx = newZeroPoint.x - mZeroPoint.x;
        int dy = newZeroPoint.y - mZeroPoint.y;

        for(Tooth tooth: mTooths){
            for(Point point: tooth.getPoints()){
                point.x = point.x + dx;
                point.y = point.y + dy;
            }
        }
    }

    public Collection<Point> getPoints(){
        Collection<Point> result = new ArrayList<>();

        for(Tooth tooth: mTooths){
            result.addAll(tooth.getPoints());
        }

        return result;
    }

    public class Tooth {
        private Collection<Point> mPoints; // точки, формирующие контур зуба
        public Collection<Point> getPoints() {
            return mPoints;
        }

        //phi -  угол поворота зуба в зубчатом колесе
        Tooth(float phi){
            mPoints = new ArrayList<>();

            float tau;
            float xi = 0;

            /* угол поворота оси симметрии зуба: половина ширины дуги делительной окружности, занимаемой зубом. Угол получится после построения эвольвенты */
            float beta = (float) ((Math.PI * _m) / (2 * _rd)) / 2;

            /* впадина зуба (несколько точек на дуге, ограниченной радиусом впадин и углом beta) */
            for(float angle = -beta; angle < 0f; angle += beta/2f){
                int x = (int) (_r1 * sin(angle));
                int y = (int) (_r1 * cos(angle));

                mPoints.add(new Point(x, y));
            }

            /* ножка зуба (одна точка у основания (радиус впадин), т.к. там прямая) */
            mPoints.add(new Point(0, (int) _r1));

            /* головка зуба (эвольвента) */
            //R - радиус точки на эвольвенте. Изменяется от делительного радиуса до радиуса вершин зубьев
            for(float R = _rd; R <= _r2; R += (_r2 - _rd)*0.1f) {
                tau = (float) sqrt((R*R - _rd*_rd)/(_rd*_rd));
                xi = (float) (Math.PI/2 - tau + acos(_rd/R));

                int xm = (int) (R * cos(xi));
                int ym = (int) (R * sin(xi));
                mPoints.add(new Point(xm, ym));
            }

            /* головка зуба (дуга окружности) */
            for(float angle = xi; angle > Math.PI/2 - beta; angle += (Math.PI/2 - beta - xi)*0.5){
                int x = (int) (_r2 * cos(angle));
                int y = (int) (_r2 * sin(angle));

                mPoints.add(new Point(x, y));
            }

            /* поворот зуба против часовой стрелки на половину его ширины, чтобы он был направлен строго по оси y */
            rotate(beta);

            /* зеркальное отражение второй половины зуба относительно оси y */
            mirror();

            /* поворот зуба в соответствии с его положением на зубчатом колесе */
            rotate(phi);
        }

        private void rotate(float alpha){
            for(Point point: mPoints){
                float R = (float) sqrt(pow(point.x - mZeroPoint.x, 2) + pow(point.y - mZeroPoint.y, 2));
                float beta = (float) acos((point.x - mZeroPoint.x)/R);

                point.x = (int) (R * cos(alpha + beta));
                point.y = (int) (R * sin(alpha + beta));
            }
        }

        private void mirror(){
            Stack<Point> newPoints = new Stack();

            for(Point point: mPoints){
                Point newPoint = new Point(-point.x, point.y);
                newPoints.add(newPoint);
            }

            while(!newPoints.empty()){
                mPoints.add(newPoints.pop());
            }
            //mPoints.addAll(newPoints);
        }
    }
}
