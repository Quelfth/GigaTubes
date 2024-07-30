package net.quelfth.gigatubes.util.code;

public final class DNFPredicate {

    private NegatablePredicate[][] predicates;

    public DNFPredicate(SimplePredicate predicate) {
        predicates = new NegatablePredicate[][]{ new NegatablePredicate[]{ NegatablePredicate.affirm(predicate) } };
    }

    private DNFPredicate(NegatablePredicate[][] predicates) {
        this.predicates = predicates;
    }

    public DNFPredicate or(DNFPredicate that) {
        NegatablePredicate[][] predicates = new NegatablePredicate[this.predicates.length + that.predicates.length][];
        int i = 0;
        for (NegatablePredicate[] disjunct : this.predicates)
            predicates[i++] = disjunct;
        for (NegatablePredicate[] disjunct : that.predicates)
            predicates[i++] = disjunct;
        return new DNFPredicate(predicates);
    }

    private static final class NegatablePredicate {
        private final boolean negated;
        private final SimplePredicate predicate;

        public NegatablePredicate(SimplePredicate predicate, boolean negated) {
            this.predicate = predicate;
            this.negated = negated;
        }

        public static NegatablePredicate affirm(SimplePredicate predicate) {
            return new NegatablePredicate(predicate, false);
        }

        public static NegatablePredicate negate(SimplePredicate predicate) {
            return new NegatablePredicate(predicate, true);
        }
    }
}

