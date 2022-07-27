package tk.duelnode.api.util.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.Hashtable;

@Getter
public class RedisManager {

    private final RedisClient redisClient;
    private final StatefulRedisConnection<String, String> redisConnection;
    private final StatefulRedisPubSubConnection<String, String> redisPubSub;

    @Getter(AccessLevel.PACKAGE) private final Hashtable<String, Message> listeners = new Hashtable<>();
    private final RedisListener listener;

    public RedisManager(RedisClient redisClient) {
        this(redisClient,null);
    }

    public RedisManager(RedisClient redisClient, String auth) {
        this.redisClient = redisClient;
        redisConnection = redisClient.connect();
        redisPubSub = redisClient.connectPubSub();
        System.out.println("[INFO] Initializing connection to redis..");
        if (auth!=null) {
            System.out.println(" | redis sub: " + redisConnection.sync().auth(auth));
            System.out.println(" | redis pub: " + redisPubSub.sync().auth(auth));
        } else {
            System.out.println("[INFO] No redis password supplied.");
        }
        listener = new RedisListener(this);
        redisPubSub.addListener(listener);

        if(redisConnection.isOpen()) {
            System.out.println("[INFO] Redis successfully connected");
        }
    }

    public RedisFuture<Long> publish(String channel, String message) {
        return redisConnection.async().publish(channel,message);
    }

    public void subscribe(String channel, Message m) {
        redisPubSub.sync().subscribe(channel);
        addListener(channel,m);
    }

    public void psubscribe(String pattern, Message m) {
        redisPubSub.sync().psubscribe(pattern);
        addListener(pattern,m);
    }

    public void psubscribe(String[] patterns, Message m) {
        for (String pattern : patterns) {
            redisPubSub.sync().psubscribe(pattern);
            addListener(pattern, m);
        }
    }

    private void addListener(String channel, Message m) {
        listeners.put(channel,m);
    }
}
