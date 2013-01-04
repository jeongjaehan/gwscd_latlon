package com.kthcorp.gw_scd_latlon_convert;

import java.io.Reader;
import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

import com.kthcorp.geo.giscore.coord.ConvCoord;
import com.kthcorp.geo.giscore.coord.DPoint;

/**
 * GW_SCD 테이블의 x,y 좌표 변환 클래스  
 * @author jeong
 *
 */
public class GwscdCoordUtmkToWgs82 {
	private Logger log = Logger.getLogger(this.getClass());
	private String environment;	// db 환경
	private String mybatis_config_path = "com/kthcorp/gw_scd_latlon_convert/MybatisConfig.xml";	
	
	/**
	 * 초기화
	 */
	public GwscdCoordUtmkToWgs82() {
		if(System.getProperty("os.name").toUpperCase().startsWith("WINDOW")){ // 테스트
			this.environment = "development";
		}else{	// 상용
			this.environment = "service";
		}
	}
	
/*	public SqlSession getSessionFactory(){
		SqlSession session = null;

		try {
			Reader reader = Resources.getResourceAsReader(this.mybatis_config_path);
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader,this.environment);
			session = sqlSessionFactory.openSession();
			return session;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return session;
	}*/
	
	public SqlSession getSessionFactory(){
		SqlSession session = null;

		try {
			Reader reader = Resources.getResourceAsReader(this.mybatis_config_path);
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
			session = sqlSessionFactory.openSession();
			return session;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return session;
	}
	
	public void goTask(){
		
		int fetchCount = 100; // 1000만 건씩 DB에서 읽어오기
		int start = 1;
		int end = 0;
		int total = 0;
		
		SqlSession session = getSessionFactory();
		GwscdMapper gwscdMapper = session.getMapper(GwscdMapper.class);
		
		total = gwscdMapper.selectGwscdTotalCount();
//		total = 20000000;
		log.info("total : "+total);
		
		while(true){
			if(end >= total){
				break;
			}
			
			end+=fetchCount;
			
			if(end>total)
				end = total;
			
			log.info("start : "+start + ", end : "+end);
			
			HashMap<String,Object> params = new HashMap<String,Object>();
			params.put("start", start);
			params.put("end", end);
			
			List<HashMap> list = gwscdMapper.selectGwscd(params);
			
			for (HashMap hashMap : list) {
				String id = hashMap.get("id").toString() ;
				String uX = hashMap.get("x").toString() ;
				String uY = hashMap.get("y").toString() ;
				DPoint up = new DPoint(Double.parseDouble(uX),Double.parseDouble(uY));
				
				// 좌표 변환 utmk -> wgs84
				try {
					DPoint wp = ConvCoord.convert(up, ConvCoord.UTMK, ConvCoord.LLW);
//					log.info("좌표변환성공 -> X : "+wp.x+", Y : "+wp.y);
					
					HashMap<String,Object> iparams = new HashMap<String,Object>();
					iparams.put("x", wp.x);
					iparams.put("y", wp.y);
					iparams.put("id", id);
					
					gwscdMapper.updateGWscd(iparams);	// 변환된 좌표 db 업데이트 
					
				} catch (Exception e) {
					log.error("좌표변환실패 변환전 좌표 [UTMK] X : "+uX+", Y : "+uY, e);
				}
				
			}
			start+=fetchCount;
		}
		
		
		
		
		
		// DB 업데이트 
		
		// 리소스 정리
		session.commit();
		session.close();		
		
	}
	
	public static void main(String[] args) {
		new GwscdCoordUtmkToWgs82().goTask();
	}
	
}
