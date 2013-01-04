package com.kthcorp.geo.giscore.coord;


public class CoordConversionFactory
{
    public static CoordConversion createCoordConversion(String srcSrs, String destSrs)  throws uGEonException
    {
        CoordConversion coordConv = new CoordConversion();

        try
        {
            CoordinateReferenceSystem srcCRS = new CoordinateReferenceSystem();
            srcCRS.decode(srcSrs);
            coordConv.setSrcCRS(srcCRS);

            CoordinateReferenceSystem dstCRS = new CoordinateReferenceSystem();
            dstCRS.decode(destSrs);
            coordConv.setDstCRS(dstCRS);

            coordConv.InitDatumVar();
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return null;
        }

        return coordConv;
    }

}
