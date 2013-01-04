package com.kthcorp.geo.giscore.coord;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.StringTokenizer;

public class Proj4Info
{
    private static Proj4Info instance = null;
    private Hashtable m_proj4InfoContainer = null;

    private Proj4Info()
    {
        init();
    }

    public static Proj4Info getInstance()
    {
        if (instance == null)
            instance = new Proj4Info();

        return instance;
    }


    protected void init()
    {
        m_proj4InfoContainer = new Hashtable();

        //잘 알려진 CRS 미리셋팅
        m_proj4InfoContainer.put("EPSG:2096","+proj=tmerc +lat_0=38 +lon_0=129.002890277778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs");
        m_proj4InfoContainer.put("EPSG:2097","+proj=tmerc +lat_0=38 +lon_0=127.002890277778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs");
        m_proj4InfoContainer.put("EPSG:2098","+proj=tmerc +lat_0=38 +lon_0=125.002890277778 +k=1 +x_0=200000 +y_0=500000 +ellps=bessel +units=m +no_defs");
        m_proj4InfoContainer.put("KATEC","+proj=tmerc +lat_0=38 +lon_0=128 +k=0.9999 +x_0=400000 +y_0=600000 +ellps=bessel +units=m +no_defs");

        m_proj4InfoContainer.put("EPSG:3092","+proj=utm +zone=51 +ellps=bessel +units=m +no_defs");
        m_proj4InfoContainer.put("EPSG:3093","+proj=utm +zone=52 +ellps=bessel +units=m +no_defs");
        m_proj4InfoContainer.put("ESRI:102151","+proj=utm +zone=51 +ellps=bessel +units=m +no_defs");
        m_proj4InfoContainer.put("ESRI:102152","+proj=utm +zone=52 +ellps=bessel +units=m +no_defs");

        m_proj4InfoContainer.put("EPSG:32651","+proj=utm +zone=51 +ellps=WGS84 +datum=WGS84 +units=m +no_defs");
        m_proj4InfoContainer.put("EPSG:32652","+proj=utm +zone=52 +ellps=WGS84 +datum=WGS84 +units=m +no_defs");

        m_proj4InfoContainer.put("EPSG:4326","+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");
        m_proj4InfoContainer.put("CRS:84","+proj=longlat +ellps=WGS84 +datum=WGS84 +no_defs");

        m_proj4InfoContainer.put("EPSG:4162","+proj=longlat +ellps=bessel +no_defs");
        m_proj4InfoContainer.put("EPSG:4166","+proj=longlat +ellps=WGS84 +towgs84=0,0,0,0,0,0,0 +no_defs");
        m_proj4InfoContainer.put("EPSG:4737","+proj=longlat +ellps=GRS80 +towgs84=0,0,0,0,0,0,0 +no_defs");
        m_proj4InfoContainer.put("EPSG:4926","+proj=longlat +ellps=GRS80 +towgs84=0.0,0.0,0.0,0.0,0.0,0.0,0.0 +no_defs");
        m_proj4InfoContainer.put("EPSG:4927","+proj=longlat +ellps=GRS80 +towgs84=0.0,0.0,0.0,0.0,0.0,0.0,0.0 +no_defs");
        m_proj4InfoContainer.put("SR-ORG:6640","+proj=tmerc +lat_0=38 +lon_0=127 +k=1 +x_0=200000 +y_0=500000 +ellps=GRS80 +units=m +no_defs");
        m_proj4InfoContainer.put("EPSG:61626405","+proj=longlat +ellps=bessel +no_defs");
        m_proj4InfoContainer.put("EPSG:61666405","+proj=longlat +ellps=WGS84 +towgs84=0.0,0.0,0.0,0.0,0.0,0.0,0.0 +no_defs ");
        m_proj4InfoContainer.put("SR-ORG:6627","+proj=merc +lon_0=0 +k=1 +x_0=0 +y_0=0 +ellps=WGS84 +datum=WGS84 +units=m +no_defs");
        m_proj4InfoContainer.put("UTMK","+proj=tmerc +lat_0=38 +lon_0=127.5 +ellps=wgs84 +x_0=1000000 +y_0=2000000 +k=0.9996 +units=m +no_defs");
    }

    public String getProj4Info(String code)
    {
        code = code.toUpperCase();
        //code = StringUtil.replace(code,"http://www.opengis.net/gml/srs/epsg.xml","EPSG");
        //code = StringUtil.replace(code,"urn:EPSG:geographicCRC","EPSG");

        Object oProj4Info = m_proj4InfoContainer.get(code);
        if(oProj4Info != null)
        {
            return (String) oProj4Info;
        }

        return onlineProj4Info(code);
    }

    protected String onlineProj4Info(String code)
    {
        String sProj4Info = null;
        String sLowerCode = code.toLowerCase();
        try
        {
            StringTokenizer token = new StringTokenizer(sLowerCode,":#.");
            String sURL = "http://www.spatialreference.org/ref/" + token.nextToken() + "/" +
                token.nextToken() + "/proj4/";
            long start = System.currentTimeMillis();
            //System.out.println("");
            URL url = new URL(sURL);
            InputStream fis = null;

            long time = System.currentTimeMillis() - start;
            //System.out.println("요청 시간 = " + time);

            URLConnection conn = url.openConnection();
            fis = conn.getInputStream();

            int contentLength = conn.getContentLength();
            byte[] buf = new byte[contentLength];

            int c = 0;
            int b = 0;
            while ( (c < buf.length) && (b = fis.read(buf, c, buf.length - c)) >= 0)
            {
                c += b;
            }

            sProj4Info = new String(buf);
            //System.out.println("onlineProj4Info " + code + " = " + sProj4Info);

            buf = null;
            fis.close();

            m_proj4InfoContainer.put(code, sProj4Info);
        }
        catch (Exception e)
        {
            m_proj4InfoContainer.put(code, "");
            e.printStackTrace();
        }

        return sProj4Info;
    }
}
