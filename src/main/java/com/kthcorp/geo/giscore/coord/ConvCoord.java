package com.kthcorp.geo.giscore.coord;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 *======================================================
 * @클래스명 : ConvCoordUtil
 * @작성자  : creamkang
 * @작성일  : 2011. 5. 13.
 * @설명    : 좌표변환 클래스 
 *======================================================
 */
public class ConvCoord {

	public static String TME = "1";
	public static String TMM = "2";
	public static String TMW = "3";
	public static String TMK = "4";
	public static String UTM51 = "5";
	public static String UTM52 = "6";
	public static String UTMK = "7";
	public static String LLW = "8";
	public static String LLB = "9";
	
    
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, uGEonException
    {
    	String src;
    	String dst;
    	DPoint srcPoint = new DPoint();
    	
    	//WGS84 -> UTM-K
    	src = ConvCoord.LLW;
    	dst = ConvCoord.UTMK;
    	
    	
    	srcPoint.x = 126.97553009053178; //location.getLongitude();
    	srcPoint.y = 37.56456569639763; //location.getLatitude();
    	
    	System.out.println("************ WGS84 -> UTM-K ");
    	DPoint dp = convert(srcPoint, src, dst);
    	System.out.println(dp.x);
    	System.out.println(dp.y);
    	
    	System.out.println("************ UTM-K -> WGS84");
    	DPoint dp2 = convert(dp, dst, src);
    	System.out.println(dp2.x);
    	System.out.println(dp2.y);
    	
    	
    	//TM -> UTM-K
//    	src = ConvCoord.TMM;
//    	dst = ConvCoord.UTMK;
//    	srcPoint.x = 193173.61607071199;
//    	srcPoint.y = 443360.98266810604;
//    	
//    	System.out.println("************ TM -> UTM-K");
//    	DPoint dp2 = convert(srcPoint, src, dst);
//    	System.out.println(dp2.x);
//    	System.out.println(dp2.y);
    	
    }

    
    public static DPoint convert(DPoint point, String Src, String Dst) throws uGEonException
    {
    	
    	String SrcStr="";
    	String DstStr="";
    	
		if(Src.equals(ConvCoord.TME))
		{
			SrcStr = "EPSG:2096";
		}		
		if(Dst.equals(ConvCoord.TME))
		{
			DstStr = "EPSG:2096";
		}
		if(Src.equals(ConvCoord.TMM))
		{
			SrcStr = "EPSG:2097";
		}		
		if(Dst.equals(ConvCoord.TMM))
		{
			DstStr = "EPSG:2097";
		}
		if(Src.equals(ConvCoord.TMW))
		{
			SrcStr = "EPSG:2098";
		}		
		if(Dst.equals(ConvCoord.TMW))
		{
			DstStr = "EPSG:2098";
		}
		if(Src.equals(ConvCoord.TMK))
		{
			SrcStr = "+proj=tmerc +lat_0=38 +lon_0=128 +k=0.9999 +x_0=400000 +y_0=600000 +ellps=bessel +units=m +no_defs";
		}		
		if(Dst.equals(ConvCoord.TMK))
		{
			DstStr = "+proj=tmerc +lat_0=38 +lon_0=128 +k=0.9999 +x_0=400000 +y_0=600000 +ellps=bessel +units=m +no_defs";
		}
		if(Src.equals(ConvCoord.UTM51))
		{
			SrcStr = "EPSG:3092";
		}		
		if(Dst.equals(ConvCoord.UTM51))
		{
			DstStr = "EPSG:3092";
		}
		if(Src.equals(ConvCoord.UTM52))
		{
			SrcStr = "EPSG:3093";
		}		
		if(Dst.equals(ConvCoord.UTM52))
		{
			DstStr = "EPSG:3093";
		}
		if(Src.equals(ConvCoord.UTMK))
		{
			SrcStr = "UTMK";
		}
		
		if(Dst.equals(ConvCoord.UTMK))
		{
			DstStr = "UTMK";
		}
		if(Src.equals(ConvCoord.LLW))
		{
			SrcStr = "EPSG:4326";
		}		
		if(Dst.equals(ConvCoord.LLW))
		{
			DstStr = "EPSG:4326";
		}
		if(Src.equals(ConvCoord.LLB))
		{
			SrcStr = "EPSG:61626405";
		}		
		if(Dst.equals(ConvCoord.LLB))
		{
			DstStr = "EPSG:61626405";
		}
		

		CoordConversion conv = CoordConversionFactory.createCoordConversion(SrcStr, DstStr);
		if(conv == null) return null;
		
		DPoint dstPoint = conv.Conv(point);
		//System.out.println("******** dstPoint.x: " + dstPoint.x);
		//System.out.println("******** dstPoint.y: " + dstPoint.y);

    	return dstPoint;
    }
}
