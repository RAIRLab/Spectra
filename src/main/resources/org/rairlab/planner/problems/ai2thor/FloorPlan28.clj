  {:name       "FloorPlan28"

         :background [ ;; Transitivity of <
                      (forall [?x ?y ?z]
                              (if (and (< (size ?x) (size ?y))
                                       (< (size ?y) (size ?z)))
                                (< (size ?x) (size ?z))))

                      ;; Transitivity of Contains
                      (forall [?x ?y ?z]
                              (if (and (Contains ?x ?y) (Contains ?y ?z))
                                (Contains ?x ?z)))
                      ;; Asymmetry of <
                      (forall [?x ?y]
                              (iff (< (size ?x) (size ?y))
                                   (not (< (size ?y) (size ?x)))))

                      ;; If there is something inside a cup, it is not empty.
                      (forall [?y]
                              (if (exists [?x] (In ?x ?y))
                                (not (Empty ?y))))

                      ;; Define Contains
                      (forall [?x ?y]
                              (if (In ?x ?y)
                                (Contains ?y ?x)))

                      ;;; Sizes of cups
                      (< (size knife) (size mug))
                      (< (size mug) (size microwave))
                      (CanPlaceInside mug microwave)
                      (CanPlaceInside knife mug)

                      ]


         :start      [(In mug coffeemachine)
                      (Empty mug)
                      (Empty microwave)]


         :goal       [
                       (Contains microwave knife)

                       ]

   :context { :work-from-scratch false
              :plan-methods
              [(define-method planMethod [?b  ?d  ?c]
                 {:goal    [(In ?b ?c)  (In ?c ?d)]
                  :while   [(< (size  ?c) (size  ?d))  (< (size  ?b) (size  ?c))  (In ?b ?d)  (Empty ?c)]
                  :actions [(removeFrom  ?b ?d)  (placeInside  ?b ?c)  (placeInside  ?c ?d)]})]}

         :actions  [(define-action placeInside [?x ?y]
                                     {:preconditions [(< (size ?x) (size ?y))
                                                      (CanPlaceInside ?x ?y)
                                                      (Empty ?y)]
                                      :additions     [(In ?x ?y)]
                                      :deletions     [(Empty ?y)]})

                      (define-action removeFrom [?x ?y]
                                     {:preconditions [(In ?x ?y)]
                                      :additions     [(Empty ?y)]
                                      :deletions     [(In ?x ?y)]})]}
