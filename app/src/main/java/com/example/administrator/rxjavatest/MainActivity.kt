package com.example.administrator.rxjavatest

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.ObservableSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    var TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        showRxJavaTest()
    }

    fun showRxJavaTest(){
        //1、create
        Observable.create(ObservableOnSubscribe<String> {
            for (i in 0..10){
                it.onNext(String.format("发送数据：$i"))
            }
            it.onComplete()
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    Log.d(TAG,"Create $it")
                },{
                    it.printStackTrace()
                })

        //2、fromArray
        val items = ArrayList<Int>()
        for (i in 0..9){
            items.add(i)
        }
        Observable.fromArray(items).subscribe{
            Log.d(TAG,"fromArray ${it.toString()}")
        }

        //3、just
        Observable.just("123","456").subscribe {
            Log.d(TAG,"just 接收到 $it")
        }

        //4、repeat
        Observable.just(3).repeat(3).subscribe(){  Log.d(TAG,"repeat 接收到 $it") }

        //5、range 从一个指定的数字x开发发射n个数字
        Observable.range(5,5).subscribe { Log.d(TAG,"range 接收到 $it") }

        //6、interval 循环间隔2秒不停的发送数据
        var disposable = Observable.interval(2,TimeUnit.SECONDS).subscribe {
            Log.d(TAG,"interval 接收到 $it")
        }
        //取消订阅
        disposable.dispose()

        //7、timer 延迟2秒发送个数据
        Observable.timer(2,TimeUnit.SECONDS).subscribe {  Log.d(TAG,"timer 接收到 $it") }

        //8、fromIterable
        Observable.fromIterable(items).subscribe {
            Log.d(TAG,"fromIterable 接收到 $it")
        }

        //9、filter
        Observable.just("123",4,"567")
                .filter {
                    it != 4
                }.subscribe {
                    Log.d(TAG,"filter 接收到 $it")
                }

        //10、first  last
        Observable.just(1,2,3).first(4).subscribe(Consumer {
            Log.d(TAG,"first 接收到 $it")
        })
        Observable.just(1,2,3).last(4).subscribe(Consumer {
            Log.d(TAG,"last 接收到 $it")
        })
        //11、map
        Observable.fromArray(items)
                .map({
                    var res = ""
                    for (i in it){
                        res += "$i"
                    }
                    res
                }).subscribe {  Log.d(TAG,"map 接收到 $it") }
        //12、flatMap
        Observable.fromArray(items).flatMap{
            Observable.just(123)
        }.subscribe{
            Log.d(TAG,"flatMap 接收到 $it")
        }
        //13、concatMap concatMap和flatMap的作用是一样的，它的结果是严格按照上游发送的顺序来发送的
        Observable.just(1,2,3,4).concatMap {
            var data = ArrayList<String>()
            data.add("t$it")
            Observable.just(data)
        }.subscribe {
            Log.d(TAG,"concatMap 接收到 ${it.toString()}")
        }
    }

}
