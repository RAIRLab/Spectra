;; (removeFrom  ?x ?y) => "Remove ?x from ?y"
;; (placeInside  ?x ?y) ==> "Place ?x inside ?y"

(define-method planMethod [?b  ?d  ?c]
  {:goal    [(In ?b ?c)  (In ?c ?d)]
   :while   [(< (size  ?c) (size  ?d))  (< (size  ?b) (size  ?c))  (In ?b ?d)  (Empty ?c)]
   :actions [(removeFrom  ?b ?d)  (placeInside  ?b ?c)  (placeInside  ?c ?d)]})