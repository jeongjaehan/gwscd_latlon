package com.kthcorp.gw_scd_latlon_convert;

import java.io.Reader;
import java.util.HashMap;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;

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
	
	public SqlSession getSessionFactory(){
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
	}
	
	public void goTask(){
		// 10000만 건씩 DB에서 읽어오기
		int fetchCount = 50;
		int start = 0;
		int end = start+fetchCount;
		
		SqlSession session = getSessionFactory();
		GwscdMapper gwscdMapper = session.getMapper(GwscdMapper.class);
		HashMap<String,Object> params = new HashMap<String,Object>();
		params.put("start", start);
		params.put("end", end);
		
		
		gwscdMapper.selectGwscd(params);
		
		// 좌표 변환 utmk -> wgs84
		
		
		
		// DB 업데이트 
		
		// 리소스 정리
		session.commit();
		session.close();		
		
	}
	
	public static void main(String[] args) {
		new GwscdCoordUtmkToWgs82().goTask();
	}
	
}
