package org.rairlab.planner.utils;

import org.rairlab.shadow.prover.representations.formula.Formula;
import org.rairlab.shadow.prover.representations.value.Compound;
import org.rairlab.shadow.prover.representations.value.Variable;
import org.rairlab.shadow.prover.utils.CollectionUtils;
import org.rairlab.planner.PlanMethod;
import us.bpsm.edn.Keyword;
import us.bpsm.edn.Symbol;
import us.bpsm.edn.parser.Parseable;
import us.bpsm.edn.parser.Parser;
import us.bpsm.edn.parser.Parsers;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Reader {


    private static final Keyword GOAL = Keyword.newKeyword("goal");
    private static final Keyword ACTIONS = Keyword.newKeyword("actions");
    private static final Keyword WHILE = Keyword.newKeyword("while");

    public static List<PlanMethod> readPlanMethodsFrom(InputStream inputStream) throws org.rairlab.shadow.prover.utils.Reader.ParsingException {

        Parseable parseable = Parsers.newParseable(new InputStreamReader(inputStream));
        Parser parser = Parsers.newParser(Parsers.defaultConfiguration());



       List<PlanMethod> planMethods = CollectionUtils.newEmptyList();


       Object current = parser.nextValue(parseable);
        while(current!=Parser.END_OF_INPUT){

           planMethods.add(readPlanMethodFrom((List<?>) current));
            current = parser.nextValue(parseable);
       }

       return planMethods;
    }


    // (def planMethod [?x ?y ?z] {:goal [...] :action [....])
    public static PlanMethod readPlanMethodFromString(String stringSpec) throws org.rairlab.shadow.prover.utils.Reader.ParsingException {

        Parseable parseable = Parsers.newParseable(new StringReader(stringSpec));
        Parser parser = Parsers.newParser(Parsers.defaultConfiguration());

        Object specObj = parser.nextValue(parseable);
        return readPlanMethodFrom((List<?>) specObj);
    }

    // (def planMethod [?x ?y ?z] {:goal [...] :action [....])
    public static PlanMethod readPlanMethodFrom(List<?> planMethodSpec) throws org.rairlab.shadow.prover.utils.Reader.ParsingException {

        Object  command = planMethodSpec.get(0);

        if(!command.toString().equals("define-method")){
            throw new AssertionError("Malformed method definition. Was expecting a 'define-method' but got "+ command);
        }
        Object  name = planMethodSpec.get(1);
        List<Symbol>  variableObjs =  (List<Symbol>)  planMethodSpec.get(2);

        List<Variable> variables = CollectionUtils.newEmptyList();
        for(Symbol varSym : variableObjs){

            variables.add((Variable) org.rairlab.shadow.prover.utils.Reader.readLogicValueFromString(varSym.toString()));

        }

        Map<?, ? > body =  (Map<?, ?>)  planMethodSpec.get(3);

        Set<Formula> goalPreconds = CollectionUtils.newEmptySet();
        Set<Formula> whilePreconds = CollectionUtils.newEmptySet();

        List<Compound> actionCompounds = CollectionUtils.newEmptyList();

        List<?> goalPrecondSpecs =  (List<?>) body.get(GOAL);

        for(Object goalPrecondSpec : goalPrecondSpecs){

            goalPreconds.add(org.rairlab.shadow.prover.utils.Reader.readFormula(goalPrecondSpec));
        }


        List<?> whilePrecondSpecs =  (List<?>) body.get(WHILE);

        for(Object whilePrecondSpec : whilePrecondSpecs){

            whilePreconds.add(org.rairlab.shadow.prover.utils.Reader.readFormula(whilePrecondSpec));
        }



        List<?> actionPrecondSpecs =  (List<?>) body.get(ACTIONS);

        for(Object actionPrecondSpec : actionPrecondSpecs){

            actionCompounds.add((Compound) org.rairlab.shadow.prover.utils.Reader.readLogicValue(actionPrecondSpec));
        }

        return new PlanMethod(goalPreconds, whilePreconds, variables, actionCompounds);
    }

}
