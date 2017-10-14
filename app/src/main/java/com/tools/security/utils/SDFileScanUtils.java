package com.tools.security.utils;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by lzx on 2016/12/13.
 * email：386707112@qq.com
 * 功能：sd卡扫描
 */

public class SDFileScanUtils {

    private Handler mHandler;
    private boolean isStop = false;
    private ArrayList<String> fileList = new ArrayList<>();
    private final int SCAN_LEVEL = 3; // 扫描的层数

    public SDFileScanUtils(Handler mHandler) {
        this.mHandler = mHandler;
        fileList.clear();
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public void start() throws IOException, InterruptedException {
        final File file = Environment.getExternalStorageDirectory();

        final int core = 3;  //线程数
        final Processor processor = new Processor(core);
        final AtomicLong count = new AtomicLong();

        FileHandler handler = new FileHandler() {
            @Override
            public void handle(File file) throws IOException {
                fileList.add(file.getName());
                count.incrementAndGet();
            }
        };

        long begin = System.nanoTime();
        processor.execute(new RecursiveTraveler(file, handler, processor, 0));
        processor.awaitForEnd(); //停止所有任务
        long end = System.nanoTime();
        if (!isStop) {
            Message message = Message.obtain();
            message.obj = fileList;
            message.what = 0;
            mHandler.sendMessage(message);
        }
        Logger.i("fileList.size: " + fileList.size() + "   耗时 (s):" + TimeUnit.NANOSECONDS.toSeconds(end - begin));
    }

    public interface FileHandler {
        void handle(File file) throws IOException;
    }

    public class RecursiveTraveler implements Runnable {

        private final Executor executor;
        private final File file;
        private final FileHandler handler;
        private final int level;

        public RecursiveTraveler(File file, FileHandler handler, Executor executor, int level) {
            if (!file.isDirectory())
                throw new IllegalArgumentException("Invalid Directory: " + file);
            this.file = file;
            this.handler = handler;
            this.executor = executor;
            this.level = level;
        }

        @Override
        public void run() {
            try {
                File[] files = file.listFiles();
                if (files == null)
                    return;
                for (File sub : files) {
                    if (sub.isFile()) {
                        handler.handle(sub);
                    } else {
                        if (!isStop) {
                            if (level < SCAN_LEVEL) {
                                executor.execute(new RecursiveTraveler(sub, handler, executor, level + 1));
                            }
                        }
                    }
                }
                //handler.handle(file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public class Processor implements Executor {
        private final ExecutorService service;

        private final TaskStatus taskStatus;

        public Processor(int core) {
            service = Executors.newFixedThreadPool(core);
            taskStatus = new TaskStatus();
        }

        public void awaitForEnd() throws InterruptedException {
            taskStatus.awaitForAllDone();
            service.shutdownNow();
        }

        @Override
        public void execute(final Runnable command) {
            taskStatus.increment();
            service.execute(new Runnable() {

                @Override
                public void run() {
                    command.run();
                    taskStatus.decrement();
                }
            });
        }
    }

    public class TaskStatus {

        private final AtomicLong status = new AtomicLong();

        public void awaitForAllDone() throws InterruptedException {
            synchronized (status) {
                do {
                    status.wait(500); //500
                } while (status.get() > 0L);
            }
        }

        public void decrement() {
            status.decrementAndGet();
            synchronized (status) {
                status.notifyAll();
            }
        }

        public void increment() {
            status.incrementAndGet();
        }
    }


}
