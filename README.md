# 欢迎使用 Common-Mvc
基于maven的spring4.3+mybatis3.4+Swagger2.6的后台整合，用于快速构建中小型API、RESTful API项目，该项目简单、快速、易扩展；使我们摆脱那些重复劳动，专注于业务代码的编写。本项目主要实现数字货币的量化交易基础框架及交易所接口的实现，使用者只写策略逻辑。项目为一拖N的实现，即一个中心服务器，N个托管者节点，每个节点可以运行多个策略，由于大多交易所对请求有限制，建议每个节点同一家交易所同时运行的策略3个以下。


### 快速开始
- clone本项目，创建下面的数据库和表
- 使用IDE导入本项目，使用maven方式导入项目
- 配置`jdbc.properties`下面的数据库相关信息（如果你需要使用mybitis逆向插件，也需要配置`generatorConfig.xml`这个文件中的数据库信息）
- 使用maven编译后，配置tomcat并部署
- 启动tomcat
- 根据需求进行快速迭代开发

### 需要配置的文件
- 所有的.properties文件

### 部分截图
![image](https://raw.githubusercontent.com/2bcoin/zheshiyigeniubidexiangmu/master/simg/1.png)
![image](https://raw.githubusercontent.com/2bcoin/zheshiyigeniubidexiangmu/master/simg/2.png)
![image](https://raw.githubusercontent.com/2bcoin/zheshiyigeniubidexiangmu/master/simg/3.png)
![image](https://raw.githubusercontent.com/2bcoin/zheshiyigeniubidexiangmu/master/simg/4.png)

### 开发者建议
- 表名，建议使用小写，多个单词使用下划线拼接
- entity内成员变量建议与表字段数量对应
- 前端统一使用`Content-Type=application/json`传参;`controller`层统一使用`@RequestBody`入参,参数可以使用Map接收,也可考虑封装成VO对象(推荐)
- 需要工具类的话建议先从`common/utils`中找，实在没有再造轮子或引入类库，尽量精简项目
- 开发规范建议遵循阿里巴巴Java开发手册（[最新版下载](https://github.com/lihengming/java-codes/blob/master/shared-resources/%E9%98%BF%E9%87%8C%E5%B7%B4%E5%B7%B4Java%E5%BC%80%E5%8F%91%E6%89%8B%E5%86%8CV1.2.0.pdf))
- 建议在公司内部使用ShowDoc、Swagger2 、RAP等开源项目来编写、管理API文档
- 页面常量信息建议放在`constants`表;如民族/地址/证件类型/性别等;
- 所有项目文档放置在`/resources/archives`目录下
- 建议所有DTO/BO放在相应service目录下;VO放在相应controller目录下
- 修改已有表结构时,不建议修改以下字段(id,enabled,deleted);因为这些字段已在开发中用到
- 增删改方法命名分别以`insert/delete/update`打头


### 相关环境(推荐使用环境)
- OS Microsoft Windows 10 Pro
- Editor IntelliJ IDEA
- Java 8
- SpringMVC 4.3
- Mybitis 3.4
- Mysql 5.5.50
- Maven 3.5.3
- Git 2.14.1
- Tomcat 7.0.85
- Swagger 2.6.1
- Restful interface


### 注意事项
- 使用mybaitis-generator插件生成dao层时请先删除原来的文件,不然生的的内容会追加到源文件中,出现代码重复
- 下载后如打不开swagger2文档，可能需要修改`webapp/common-core-swagger-ui/config.js`文件中得地址
