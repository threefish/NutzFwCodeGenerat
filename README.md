# NutzFw Code Generat
## 代码生成器

### 模版修改 
   -  Settings >> Editor >> File and Code Templates >> Other >> NutzFw
   
- [NutzFw项目模版下载地址](https://github.com/threefish/NutzFwCodeGenerat/tree/master/release)
  - 如不是[NutzFw](https://github.com/threefish/NutzFw)项目，请务必修改模版
 
 ### 模版语法采用BEETL
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
    
- 变量列表 -- 使用#[base.变量名]取用

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
    | richText | boolean | 是否有UE富文本编辑器 |
    | attachment | boolean | 是否有附件上传 |
    | fields | List<JavaFieldVO> | 当前实体类的字段列表 |
    
- 当前实体类的字段信息
    
    | 变量名   |      类型      |  意义 | 默认值 |
    |----------|:-------------:|------:|------:|
    | name | String| JAVA字段名称 | 无|
    | primaryKey| boolean| 主键 |false|
    | comment | String| 字段描述  |无|
    | columnName| String| 数据库字段名 |无|
    | type |String | JAVA字段类型 |无|
    | fullType |String | JAVA字段类型包含引用 |无|
    | date | boolean| 是否日期 |false|
    | 以下为NutzFw项目专用|
    | dict |boolean | 是否字典 |false|
    | dictCode |String | 字典Code |无|
    | required |boolean | 是否必填必选字段 |false|
    | text |int | 文本类型 输入框2-多行文本框3-百度UE4 |false|
    | attachment |boolean | 附件类型|false|
    | attachmentType |int | 附件类型-多附件0-单附件1|0|
    | attachmentAllIsImg |boolean | 附件全部是图片|false|
    | attachSuffix |String | 限制附件格式(jpg,png)|xlsx,xls,png,jpg,doc,docx|
    | placeholder |String |提示信息|无|
    | maxLength |int |文本最大长度|10|
    | oneOne |boolean |是单表关联|false|
    | oneOneField |String |表关联字段||
    | oneOneClassName |String |表关联类||
    | oneOneClassQualifiedName |String |表关联类全路径||
      
