# RxJavaTest
study Rxjava 
## Rxjava具体方法
##### 1、create

##### 2、from  fromArray  fromIterable

##### 3、just

##### 4、repeat

##### 5、range  

##### 6、interval 轮询

##### 8、filter 过滤

##### 10、first  last

##### 11、map 
发送数据按照函数方法进行更换函数处理后发送的是一个基本类型的数据

##### 12、flatMap
flatMap将一个发射数据的Observable变换为多个Observables，然后将它们发射的数据合并后放进一个单独的Observable。

发送数据按照函数方法进行更换，函数处理后返回Observable的数据

##### 13、concatMap
concatMap和flatMap的作用是一样的，它的结果是严格按照上游发送的顺序来发送的

##### 14、Buffer

Buffer操作符会定期收集Observable的数据放进一个数据包裹，然后发射这些包裹，并不是一次发射一个值
Buffer操作符将一个Observable变换为另一个，原来的Observable正常发射数据，变换产生的Observable发射这些数据的缓存集合。如果原来的Observable发射了一个onError通知，Buffer会立即传递这个通知，而不是首先发射缓存的数据。

##### Buffer变体

* Buffer(count) 以列表List的形式发射非重叠的缓存，每一个缓存至多包含来自原始Observable的count项数据
* Buffer(count,skip) 从原始Observable的第一项数据开始创建新的缓存。每当接收到skip数据，用count项数据来填充‘

##### 15、Scan

Scan连续地对数据序列的每一项应用一个函数，然后连续发射结果
Scan操作符对原始Observable发射的第一项数据应用一个函数，然后将这个函数的结果作为自己的第一项数据发射。将函数的结果同第二项数据一起填充给这个函数来产生自己的第二项数据。持续进行这个过程来产生剩余的数据序列。(数据累加求和例子)

##### 16、Zip

ZIP通过一个函数将多个Observable发送的事件结合到一起，然后发送这些组合到一起的事件。按照严格的顺序应用这个函数，只发射与发射项最少的那个Observable一样多的数据

可以用来处理2个请求同时需要的数据

##### 17、take

##### 18、skip
skip(n)操作符，会跳过前n个结果

##### 19、replay
relay(n)，使得即使在未订阅时，被订阅者已经发射了数据，订阅者也可以收到被订阅者在订阅之前最多n个数据。

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
        
    第二个会接收到 “second”，“thread”
    
##### 20、concat
连接两个被订阅者，订阅者将会按照a->b的顺序收到两个被订阅者所发射的消息

    var obser1 = Observable.range(1,5)
    var obser2 = Observable.range(6,15)
    Observable.concat(obser1,obser2).subscribe { Log.d(TAG, "concat 接收到 $it")}
    
    结果是：  concat 接收到 1 到 20
    
    
    val just1 = Observable.just(1, 2, 3)
    val just2 = Observable.just(4, 5, 6)
    Observable.concat(just1,just2).subscribe {  Log.d(TAG, "concat 接收到 $it")}
    
    结果是：  concat 接收到 1 到 6

##### 21、merge
merge和concat类似，也是用来连接两个被订阅者，但是它不保证两个被订阅发射数据的顺序。
##### 22、throttleFirst
throttleFirst用来解决抖动的问题，我们可以设置一段时间，之后它会发射固定时间长度之内的第一个事件，而屏蔽其它的事件。
##### 23、throttleLast
##### 24、debounce
Observable.debounce(float value, Unit unit)，dounce，被订阅者在收到要发射消息的指令后，会等待一段时间，如果在这段时间内没有新的消息发射指令，那么它会发射这条消息，否则它会丢弃掉它，从这个新收到的值开始重新等待设置的时间长度。
##### 25、window
window(n,TimeUnit.SECONDS)每隔ns集中发射这段时间内的数据，而不是一有数据就发射。
window(n) 类似buffer
##### 26、SwitchMap
当上一个任务尚未完成时，就开始下一个任务的话，上一个任务就会被取消掉。如果，都是在同一个线程里跑的话。那么，这个操作符与ConcatMap就无异了，都是先跑的先到。只有在不同的线程里跑的时候，即线程方案为newThread的时候。才会出现这种情况。

