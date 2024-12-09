package com.visualization.manager.orphan;

import com.google.gson.Gson;
import com.visualization.runtime.*;
import okhttp3.*;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class FlinkManager {

    public static void execute(VisualStage stage) {
        try {
            Map<String, Object> conf = stage.getFlink();
            FlinkTask task = new FlinkTask(conf, stage.getRuntimeContext());
            task.execute();
        } catch (Throwable e) {
            stage.getRuntimeContext().setStatus(VStageStatus.FOUND_EXCEPTION);
            throw new RuntimeException(e);
        }
    }

    private enum JobStatus {
        NULL, DEPLOYING, INITIALIZING, RUNNING, CREATED, RECONCILING, SCHEDULED, FINISHED, CANCELING, FAILED, CANCELED;
    }

    private static class FlinkTask {

        private VContext context;
        private Map<String, Object> conf;
        private String site;
        private String jarId;
        private String jobId;
        private String options;

        private FlinkTask(Map<String, Object> conf, VContext context) {
            this.conf = conf;
            this.site = (String) conf.get("site");
            this.jarId = (String) conf.get("jarId");
            this.options = (String) conf.get("options");
            this.context = context;
        }

        private void execute() throws IOException, InterruptedException {
            submitJob();
            changeStageStatus();
        }

        private void submitJob() throws IOException {
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            builder.retryOnConnectionFailure(true);
            OkHttpClient client = builder.build();
            Request.Builder requestBuilder = new Request.Builder();
            String path = MessageFormat.format("{0}/jars/{1}/run", site, jarId);
            VFunctions.LOG.handle(new VLog(context, "开始提交Flink Job", VLogTheme.INFO));
            Request request = requestBuilder.url(path).post(RequestBody.create(options.getBytes())).build();
            Response response = client.newCall(request).execute();
            String reqBody = response.body().string();
            Gson gson = new Gson();
            Map map = gson.fromJson(reqBody, Map.class);
            this.jobId = (String) map.get("jobid");
            context.setStatus(VStageStatus.WAITING_SUBTASK);
        }

        private void changeStageStatus() throws IOException, InterruptedException {
            JobStatus jobStatus = JobStatus.INITIALIZING;
            if (!Boolean.FALSE.equals(conf.get("waitUntilFinish"))) {
                String str = conf.getOrDefault("waitInterval", 30).toString();
                int waitInterval = NumberUtils.toInt(str, 30);
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.retryOnConnectionFailure(true);
                OkHttpClient client = builder.build();
                Request.Builder requestBuilder = new Request.Builder();
                String path = MessageFormat.format("{0}/jobs/{1}", site, this.jobId);
                Request request = requestBuilder.url(path).build();
                ResponseBody body;
                Gson gson = new Gson();
                boolean wait = true;
                while (wait) {
                    body = client.newCall(request).execute().body();
                    Map map = gson.fromJson(body.charStream(), Map.class);
                    jobStatus = JobStatus.valueOf(map.getOrDefault("state", "NULL").toString());
                    switch (jobStatus) {
                        case FINISHED:
                        case CANCELED:
                        case FAILED:
                            wait = false;
                            break;
                        default:
                            TimeUnit.SECONDS.sleep(waitInterval);
                    }
                }
            } else {
                jobStatus = JobStatus.FINISHED;
            }
            if (jobStatus == JobStatus.FINISHED) {
                context.setStatus(VStageStatus.SUBTASK_FINISH);
            } else {
                context.setStatus(VStageStatus.FOUND_EXCEPTION);
            }
        }

    }
}
