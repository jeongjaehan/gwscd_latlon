package com.kthcorp.geo.giscore.coord;

/*
enum GeoEllips {kBessel1984 = 0, kWgs84 = 1};
enum GeoSystem {kGeographic = 0, kTmWest = 1, kTmMid = 2, kTmEast = 3, kKatec = 4, kUtm52 = 5, kUtm51 = 6};
*/
public class CoordConversion
{
    /*
     * 좌표값에 *10을 한 정수값 사용.(TM Katech 기본)
     */
    public static final int INTEGERCOORDVALUE = 10;

    public static double PI = 3.14159265358979;
    public static double EPSLN = 0.0000000001;
    public static double S2R = 4.84813681109536E-06;
    public static int X_W2B = 128;
    public static int Y_W2B = -481;
    public static int Z_W2B = -664;

    public CoordinateReferenceSystem m_srcCrs = null;
    public CoordinateReferenceSystem m_dstCrs = null;

    static final long max_iter = 6; // maximun number of iterations

// Internal Value for Tm2Geo
    double m_dSrcE0, m_dSrcE1, m_dSrcE2, m_dSrcE3;
    double m_dSrcE, m_dSrcEs, m_dSrcEsp;
    double m_dSrcMl0, m_dSrcInd;

// Internal Value for Geo2Tm
    double m_dDstE0, m_dDstE1, m_dDstE2, m_dDstE3;
    double m_dDstE, m_dDstEs, m_dDstEsp;
    double m_dDstMl0, m_dDstInd;

// Internal Value for DatumTrans
    double m_dTemp;
    double m_dEsTemp;
    int m_iDeltaX;
    int m_iDeltaY;
    int m_iDeltaZ;
    double m_dDeltaA, m_dDeltaF;

// 경위도 좌표의 초단위 범위
    public static final int GEO_SEC_MINX = 450000;
    public static final int GEO_SEC_MAXX = 464400;
    public static final int GEO_SEC_MINY = 115200;
    public static final int GEO_SEC_MAXY = 136800;

    // 경위도 좌표의 도분초  범위
    public static final int GEO_DMS_MINX = 1250000;
    public static final int GEO_DMS_MAXX = 1290000;
    public static final int GEO_DMS_MINY = 320000;
    public static final int GEO_DMS_MAXY = 380000;

    public CoordConversion()
    {
    }

    public void setSrcCRS(CoordinateReferenceSystem crs)
    {
        this.m_srcCrs = crs;

        double temp = m_srcCrs.m_Minor / m_srcCrs.m_Major;
        m_dSrcEs = 1.0 - temp * temp;
        m_dSrcE = Math.sqrt(m_dSrcEs);
        m_dSrcE0 = e0fn(m_dSrcEs);
        m_dSrcE1 = e1fn(m_dSrcEs);
        m_dSrcE2 = e2fn(m_dSrcEs);
        m_dSrcE3 = e3fn(m_dSrcEs);
        m_dSrcMl0 = m_srcCrs.m_Major *
            mlfn(m_dSrcE0, m_dSrcE1, m_dSrcE2, m_dSrcE3,
                 m_srcCrs.m_LatCenter);
        m_dSrcEsp = m_dSrcEs / (1.0 - m_dSrcEs);

        if (m_dSrcEs < 0.00001)
            m_dSrcInd = 1.0;
        else
            m_dSrcInd = 0.0;

//        InitDatumVar();
    }

    public void setDstCRS(CoordinateReferenceSystem crs)
    {
        this.m_dstCrs = crs;

        double temp = m_dstCrs.m_Minor / m_dstCrs.m_Major;
        m_dDstEs = 1.0 - temp * temp;
        m_dDstE = Math.sqrt(m_dDstEs);
        m_dDstE0 = e0fn(m_dDstEs);
        m_dDstE1 = e1fn(m_dDstEs);
        m_dDstE2 = e2fn(m_dDstEs);
        m_dDstE3 = e3fn(m_dDstEs);
        m_dDstMl0 = m_dstCrs.m_Major *
            mlfn(m_dDstE0, m_dDstE1, m_dDstE2, m_dDstE3,
                 m_dstCrs.m_LatCenter);
        m_dDstEsp = m_dDstEs / (1.0 - m_dDstEs);

        if (m_dDstEs < 0.00001)
            m_dDstInd = 1.0;
        else
            m_dDstInd = 0.0;

//        InitDatumVar();
    }

    public void InitDatumVar()
    {
        int iDefFact;
        double dF;

        // direction factor for datum transformation
        // eg) Bessel to Bessel would be 0
        //     WGS84  to Bessel would be 1
        //     BEssel to WGS84 would be -1
        iDefFact = m_srcCrs.m_eEllips - m_dstCrs.m_eEllips;
        m_iDeltaX = iDefFact * X_W2B;
        m_iDeltaY = iDefFact * Y_W2B;
        m_iDeltaZ = iDefFact * Z_W2B;

        m_dTemp = m_srcCrs.m_Minor / m_srcCrs.m_Major;

        dF = 1.0 - m_dTemp; // flattening
        m_dEsTemp = 1.0 - m_dTemp * m_dTemp; // e2

        m_dDeltaA = m_dstCrs.m_Major - m_srcCrs.m_Major; // output major axis - input major axis
        m_dDeltaF = m_srcCrs.m_Minor / m_srcCrs.m_Major -
            m_dstCrs.m_Minor / m_dstCrs.m_Major; // Output Flattening - input flattening
    }

    /**
     *
     * @param _in : "도"단위의 경위도 좌표 또는 double형태의 TM/UTM좌표
     * @return "도"단위 경위도 또는 double형태의 TM/UTM좌표
     */
    public DPoint Conv(DPoint _in)
    {
        double dInLon, dInLat;
        double dOutLon, dOutLat;
        double dTmX, dTmY;

        DPoint _out = new DPoint();
        DPoint din = new DPoint();
        DPoint dout = new DPoint();
        DPoint tm = new DPoint();

        if (m_srcCrs.m_eSystem == GeoSystem.kGeographic)
        {
            din.x = D2R(_in.x);
            din.y = D2R(_in.y);
        }
        else
        {
            // Geographic calculating
            // Tm2Geo(dInX, dInY, dInLon, dInLat);
            Tm2Geo(_in.x, _in.y, din);
        }

//        if (m_eSrcEllips == m_eDstEllips)
        if (m_srcCrs.m_eEllips == m_dstCrs.m_eEllips)
        {
            //dOutLon = dInLon;
            //dOutLat = dInLat;
            dout.x = din.x;
            dout.y = din.y;
        }
        else
        {
            // Datum transformation using molodensky function
            //DatumTrans(dInLon, dInLat, dOutLon, dOutLat);
            DatumTrans(din.x, din.y, dout);
        }

        // now we should make a output. but it depends on user options
        // if output option is latitude & longitude
//        if (m_eDstSystem == GeoSystem.kGeographic)
        if (m_dstCrs.m_eSystem == GeoSystem.kGeographic)
        {
            _out.x = R2D(dout.x);
            _out.y = R2D(dout.y);
        }
        else
        {
            // TM or UTM calculating
            Geo2Tm(dout.x, dout.y, tm);
            _out.x = tm.x;
            _out.y = tm.y;
        }
        return _out;
    }

    protected double D2R(double degree)
    {
        return (degree * Math.PI / 180.0);
    }

    protected double R2D(double radian)
    {
        return (radian * 180.0 / Math.PI);
    }

    double e0fn(double x)
    {
        return 1.0 - 0.25 * x * (1.0 + x / 16.0 * (3.0 + 1.25 * x));
    }

    double e1fn(double x)
    {
        return 0.375 * x * (1.0 + 0.25 * x * (1.0 + 0.46875 * x));
    }

    double e2fn(double x)
    {
        return 0.05859375 * x * x * (1.0 + 0.75 * x);
    }

    double e3fn(double x)
    {
        return x * x * x * (35.0 / 3072.0);
    }

    double e4fn(double x)
    {
        double con, com;

        con = 1.0 + x;
        com = 1.0 - x;
        return Math.sqrt(Math.pow(con, con) * Math.pow(com, com));
    }

    double mlfn(double e0, double e1, double e2, double e3, double phi)
    {
        return e0 * phi - e1 * Math.sin(2.0 * phi) + e2 * Math.sin(4.0 * phi) -
            e3 * Math.sin(6.0 * phi);
    }

    double asinz(double value)
    {
        if (Math.abs(value) > 1.0)
            value = (value > 0 ? 1 : -1);

        return Math.sin(value);
    }

    protected void Tm2Geo(double x, double y, DPoint _point)
    {
        double lon, lat;
        double con; // temporary angles
        double phi; // temporary angles
        double delta_Phi; // difference between longitudes
        long i; // counter variable
        double sin_phi, cos_phi, tan_phi; // sin cos and tangent values
        double c, cs, t, ts, n, r, d, ds; // temporary variables
        double f, h, g, temp; // temporary variables

        if (m_dSrcInd != 0)
        {
            f = Math.exp(x /
                         (m_srcCrs.m_Major * m_srcCrs.m_ScaleFactor));
            g = 0.5 * (f - 1.0 / f);
            temp = m_srcCrs.m_LatCenter +
                y / (m_srcCrs.m_Major * m_srcCrs.m_ScaleFactor);
            h = Math.cos(temp);
            con = Math.sqrt( (1.0 - h * h) / (1.0 + g * g));
            lat = asinz(con); //lat = asinz(con);

            if (temp < 0)
                lat *= -1;

            if ( (g == 0) && (h == 0))
                lon = m_srcCrs.m_LonCenter;
            else
                lon = Math.atan(g / h) + m_srcCrs.m_LonCenter;
        }

        // TM to LL inverse equations from here

        x -= m_srcCrs.m_FalseEasting;
        y -= m_srcCrs.m_FalseNorthing;

        con = (m_dSrcMl0 + y / m_srcCrs.m_ScaleFactor) /
            m_srcCrs.m_Major;
        phi = con;

        i = 0;
        while (true)
        {
            delta_Phi = ( (con + m_dSrcE1 * Math.sin(2.0 * phi) -
                           m_dSrcE2 * Math.sin(4.0 * phi) +
                           m_dSrcE3 * Math.sin(6.0 * phi)) / m_dSrcE0) - phi;
            phi = phi + delta_Phi;
            if (Math.abs(delta_Phi) <= EPSLN)break;

            if (i >= max_iter)
            {
                //System.out.println(
                //    "Conversion :: error ::Latitude failed to converge");
                return;
            }
            i++;
        }

        if (Math.abs(phi) < (Math.PI / 2))
        {
            sin_phi = Math.sin(phi);
            cos_phi = Math.cos(phi);
            tan_phi = Math.tan(phi);
            c = m_dSrcEsp * cos_phi * cos_phi;
            cs = c * c;
            t = tan_phi * tan_phi;
            ts = t * t;
            con = 1.0 - m_dSrcEs * sin_phi * sin_phi;
            n = m_srcCrs.m_Major / Math.sqrt(con);
            r = n * (1.0 - m_dSrcEs) / con;
            d = x / (n * m_srcCrs.m_ScaleFactor);
            ds = d * d;
            lat = phi -
                (n * tan_phi * ds / r) *
                (0.5 -
                 ds / 24.0 *
                 (5.0 + 3.0 * t + 10.0 * c - 4.0 * cs - 9.0 * m_dSrcEsp -
                  ds / 30.0 *
                  (61.0 + 90.0 * t + 298.0 * c + 45.0 * ts - 252.0 * m_dSrcEsp -
                   3.0 * cs)));
            lon = m_srcCrs.m_LonCenter +
                (d *
                 (1.0 -
                  ds / 6.0 *
                  (1.0 + 2.0 * t + c -
                   ds / 20.0 *
                   (5.0 - 2.0 * c + 28.0 * t - 3.0 * cs + 8.0 * m_dSrcEsp +
                    24.0 * ts))) / cos_phi);
        }
        else
        {
            lat = Math.PI * 0.5 * Math.sin(y);
            lon = m_srcCrs.m_LonCenter;
        }
        _point.x = lon;
        _point.y = lat;
    }

    //public void DatumTrans(double dInLon, double dInLat, double dOutLon, double dOutLat){//dOutLon, dOutLat
    protected void DatumTrans(double dInLon, double dInLat, DPoint _dout)
    { //dOutLon, dOutLat
        double dRm, dRn;
        double dDeltaPhi, dDeltaLamda;
        //double dDeltaH;
        dRm = m_srcCrs.m_Major * (1.0 - m_dEsTemp) /
            Math.pow(1.0 - m_dEsTemp * Math.sin(dInLat) * Math.sin(dInLat), 1.5);
        dRn = m_srcCrs.m_Major /
            Math.sqrt(1.0 - m_dEsTemp * Math.sin(dInLat) * Math.sin(dInLat));

        dDeltaPhi = ( ( ( ( -m_iDeltaX * Math.sin(dInLat) * Math.cos(dInLon) -
                           m_iDeltaY * Math.sin(dInLat) * Math.sin(dInLon)) +
                         m_iDeltaZ * Math.cos(dInLat)) +
                       m_dDeltaA * dRn * m_dEsTemp * Math.sin(dInLat) *
                       Math.cos(dInLat) / m_srcCrs.m_Major) +
                     m_dDeltaF * (dRm / m_dTemp + dRn * m_dTemp) *
                     Math.sin(dInLat) * Math.cos(dInLat)) / dRm;
        dDeltaLamda = ( -m_iDeltaX * Math.sin(dInLon) +
                       m_iDeltaY * Math.cos(dInLon)) / (dRn * Math.cos(dInLat));
        //dDeltaH = iDeltaX * cos(dInLat) * cos(dInLon) + iDeltaY * cos(dInLat) * sin(dInLon) + iDeltaZ * sin(dInLat) - dDeltaA * m_arMajor[eSrcEllips] / dRn + dDeltaF * dTemp * dRn * sin(dInLat) * sin(dInLat);
        //dOutLat = dInLat + dDeltaPhi;
        //dOutLon = dInLon + dDeltaLamda;

        _dout.x = dInLon + dDeltaLamda;
        _dout.y = dInLat + dDeltaPhi;
    }

    // public void Geo2Tm(double lon, double lat, double x, double y){//double x, double y 포인터
    protected void Geo2Tm(double lon, double lat, DPoint _point)
    { //double x, double y 포인터
        double delta_lon; // Delta longitude (Given longitude - center longitude)
        double sin_phi, cos_phi; // sin and cos value
        double al, als; // temporary values
        double b, c, t, tq; // temporary values
        double con, n, ml; // cone constant, small m

        // LL to TM Forward equations from here
        delta_lon = lon - m_dstCrs.m_LonCenter;
        sin_phi = Math.sin(lat);
        cos_phi = Math.cos(lat);

        if (m_dDstInd != 0)
        {
            b = cos_phi * Math.sin(delta_lon);
            if ( (Math.abs(Math.abs(b) - 1.0)) < 0.0000000001)
            {
                //System.out.println("Conversion :: error :: 지정하신 점이 무한대로 갑니다");
                return;
            }
        }
        else
        {
            b = 0;
            _point.x = 0.5 * m_dstCrs.m_Major *
                m_dstCrs.m_ScaleFactor * Math.log( (1.0 + b) / (1.0 - b));
            con = Math.acos(cos_phi * Math.cos(delta_lon) /
                            Math.sqrt(1.0 - b * b));
            if (lat < 0)
            {
                con = -con;
                _point.y = m_dstCrs.m_Major *
                    m_dstCrs.m_ScaleFactor *
                    (con - m_dstCrs.m_LatCenter);
            }
        }

        al = cos_phi * delta_lon;
        als = al * al;
        c = m_dDstEsp * cos_phi * cos_phi;
        tq = Math.tan(lat);
        t = tq * tq;
        con = 1.0 - m_dDstEs * sin_phi * sin_phi;
        n = m_dstCrs.m_Major / Math.sqrt(con);
        ml = m_dstCrs.m_Major *
            mlfn(m_dDstE0, m_dDstE1, m_dDstE2, m_dDstE3, lat);

        _point.x = m_dstCrs.m_ScaleFactor * n * al *
            (1.0 +
             als / 6.0 *
             (1.0 - t + c +
              als / 20.0 *
              (5.0 - 18.0 * t + t * t + 72.0 * c - 58.0 * m_dDstEsp))) +
            m_dstCrs.m_FalseEasting;
        _point.y = m_dstCrs.m_ScaleFactor *
            (ml - m_dDstMl0 +
             n * tq *
             (als *
              (0.5 +
               als / 24.0 *
               (5.0 - t + 9.0 * c + 4.0 * c * c +
                als / 30.0 *
                (61.0 - 58.0 * t + t * t + 600.0 * c - 330.0 * m_dDstEsp))))) +
            m_dstCrs.m_FalseNorthing;
    }

    protected void D2Dms(double dInDecimalDegree, int iOutDegree,
                         int iOutMinute, double dOutSecond)
    { //포인터

        double dTmpMinute;

        iOutDegree = (int) dInDecimalDegree;
        dTmpMinute = (dInDecimalDegree - iOutDegree) * 60.0;
        iOutMinute = (int) dTmpMinute;
        dOutSecond = (dTmpMinute - iOutMinute) * 60.0;
        if ( (dOutSecond + 0.00001) >= 60.0)
        {
            if (iOutMinute == 59)
            {
                iOutDegree++;
                iOutMinute = 0;
                dOutSecond = 0.0;
            }
            else
            {
                iOutMinute++;
                dOutSecond = 0.0;
            }
        }
    }


}
