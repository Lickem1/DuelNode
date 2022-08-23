package tk.duelnode.lobby.data.queue;

import lombok.Getter;

@Getter
public class Queue {

    private int queueTimer;
    public int queue_animation_dots = 0;

    public void increment() {
        queueTimer++;
    }

    public String getTimeInQueue() {
        int i = queueTimer;
        int ms = i / 60;
        int ss = i % 60;

        String format;
        if(ms >= 1) format = ms +"m " + ss + "s";
        else format = ss + "s";

        return format;
    }
}
