package com.kthcorp.gw_scd_latlon_convert;

import java.util.HashMap;
import java.util.List;


public interface GwscdMapper {
	public List<HashMap> selectGwscd(HashMap<String, Object> params);
	public int selectGwscdTotalCount();
	public int updateGWscd(HashMap<String, Object> params);
}
