package me.bafbi.qdrawer.datatype;

import org.apache.commons.lang.SerializationUtils;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class BlockArrayDataType implements PersistentDataType<byte[], Block[]> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<Block[]> getComplexType() {
        return Block[].class;
    }

    @Override
    public byte @NotNull [] toPrimitive(Block @NotNull [] complex, @NotNull PersistentDataAdapterContext context) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            //Bukkit.getLogger().info("aa");

            // Write the size of the array
            dataOutput.writeInt(Array.getLength(complex));

            // Save every element in the list
            for (int i = 0; i < Array.getLength(complex); i++) {
                dataOutput.writeObject(complex[i].getWorld().getKey().asString());
                dataOutput.writeLong(complex[i].getBlockKey());
            }
            //dataOutput.writeObject(complex);
            dataOutput.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SerializationUtils.serialize(complex);
    }

    @Override
    public Block @NotNull [] fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        try {
            InputStream inputStream = new ByteArrayInputStream(primitive);
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            List<Block> blockList = new ArrayList<>();

            int arrayLength = dataInput.readInt();
            //Bukkit.getLogger().info(String.valueOf(arrayLength));
            for (int i = 0; i < arrayLength; i++) {
                String worldKeyString = (String) dataInput.readObject();
                long blockKey = dataInput.readLong();
                //Bukkit.getLogger().info(worldKeyString);
                //Bukkit.getLogger().info(String.valueOf(blockKey));

                NamespacedKey worldKey = NamespacedKey.fromString(worldKeyString);
                blockList.add(i, Bukkit.getWorld(worldKey).getBlockAtKey(blockKey));
            }

            dataInput.close();

            return blockList.toArray(new Block[0]);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return (Block[]) SerializationUtils.deserialize(primitive);
    }

}
