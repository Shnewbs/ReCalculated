package sonar.core.client.gui.widgets;

/**
 * Enum for determining the orientation of a scroller (vertical or horizontal).
 */
public enum ScrollerOrientation {

    VERTICAL, HORIZONTAL;

    public boolean isVertical() {
        return this == VERTICAL;
    }

    public boolean isHorizontal() {
        return this == HORIZONTAL;
    }
}
