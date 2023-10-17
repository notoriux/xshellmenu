package it.xargon.xshellmenu;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

public interface XSMenuRootProvider {
	static Map<String, XSMenuRootProvider> getInstances() {
		final Map<String, XSMenuRootProvider> result = new HashMap<>();
		ServiceLoader<XSMenuRootProvider> loader = ServiceLoader.load(XSMenuRootProvider.class);
		
		loader.forEach(p -> {
			if (result.containsKey(p.getName())) throw new IllegalStateException("Duplicated XSMenuRootProvider with name \"" + p.getName() + "\". Check your classpath and retry.");
			result.put(p.getName(), p);
		});
				
		return result;
	}
	
	public String getName();
	public XSMenuItem getRootItem(String... args);
}
