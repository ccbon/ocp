package org.ocpteam.core;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * A container implementation for Context and dependency injection (CDI)
 * 
 * @author Ejder Bastug
 * @author Jean Louis Guenego
 * 
 * TODO: This class needs be implemented. 
 */
public class DefaultCloudContainer implements CloudContainer {
	/**
	 * Name of the container
	 */
	private String name;
	
	/**
	 * Parent container
	 */
	
	private DefaultCloudContainer parent;
	
	/**
	 * Child containers
	 */
	private Set<DefaultCloudContainer> children = new HashSet<DefaultCloudContainer>();
	
	/**
	 * Properties of the container instance
	 */
	private final Properties properties = new Properties();
	
	/**
	 * Starts the container and its children
	 */
	public void start(){
		//TODO: Implement
	}
	
	/**
	 * Stopts the container and its children.
	 */
	public void stop(){
		//TODO: Implement
	}

	
	/**
	 * Specifies which functinality needs to be used
	 * @param clazz
	 * @return
	 */
	public InsteadOf use(Class<?> clazz){
		return new InsteadOf(this);
	}
	
	public DefaultCloudContainer design(){
		DefaultCloudContainer cloudContainer =  new DefaultCloudContainer();
		
		return cloudContainer;
	}
	
	public class InsteadOf {
		DefaultCloudContainer root;
		
		protected InsteadOf(DefaultCloudContainer root){
			this.root = root;
		}
		
		public DefaultCloudContainer insteadOf(Class<?> clazz){
			// TODO: Process requirement
			return root;
		}
		
		public DefaultCloudContainer insteadOfDefault(){
			// TODO: Process requirement
			return root;
		}
	}
}
