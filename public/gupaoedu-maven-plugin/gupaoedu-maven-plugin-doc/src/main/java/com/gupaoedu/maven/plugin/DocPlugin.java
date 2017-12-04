package com.gupaoedu.maven.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.core.common.doc.ParseHTML;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal doc 
 * @phase compile
 * @author Tom
 *
 */
public class DocPlugin extends AbstractMojo {

	/**
	 * @parameter expression="${targetFile}"
	 */
	private String targetFile;
	
	/**
	 * @parameter expression="${host}"
	 */
	private String host;
	
	/**
	 * @parameter expression="${basePage}"
	 */
	private String basePage;
	
	/**
	 * @parameter expression="${classPath}"
	 */
	private String classPath;
	
	
	public void execute() throws MojoExecutionException, MojoFailureException {
		this.getLog().info("================ 这是咕泡学院的自定义插件 ======================");
		this.getLog().info("获取到参数列表");
		this.getLog().info("classPath=" + classPath);
		this.getLog().info("basePage=" + basePage);
		this.getLog().info("targetFile=" + targetFile);
		this.getLog().info("host=" + host);
		
		try{
			List<Class<?>> clazz = new ArrayList<Class<?>>();
			File dir = new File(classPath);
			list(clazz,dir);
		
			ParseHTML.generate(targetFile, clazz, host);
			this.getLog().info("已成功生成文档");
		}catch(Exception e){
			e.printStackTrace();
			this.getLog().error(e.getMessage());
		}
	}
	
	/**
	 * 递归扫描所有的class文件
	 * @param clazz
	 * @param dir
	 */
	private void list(List<Class<?>> clazz,File dir){
		File [] files = dir.listFiles();
		for(File f : files){
			
			if(f.isDirectory()){
				list(clazz,f);
			}else{
				if(!f.getName().endsWith(".class")){ continue; }
				String className = f.getPath().replaceAll("\\\\", "/").replaceAll(classPath.replaceAll("\\\\", "/"), "").replaceAll("/", ".").replaceAll("\\.class", "");
				className = className.substring(1, className.length());
				if(className.startsWith(basePage)){
					Class<?> c;
					try {
						c = this.getClass().getClassLoader().loadClass(className);
						clazz.add(c);
					} catch (ClassNotFoundException e) {
						continue;
					}
				}
			}
		}
	}
	
	
	
	

}
