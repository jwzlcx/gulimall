package com.atguigu.gulimall.search.thread;

import org.elasticsearch.common.recycler.Recycler;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * <p>Title: ThreadTest</p>
 * Description：
 */
public class ThreadTest {

	public static ExecutorService service = Executors.newFixedThreadPool(10);

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		System.out.println("main....start");
//		CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//		}, service);


		/**
		 * 方法完成后的感知
		 */
//		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//			return i;
//		}, service).whenComplete((result , ex) -> {
//			// 这里能获取异常信息 但是没法修改数据
//			System.out.println("异步任务成功完成了... 结果:" + result);
//			// 感知异常 给出默认结果
//		}).exceptionally(ex -> 10);

		/**
		 * 方法执行完成后的处理
		 */
//		CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//			return i;
//		}, service).handle((result, ex) -> {
//			if(result != null){
//				return result * 8;
//			}
//			if(ex != null){
//				System.out.println("异常为:" + ex);
//				return -1;
//			}
//			return 0;
//		});
//		System.out.println("main....end 结果：" + future.get());

		/**
		 * 线程串行化
		 */
//		CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//			return i;
//		}, service).thenRunAsync(() -> {
//			// thenRunAsync 不能获取执行结果
//			System.out.println("任务2启动了...");
//		},service);

		/**
		 * 使用上一步的结果 但是没有返回结果
		 */
//		CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//			return i;
//		}, service).thenAcceptAsync(res -> System.out.println("thenAcceptAsync获取上一步执行结果：" + res));

		/**
		 * 能接受上一步的结果 还有返回值
		 */
//		CompletableFuture<String> async = CompletableFuture.supplyAsync(() -> {
//			System.out.println("当前线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("运行结束" + i);
//			return i;
//		}, service).thenApplyAsync(res -> {
//			System.out.println("任务2启动了...");
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			return "thenApplyAsync" + res;
//		});
//		System.out.println("thenApplyAsync获取结果:" + async.get());

		/**
		 * 两任务合并
		 */
//		CompletableFuture<Object> async1 = CompletableFuture.supplyAsync(() -> {
//			System.out.println("任务1线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			System.out.println("任务1结束" + i);
//			return i;
//		}, service);
//
//		CompletableFuture<Object> async2 = CompletableFuture.supplyAsync(() -> {
//			System.out.println("任务2线程" + Thread.currentThread().getId());
//			int i = 10 / 2;
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			System.out.println("任务2结束" + i);
//			return "任务合并";
//		}, service);

		// 合并上面两个任务 这个不能感知结果
//		async1.runAfterBothAsync(async2,() ->{
//			System.out.println("任务3开始...");
//		} ,service);

		// 合并上面两个任务 可以感知前面任务的结果
//		async1.thenAcceptBothAsync(async2,(res1, res2) -> {
//			System.out.println("任务3开始... 任务1的结果：" + res1 + "任务2的结果：" + res2);
//		},service);

		/**
		 * 合并两个任何 还可以返回结果
		 */
//		CompletableFuture<String> async = async1.thenCombineAsync(async2, (res1, res2) -> res1 + ":" + res2 + "-> fire", service);
//		System.out.println("自定义返回结果：" + async.get());

		/**
		 * 合并两个任务 其中任何一个完成了 就执行这个
		 */
//		async1.runAfterEitherAsync(async2, () ->{
//
//			System.out.println("任务3开始...之前的结果:");
//		},service);

		/**
		 * 感知结果 自己没有返回值
		 */
//		async1.acceptEitherAsync(async2, (res)-> System.out.println("任务3开始...之前的结果:" + res), service);


//		CompletableFuture<String> async = async1.applyToEitherAsync(async2, (res) -> {
//
//			System.out.println("任务3开始...之前的结果:" + res);
//			return res.toString() + "-> fire";
//		}, service);
//		System.out.println("任务3返回的结果：" + async.get());


//		CompletableFuture<String> img = CompletableFuture.supplyAsync(() -> {
//			System.out.println("查询商品图片信息");
//			return "1.jpg";
//		},service);
//
//		CompletableFuture<String> attr = CompletableFuture.supplyAsync(() -> {
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			System.out.println("查询商品属性");
//			return "麒麟990 5G  钛空银";
//		},service);
//
//
//		CompletableFuture<String> desc = CompletableFuture.supplyAsync(() -> {
//			System.out.println("查询商品介绍");
//			return "华为";
//		},service);

		/**
		 * 等这三个都做完
		 */
//		CompletableFuture<Void> allOf = CompletableFuture.allOf(img, attr, desc);
//		allOf.join();
//
//		System.out.println("main....end"  + desc.get() + attr.get() + img.get());

//		CompletableFuture<Object> anyOf = CompletableFuture.anyOf(img, attr, desc);
//		anyOf.get();

//		service.execute(new Thread01());
//		System.out.println("main....end" + anyOf.get());
//		CompletableFuture<String> future01=CompletableFuture.supplyAsync(()->{
//					int i=10-4;
//					System.out.println("任务一线程"+Thread.currentThread().getId());
//					return i;
//				},service).thenApplyAsync((res)->{
//					System.out.println("任务二线程"+Thread.currentThread().getId()+" res");
//					return "hellp"+res;
//				},service);

		//无返回值的线程
		CompletableFuture<Void> future01=CompletableFuture.runAsync(()->{
			int i=10-4;
			System.out.println("任务一线程"+Thread.currentThread().getId());
		},service);
		CompletableFuture<Void> future02=CompletableFuture.runAsync(()->{
			int i=10-4;
			System.out.println("任务二线程"+Thread.currentThread().getId());
		},service);

		//有返回的线程
		CompletableFuture<Integer> future03=CompletableFuture.supplyAsync(()->{
			int i=3;
			System.out.println("任务三线程"+Thread.currentThread().getId());
			return i;
		},service);
		CompletableFuture<Integer> future04=CompletableFuture.supplyAsync(()->{
			int i=4;
			System.out.println("任务四线程开始");
			try {
				Thread.sleep(3000);
				System.out.println("任务四线程结束"+Thread.currentThread().getId());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			return i;
		},service);


		future01.runAfterBothAsync(future02,()->{
			System.out.println("不接受，不返回，组合任务1开始");
				}
				,service);
		future03.thenAcceptBothAsync(future04,(res1,res2)->{
					System.out.println("接受上步的返回值，组合任务2开始" +res1+res2);
				}
				,service);
		CompletableFuture<Integer> future7=future03.thenCombineAsync(future04,(res1,res2)->{
					System.out.println("接受并带返回值，组合任务3开始" +res1+res2);
					return res1+res2;
				}
				,service);

		future01.runAfterEitherAsync(future04,()->{
			System.out.println("either，相当于或，有一个运行完成就运行这个。run无输入无输出");
		},service);
		future03.acceptEitherAsync(future04,(res03)->{

			System.out.println("either，相当于或，有一个运行完成就运行这个。accepc有输入无输出。"+"接受的值是"+res03);
		},service);
		CompletableFuture<Integer> future8=future03.applyToEitherAsync(future04,(res03)->{
			int i=8;
			System.out.println("either，相当于或，有一个运行完成就运行这个。apply有输入有输出。"+"接受的值是"+res03);
			return i;
		},service);

		CompletableFuture<Void> future9=CompletableFuture.allOf(future04,future02,future03);
		future9.get();
		System.out.println("输出："+future03.get());
		System.out.println("输出的值是："+future8.get());

		service.shutdown();

	}
	public static class Thread01 extends Thread{
		public void run(){
			System.out.println("test1");
		}
	}
}
