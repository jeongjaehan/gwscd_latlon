package com.kthcorp.geo.giscore.coord;

public class uGEonException extends Exception
{
    String sErrorCode = null;
    String sErrorDesc = null;
    ////////////////////////////////////////////////////////////////////////////
    /**
    *  uGEonException 생성자. 에러 정보를 설정
    *
    *  @param     errCode : 에러코드
    *  @param     errDesc : 에러설명
    *  @param     cause : Throwable 정보
    *  @return    없음.
    *  @exception 없음.
    */
    public uGEonException(String errCode, String errDesc, Throwable cause)
    {
        super(cause);

        sErrorCode = errCode;
        sErrorDesc = errDesc;
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
    *  uGEonException 생성자. 에러 정보를 설정
    *
    *  @param     errCode : 에러코드
    *  @param     errDesc : 에러설명
    *  @return    없음.
    *  @exception 없음.
    */
    public uGEonException(String errCode, String errDesc)
    {
        sErrorCode = errCode;
        sErrorDesc = errDesc;
    }

    public String getErrorCode()
    {
        return sErrorCode;
    }

    public String getErrorDesc()
    {
        return sErrorDesc;
    }
}
