# Spectra




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
