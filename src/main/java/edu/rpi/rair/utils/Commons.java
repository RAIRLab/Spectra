package edu.rpi.rair.utils;

import clojure.lang.Obj;
import com.naveensundarg.shadow.prover.representations.formula.And;
import com.naveensundarg.shadow.prover.representations.formula.Existential;
import com.naveensundarg.shadow.prover.representations.formula.Formula;
import com.naveensundarg.shadow.prover.representations.formula.Universal;
import com.naveensundarg.shadow.prover.representations.value.Value;
import com.naveensundarg.shadow.prover.representations.value.Variable;
import com.naveensundarg.shadow.prover.utils.CollectionUtils;
import com.naveensundarg.shadow.prover.utils.Sets;
import edu.rpi.rair.State;
import us.bpsm.edn.parser.Parseable;

import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
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

    public static Formula generalize(Map<Value, Variable> universalValueVariableMap, Set<Formula> formulae){

        Set<Value> allValues = formulae.stream().map(Formula::valuesPresent).reduce(Sets.newSet(), Sets::union).stream().filter(x->x.isConstant()).collect(Collectors.toSet());

        Map<Value, Variable> existentialValueVariableMap = makeVariables(Sets.difference(allValues, universalValueVariableMap.keySet()), universalValueVariableMap.size() + 1);


        Map<Value, Variable> combinedMap  = combine((universalValueVariableMap), (existentialValueVariableMap));
        Formula f = (new And(formulae.stream().collect(Collectors.toList()))).generalize(combinedMap);

        Variable[] universalVars =  new Variable[universalValueVariableMap.values().size()];
        universalVars = universalValueVariableMap.values().toArray(universalVars);

        Variable[] existentialVars =  new Variable[existentialValueVariableMap.values().size()];
        existentialVars = existentialValueVariableMap.values().toArray(existentialVars);


        System.out.println(new Universal(universalVars, new Existential(existentialVars, f)));
        return new Universal(universalVars, new Existential(existentialVars, f));
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

    public static<U, V, W> Object runAndTime(BiFunction<U, V, W> function, U u, V v, String  message){

        long start, end;

        start = System.currentTimeMillis();

        Object w =  function.apply(u, v);

        end = System.currentTimeMillis();


        System.out.println("Timing for: " + message  +  (end - start)/1000 + " s");

        return  w;
    }
}

