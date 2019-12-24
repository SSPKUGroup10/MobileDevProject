
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
- 异步请求中不能访问主线程中的变量，但是事实上很多时候需要用到的变量都可以在子线程（onResponse和onFailure中）

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
