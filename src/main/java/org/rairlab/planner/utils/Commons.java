package org.rairlab.planner.utils;

import com.diogonunes.jcdp.color.ColoredPrinter;
import com.diogonunes.jcdp.color.api.Ansi;
import org.rairlab.shadow.prover.representations.formula.And;
import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Value;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.CollectionUtils;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

/**
 * Created by naveensundarg on 1/15/17.
 */
public class Commons {


    public static Map<Value, Variable> makeVariables(Set<Value> values){


        return makeVariables(values, 1);

    }

     public static Map<Value, Variable> makeVariables(Set<Value> values, int startValue){
        int n = startValue;

        Map<Value, Variable> valueVariableMap = CollectionUtils.newMap();

        for(Value value: values){

            Variable variable = new Variable("?" +value);
            valueVariableMap.put(value, variable);
            n = n +1;


        }

        return valueVariableMap;
    }

    public static Set<Formula> generalize(Map<Value, Variable> universalValueVariableMap, Set<Formula> formulae){


        return formulae.stream().map(x->x.generalize(universalValueVariableMap)).collect(Collectors.toSet());
    }

    public static <U, V> Map<V, U> reverse(Map<U, V> map){

        Map<V, U> reverseMap = CollectionUtils.newMap();

        map.forEach((u, v) -> reverseMap.put(v, u));

        return reverseMap;
    }

    public static <U, V> Map<U, V> combine(Map<U, V> map1, Map<U, V> map2){

        Map<U, V> combinedMap = CollectionUtils.newMap();

        map1.forEach(combinedMap::put);
        map2.forEach(combinedMap::put);

        return combinedMap;
    }

    static ColoredPrinter cp = new ColoredPrinter.Builder(1, false).build();

    public static<U, V, W> Object runAndTime(BiFunction<U, V, W> function, U u, V v, String  message){

        cp.setAttribute(Ansi.Attribute.BOLD);

        long start, end;

        start = System.currentTimeMillis();

        Object w =  function.apply(u, v);

        end = System.currentTimeMillis();


        cp.println("Timing for: " + message  +  (end - start)/1000 + " s");

        if(w!=null){
            cp.println(w);

        } else {

            cp.setForegroundColor(Ansi.FColor.RED);
            cp.println("COULD NOT PLAN");
        }

        cp.clear();
        return  w;
    }

    public static And makeAnd(Set<Formula> formulae){

        return new And(new ArrayList<>(formulae));
    }
}

