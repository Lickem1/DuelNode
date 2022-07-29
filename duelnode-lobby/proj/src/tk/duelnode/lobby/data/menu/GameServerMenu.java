package tk.duelnode.lobby.data.menu;

import lombok.Getter;
import tk.duelnode.lobby.data.packet.ClassType;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;
import tk.duelnode.lobby.util.menu.MenuBuilder;

@Getter
@Init(classType = ClassType.CONSTRUCT)
public class GameServerMenu {

    private final MenuBuilder menu = new MenuBuilder(9);

    public GameServerMenu() {
        System.out.println("This is todo");
    }
}
