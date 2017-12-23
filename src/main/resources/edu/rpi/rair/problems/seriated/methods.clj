;; (removeFrom  ?x ?y) => "Remove ?x from ?y"
;; (placeInside  ?x ?y) ==> "Place ?x inside ?y"

(define-method planMethod [?b ?c ?d]
  {:goal [(In ?b ?c) (In ?c ?d)]
   :while [(In ?b ?d) (Empty ?c)
           (< (size ?c) (size ?d))
           (< (size ?b) (size ?c))]
   :actions [(removeFrom  ?b ?d)  (placeInside  ?b ?c)  (placeInside  ?c ?d)]})