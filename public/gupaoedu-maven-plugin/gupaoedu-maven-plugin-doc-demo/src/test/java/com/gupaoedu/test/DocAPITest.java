package com.gupaoedu.test;

import java.util.ArrayList;
import java.util.List;

import javax.core.common.doc.ParseHTML;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath*:application-context.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class DocAPITest {
	@Autowired ApplicationContext app;
	
	@Test
//	@Ignore
	public void test(){
		
		List<Class<?>> clazz = new ArrayList<Class<?>>();
		for(String name : app.getBeanDefinitionNames()){
			try{
				Object bean = app.getBean(name);
				clazz.add(bean.getClass());
			}catch(Exception e){
				continue;
			}
		}
		
		ParseHTML.generate("C:/Users/Lily/Desktop/open-api.html", clazz, "api.gupaoedu.com");
		
	}
	
	
//		public static void main(String[] args) {
//			List<Class> clazz = new ArrayList<Class>();
//			File dir = new File("E:/GP_WORKSPACE/gupaoedu-maven-plugin-demo/target/classes");
//			new DocAPITest().list(clazz, dir);
//		}
//		
//		
//		private void list(List<Class> clazz,File dir){
//			File [] files = dir.listFiles();
//			for(File f : files){
//				
//				if(f.isDirectory()){
//					list(clazz,f);
//				}else{
//					String path = f.getPath().replaceAll("\\\\", "/").replaceAll("E:/GP_WORKSPACE/gupaoedu-maven-plugin-demo/target/classes", "").replaceAll("/", ".");
////					this.getLog().info(path);
//					System.out.println(path);
//					path = path.substring(1, path.length()).replaceAll("\\.class", "");
//					if(path.startsWith("com.gupaoedu")){
//						String  className = path.replaceAll("\\.class", "");
//						Class<?> c;
//						try {
//							c = Class.forName(className);
//							clazz.add(c);
//						} catch (ClassNotFoundException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//		}
		
	
}
