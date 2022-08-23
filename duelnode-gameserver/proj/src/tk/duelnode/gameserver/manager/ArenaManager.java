package tk.duelnode.gameserver.manager;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import lombok.Getter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.gameserver.GameServer;
import tk.duelnode.api.game.arena.Arena;
import tk.duelnode.api.game.arena.ArenaState;
import tk.duelnode.api.game.arena.Cube;
import tk.duelnode.gameserver.data.game.LocalGame;
import tk.duelnode.gameserver.manager.dynamic.annotations.Init;
import tk.duelnode.gameserver.util.WorldEditUtil;

import java.io.File;
import java.util.*;

@Getter
@Init(priority = 90, classType = ClassType.CONSTRUCT)
public class ArenaManager {

    private final Map<String, Arena> allArenas = new LinkedHashMap<>();
    private final Map<String, Arena> availableArenas = new LinkedHashMap<>();
    private final Map<String, Arena> arenasInUse = new LinkedHashMap<>();

    private double pasteXZ = -25E6;
    private int grid = 0;
    private final int yLevel = 70;

    public ArenaManager() {
        try {
            File[] schematics = GameServer.getInstance().getDataFolder().listFiles();

            for (File file : schematics) {
                String name = file.getName().replaceAll("\\.schematic", "");
                Clipboard clipboard = DynamicManager.get(WorldEditUtil.class).load(file);
                World world = Bukkit.getWorld("world");
                Arena arena = new Arena(name);
                Cube cube = new Cube();
                cube.setWorld(world);

                for (int x = 0; x < clipboard.getDimensions().getBlockX(); x++) {
                    for (int y = 0; y < clipboard.getDimensions().getBlockY(); y++) {
                        for (int z = 0; z < clipboard.getDimensions().getBlockZ(); z++) {
                            Vector t = new Vector(x, y, z).add(clipboard.getMinimumPoint());
                            BaseBlock block = clipboard.getBlock(t);

                            boolean remove = false;
                            if (block.getId() == Material.SPONGE.getId()) {
                                if(y==27) cube.setBlockX(new Location(world, x, (y+yLevel), z));
                                else if(y == 0) cube.setBlockZ(new Location(world, x, (y+yLevel), z));
                                remove = true;
                            } else if (block.getId() == Material.WALL_SIGN.getId() || block.getId() == Material.SIGN_POST.getId()) {
                                String[] lines = DynamicManager.get(WorldEditUtil.class).getText(block);
                                remove = handleLines(arena, lines, block, x, y, z);
                            }
                            if (remove) {
                                clipboard.setBlock(t, new BaseBlock(0));
                            }
                        }
                    }
                }
                arena.setCuboid(cube);
                arena.setClipboard(clipboard);

                allArenas.put(name, arena);

                System.out.println(" * [DN] Loaded arena template: " + allArenas.get(name).getID());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isArenaFree(Arena arena) {
        return (availableArenas.get(arena.getID()) != null);
    }

    public void setArenaState(Arena arena, ArenaState state) {
        switch (state) {
            case UNAVAILABLE:
                availableArenas.remove(arena.getID());
                arenasInUse.put(arena.getID(), arena);
                break;
            case AVAILABLE:
                arenasInUse.remove(arena.getID(), arena);
                availableArenas.put(arena.getID(), arena);
                break;
        }
    }

    public void rollBackArena(LocalGame game) {
        Bukkit.getServer().getScheduler().runTask(GameServer.getInstance(), () -> {
            for(Block blocks : game.getBlocksPlaced()) {
                blocks.setType(Material.AIR);
            }
        });
    }

    public Arena getFreeArena() {
        if(availableArenas.isEmpty()) {
            Object[] values = allArenas.values().toArray();
            return pasteNewArena((Arena) values[new Random().nextInt(values.length)]);
        }
        else return availableArenas.entrySet().iterator().next().getValue();
    }

    private Arena pasteNewArena(Arena a) {
        World world = Bukkit.getWorld("world");
        Arena cloned = a.clone();
        Cube cuboid = cloned.getCuboid();
        Clipboard clipboard = cloned.getClipboard();

        System.out.println("[Debug] Pasting new arena at (" + pasteXZ + ", " + yLevel + ", " + pasteXZ + ")");
        DynamicManager.get(WorldEditUtil.class).paste(clipboard, new Vector(pasteXZ, yLevel, pasteXZ), world);
        fixLocations(cloned.getLoc1(), cloned.getLoc2(), cloned.getCenter(), cuboid.getBlockX(), cuboid.getBlockZ());

        availableArenas.put(cloned.getID(), cloned);
        grid += 200;
        pasteXZ += grid;
        return cloned;
    }

    private boolean handleLines(Arena arena, String[] lines, BaseBlock block, int x, int y, int z) {
        String meta_id = lines[0].toLowerCase();
        if (meta_id.startsWith("arena:")) {
            meta_id = meta_id.substring(6);

            switch (meta_id) {

                case "name":
                    arena.setDisplayName(lines[1]);
                    break;
                case "center":
                    arena.setCenter(new Location(Bukkit.getWorld("world"), x, y, z, 0, 0));
                    break;
                case "spawn1":
                    arena.setLoc1(new Location(Bukkit.getWorld("world"), x, y, z, Float.parseFloat(lines[1]), 0));
                    break;
                case "spawn2":
                    arena.setLoc2(new Location(Bukkit.getWorld("world"), x, y, z, Float.parseFloat(lines[1]), 0));
                    break;
                case "icon":
                    ItemStack stack = new ItemStack(Material.getMaterial(Integer.parseInt(lines[1])), 1, Short.parseShort(lines[2]));
                    arena.setIcon(stack);
                    break;
            }
            return true;
        }
        return false;
    }

    private void fixLocations(Location... locations) {
        for(Location loc : locations) loc.add(new org.bukkit.util.Vector(pasteXZ, yLevel, pasteXZ));
    }

    public Arena getFreeArenaFromID(String id) {
        return availableArenas.get(id);
    }
}
