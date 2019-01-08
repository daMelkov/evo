package ru.evo.model;

import android.graphics.Point;

import java.util.ArrayList;
import java.util.Collection;

import static java.lang.Math.acos;
import static java.lang.Math.cos;
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

        mZeroPoint = new Point();

        mTooths = new ArrayList<>();
        //for(int i=1; i<=_z; i++){
            Tooth tooth = new Tooth(0);
            mTooths.add(tooth);
        //}
    }

    public Collection<Point> getPoints(){
        Collection result = new ArrayList();

        for(Tooth tooth: mTooths){
            result.addAll(tooth.getPoints());
        }

        return result;
    }

    class Tooth {
        private float _phi; // угол поворота зуба
        private float _s; // ширина зуба
        private Collection<Point> mPoints; // точки, формирующие контур зуба

        public Collection<Point> getPoints() {
            return mPoints;
        }

        public Tooth(float phi){
            _phi = phi;
            mPoints = new ArrayList<>();

            float tau;
            float xi;

            _s = (float) (_m * Math.PI/2);
            float beta = _s / _rd;

            float R = _rd;
            while(R <= _r2){
                tau = (float) sqrt((R*R - _rd*_rd)/(_rd*_rd));
                xi = (float) (Math.PI/2 - tau + acos(_rd/R));

                int xm = (int) (R * cos(xi));
                int ym = (int) (R * sin(xi));
                mPoints.add(new Point(xm, ym));

                R = (float) (R + _h1 * 0.1);
            }
        }
    }
}
