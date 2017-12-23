{:definitions
    {:name       "Blockworld"

     :background []

     :start      [(Clear A)
                  (Clear B)
                  (Clear C)
                  (On A Table)
                  (On B Table)
                  (On C Table)]

     :goal       []

     :actions  [(define-action stack [?x ?y]
                                 {:preconditions [(On ?x Table)
                                                  (On ?y Table)
                                                  (Clear ?x)
                                                  (Clear ?y)]
                                  :additions     [(On ?x ?y)]
                                  :deletions     [(Clear ?y)
                                                  (On ?x Table)]})

                (define-action unstack [?x ?y]
                                 {:preconditions [(On ?x ?y)
                                                  (Clear ?x)]
                                  :additions     [(On ?x Table)
                                                  (Clear ?y)]
                                  :deletions     [(On ?x ?y)]})]
     }

    :goals {G1 {:priority 1.0
                :state    [(On A B)]}
            G2 {:priority 1.0
                :state    [(On C A)]}}
}
