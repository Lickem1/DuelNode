package tk.duelnode.api.util.redis;

import io.lettuce.core.pubsub.RedisPubSubListener;

public class RedisListener implements RedisPubSubListener<String, String> {

    private final RedisManager redis;
    public RedisListener(RedisManager manager) {
        this.redis = manager;
    }

    @Override
    public void message(String s, String s2) {
        redis.getListeners().get(s).message(s, s2);

    }

    @Override
    public void message(String s, String k1, String s2) {
        redis.getListeners().get(s).message(k1, s2);
    }

    @Override public void subscribed(String s, long l) { }
    @Override public void psubscribed(String s, long l) { }
    @Override public void unsubscribed(String s, long l) { }
    @Override public void punsubscribed(String s, long l) { }
}
