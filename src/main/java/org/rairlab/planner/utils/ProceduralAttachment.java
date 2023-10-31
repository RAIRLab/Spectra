package org.rairlab.planner.utils;

import org.rairlab.shadow.prover.representations.formula.Formula;

import java.util.Optional;
import java.util.Set;

/**
 * Created by naveensundarg on 1/26/17.
 */
public  interface ProceduralAttachment {

    Optional<Boolean> satisfies(Set<Formula> base, Formula goal);
}
