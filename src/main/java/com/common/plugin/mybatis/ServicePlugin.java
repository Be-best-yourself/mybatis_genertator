package com.common.plugin.mybatis;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.internal.util.StringUtility;
/**
 * 如要生成service，请将此插件配置到xml文件中
 * @author zhang
 *
 */
public class ServicePlugin extends PluginAdapter {

	private final static String BASE_SERVICE="com.common.service.BaseService";
	
	private final static String BASE_SERVICE_IMPL="com.common.service.impl.BaseServiceImpl";
	
	// service目录
	private String serviceTargetDir;
	// 服务全类名
	private String serviceInterfaceClass;
	// 服务实现全类名
	private String implementationClass;
	//注解
	private String annotation;

	@Override
	public boolean validate(List<String> warnings) {
		serviceTargetDir = properties.getProperty("serviceTargetDir");
		boolean valid = StringUtility.stringHasValue(serviceTargetDir);

		serviceInterfaceClass = properties.getProperty("serviceInterfaceClass");
		boolean valid2 = StringUtility.stringHasValue(serviceInterfaceClass);

		implementationClass = properties.getProperty("implementationClass");
		boolean valid3 = StringUtility.stringHasValue(implementationClass);

		annotation = properties.getProperty("annotation");
		
		return valid && valid2 && valid3;
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		
		
		// 遍历都生成了哪些类，判断生成的类是不是接口，如果不是接口，就是实体类，用这个实体类做泛型
		String entityName = null;
		String entityPageName=null;
		List<GeneratedJavaFile> generatedJavaFiles = introspectedTable.getGeneratedJavaFiles();
		for (GeneratedJavaFile generatedJavaFile : generatedJavaFiles) {
			CompilationUnit compilationUnit = generatedJavaFile.getCompilationUnit();
			boolean isInterface = compilationUnit.isJavaInterface();
			if (!isInterface) {
				String shortName = compilationUnit.getType().getShortName();
				entityName = shortName;
				entityPageName=compilationUnit.getType().getFullyQualifiedName();
			}
		}

		
		List<GeneratedJavaFile> classJavaFile=new ArrayList<GeneratedJavaFile>();
		// 获得上下文编码
		JavaFormatter javaFormatter = context.getJavaFormatter();
		
		/**
		 * 创建接口
		 * 
		 **/
		Interface serviceInterface = new Interface(serviceInterfaceClass);
		serviceInterface.setVisibility(JavaVisibility.PUBLIC);
		serviceInterface.addJavaDocLine("/**");
		serviceInterface.addJavaDocLine(" * 自动生成Sevice");
		serviceInterface.addJavaDocLine(" */");
		// 添加继承接口
		serviceInterface.addImportedType(new FullyQualifiedJavaType(BASE_SERVICE));
		int lastIndexOf = BASE_SERVICE.lastIndexOf(".");
		FullyQualifiedJavaType interfaceSuperInterface = new FullyQualifiedJavaType(BASE_SERVICE.substring(lastIndexOf + 1));
		serviceInterface.addSuperInterface(interfaceSuperInterface);
		// 添加泛型支持
		serviceInterface.addImportedType(new FullyQualifiedJavaType(entityPageName));
		interfaceSuperInterface.addTypeArgument(new FullyQualifiedJavaType(entityName));
		
	
		/**
		 * 创建接口实现类
		 * 
		 **/
		TopLevelClass serviceImpl=new TopLevelClass(implementationClass);
		serviceImpl.setVisibility(JavaVisibility.PUBLIC);
		// 添加实现接口
		serviceImpl.addImportedType(new FullyQualifiedJavaType(serviceInterfaceClass));
		FullyQualifiedJavaType implServiceJavaType = new FullyQualifiedJavaType(serviceInterfaceClass.substring(serviceInterfaceClass.lastIndexOf(".") + 1));
		serviceImpl.addSuperInterface(implServiceJavaType);
		//添加注解
		if (annotation!=null) {
			serviceImpl.addImportedType(new FullyQualifiedJavaType(annotation));
			String annotationName = annotation.substring(annotation.lastIndexOf(".")).replace(".", "@");
			serviceImpl.addAnnotation(annotationName);
		}
		//添加继承类
		serviceImpl.addImportedType(new FullyQualifiedJavaType(BASE_SERVICE_IMPL));
		FullyQualifiedJavaType serviceImplSuperClass=new FullyQualifiedJavaType(BASE_SERVICE_IMPL.substring(BASE_SERVICE_IMPL.lastIndexOf(".") + 1));
		serviceImpl.setSuperClass(serviceImplSuperClass);
		// 添加泛型支持
		serviceImpl.addImportedType(new FullyQualifiedJavaType(entityPageName));
		serviceImplSuperClass.addTypeArgument(new FullyQualifiedJavaType(entityName));
		
		
		
		//创建接口
		GeneratedJavaFile serviceInterfaceJavafile = new GeneratedJavaFile(serviceInterface, serviceTargetDir, javaFormatter);
		classJavaFile.add(serviceInterfaceJavafile);
		
		//创建接口实现类
		GeneratedJavaFile serviceImplJavafile = new GeneratedJavaFile(serviceImpl, serviceTargetDir, javaFormatter);
		classJavaFile.add(serviceImplJavafile);
		
		return classJavaFile;
	}

}
