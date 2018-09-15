package com.common.plugin.mybatis;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.JavaFormatter;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 自定义插件：生成selectAll,selectByPage,selectByPageAndT的mapper
 * 
 * 覆盖原来*Dao.java文件，统一实现baseDao<T>接口，方便管理
 * 
 * @author zhang
 *
 */
public class CustomPlugin extends PluginAdapter {

	private String daoTargetDir;
	private String baseDao;

	private String annotation;

	/**
	 * 验证参数是否有效
	 * 
	 * @param warnings
	 * @return
	 */
	public boolean validate(List<String> warnings) {
		daoTargetDir = properties.getProperty("targetProject");
		boolean valid = StringUtility.stringHasValue(daoTargetDir);

		baseDao = properties.getProperty("baseDao");
		boolean valid2 = StringUtility.stringHasValue(baseDao);

		// 注解不验证
		annotation = properties.getProperty("annotation");

		return valid && valid2;
	}

	/**
	 * 生成mapping 添加自定义sql
	 */
	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		// 创建insertAndSelectId
		XmlElement insertAndSelectId = new XmlElement("insert");
		insertAndSelectId.addAttribute(new Attribute("id", "insertTCacheId"));
		insertAndSelectId.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
		insertAndSelectId.addElement(
				new TextElement("insert into " + introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime()
						+ insertAndCacheIdToT(introspectedTable)));

		// 创建Select查询
		XmlElement select = new XmlElement("select");
		select.addAttribute(new Attribute("id", "selectAll"));
		select.addAttribute(new Attribute("resultMap", "BaseResultMap"));
		select.addAttribute(new Attribute("parameterType", introspectedTable.getBaseRecordType()));
		select.addElement(new TextElement("select <include refid=\"Base_Column_List\" /> from "
				+ introspectedTable.getFullyQualifiedTableNameAtRuntime()));
		// 创建queryPage查询
		XmlElement queryPage = new XmlElement("select");
		queryPage.addAttribute(new Attribute("id", "selectByPage"));
		queryPage.addAttribute(new Attribute("resultMap", "BaseResultMap"));
		queryPage.addAttribute(new Attribute("parameterType", "com.common.entity.Page"));
		queryPage.addElement(new TextElement("select " + "<include refid=\"Base_Column_List\" /> " + "from "
				+ introspectedTable.getFullyQualifiedTableNameAtRuntime()
				+ " limit #{page.startSize,jdbcType=INTEGER}, #{page.pageSize,jdbcType=INTEGER}"));

		// 创建queryPage查询
		XmlElement queryByT = new XmlElement("select");
		queryByT.addAttribute(new Attribute("id", "selectByT"));
		queryByT.addAttribute(new Attribute("resultMap", "BaseResultMap"));
		queryByT.addAttribute(new Attribute("parameterType", "com.common.entity.Page"));
		queryByT.addElement(new TextElement("select " + "<include refid=\"Base_Column_List\" /> " + "from "
				+ introspectedTable.getFullyQualifiedTableNameAtRuntime()
				+ getBeanPropertyAndCommonWhere(introspectedTable)));

		// 创建queryPagebyT查询
		XmlElement queryPageByT = new XmlElement("select");
		queryPageByT.addAttribute(new Attribute("id", "selectByPageAndT"));
		queryPageByT.addAttribute(new Attribute("resultMap", "BaseResultMap"));
		queryPageByT.addElement(new TextElement(" select \n" + "<include refid=\"Base_Column_List\" /> " + "\n from "
				+ introspectedTable.getFullyQualifiedTableNameAtRuntime()
				+ getBeanPropertyAndCommonWhere(introspectedTable)
				+ " limit #{page.startSize,jdbcType=INTEGER}, #{page.pageSize,jdbcType=INTEGER}"));

		XmlElement parentElement = document.getRootElement();
		parentElement.addElement(insertAndSelectId);
		parentElement.addElement(select);
		parentElement.addElement(queryPage);
		parentElement.addElement(queryByT);
		parentElement.addElement(queryPageByT);
		return super.sqlMapDocumentGenerated(document, introspectedTable);
	}

	// 复写此方法，增加类，如果包中有相同类名，则翻盖那个类
	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

		// 获得上下文编码
		JavaFormatter javaFormatter = context.getJavaFormatter();

		GeneratedJavaFile mapperJavafile = null;

		List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<GeneratedJavaFile>();

		List<GeneratedJavaFile> generatedJavaFiles = introspectedTable.getGeneratedJavaFiles();

		String entityName = null;
		String entityPageName = null;
		// 遍历都生成了哪些类，判断生成的类是不是接口，如果不是接口，就是实体类，用这个实体类做泛型
		for (GeneratedJavaFile generatedJavaFile : generatedJavaFiles) {
			CompilationUnit compilationUnit = generatedJavaFile.getCompilationUnit();
			boolean isInterface = compilationUnit.isJavaInterface();
			if (!isInterface) {
				String shortName = compilationUnit.getType().getShortName();
				entityName = shortName;
				entityPageName = compilationUnit.getType().getFullyQualifiedName();
			}
		}

		// 将自动生成的dao重写覆盖掉，纺一继承接口，统一管理
		for (GeneratedJavaFile generatedJavaFile : generatedJavaFiles) {
			CompilationUnit compilationUnit = generatedJavaFile.getCompilationUnit();
			FullyQualifiedJavaType fullyQualifiedJavaType = compilationUnit.getType();
			String shortName = fullyQualifiedJavaType.getShortName();

			// 将*Dao.java翻盖重写
			if (shortName.endsWith("Dao")) {

				Interface mapperInterface = new Interface(fullyQualifiedJavaType.getFullyQualifiedName());
				mapperInterface.setVisibility(JavaVisibility.PUBLIC);
				mapperInterface.addJavaDocLine("/**");
				mapperInterface.addJavaDocLine(" * 自动生成");
				mapperInterface.addJavaDocLine(" */");
				// 添加注解
				if (annotation != null) {
					mapperInterface.addImportedType(new FullyQualifiedJavaType(annotation));
					String annotationName = annotation.substring(annotation.lastIndexOf(".")).replace(".", "@");
					mapperInterface.addAnnotation(annotationName);

				}

				// 添加继承接口
				mapperInterface.addImportedType(new FullyQualifiedJavaType(baseDao));
				int lastIndexOf = baseDao.lastIndexOf(".");
				FullyQualifiedJavaType superInterface = new FullyQualifiedJavaType(baseDao.substring(lastIndexOf + 1));
				mapperInterface.addSuperInterface(superInterface);

				// 添加泛型支持
				mapperInterface.addImportedType(new FullyQualifiedJavaType(entityPageName));
				superInterface.addTypeArgument(new FullyQualifiedJavaType(entityName));

				mapperJavafile = new GeneratedJavaFile(mapperInterface, daoTargetDir, javaFormatter);
				mapperJavaFiles.add(mapperJavafile);
			}

		}
		return mapperJavaFiles;
	}

	/**
	 * 拼接<where><if></if></where>语句
	 * 
	 * @param introspectedTable
	 * 
	 * @return 拼接<where><if></if></where>语句
	 */
	public String getBeanPropertyAndCommonWhere(IntrospectedTable introspectedTable) {
		StringBuffer sb = new StringBuffer();
		sb.append("	\n<where>\n");
		List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
		for (IntrospectedColumn introspectedColumn : allColumns) {
			String jdbcTypeName = introspectedColumn.getJdbcTypeName();
			String javaProperty = introspectedColumn.getJavaProperty();
			String actualColumnName = introspectedColumn.getActualColumnName();
			sb.append("		<if test=\"t.").append(javaProperty).append("!= null\">");
			sb.append("\n");
			sb.append("			and ").append(actualColumnName).append("=#{t.").append(javaProperty)
					.append(",jdbcType=").append(jdbcTypeName).append("}");
			sb.append("\n");
			sb.append("		</if>\n");
		}
		sb.append("	</where>\n");
		return sb.toString();
	}

	/**
	 * @param introspectedTable
	 * @return
	 */
	public String insertAndCacheIdToT(IntrospectedTable introspectedTable) {
		StringBuffer sb = new StringBuffer();
		sb.append(" (");
		List<IntrospectedColumn> allColumns = introspectedTable.getAllColumns();
		for (IntrospectedColumn introspectedColumn : allColumns) {
			String actualColumnName = introspectedColumn.getActualColumnName();
			sb.append(actualColumnName + ",");
		}
		sb.deleteCharAt(sb.length()-1).append(")\n");
		sb.append("values (");
		for (IntrospectedColumn introspectedColumn : allColumns) {
			String jdbcTypeName = introspectedColumn.getJdbcTypeName();
			String javaProperty = introspectedColumn.getJavaProperty();
			sb.append("#{" + javaProperty + ",jdbcType=" + jdbcTypeName + "},");
		}
		sb.deleteCharAt(sb.length()-1).append(")\n");
		sb.append("<selectKey resultType=\"int\" keyProperty=\"id\">\n");
		sb.append("select @@identity");
		sb.append("\n</selectKey>");
		return sb.toString();
	}
}
