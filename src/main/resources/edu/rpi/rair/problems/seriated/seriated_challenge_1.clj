
 {:definitions
        {:name       "Seriated Cup Challenge 1"

         :background [ ;; Transitivity of <
                      (forall [?x ?y ?z]
                              (if (and (< (size ?x) (size ?y))
                                       (< (size ?y) (size ?z)))
                                (< (size ?x) (size ?z))))
                      ;; Asymmetry of <
                      (forall [?x ?y]
                              (iff (< (size ?x) (size ?y))
                                   (not (< (size ?y) (size ?x)))))

                      ;; If there is something inside a cup, it is not empty.
                      (forall [?y]
                              (if (exists [?x] (In ?x ?y))
                                (not (Empty ?y))))

                      ;;; Sizes of cups
                      (< (size a) (size b))
                      (< (size b) (size c))
                      (< (size c) (size d))
                      (< (size d) (size e))
                      (< (size e) (size f))
                      (< (size f) (size g))
                      (< (size g) (size h))]


         :start      [(In a b)
                      (In b d)
                       (Empty c)]


         :goal       [ ]

         :actions  [(define-action placeInside [?x ?y]
                                     {:preconditions [(< (size ?x) (size ?y))
                                                      (Empty ?y)]
                                      :additions     [(In ?x ?y)]
                                      :deletions     [(Empty ?y)]})

                      (define-action removeFrom [?x ?y]
                                     {:preconditions [(In ?x ?y)]
                                      :additions     [(Empty ?y)]
                                      :deletions     [(In ?x ?y)]})]}

 :goals {G1 {:priority 1.0
             :state    [(In a b)
                        (In b c)
                        (In c d)]}}


;;(removeFrom  b d)  (placeInside  b c)  (placeInside  c d)


 }
