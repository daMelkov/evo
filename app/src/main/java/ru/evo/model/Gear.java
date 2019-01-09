package ru.evo.model;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class Gear {
    private float _m; // модуль зубчатого колеса
    //private float _r1; // радиус впадин зубьев
    private float _r2; // радиус вершин зубьев
    private float _rd; // радиус делительной окружности
    private float _h1; // высота ножки зуба
    private float _h2; // высота головки зуба
    private int _z;  // число зубьев

    private Point mZeroPoint; // точка привязки колеса
    private Collection<Tooth> mTooths; // зубья

    public Gear(int z, float r2) {
        _z = z;
        _r2 = r2;
        _m = 2f * _r2/(_z + 2f);
        _h1 = 1.25f*_m;
        _h2 = _m;
        //_r1 = _rd - _h1;
        _rd = _r2 - _h2;

        mZeroPoint = new Point(0,0);

        mTooths = new ArrayList<>();
        for(int i=1; i<=_z; i++){
            float toothAngle = (float) (2*Math.PI/_z*i); // угол поворота зуба согласно его положению на зубчатом колесе

            Tooth tooth = new Tooth(toothAngle);
            mTooths.add(tooth);
        }
    }

    public void placeTo(Point newZeroPoint){
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

    class Tooth {
        private Collection<Point> mPoints; // точки, формирующие контур зуба
        Collection<Point> getPoints() {
            return mPoints;
        }

        //phi -  угол поворота зуба в зубчатом колесе
        Tooth(float phi){
            mPoints = new ArrayList<>();

            float tau;
            float xi;

            /* построение эвольвенты зуба */
            //R - радиус точки на эвольвенте. Изменяется от делительного радиуса до радиуса вершин зубьев
            for(float R = _rd; R <= _r2; R += _h1*0.1f) {
                tau = (float) sqrt((R*R - _rd*_rd)/(_rd*_rd));
                xi = (float) (Math.PI/2 - tau + acos(_rd/R));

                int xm = (int) (R * cos(xi));
                int ym = (int) (R * sin(xi));
                mPoints.add(new Point(xm, ym));
            }

            /* угол поворота зуба после построения его эвольвенты */
            float beta = (float) ((Math.PI * _m) / (2 * _rd));

            /* поворот зуба против часовой стрелки на половину его ширины, чтобы он был направлен строго по оси y */
            rotate(beta/2);

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
            ArrayList newPoints = new ArrayList();

            for(Point point: mPoints){
                Point newPoint = new Point(-point.x, point.y);
                newPoints.add(newPoint);
            }

            mPoints.addAll(newPoints);
        }
    }
}
