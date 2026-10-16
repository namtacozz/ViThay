package com.vithay.libman.util;

import javafx.scene.Cursor;
import javafx.scene.Node;

/**
 * UI Utilities for draggable and resizable interactive components in LibMan.
 * Implements Movable Panel & Draggable Dialogs according to B3.md UI/UX patterns.
 */
public class UIUtils {

    private static class DragContext {
        double x, y;
    }

    /**
     * Attaches mouse drag listeners to a header handle to move a floating dialog or panel.
     *
     * @param headerNode   The node used as the drag handle (e.g., modal header)
     * @param movablePanel The root panel that translates in 2D space
     */
    public static void makeDraggable(Node headerNode, Node movablePanel) {
        if (headerNode == null || movablePanel == null) return;

        DragContext context = new DragContext();
        headerNode.setCursor(Cursor.MOVE);

        headerNode.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
                Object target = mouseEvent.getTarget();
                if (target instanceof javafx.scene.control.ButtonBase || (target instanceof Node && ((Node) target).getParent() instanceof javafx.scene.control.ButtonBase)) {
                    return;
                }
                context.x = movablePanel.getTranslateX() - mouseEvent.getScreenX();
                context.y = movablePanel.getTranslateY() - mouseEvent.getScreenY();
            }
        });

        headerNode.addEventHandler(javafx.scene.input.MouseEvent.MOUSE_DRAGGED, mouseEvent -> {
            if (mouseEvent.isPrimaryButtonDown()) {
                Object target = mouseEvent.getTarget();
                if (target instanceof javafx.scene.control.ButtonBase || (target instanceof Node && ((Node) target).getParent() instanceof javafx.scene.control.ButtonBase)) {
                    return;
                }
                movablePanel.setTranslateX(mouseEvent.getScreenX() + context.x);
                movablePanel.setTranslateY(mouseEvent.getScreenY() + context.y);
            }
        });
    }

    /**
     * Resets the translation coordinates of a movable panel back to center/origin.
     *
     * @param movablePanel The panel to reset
     */
    public static void resetPosition(Node movablePanel) {
        if (movablePanel != null) {
            movablePanel.setTranslateX(0);
            movablePanel.setTranslateY(0);
        }
    }
}
