package com.qjs.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qjs.entity.SysUserEntity;
import com.qjs.utils.ShiroUtils;

/**
 * Controller公共组件
 * 
 * @author fengqiang
 */
public abstract class AbstractController {
	protected Logger logger = LoggerFactory.getLogger(getClass());
	
	protected SysUserEntity getUser() {
		return ShiroUtils.getUserEntity();
	}

	protected Long getUserId() {
		return getUser().getUserId();
	}
}
