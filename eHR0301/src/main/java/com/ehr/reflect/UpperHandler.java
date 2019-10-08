package com.ehr.reflect;

//import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.cglib.proxy.InvocationHandler;

public class UpperHandler implements InvocationHandler {
	Object target;

	public UpperHandler() {
	}

	public UpperHandler(Object target) {
		// 어떤종류의 인터페이스를 구현한 타깃에도 적용가능
		this.target = target;
	}

	//dynamic proxy
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// Method chartAtmethod =String.class.getMethod("charAt", int.class);
		for (Object obj : args) {
			System.out.println("obj: " + obj);
		}

		Object ret = method.invoke(target, args); //일반화
		//PointCut: 필터링된 조인 포인트: 원하는 특정 메소드에만 적용.
		if (ret instanceof String && method.getName().startsWith("sayH")) {
			return ((String) (ret)).toUpperCase();
		} else {
			return ret;
		}
	}
}
