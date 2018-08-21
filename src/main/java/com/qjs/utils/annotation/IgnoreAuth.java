package com.qjs.utils.annotation;

import java.lang.annotation.*;

/**
 * 忽略Token验证
 * @author fengqiang
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreAuth {

}
