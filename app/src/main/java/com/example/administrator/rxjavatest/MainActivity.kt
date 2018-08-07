package com.example.administrator.rxjavatest

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import java.util.*
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    var TAG = "MainActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

    }

    fun showRxJavaTest(v : View){
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
        //14、buffer
        //先把发送数据到一个缓冲区域，当数据达到count个的时候在发送一个list集合
        Observable.just(1,2,3).buffer(1)
                .subscribe {
                    Log.d(TAG,"buffer 接收到 ${it.toString()}")
                }
        //15、scan
        //对发送的数据按照函数方法处理后的结果向后通知，并且将得到的结果与再次发送的数据按照函数方法处理后继续向后通知
        //结果
        //scan 接收到 1
        //scan 接收到 3
        //scan 接收到 6
        //scan 接收到 10
        Observable.just(1,2,3,4)
                .scan { t1: Int, t2: Int ->
                    t1 + t2
                }.subscribe {
                    Log.d(TAG,"scan 接收到 $it")
                }
        //16、zip
        //将多个发送源数据按照函数方法处理后依次发送通知，并且只会处理少的那个数据源
        //可以用来处理2个请求同时需要的数据
        var obser1 = Observable.range(1,5)
        var obser2 = Observable.range(6,15)
        Observable.zip(obser1,obser2, BiFunction<Int,Int,String> { t1, t2 ->
            "$t1$t2"
        }).subscribe {    Log.d(TAG,"zip 接收到 $it") }
        //17、take
        Observable.just("1",2,3,"4")
                .take(3)
                .subscribe {    Log.d(TAG,"take 接收到 $it") }
        //18、skip
        //skip(n)操作符，会跳过前n个结果
        Observable.just(1,2,3,4)
                .skip(2)
                .subscribe { Log.d(TAG,"skip 接收到 $it")  }

        //19、replay
        //relay(n)，使得即使在未订阅时，被订阅者已经发射了数据，订阅者也可以收到被订阅者在订阅之前最多n个数据。
        var connectableObservable = Observable.create<String> {
            it.onNext("start")
            it.onNext("second")
            it.onNext("thread")
            it.onComplete()
        }.replay(2)
        connectableObservable.connect()
        connectableObservable.subscribe(object : Observer<String>{
            override fun onComplete() {
                Log.d(TAG,"onComplete...")
            }

            override fun onSubscribe(d: Disposable) {
            }

            override fun onNext(t: String) {
                Log.d(TAG,"onNext...$t")
            }

            override fun onError(e: Throwable) {
            }

        })
        connectableObservable.subscribe { Log.d(TAG, "replay 接收到 $it") }

        //20、concat
        Observable.concat(obser1,obser2).subscribe { Log.d(TAG, "concat 接收到 $it") }

//        val just1 = Observable.just(1, 2, 3)
//        val just2 = Observable.just(4, 5, 6)
//        Observable.concat(just1,just2).subscribe {  Log.d(TAG, "concat 接收到 $it")  }

        //21、merge和concat类似
        //merge和concat类似，也是用来连接两个被订阅者，但是它不保证两个被订阅发射数据的顺序。

        //22、throttleFirst
        //结果 1
        Observable.just(1,2,3)
                .throttleFirst(1,TimeUnit.SECONDS)
                .subscribe { Log.d(TAG, "throttleFirst 接收到 $it")  }
        //23、throttleLast

        //24、debounce
        //结果 3
        Observable.just(1,2,3).debounce(1,TimeUnit.SECONDS)
                .subscribe {  Log.d(TAG, "debounce 接收到 $it")   }

        //25、window
//        Observable.just(1,2,3).window(3).subscribe {
//            it.subscribe {  Log.d(TAG, "window 接收到 $it") }
//        }
        Observable.just(1,2,3).window(1,TimeUnit.SECONDS).subscribe {
            it.subscribe {  Log.d(TAG, "window 接收到 $it") }
        }
    }

}
