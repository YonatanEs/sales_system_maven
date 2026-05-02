package Clases;

import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.util.List;

public class OrdenPersonalizado extends FocusTraversalPolicy {
    private final List<Component> orden;

    public OrdenPersonalizado(List<Component> orden) {
        this.orden = orden;
    }

    @Override
    public Component getComponentAfter(Container aContainer, Component aComponent) {
        int idx = (orden.indexOf(aComponent) + 1) % orden.size();
        return orden.get(idx);
    }

    @Override
    public Component getComponentBefore(Container aContainer, Component aComponent) {
        int idx = orden.indexOf(aComponent) - 1;
        if (idx < 0) idx = orden.size() - 1;
        return orden.get(idx);
    }

    @Override
    public Component getDefaultComponent(Container aContainer) {
        return orden.get(0);
    }

    @Override
    public Component getLastComponent(Container aContainer) {
        return orden.get(orden.size() - 1);
    }

    @Override
    public Component getFirstComponent(Container aContainer) {
        return orden.get(0);
    }
}


