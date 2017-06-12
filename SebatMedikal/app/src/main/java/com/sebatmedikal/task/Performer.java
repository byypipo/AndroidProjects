package com.sebatmedikal.task;

import com.sebatmedikal.mapper.Mapper;
import com.sebatmedikal.remote.model.request.RequestModel;
import com.sebatmedikal.remote.model.response.ResponseModel;
import com.sebatmedikal.util.HttpUtil;
import com.sebatmedikal.util.LogUtil;

/**
 * Created by orhan on 27.05.2017.
 */
public interface Performer {
    void perform(boolean success);
}
