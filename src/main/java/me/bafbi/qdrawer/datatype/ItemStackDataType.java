package me.bafbi.qdrawer.datatype;

import org.apache.commons.lang.SerializationUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class ItemStackDataType implements PersistentDataType<byte[], ItemStack> {

    @Override
    public @NotNull Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public @NotNull Class<ItemStack> getComplexType() {
        return ItemStack.class;
    }

    @Override
    public byte @NotNull [] toPrimitive(@NotNull ItemStack complex, @NotNull PersistentDataAdapterContext context) {
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            BukkitObjectOutputStream osbo = new BukkitObjectOutputStream(os);

            osbo.writeObject(complex);
            osbo.close();
            return os.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return SerializationUtils.serialize((Serializable) complex);
    }

    @Override
    public @NotNull ItemStack fromPrimitive(byte @NotNull [] primitive, @NotNull PersistentDataAdapterContext context) {
        try {
            InputStream is = new ByteArrayInputStream(primitive);
            BukkitObjectInputStream isbo = new BukkitObjectInputStream(is);
            return (ItemStack) isbo.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
