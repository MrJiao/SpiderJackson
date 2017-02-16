package com.jackson.common.control;

/**
 * Created by Jackson on 2016/12/26.
 */
public class ControlConfig {
    //线程数
    private int corePoolSize;
    //线程池中的最大任务数
    private int maxTaskCache;
    //线程池中的最小任务数，当小于这个数时，开始添加任务
    private int minTaskCache;
    //url池中最小缓存数
    private int minServiceCatch;
    //每次从数据库中获取的url数量
    private int getServiceCatchSize;

    private ControlConfig(int corePoolSize, int maxTaskCache, int minTaskCache, int minServiceCatch, int getServiceCatchSize) {
        this.corePoolSize = corePoolSize;
        this.maxTaskCache = maxTaskCache;
        this.minTaskCache = minTaskCache;
        this.minServiceCatch = minServiceCatch;
        this.getServiceCatchSize = getServiceCatchSize;
    }

    public int getCorePoolSize() {
        return corePoolSize;
    }

    public int getMaxTaskCache() {
        return maxTaskCache;
    }

    public int getMinTaskCache() {
        return minTaskCache;
    }

    public int getMinServiceCatch() {
        return minServiceCatch;
    }

    public int getGetServiceCatchSize() {
        return getServiceCatchSize;
    }

    public static Builder builder(){
        return new Builder();
    }


    public static class Builder{
        private Builder(){}

        private int corePoolSize = 10;
        private int maxTaskCache = 80;
        private int minTaskCache = 20;
        private int minServiceCatch = 10;
        private int getServiceCatchSize = 100;

        public Builder setCorePoolSize(int corePoolSize) {
            this.corePoolSize = corePoolSize;
            return this;
        }

        public Builder setMaxTaskCache(int maxTaskCache) {
            this.maxTaskCache = maxTaskCache;
            return this;
        }

        public Builder setMinTaskCache(int minTaskCache) {
            this.minTaskCache = minTaskCache;
            return this;
        }

        public Builder setMinServiceCatch(int minServiceCatch) {
            this.minServiceCatch = minServiceCatch;
            return this;
        }

        public Builder setGetServiceCatchSize(int getServiceCatchSize) {
            this.getServiceCatchSize = getServiceCatchSize;
            return this;
        }

        public ControlConfig build(){
            return new ControlConfig(this.corePoolSize,this.maxTaskCache,this.minTaskCache,this.minServiceCatch,this.getServiceCatchSize);
        }
    }
}
