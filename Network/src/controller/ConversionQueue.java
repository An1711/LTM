package controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Singleton queue manager that processes conversion requests sequentially.
 */
public class ConversionQueue {

    private static final ConversionQueue INSTANCE = new ConversionQueue();

    private final BlockingQueue<RequestJob> queue = new LinkedBlockingQueue<>();
    // keep quick lookup of jobs by id
    private final ConcurrentHashMap<Long, RequestJob> jobs = new ConcurrentHashMap<>();

    private volatile long counter = 1L;

    private ConversionQueue() {
        Thread t = new Thread(this::runLoop, "ConversionQueue-Worker");
        t.setDaemon(true);
        t.start();
    }

    public static ConversionQueue getInstance() {
        return INSTANCE;
    }

    public RequestJob enqueue(int type, int userId, String originalFileName, String inputPath) {
        long id = nextId();
        RequestJob job = new RequestJob(id, type, userId, originalFileName, inputPath);
        jobs.put(id, job);
        queue.offer(job);
        return job;
    }

    public List<RequestJob> listByUser(int userId) {
        List<RequestJob> res = new ArrayList<>();
        for (RequestJob j : jobs.values()) {
            if (j.getUserId() == userId) res.add(j);
        }
        return res;
    }

    private long nextId() {
        return counter++;
    }

    private void runLoop() {
        while (true) {
            try {
                RequestJob job = queue.take();
                job.setStatus(RequestJob.Status.PROCESSING);

                // call WorkerManager to process; WorkerManager will save link to DB
                String result = WorkerManager.sendConvertJob(job.getType(), job.getUserId(), job.getOriginalFileName(), job.getInputPath());
                job.setResult(result);

                if (result != null && result.startsWith("OK|")) {
                    job.setStatus(RequestJob.Status.DONE);
                } else {
                    job.setStatus(RequestJob.Status.FAILED);
                }

                // delete temp input file if exists
                try {
                    File in = new File(job.getInputPath());
                    if (in.exists()) in.delete();
                } catch (Exception ignore) {}

            } catch (Exception e) {
                e.printStackTrace();
                try { Thread.sleep(1000); } catch (InterruptedException ignore) {}
            }
        }
    }
}
