package org.seckill.seckillStatEnum;
//使用枚举进行数据处理
public enum seckillStatEnum {
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1,"重复秒杀"),
    INNER_ERROR(-2,"系统异常"),
    DATA_REWRITE(-3,"数据篡改");

    private int state;
    private String stateInfo;

    seckillStatEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static seckillStatEnum stateof(int index){
        for(seckillStatEnum state:values()){
            if(state.getState()==index){
                return state;
            }
        }
        return null;
    }
}
