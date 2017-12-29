# Spectra

Spectra is a general purpose planning system. It extends STRIPS-style planning by allowing arbitray first-order formulae for state descriptions and background knowledge rather than just predicates. This allows, for instance, handling domains with infinite or unbounded objects elegantly (among other things). 

[Overview Presentation (pdf)](https://drive.google.com/open?id=1RHulFDgASACBkjvl-8ZEidj50NbGmKPu)

* Existing Planners are propositional 
* Drawbacks: 
 * **Expressivity**: Cannot express arbitrary constraints.  *“At every step make sure that no two blocks on the table have same color”*
   * **Domain Size**: Scaling to large domains of arbitrary sizes poses difficulty. 

# Architecture #

 
![spectra-arch.png](https://bitbucket.org/repo/Mjq4bX/images/2495888298-spectra-arch.png)

# Example #

![examples.png](https://bitbucket.org/repo/Mjq4bX/images/3136509575-examples.png)
# Scaling Up #

Two approaches: 

1. **Procedural Attachments**: Special purpose procedural code that can bypass strict formal reasoning.

2. *μ*-**methods**: Written in denotational proof language. Preserves soundness by letting us write down commonly used patterns of reasoning (a bit unwieldy integration now than the first approach)



```clojure
;; (removeFrom  ?x ?y) => "Remove ?x from ?y"
;; (placeInside  ?x ?y) ==> "Place ?x inside ?y"
(define-method planMethod [?b ?c ?d]
  {:goal [(In ?b ?c) (In ?c ?d)]
   :while [(In ?b ?d) (Empty ?c)
           (< (size ?c) (size ?d))
           (< (size ?b) (size ?c))]
   :actions [(removeFrom  ?b ?d) (placeInside  ?b ?c) (placeInside  ?c ?d)]})
Roughly, a method has conditions that the goal and background + start state should satisfy. If the conditions are satisfied, a plan template is generated (note the variables).
The planner then verifies if the plan template works, if so it outputs the plan. 
```
