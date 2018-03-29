# 初始化工作  
使用这个版本发布系统，需要进行配置的初始化工作，主要是分为2部分，一部分是部署发布系统，一部分是服务器初始化。  

## 部署发布系统  
### web容器  
推荐使用tomcat7+版本，发布系统的日志推送使用到了websocket，所以根据目前所了解，tomcat7/8 是比较好的选择。  
###部署  
发布系统主要是分为2个web模块，一个是发布系统，另外一个是log agent，专门用于接收日志。所以部署的时候，是要发布2个子系统，域名配置相应地也要2个。  

## 初始化  
### 发布系统所在的服务器
主要是java环境（1.8+） + tomcat + ansible + web用户配置。  
**ansible**  
ansible在发布系统里面充当非常重要的角色，他负责将编译脚本、发布脚本、打包文件传输到目标服务器，所以必须配置和安装ansible。  
````
ansible依赖环境安装
yum  -y  install  libffi  libffi-devel  pip  python-devel  python
pip  install  paramiko  PyYAML  Jinja2  httplib2
yum  install  libselinux-python  -y
pip  install  --upgrade  pip
#yum  install  ansible  -y
pip  install  ansible

ansible  all  -m  ping  --ask-pass

mkdir  /etc/ansible
cat  >  /etc/ansible/ansible.cfg  <<EOF
[defaults]
#pipelining  =  true
host_key_checking  =  False
#forks  =  100
inventory            =  /etc/ansible/hosts
remote_port        =  32200
log_path        =  /var/log/ansible.log
[privilege_escalation]
#become=True
#become_method=su
#become_user=root
#become_ask_pass=True
[paramiko_connection]
[ssh_connection]
[accelerate]
[selinux]
EOF

echo  "127.0.0.1"  >  /etc/ansible/hosts
````

**配置编译服务器的内网ip**：在发布系统的机器配置编译服务器的IP，具体的文件是 **/data/ansible/hosts.inventory**  
需要写入的内容如下：  
```
[all]
10.10.11.124
```
10.10.11.124 是编译服务器的IP  

**web用户配置**  
发布系统默认所有系统采用名为web的用户进行发布，调用ansible进行文件传输也是通过web用户进行传输，所以需要配置web用户相关的权限，建议让运维统一配置。  


### 编译服务器的从初始化  
**1.创建web用户**
将web用户的密钥同步到这台机器上  

**2.配置web用户的目录权限**  
web用户的操作目录脚本都在/data/目录，按照功能来讲分为以下几个目录  
**/data/project/**:主要是放具体项目的部署打包文件目录，比如passport、pay、public-service等具体项目的目录，
每个目录下会有不同的模块目录，模块目录下有具体的打包文件。  
  
**/data/project/** 目录下还有一个比较特殊的目录，**shell** ，即/data/project/shell，这个目录是存放具体各个项目
发布的shell脚本，发布系统通过执行这些shell脚本来实现启动、回滚、停止、重启的功能。  

**/data/webapps/**:该目录是resin服务器相关项目部署解压的目录，一个war包被打包传输到/data/project/目录下后
resin启动该项目后会将war包内容解压放到/data/webapps/目录下，子目录是发布系统项目配置的域名作为下一级目录，比如
"passport.beiliao.com"  

**所以总的来说，必须保证web用户是/data/project/ /data/webapps/目录的所有者，拥有rwx权限**