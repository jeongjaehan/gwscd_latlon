package com.kthcorp.geo.giscore.coord;

/**
 * <p>Title: </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.0
 */

public class GeoSystem
{
    public final static int kGeographic = 0;
    public final static int kTmWest = 1;
    public final static int kTmMid = 2;
    public final static int kTmEast = 3;
    public final static int kKatec = 4;
    public final static int kUtm52 = 5;
    public final static int kUtm51 = 6;

    /*
     * 패킷 정의 상수
     */
    public final static String sLLBessel = "LL_B";
    public final static String sLLWgs84 = "LL_W";
    public final static String sLLMPC = "LL_M";
    public final static String sTMWest = "TMW";
    public final static String sTMMiddle = "TMM";
    public final static String sTMEast = "TME";
    public final static String sTMKatech = "KAT";
    public final static String sUTM51 = "UTM1";
    public final static String sUTM52 = "UTM2";

    public static int geosys_str2int(String _strGeoSystem)
    {
        int geoSystem = -1;
        if (_strGeoSystem.compareToIgnoreCase(sTMWest) == 0)
        {
            geoSystem = kTmWest;
        }
        else if (_strGeoSystem.compareToIgnoreCase(sTMMiddle) == 0)
        {
            geoSystem = kTmMid;
        }
        else if (_strGeoSystem.compareToIgnoreCase(sTMEast) == 0)
        {
            geoSystem = kTmEast;
        }
        else if (_strGeoSystem.compareToIgnoreCase(sTMKatech) == 0)
        {
            geoSystem = kKatec;
        }
        else if (_strGeoSystem.compareToIgnoreCase(sUTM51) == 0)
        {
            geoSystem = kUtm52;
        }
        else if (_strGeoSystem.compareToIgnoreCase(sUTM52) == 0)
        {
            geoSystem = kUtm51;
        }
        else
        {
            geoSystem = kGeographic;
        }
        return geoSystem;
    }

}
