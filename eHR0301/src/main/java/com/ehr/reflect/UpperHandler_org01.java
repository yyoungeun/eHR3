package com.ehr.reflect;

//import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.springframework.cglib.proxy.InvocationHandler;

public class UpperHandler_org01 implements InvocationHandler {
	Hello target;
	
	public UpperHandler_org01() {}
	public UpperHandler_org01(Hello target) {
		this.target = target;
	}
	
	
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//Method chartAtmethod =String.class.getMethod("charAt", int.class);
		System.out.println("method: "+method.getName());
		for(Object obj:args) {
			System.out.println("obj: "+obj);
		}
		String ret = (String) method.invoke(target, args);//타깃으로 위임, 인터페이스 모든메소드 호출에 적용.
		System.out.println("ret: "+ret);
		return ret.toUpperCase();//부가기능
	}

}
