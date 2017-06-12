package com.sebatmedikal.task;

import android.content.Intent;
import android.os.AsyncTask;

import com.sebatmedikal.activity.OperationsActivity;
import com.sebatmedikal.mapper.Mapper;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.response.ResponseModel;
import com.sebatmedikal.remote.model.response.ResponseModelError;
import com.sebatmedikal.remote.model.response.ResponseModelLogin;
import com.sebatmedikal.remote.model.response.ResponseModelSuccess;
import com.sebatmedikal.util.CompareUtil;
import com.sebatmedikal.util.HttpUtil;
import com.sebatmedikal.util.LogUtil;
import com.sebatmedikal.util.NullUtil;

/**
 * Created by orhan on 27.05.2017.
 */
public class BaseTask extends AsyncTask<Void, Void, Boolean> {
    private String url;
    private RequestModel requestModel;
    private Performer performer;

    private Object content;
    private String accessToken;
    private String errorMessage;

    private boolean serverUnreachable = false;

    public BaseTask(String url, RequestModel requestModel, Performer performer) {
        this.url = url;
        this.requestModel = requestModel;
        this.performer = performer;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        if (NullUtil.isNull(performer)) {
            return false;
        }

        try {
            String response = HttpUtil.sendPost(url, requestModel.toString());

            if (CompareUtil.equal(response, HttpUtil.timeoutErrorString)) {
                serverUnreachable = true;
                return false;
            }

            ResponseModel responseModel = Mapper.responseModelMapper(response);

            if (responseModel instanceof ResponseModelError) {
                ResponseModelError responseModelError = (ResponseModelError) responseModel;
                errorMessage = responseModelError.getError();
                return false;
            }

            if (responseModel instanceof ResponseModelLogin) {
                ResponseModelLogin responseModelLogin = (ResponseModelLogin) responseModel;
                accessToken = responseModelLogin.getAccessToken();
                content = responseModelLogin.getUser();
            } else {
                ResponseModelSuccess responseModelSuccess = (ResponseModelSuccess) responseModel;
                content = responseModelSuccess.getContent();
            }

            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public Object getContent() {
        return content;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isServerUnreachable() {
        return serverUnreachable;
    }

    @Override
    protected void onPostExecute(final Boolean success) {
        LogUtil.logMessage(BaseTask.class, "onPostExecute success: " + success);
        performer.perform(success);
    }

    @Override
    protected void onCancelled() {
        LogUtil.logMessage(BaseTask.class, "onPostExecute onCancelled");
        performer.perform(false);
    }
}
