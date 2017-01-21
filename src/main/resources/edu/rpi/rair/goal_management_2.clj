{:definitions
        {:name       "test"
         :background []
         :start      [(forall [?x ?room1 ?room2] (implies (and (not (= ?room1 ?room2)) (in ?x ?room1)) (not (in ?x ?room2))))
                      (not (= room1 room2))
                      (not (= prisoner commander))
                      (not (= self prisoner))
                      (not (= self commander))
                      (person prisoner)
                      (person commander)
                      (in self room1)
                      (in commander room2)
                      (in prisoner room1)
                      (open (door room2))
                      (not (open (door room1)))

                      (forall [?x ?y ?room] (implies (and (and (in ?x ?room) (in ?y ?room)) (not (= ?x ?y))) (sameroom ?x ?y)))
                      (forall [?x ?y] (implies (sameroom ?x ?y) (exists ?room (and (in ?x ?room) (in ?y ?room)))))
                      ]

         :goal       []

         :actions
                     [(define-action open-door [?room]
                                     {:preconditions [(not (open (door ?room)))]
                                      :additions     [(open (door ?room))]
                                      :deletions     [(not (open (door ?room)))]})



                      (define-action accompany [?person ?room1 ?room2]
                                     {:preconditions [(in ?person ?room1)
                                                      (in self ?room1)
                                                      (open (door ?room1))
                                                      (open (door ?room2))]

                                      :additions     [(in ?person ?room2)
                                                      (in self ?room2)]

                                      :deletions     [(in ?person ?room1)
                                                      (in self ?room1)]})

                      (define-action move [?person ?room2 ?room1]
                                     {:preconditions [(in ?person ?room2)
                                                      (open (door ?room1))
                                                      (open (door ?room2))]

                                      :additions     [(in ?person ?room1)]

                                      :deletions     [(in ?person ?room2)]})

                      (define-action get-interrogated [?room]
                                     {:preconditions [(in commander ?room)
                                                      (in prisoner ?room)]

                                      :additions     [(interrogates commander prisoner)]

                                      :deletions     []})

                     (define-action open-door [?room ?actor]
                                 {:preconditions [(not (open (door ?room)))]
                                  :additions     [(open (door ?room))
                                                  (did (open-door ?actor ?room))]
                                  :deletions     [(not (open (door ?room)))]})]
         }


 :goals {
         G2 {:priority 2.0
             :state    [(sameroom prisoner room2)]}
         }
 }
