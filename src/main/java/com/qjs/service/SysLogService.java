package com.qjs.service;

import java.util.List;
import java.util.Map;

import com.qjs.entity.SysLogEntity;

/**
 * 系统日志
 * 
 * @author fengqiang
 */
public interface SysLogService {
	
	SysLogEntity queryObject(Long id);
	
	List<SysLogEntity> queryList(Map<String, Object> map);
	
	int queryTotal(Map<String, Object> map);
	
	void save(SysLogEntity sysLog);
	
	void update(SysLogEntity sysLog);
	
	void delete(Long id);
	
	void deleteBatch(Long[] ids);
}
