package com.ehr.service;

import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.springframework.aop.ClassFilter;
import org.springframework.util.PatternMatchUtils;

/**
 * 포인트컷(Pointcut)
 필터링된 조인 포인트: 원하는 특정 class 메소드에만 적용.

 * @author sist
 *
 */
public class NameMatchClassPointCut extends NameMatchMethodPointcut {
	static final Logger LOG=Logger.getLogger(NameMatchClassPointCut.class);

	public void setMappedClassName(String mappedName) {
		this.setClassFilter(new SimpleClassFilter(mappedName));
	}
	
	static class SimpleClassFilter implements ClassFilter{
		String mappedName;
		public SimpleClassFilter(String mappedName) {
			this.mappedName = mappedName;
		}
		
		@Override
		public boolean matches(Class<?> clazz) {
			LOG.debug("1. PointCut mappedName:"+mappedName);
			LOG.debug("2. PointCut clazz:"+clazz.getSimpleName());
			LOG.debug("3. PointCut PatternMatchUtils:"
			+PatternMatchUtils.simpleMatch(mappedName, clazz.getSimpleName()));
			return PatternMatchUtils.simpleMatch(mappedName, clazz.getSimpleName());
		}
		
	}
	
	
	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		// TODO Auto-generated method stub
		return super.matches(method, targetClass);
	}
	
	
	
}
