package tk.duelnode.lobby.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sk89q.jnbt.CompoundTag;
import com.sk89q.jnbt.StringTag;
import com.sk89q.jnbt.Tag;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.blocks.SignBlock;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.registry.WorldData;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import tk.duelnode.api.API;
import tk.duelnode.api.util.packet.ClassType;
import tk.duelnode.lobby.manager.dynamic.annotations.Init;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

@Init(classType = ClassType.CONSTRUCT)
public class WorldEditUtil {

    public void paste(Clipboard clipboard, Vector location, World world) {
        com.sk89q.worldedit.world.World weWorld = new BukkitWorld(world);
        ClipboardHolder holder = new ClipboardHolder(clipboard, weWorld.getWorldData());
        EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(weWorld, Integer.MAX_VALUE);

        try {
            Operations.complete(holder.createPaste(editSession, holder.getWorldData()).to(location).build());
        } catch (WorldEditException e) {
            e.printStackTrace();
        }

    }

    private final String EMPTY = "";

    public String[] getText(BaseBlock block) {
        String[] lines = getText_(block);
        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("\"") && lines[i].endsWith("\""))
                lines[i] = lines[i].substring(1, lines[i].length() - 1);
            else if (lines[i].startsWith("{\"") && lines[i].endsWith("\"}")) {
                lines[i] = BaseComponent.toPlainText(ComponentSerializer.parse(lines[i]));
            }
        }
        return lines;
    }

    public String[] getText_(BaseBlock block) {
        try {
            SignBlock sblock = new SignBlock(block.getId(), block.getData());
            sblock.setNbtData(block.getNbtData());
            String[] array = sblock.getText();
            for (int i = 0; i < array.length; i++)
                array[i] = stripText(array[i]);
            return array;
        } catch (Exception ignored) {
        }
        try {
            String[] text = new String[]{EMPTY, EMPTY, EMPTY, EMPTY};
            CompoundTag tag = block.getNbtData();
            Map<String, Tag> values = tag.getValue();
            Tag t = values.get("id");
            if (t instanceof StringTag) {
                StringTag st = (StringTag) t;
                if (st.getValue().equals("Sign") || st.getValue().equals("minecraft:sign")) {
                    t = values.get("Text1");
                    if (t instanceof StringTag) text[0] = ((StringTag) t).getValue();
                    t = values.get("Text2");
                    if (t instanceof StringTag) text[1] = ((StringTag) t).getValue();
                    t = values.get("Text3");
                    if (t instanceof StringTag) text[2] = ((StringTag) t).getValue();
                    t = values.get("Text4");
                    if (t instanceof StringTag) text[3] = ((StringTag) t).getValue();
                } else {
                    throw new RuntimeException("'Sign' or 'minecraft:sign' tile entity expected, got '" + t.getValue() + "'");
                }
            } else {
                throw new RuntimeException("Expected StringTag got " + t.getClass().getSimpleName());
            }
            return text;
        } catch (Exception e) {
            throw new RuntimeException("Unable to parse sign data! (worldedit-native, and backup failed)", e);
        }
    }

    private String stripText(String json) {
        if (json == EMPTY) return EMPTY;
        JsonObject object = API.getGson().fromJson(json, JsonObject.class);
        StringBuilder builder = new StringBuilder();
        builder.append(object.get("text").getAsString());
        if (object.get("extra") != null)
            for (JsonElement e : object.get("extra").getAsJsonArray())
                builder.append(e.getAsString());
        return builder.toString();
    }

    public Clipboard load(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            BufferedInputStream bis = new BufferedInputStream(fis);
            ClipboardReader reader = ClipboardFormat.SCHEMATIC.getReader(bis);
            WorldData worldData = new BukkitWorld(Bukkit.getWorld("world")).getWorldData(); // does the world matter?
            Clipboard clipboard = reader.read(worldData);
            return clipboard;
        }
    }

    public float getRot(BaseBlock block) {
        if (block.getId() == Material.SIGN_POST.getId()) {
            return block.getData() * 22.5f;
        } else if (block.getId() == Material.WALL_SIGN.getId()) {
            switch (block.getData()) {
                case 3:
                    return 0;
                case 4:
                    return 90;
                case 5:
                    return -90;
                default:
                    return 180;
            }
        }
        return 0;
    }

}
