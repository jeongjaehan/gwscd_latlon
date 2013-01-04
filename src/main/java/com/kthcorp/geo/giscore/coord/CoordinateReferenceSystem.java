package com.kthcorp.geo.giscore.coord;

import java.util.StringTokenizer;

public class CoordinateReferenceSystem
{
    int m_eEllips;
    int m_eSystem;

    // Ellips Factor List
    public double m_Major = 0.0;
    public double m_Minor = 0.0;

    // System Factor List
    public double m_ScaleFactor = 1.0;
    public double m_LonCenter = 0.0;
    public double m_LatCenter = 0.0;
    public double m_FalseNorthing = 0.0;
    public double m_FalseEasting = 0.0;

    public String m_sUnit = null;

    protected String m_sProj = null;
    protected String m_sEllips = null;
    protected String m_sDatum = null;
    protected String m_sZone = null;

    public CoordinateReferenceSystem()
    {
    }

    public int getSystem()
    {
        return m_eSystem;
    }

    public void decode(String code)
    {
        code = code.trim().toLowerCase();

        if(code.startsWith("+proj="))
        {
            decodeProj4(code);
        }
        else
        {
            Proj4Info proj4 = Proj4Info.getInstance();
            String sProj4Info = proj4.getProj4Info(code);
            decodeProj4(sProj4Info);
        }

        initParameter();
    }

    protected void decodeProj4(String code)
    {
        StringTokenizer token = new StringTokenizer(code, " =");

        while(token.hasMoreTokens())
        {
            String sName = token.nextToken();
            if(sName.compareToIgnoreCase("+proj") == 0)
            {
                m_sProj = token.nextToken();
            }
            else if(sName.compareToIgnoreCase("+lat_0") == 0)
            {
                String sValue = token.nextToken();
                m_LatCenter = Double.valueOf(sValue);
                m_LatCenter = D2R(m_LatCenter);
            }
            else if(sName.compareToIgnoreCase("+lon_0") == 0)
            {
                String sValue = token.nextToken();
                m_LonCenter = Double.valueOf(sValue);
                m_LonCenter = D2R(m_LonCenter);
            }
            else if(sName.compareToIgnoreCase("+k") == 0)
            {
                String sValue = token.nextToken();
                m_ScaleFactor = Double.valueOf(sValue);
            }
            else if(sName.compareToIgnoreCase("+x_0") == 0)
            {
                String sValue = token.nextToken();
                m_FalseEasting = Double.valueOf(sValue);
            }
            else if(sName.compareToIgnoreCase("+y_0") == 0)
            {
                String sValue = token.nextToken();
                m_FalseNorthing = Double.valueOf(sValue);
            }
            else if(sName.compareToIgnoreCase("+ellps") == 0)
            {
                m_sEllips = token.nextToken();
                if(m_sEllips.equalsIgnoreCase("grs80"))
                {
                	m_sEllips="wgs84";
                }
            }
            else if(sName.compareToIgnoreCase("+units") == 0)
            {
                m_sUnit = token.nextToken();
            }
            else if(sName.compareToIgnoreCase("+zone") == 0)
            {
                m_sZone = token.nextToken();
            }
            else if(sName.compareToIgnoreCase("+datum") == 0)
            {
                m_sDatum = token.nextToken();
            }
        }


    }

    protected double D2R(double degree)
    {
        return (degree * Math.PI / 180.0);
    }

    protected void initParameter()
    {
        initEllipsParameter();
        //Projection이 UTM 일때 ZONE 정보를 이용하여 Parameter 정보셋팅
        if(m_sProj.compareToIgnoreCase("longlat") == 0)
        {
            m_eSystem = GeoSystem.kGeographic;
        }
        else m_eSystem = 10; // 그냥 GeoSystem.kGeographic 다르게 임의 숫자 부여

        if(m_sProj.compareToIgnoreCase("utm") == 0)
        {
            //Zone은 3도를 출발으로 6도씩 증가함. 180도 넘으면 180도를 뺀다.
            if(m_sZone != null)
            {
                int iZone = Integer.parseInt(m_sZone);
                double rad3 = 0.10471975511966;
                m_LonCenter = rad3/2 + rad3 * (iZone-1);
                if(m_LonCenter >= 3.14159265358979) m_LonCenter = m_LonCenter-3.14159265358979;
                m_ScaleFactor = 0.9996;
                m_FalseEasting = 500000.0;
                m_sUnit = "m";
            }
        }
    }

    protected void initEllipsParameter()
    {
        m_eEllips = GeoEllips.ellipse_str2int(m_sEllips);
        if(m_eEllips == GeoEllips.kWgs84)
        {
            m_Major = 6378137.0;
            m_Minor = 6356752.3142;
        }
        else if(m_eEllips == GeoEllips.kGrs80)
        {
            //GRS80 장축/단축은 확인 후 수정해야 함.
            m_Major = 6378137.0;
            m_Minor = 6356752.31414;
        }
        else if(m_eEllips == GeoEllips.kBessel1984)
        {
            m_Major = 6377397.155;
            m_Minor = 6356078.96325;
        }
    }

    public static void main(String[] args)
    {
//        String code = "+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs ";
    	String code = "+proj=tmerc +lat_0=38 +lon_0=127.50289 +k=0.9996 +x_0=1000000 +y_0=2000000 +ellps=bessel +units=m +no_defs";
        CoordinateReferenceSystem crs = new CoordinateReferenceSystem();
        crs.decode(code);
    }
}
