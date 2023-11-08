package it.xargon.xshellmenu.api;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ExecutorService;

import javax.swing.Icon;

public final class XSPlatformResource<T> {
	public final static XSPlatformResource<Icon> GENERIC_ICON = resourceOf(Icon.class);
	public final static XSPlatformResource<Icon> APPLICATION_ICON = resourceOf(Icon.class);
	public final static XSPlatformResource<ScheduledExecutorService> TASK_SCHEDULER = resourceOf(ScheduledExecutorService.class);
	public final static XSPlatformResource<ExecutorService> ICONFETCHER_SCHEDULER = resourceOf(ExecutorService.class);
	
	private Class<T> resourceClass;
	
	private static <T> XSPlatformResource<T> resourceOf(Class<T> resourceClass) {
		return new XSPlatformResource<>(resourceClass);
	}
	
	private XSPlatformResource(Class<T> resourceClass) {
		this.resourceClass = Objects.requireNonNull(resourceClass);
	}
	
	public T cast(Object obj) {return resourceClass.cast(obj);}
}
