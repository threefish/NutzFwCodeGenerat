# NutzFw Code Generat
## 代码生成器

- 模版语法采用BEETL
```java   
        /** 模板占位起始符号 */
       	String placeholderStart = "#[";
       	/** 模板占位结束符号 */
       	String placeholderEnd = "]";
       	/** 控制语句起始符号 */
       	String statementStart = "<##";
       	/** 控制语句结束符号 */
       	String statementEnd = "##>";
```

- 变量列表

    | 变量名   |      类型      |  意义 |
    |----------|:-------------:|------:|
    | entityName | String| entityName |
    | entityPackage| String| entity Package |
    | serviceFileName | String| service File Name  |
    | servicePackage| String| service Package |
    | serviceImplFileName |String | serviceImpl File Name |
    | serviceImplPackage |String | serviceImpl Package |
    | actionFileName | String| action File Name |
    | actionPackage |String | action package |
    | funName |String | 当前功能名称 |
    | templatePath |String | HTML模版目录  |
    | user | String| 当前用户  |
    | primaryKey |String |主键  |
    | uuid | boolean | 主键是否是UUID |
    | base | 对象 | 以上所有变量都可以使用#[base.变量名]取用 |
    | fields | List<JavaFieldVO> | 当前实体类的字段列表 |
    
- 当前实体类的字段信息
    
    | 变量名   |      类型      |  意义 |
    |----------|:-------------:|------:|
    | name | String| JAVA字段名称 |
    | primaryKey| boolean| 主键 |
    | comment | String| 字段描述  |
    | dbName| String| 数据库字段值 |
    | type |String | JAVA字段类型 |
    | fullType |String | JAVA字段类型包含引用 |
    | date | boolean| 是否日期 |
    | dict |boolean | 是否字典 |
    | dictCode |String | 字典Code |
      
