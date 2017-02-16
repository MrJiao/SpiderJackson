package com.jackson.common.source;

/**
 * Created by Jackson on 2016/12/21.
 */
public enum GlobalSource {
    instance;

    public CommonSource getCommonSource(){
        return CommonSource.newInstance();
    }

}
