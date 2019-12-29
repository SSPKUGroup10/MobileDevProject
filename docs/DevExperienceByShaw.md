
**记录一下开发过程中遇到的问题**

## 参考资料
- [material design](https://md.maxoxo.design/components/cards.html#)
- [关于material design的介绍](http://www.uisdc.com/material-design-knowledge)
- [material design API](https://material-io.cn/develop/android/components/material-card-view/)

# 组件和组件库

## 底部导航栏 BottomNavigationView
- 里面的item通过menu文件配置；
- res下新建一个menu文件夹，将每个menu.xml放在里面
- menu文件内通过item配置各个按钮
- 每个item配置icon和title
- 导航栏控件有默认的区分active和inactive的图标，如果想自定义要配置一个selector
- as中有自带的icon，在drawable下new-Vector Asset选择，可以自由修改颜色和大小
- 也可以在java文件中通过addItem(new BottomNavigationItem)来添加
- bottomnavigationview+viewpager+fragment来实现界面切换

## 卡片 MaterialCardView
- 每个卡片里面可以包含一个组件，它只是一张卡片，而不是卡片列表
- 卡片可以在z轴上上浮（app:cardElevation="10dp"）
- 设置margin让它更好看

## 选项卡 TabLayout
- tablayout+viewpager+fragment来实现选项卡界面切换。
- fragment需要自己配置并绘制布局，如果每个选项内容不同还要配置不同的fragment

## 列表 RecyclerView
- 手动注册点击时间
- 配置adapter和viewholder来使用
- item的布局也要额外生成
- **遇到的终极bug：item的高度不能match_parent!**

## 时间选择 TimePicker & TimePickerDialog
- TimePicker是一个直接展示在界面上的时间选择组件；
- 一般用到的都是在某个需要选择时间的场景，点击按钮弹出一个对话框，在里面显示时间。
- 给按钮添加点击事件，点击事件中new一个TimePickerDialog，它自带的两个参数表明了选中的hour和minute。

## 一个组件库 com.github.rey5137:material:1.2.5
- 最新的1.3.0引入后会发生错误，issue中提供的方法是改成1.2.5，的确可以成功引入
- 尝试了一下EditText，提供的wiki不太会用，不知道是不是版本号的问题

## 约束性布局
- 使用baseline约束来确保同一行中的两个控件文字对齐（例如一个按钮一个文本框在同一行这种）
- 给guideline

# 开发技巧

## intent传递类
- 如果需要携带对象作为putExtra的内容，那么这个对象要实现序列化
- 如果是fragment向外传递信息，那么参数就要变成getActivity()，来替代活动中的MainActivity.this。

## 使用okhttp3异步请求访问网络
- okhttp3库中提供异步请求的方法，不需要自己开子线程。
- 封装了两个发送get和post异步请求的方法。这里的post上传的是body中的数据，格式为json。
- 别忘了给android加上访问INTERNET的权限。
```
    public static void getOkHttpRequest(String address, okhttp3.Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        okHttpClient.newCall(request).enqueue(callback);
    }

    public static void postOkHttpRequest(String address, String jsonInfo, Callback callback){
        OkHttpClient okHttpClient = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType
                .parse("application/json; charset=utf-8"), jsonInfo);
        Request request = new Request.Builder().url(address)
                .post(requestBody)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
```
- 后面又出现了在请求头中写入内容的需求，方法是requestbody调用addHeader(key, value)方法。
- 异步请求中不能访问主线程中的变量，但是事实上很多时候需要用到的变量都可以在子线程（onResponse和onFailure）中定义，需要在主线程中进行ui操作的功能再调用runOnUiThread方法。

## 组件自动绑定库 butterknife
- 替代findViewById，减少冗余代码，貌似还能提高性能
- 配置：之前网上的8.8.0版本和androidx有不兼容的地方，经查资料改成10.0.0就可以使用了。
```
implementation 'com.jakewharton:butterknife:10.0.0'
annotationProcessor 'com.jakewharton:butterknife-compiler:10.0.0'
```
- as插件：File | Settings | Plugins 搜索butterknife，下载安装Android butterknife Zeleney，就可以在generate选项中自动生成组件引用和绑定对应的ui控件。快捷键alt+insert。

## 定义接口在完成一些操作
- 三个界面的自定义fragment都继承自android的fragment类，但是三个自定义frag中都要实现一个getTitle()方法，而父类中没有这个方法。为了把三类自定义frag都放在一个list中，可以让三个自定义frag都实现一个接口I，把List< Fragment >改成List< I >，在接口中定义getTitle()方法。

## Adapter的作用
数据源中，有一个数据列表，和指向列表当前position的指针cursor
ui中，是显示数据的listview（或recycleview）
adapter负责适配这二者，将数据源中的数据映射到view界面上。

## 数据多做封装
- 各种间距、字号、颜色等，根据应用场景不同命名成dimen变量，例如一列按钮之间的间距；每个布局和手机边框的间距等，这样保证整体风格的统一，而且修改起来也方便。
- 后端代码中用到的常量写到常量文件中，例如本地存储SP的文件名等。

## 移动端和数据库的统一
- 一定要提前进行软件需求分析并且很完整的描述清楚各个实体的属性、需要进行的逻辑等。不然后面的统一真的是令人发指。

# bug调试

## R不存在
- 默认的activity文件直接放在java包下，如果要新建一个activity package，需要在activity.java中手动导入r包
- 在File | Settings | Editor | General | Auto Import中可以勾选自动导入

## 使用ButterKnife
- 千万别忘了除了快捷简便的自动生成，还要再create中写上
```
    ButterKnife.bind(this);
```
- 如果是在fragment中写，要在createView中写上
```
    ButterKnife.bind(this, view);
```

## 让底部按钮随着页面的滑动一起变化
要重写addOnPageChangeListener中的onPageSelected方法，监听当前的position来设置menuitem的index。

## 访问网络
网络请求不能放在子线程中，使用okhttp3提供的异步请求方法可以异步执行，不需要自己再开启子线程。


# 开发流程
12.9 下一步内容：
- 写一个onResponse的接口回调，拿到响应的信息；（handler应该是更好的方法）
- 创建圈子的时候携带创建者信息，把圈子内容发送给圈子界面。
- 后台自动更新token。

# 功能模块
## 登录注册

## 创建新圈子
信息不全不能创建


## 功能逻辑

### 登录界面
主页面

点击注册：跳转到注册界面

输入用户名和密码，点击登录，发送网络请求。若登录成功，会创建一个User对象，存储的内容包括用户id,name,密码，加入的圈子和未加入的圈子两个列表。

将返回的token和user信息存储到sp中。

token应该创建一个服务来在后台更新，从而避免每次总是进行判断。

**此时还通过Intent进行了传输（key = user_info），两种方式选择其中一个。**

**登录失败弹出提示信息**

### 注册界面
从登录界面进入

输入用户名和密码。判断两次输入密码是否一致，如果不一致会弹出提示信息。

点击注册发送网络请求。提交用户名和密码，在数据库中创建该用户，生成id。注册成功则返回到登录界面，注册失败会留在这里。

**注册成功后将用户名密码自动填充到登录界面中，注册失败时弹出提示信息**

### Home界面
- 从登录界面跳转而来，接收一个Intent携带消息user_info，含有当前用户的完整User类信息。
- 包含三个fragment：主界面Home，探索圈子界面Explore，个人信息界面My。
- 默认界面时HomeFragment
- **加载Home界面的时候，三个fragment都会被同时初始化，要确保此时生命周期里执行的代码中操作的内容都能访问到，都不是null**

初始内容：
- 创建了一个Application对象来存储User信息和token信息（可能不需要了）
- 从登录界面读取Intent信息user_info来获得User信息，并存储Application对象
- 从文件中读取token并存入Application对象

界面初始化：
- 给当前界面的ViewPager配置一个Adapter（BodyVpAdapter)，给适配器添加三个fragment的实例
- 创建fragment实例的时候将user对象传递了进去作为信息传递。
    - **这样好像不太好，会导致每个界面中的user信息相互独立，感觉可以改成统一从sp读取**
- 给ViewPager配置监听器，使得底部导航栏的图标会随着fragment的切换而切换。
- 给底部导航栏配置监听事件，确定选中哪个item显示对应的fragment。

### HomeFragment
- 创建实例的时候接收一个User参数，存储当前用户的信息。

进入的时候自动加载当前用户的已经加入的圈子列表，使用RecyclerView存放。

自动加载，应该在当前界面显示的时候去执行加载并显示列表的代码。不需要考虑更新的问题，通过确保存储在sp中的圈子列表是即时更新的，来确保当前界面加载的是最新的。
**有一个问题，如何对当前列表进行更新**

事实上这个界面中没有用到user的信息。只有刷新的时候，传递用户数据。但是我觉得可以改成从文件中读取。（不过一般用户名和密码不会修改，应该不会存在不一致的问题）。

**圈子信息目前是存放在文件当中的：**
- 这样的话，必须确保每次创建和加入圈子之后，都要更新存储在sp中的圈子信息
- 那么在两个地方进行这个操作：创建圈子活动中创建成功返回主界面的时刻（类似于登录成功进入主界面的时刻），在圈子界面点击加入的时刻。
- **这两个地方再检查一下**

刷新功能：
- 如果前面的逻辑正确的话，事实上是不需要刷新功能的。刷新功能可能放到探索其他圈子的界面更加有用。
- 当前的刷新功能是，利用当前的用户信息去发送网络请求，和登录界面是相同的逻辑，拿到用户的圈子信息，并存储到文件中。
- **关键在于，刷新之后，如何更新界面中显示的列表中的内容。**

界面初始化：
- 给当前界面的RecyclerView定义线性布局管理器，配置adapter（ListRcvAdapter），将infoList传递给它
- 创建圈子按钮添加点击事件，用startActivityForResult的形式来进入创建圈子界面，返回的状态码是1.

接收返回信息：
- 接收来自创建圈子界面返回的新创建的圈子信息，并将信息添加到sp中。
- 通过addGroupItem方法来将新圈子添加到列表中，这里能够即时响应，是否应该把这个思路应用到完全刷新整个圈子的内容中？

### ExploreFragment

### MyFragment
这个界面中可能会修改user信息，修改的时候要相应存储到文件中

### 创建圈子界面
从HomeFragment中点击创建圈子按钮跳转，启动方式为startActivityForResult，Intent中不传递任何信息

文本框：
填写圈子名称，圈子描述，圈子规则，设定打卡的开始时间和结束时间。如果信息填写不完整则不能创建，会弹出提示信息。

点击创建，发送网络请求，请求需要用到用户信息，圈子信息，token。
- 圈子信息从当前界面中获得，并在此时进行完整性检查
- 用户信息从sp中读取。注意读取userid是用于给圈子补充masterId的属性，而不是网络请求的需要
- token信息从


