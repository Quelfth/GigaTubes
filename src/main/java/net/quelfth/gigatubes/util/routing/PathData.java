package net.quelfth.gigatubes.util.routing;

import javax.annotation.Nullable;

import net.quelfth.gigatubes.util.Comparison;

import java.util.ArrayList;

public final class PathData {
    public final ArrayList<PathDatum> data;

    PathData(ArrayList<PathDatum> data) {
        this.data = data;
    }

    public PathData(PathDatum... data) {
        this.data = new ArrayList<>();
        for(PathDatum datum : data)
            this.data.add(datum);
    }

    public PathData clone() {
        ArrayList<PathDatum> data = new ArrayList<>();
        for (PathDatum datum : this.data)
            data.add(datum);
        return new PathData(data);
    }

    public PathDatum get(final int i) {
        return data.get(i);
    }


    public void set(final int i, PathDatum datum) {
        data.set(i, datum);
    }

    public int size() {
        return data.size();
    }

    public void add(PathDatum datum) {
        data.add(datum);
    }

    public PathData then(final PathDatum next) {
        PathData data = clone();
        for (int i = 0; i < data.size(); i++)
            data.set(i, data.get(i).then(next));
        return data;
    }

    @Nullable
    public Comparison compare(final PathDatum other) {
        Comparison comparison = Comparison.EQUAL;
        for (PathDatum datum : data)
            comparison = Comparison.aggregate(comparison, datum.compareTotal(other));
        return comparison;
    }

    public boolean better(final PathDatum other) {
        for (PathDatum datum : data)
            if (datum.better(other))
                return true;
        return false;
    }

    public boolean betterOrEqual(final PathDatum other) {
        for (PathDatum datum : data)
            if (datum.betterOrEqual(other))
                return true;
        return false;
    }
}