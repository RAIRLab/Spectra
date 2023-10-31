package org.rairlab.planner;

import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.formula.Predicate;
import org.rairlab.shadow.prover.representations.value.Compound;
import org.rairlab.shadow.prover.representations.value.Constant;
import org.rairlab.shadow.prover.representations.value.Value;

import java.util.Arrays;
import java.util.function.Function;

public class Simplifier {


    public static Formula simplify(Formula input){

        if(input instanceof Predicate){

            Value[] args = ((Predicate) input).getArguments();
            Value[] outputArgs = new Value[args.length];

            for(int i = 0; i < args.length; i++){

                outputArgs[i] = simplify(args[i]);
            }

            return new Predicate(((Predicate) input).getName(), outputArgs);

        }

        return input;
    }

    public static Value simplify(Value input){


        if(input instanceof Compound && input.getName().equals("remove")){


            Value item = input.getArguments()[0];
            Value list = input.getArguments()[1];



            Value answer = removeFromCons(item, list);

            return answer;



        } else {

            return input;
        }

    }

    private static Value removeFromCons(Value item, Value list) {


        if(list.equals(new Constant("el"))){

            return new Constant("el");
        }
        else if (list instanceof Compound && list.getName().equals("cons")) {

            if(list.getArguments()[0].equals(item)){

                return removeFromCons(item, list.getArguments()[1]);

            } else {


                return new Compound("cons", new Value[]{list.getArguments()[0], removeFromCons(item, list.getArguments()[1])});

            }
        }

        return list;


    };


}
