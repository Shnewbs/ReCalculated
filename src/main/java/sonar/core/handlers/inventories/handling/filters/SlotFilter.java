package sonar.core.handlers.inventories.handling.filters;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import sonar.core.helpers.SonarHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SlotFilter implements IInsertFilter, IExtractFilter {

    public int[] filtered;
    @Nullable
    public Boolean defaultReturn;
    public Direction[] faces;

    public SlotFilter(Boolean defaultReturn, int[] filtered) {
        this.defaultReturn = defaultReturn;
        this.filtered = filtered;
        this.faces = null;
    }

    public SlotFilter(Boolean defaultReturn, int[] filtered, int[] faces) {
        this.defaultReturn = defaultReturn;
        this.filtered = filtered;
        Direction[] newFaces = new Direction[faces.length];
        for (int i = 0; i < faces.length; i++) {
            newFaces[i] = Direction.values()[faces[i]];
        }
        this.faces = newFaces;
    }

    public SlotFilter(Boolean defaultReturn, int[] filtered, Direction... faces) {
        this.defaultReturn = defaultReturn;
        this.filtered = filtered;
        this.faces = faces;
    }

    public boolean checkSlot(int slot) {
        return SonarHelper.intContains(filtered, slot);
    }

    public boolean checkFace(Direction face) {
        return (this.faces == null || face == null || SonarHelper.arrayContains(faces, face));
    }

    public boolean checkFilter(int slot, Direction face) {
        return ((this.faces == null || face == null || SonarHelper.arrayContains(faces, face)) && SonarHelper.intContains(filtered, slot));
    }

    /**
     * Checks if this slot filter should be used for the given face before checking the filter,
     * returns true if the filter doesn't affect the given face
     */
    public boolean checkFilterSafe(int slot, Direction face) {
        if (checkFace(face)) {
            return checkSlot(slot);
        }
        return true;
    }

    @Override
    public Boolean canExtract(int slot, int amount, Direction face) {
        if (checkFace(face)) {
            return checkSlot(slot);
        }
        return defaultReturn;
    }

    @Override
    public Boolean canInsert(int slot, @Nonnull ItemStack stack, Direction face) {
        if (checkFace(face)) {
            return checkSlot(slot);
        }
        return defaultReturn;
    }
}
