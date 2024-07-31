package net.quelfth.gigatubes.util.routing;

import javax.annotation.Nullable;

import net.quelfth.gigatubes.util.Comparison;
import net.quelfth.gigatubes.util.code.Predicate;

public final class PathDatum {
    public final int speed;
    public final Predicate filter;

    

    private PathDatum(final int speed, final Predicate filter) {
        this.speed = speed;
        this.filter = filter;
    }

    public static PathDatum ofFilter(final Predicate filter) {
        return new PathDatum(Integer.MAX_VALUE, filter);
    }

    public static PathDatum ofSpeed(final int speed) {
        return new PathDatum(speed, Predicate.ANY);
    }

    public PathDatum then(final PathDatum next) {
        return new PathDatum(Math.min(speed, next.speed), Predicate.and(filter, next.filter));
    }

    public boolean better(final PathDatum other) {
        return compareTotal(other) == Comparison.GREATER;
    }

    private Comparison compareSpeed(final PathDatum that) {
        if (speed == that.speed)
            return Comparison.EQUAL;
        if (speed > that.speed)
            return Comparison.GREATER;
        return Comparison.LESS;
    }

    @Nullable
    private Comparison compareFilter(final PathDatum that) {
        if (filter.identical(that.filter))
            return Comparison.EQUAL;
        if (filter.obviousSuperset(that.filter))
            return Comparison.GREATER;
        if (that.filter.obviousSuperset(filter))
            return Comparison.LESS;
        return null;
    }
    
    public Comparison compareTotal(final PathDatum that) {
        return Comparison.aggregate(compareSpeed(that), compareFilter(that));
    }

    public boolean betterOrEqual(final PathDatum other) {
        Comparison comparison = compareTotal(other);
        return comparison == Comparison.EQUAL || comparison == Comparison.GREATER;
    }

    public boolean better(final PathData other) {
        for (PathDatum datum : other.data)
            if (!better(datum))
                return false;
        return true;
    }

    public boolean betterOrEqual(final PathData other) {
        for (PathDatum datum : other.data)
            if (!betterOrEqual(datum))
                return false;
        return true;
    }
}