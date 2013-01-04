package com.kthcorp.geo.giscore.coord;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */
public class GeoEllips {
    public static final int kBessel1984 = 0;
    public static final int kWgs84 = 1;
    public static final int kGrs80 = 2;

    /*
     * 패킷 정의 상수
     */
    public static final String sBESSEL = "bessel";
    public static final String sWGS84 = "WGS84";
    public static final String sGRS80 = "GRS80";

    public static int ellipse_str2int(String _strEllipse)
    {
        int geoellipse = -1;
        if (_strEllipse.compareToIgnoreCase(sWGS84) == 0)
        {
            geoellipse = kWgs84;
        }
        else if(_strEllipse.compareToIgnoreCase(sGRS80) == 0)
        {
            geoellipse = kGrs80;
        }
        else
        {
            geoellipse = kBessel1984;
        }
        return geoellipse;
    }
}
