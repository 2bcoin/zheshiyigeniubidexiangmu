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

### 项目说明
- 待添加

### 交流
- 待添加
