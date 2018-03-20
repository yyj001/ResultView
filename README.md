**关注我**

githup:[https://github.com/yyj001](https://github.com/yyj001)

csdn: [http://my.csdn.net/weixin_38379772](http://my.csdn.net/weixin_38379772)

ResultView
====
# 简介 introduction
对处理事件响应的一个小动画库
an animation showing the result of event

*  **例子 demo**

<img src="image/animation.gif" height="576" width="324" style="margin-left:100"/>

**一些特点 figures：**
1. 可以自定义颜色 change the color
2. 自定义时长 change duration
3. 自定义内部尺寸 change size

### 使用how to use

 - **添加jitpac仓库 Add it in your root build.gradle at the end of repositories:**
 ```
	    allprojects {
		   repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	    }
```
 -  **Add the dependency**
```sh
	dependencies {
	        compile 'com.github.yyj001:ResultView:v1.0'
	}
```

- **add ResultView to your layout**
  
```xml
   <com.ish.mylibrary.mView.ResultView
        android:layout_width="100dp"
        android:layout_height="100dp"
        />
```
- **xml属性 some properties**
```xml
<com.ish.mylibrary.mView.ResultView
        android:layout_width="100dp"
        android:layout_height="100dp"
        android:layout_centerInParent="true"
        app:back_color="@color/white"
        app:theme_color="@color/green"
        app:mType="TYPE_FAILD"
        app:duration="2000"
        app:ring_width="3dp"
        />
```
- **一些改变控件的方法 some jave methods**
```java
        resultView.setBackColor(R.color.red);
        resultView.setThemeColor(R.color.white);
        resultView.setDuration(2000);
        resultView.setRingWidth(3);
        resultView.setType(ResultView.TYPE_WARN);
```
- **开始与重置动画play and reset the animation**
```java
        resultView.play();
        resultView.reset();
```

‘’
