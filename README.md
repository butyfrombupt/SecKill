# SecKill
## 电商秒杀项目
该项目是慕课网的一实战项目，具有一定的学习意义
- [介绍](#介绍)
- [系统架构](#系统架构)
- [模块介绍](#模块介绍)

## 介绍
本项目主要是模拟应对大并发场景下，如何完成商品的秒杀，以及针对秒杀场景下为应对大并发所做的优化。

项目的技术结构如下图所示：
![结构图](https://github.com/butyfrombupt/SecKill/blob/master/src/main/resource/static/img/projectStructure.png)

## 系统架构
前端技术 ：Bootstrap + jQuery + Thymeleaf

后端技术 ：SpringBoot + MyBatis + MySQL

中间件技术 : Druid + Redis + RabbitMQ
## 模块介绍

登录部分主要有以下几个部分：

### 明文密码两次MD5处理
客户端：C_PASS=MD5(明文+固定salt)
服务端：S_PASS=MD5(C_PASS+随机salt)
加密：MD5Util封装的两个方法

第一次 （在前端加密，客户端）：密码加密是（明文密码+固定盐值）生成md5用于传输，目的，由于http是明文传输，当输入密码若直接发送服务端验证，此时被截取将直接获取到明文密码，获取用户信息。

加盐值是为了混淆密码，原则就是明文密码不能在网络上传输。

第二次：在服务端再次加密，当获取到前端发送来的密码后。通过MD5（密码+随机盐值）再次生成密码后存入数据库。

防止数据库被盗的情况下，通过md5反查，查获用户密码。方法是盐值会在用户登陆的时候随机生成，并存在数据库中，这个时候就会获取到。

第二次的目的： 黑客若是同时黑掉数据库，通过解析前端js文件，知道如果md5加密的过程，就知道此时用户的密码。

但是此时我们要是在后端加入随机盐值和传输密码的md5组合，黑客是无法知道通过后端密码加密过程的，从而无法知道密码。

第一次作用：防止用户明文密码在网络进行传输
第二次作用：防止数据库被盗，避免通过MD5反推出密码，双重保险

### JSR303参数检验和全局异常处理器
JSR303 是一套 JavaBean 参数校验的标准，它定义了很多常用的校验注解。如@NotNull、@Email、@Max等。

在这个系统中，我们自定义了一个注解@IsMobile完成手机号码的参数检验,@IsMobile的校验处理器为IsMobileValidator。

定义一个全局异常GlobalException和全局异常处理器GlobalExceptionHandler，可以完成系统异常的捕获和异常的统一处理。

### 分布式Session
在用户登录成功之后，将用户信息存储在redis中，然后生成一个token返回给客户端，这个token为存储在redis中的用户信息的key，这样，当客户端第二次访问服务端时会携带token，首先到redis中获取查询该token对应的用户使用是否存在，这样也就不用每次到数据库中去查询是不是该用户了，从而减轻数据库的访问压力。

### 全局异常统一处理
exceptionHandler统一策略处理
通过拦截所有异常，对各种异常进行相应的处理，当遇到异常就逐层上抛，一直抛到最终由一个统一的、专门负责异常处理的地方处理，这有利于对异常的维护。

秒杀部分主要有以下几个部分：
这里主要讨论一下优化：
### 页面级缓存+URL缓存+对象缓存
所谓页面缓存，指的是对于服务端的请求，不直接从系统中获取页面资源，而是先从缓存中获取页面资源，如果缓存中不存在页面资源，则系统将渲染页面并存储页面到缓存中，然后将页面返回。

来看商品列表页的请求过程；请求到服务端，服务端查询数据库中的商品列表信息然后存储在Model对象中，Thymeleaf页面获取在Model对象中的商品列表信息然后动态渲染，再返回给客户端。如果每次请求都做这样的工作，势必会对服务器和系统造成一定的压力（系统的压力主要来源于每次Thymeleaf页面获取在Model对象的信息都要渲染一次），所以可以做一个页面级的缓存，减轻数据库和系统的压力。

在本项目中，我们对商品列表页做一个缓存，因为商品列表页的数据相对表话不是太频繁，所以将其缓存在redis中，这样不用每次都查询数据库中的商品信息，然后再使用Thymeleaf渲染返回，而是直接从redis中返回。另外，由于商品列表页请求返回的是html，所以这里使用ThymeleafViewResolver手动渲染页面，这样就可以将页面直接通过系统返回给客户端。

而所谓URL缓存，实际上和页面缓存是一样的，在本项目中，我们对商品详情页做了缓存，商品详情页的请求需要goodsId，也就是说，对每一个goodsId都做了一个缓存，其他的和商品列表页的缓存思路是一致的，只不过商品取详情页是需要动态的根据goodsId来取。

通过上面的缓存差异可知，URL缓存和页面缓存的不同之处在于，URL缓存需要根据URL中的参数动态地取缓存，而页面缓存则不需要。

一般来讲，URL缓存和页面缓存的缓存时间都比较短。在本项目中，我们设置商品详情页和商品列表页的缓存时间为60s。

对象缓存是一种更细粒度的缓存，顾名思义就是对对象就行缓存，在本项目中，我们将UserService中getMiaoshaUserById获取的对象进行了缓存，另外，UserService中getMisaoshaUserByToken获取的对象也做了一个缓存。UserService中updatePassword方法同样做了对象级的缓存，但是值得注意的是，这个方法里对缓存中的数据进行了更改，因此，需要将将更改的对象先从缓存中取出，然后删除缓存中对应的数据，然后在将新的数据更新到数据库中，再将数据缓存到redis中。

为什么要先删除缓存在写入缓存呢？因为如果不删除，以前的请求仍然可以访问通过原来的token访问到以前的数据，除了造成数据的不一致还会有安全问题，所以需要删除以前的缓存在写入新的缓存。

页面静态化，前后端分离
页面静态化指的是将页面直接缓存到客户端。常用的技术有Angular.js，Vue.js。

其实现方式就是通过ajax异步请求服务器获取动态数据，对于非动态数据部分缓存在客户端，客户端通过获取服务端返回的json数据解析完成相应的逻辑。

在本项目中，我们对商品详情页和订单详情页做了一个静态化处理。

对于商品详情页，异步地从服务端获取商品详情信息，然后客户端完成页面渲染工作。除此之外，对于秒杀信息的获取也是通过异步获取完成的。例如，当秒杀开始时，用户执行秒杀动作，客户端就会轮询服务器获取秒杀结果。而不需要服务器直接返回页面。

而对于订单详情页，实际上也是同样的思路。

### 本地标记 + redis预处理 + RabbitMQ异步下单 + 客户端轮询
通过三级缓冲保护，1、本地标记 2、redis预处理 3、RabbitMQ异步下单，最后才会访问数据库，这样做是为了最大力度减少对数据库的访问。

在秒杀阶段使用本地标记对用户秒杀过的商品做标记，若被标记过直接返回重复秒杀，未被标记才查询redis，通过本地标记来减少对redis的访问
抢购开始前，将商品和库存数据同步到redis中，所有的抢购操作都在redis中进行处理，通过Redis预减少库存减少数据库访问
为了保护系统不受高流量的冲击而导致系统崩溃的问题，使用RabbitMQ用异步队列处理下单，实际做了一层缓冲保护，做了一个窗口模型，窗口模型会实时的刷新用户秒杀的状态。
client端用js轮询一个接口，用来获取处理状态

### Nginx水平扩展
nginx.conf中多配置几个反向代理，可以分配权重，分配到不同的服务器中，LVS能在nginx上的基础上，再多配置几个nginx

### 超卖问题解决
描述：比如某商品的库存为1，此时用户1和用户2并发购买该商品，用户1提交订单后该商品的库存被修改为0，而此时用户2并不知道的情况下提交订单，该商品的库存再次被修改为-1，这就是超卖现象

对库存更新时，先对库存判断，只有当库存大于0才能更新库存
对用户id和商品id建立一个唯一索引，通过这种约束避免同一用户发同时两个请求秒杀到两件相同商品
实现乐观锁，给商品信息表增加一个version字段，为每一条数据加上版本。每次更新的时候version+1，并且更新时候带上版本号，当提交前版本号等于更新前版本号，说明此时没有被其他线程影响到，正常更新，如果冲突了则不会进行提交更新。当库存是足够的情况下发生乐观锁冲突就进行一定次数的重试。
