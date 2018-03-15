{:definitions
 {:name       "heroism"
  :background [(forall [?x ?y]
                       (iff (and (Inside ?x ?y) (OnFire ?y))
                            (not (Safe ?x))))
               (forall [?x ?y]
                       (iff (not (Inside ?x ?y))
                            (Outside ?x ?y)))
               (not (= self stranger))]
  :start      [(Inside stranger building)
               (Outside self building)]

  :actions    [(define-action movePersonFrom [?person ?building]
                 {:preconditions [(Inside self ?building)]
                  :additions     [(Outside stranger ?building) (Outside self ?building)]
                  :deletions     [(Inside stranger ?building) (Inside self ?building)]})

               (define-action moveInside [?building]
                 {:preconditions [(Outside self ?building)]
                  :additions [(Inside self ?building)]
                  :deletions [(Outside self ?building)]})]}

 :goals {G1 {:priority 5.0
             :state    [(Safe self)]}

         G2 {:priority 1.0
             :state    [(Safe stranger)]}}}