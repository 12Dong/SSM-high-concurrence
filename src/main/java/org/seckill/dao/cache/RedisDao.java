package org.seckill.dao.cache;

import org.seckill.entity.Seckill;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;

public class RedisDao {
    private JedisPool jedisPool;

    private final Logger logger =  LoggerFactory.getLogger(this.getClass());
    public RedisDao(String ip,int port){
        jedisPool = new JedisPool(ip,port);
    }

    //做了一个模式 通过字节码 和 对象的属性进行序列化
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);


    public Seckill getSeckill(long seckillId){
//        redis操作逻辑
        try{
            //Jedis  相当于 Connection
            Jedis jedis =   jedisPool.getResource();

            try{
                String key = "seckill:"+seckillId;
                //jedis 并没有内部序列化
                // get->二进制数组->反序列化->object(Seckill类型)
                // 使用自定义 序列化
                //告知 class类
                byte[] bytes = jedis.get(key.getBytes());
                if(bytes!=null){
                    Seckill seckill = schema.newMessage();//空对象
                    ProtostuffIOUtil.mergeFrom(bytes,seckill,schema);
                    return seckill;
                    //seckill被反序列 与jdk相差两个数量级
                }
            }finally{

                jedis.close();
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
    public String putSeckill(Seckill seckill){
        //set Object(seckill_>bytes[]) 序列化
        Jedis jedis = jedisPool.getResource();
        try{
            try{
//                构造key
                String key = "seckill:"+seckill.getSeckillId();
                //提供缓存器
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill,schema,LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                // 超时缓存
                //缓存时间
                int timeout = 60*60;
                String result = jedis.setex(key.getBytes(),timeout,bytes);
                return result;
            }finally{
                jedis.close();
            }
        }catch(Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
