package be.nabu.glue.appium;

import java.util.ArrayList;
import java.util.List;

import be.nabu.glue.api.StaticMethodFactory;

public class AppiumStaticMethodFactory implements StaticMethodFactory {

	@Override
	public List<Class<?>> getStaticMethodClasses() {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		classes.add(AppiumMethods.class);
		return classes;
	}

}
