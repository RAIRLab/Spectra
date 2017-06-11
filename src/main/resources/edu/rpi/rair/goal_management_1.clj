{:definitions
        {:name       "demo 1"


         :background [(forall [?x ?room1 ?room2]
                              (if (not (= ?room1 ?room2))
                                (if (in ?x ?room1) (not (in ?x ?room2)))))
                      (not (= room1 room2))
                      (not (= prisoner commander))
                      (not (= self prisoner))
                      (not (= self commander))
                      (person prisoner)
                      (imprisoned prisoner)
                      (person commander)]



         :start      [(in self room1)
                      (in commander room2)
                      (in prisoner room1)
                      (open (door room2))
                      (not (open (door room1)))]

         :goal       []

         :actions
                     [(define-action open-door [?room]
                                     {:preconditions [(not (open (door ?room)))]
                                      :additions     [(open (door ?room))]
                                      :deletions     [(not (open (door ?room)))]})



                      (define-action accompany [?person ?room1 ?room2]
                                     {:preconditions [(not (= ?room1 ?room2))
                                                      (in ?person ?room1)
                                                      (in self ?room1)
                                                      (open (door ?room1))
                                                      (open (door ?room2))]

                                      :additions     [(in ?person ?room2)
                                                      (in self ?room2)]

                                      :deletions     [(in ?person ?room1)
                                                      (in self ?room1)]})

                      (define-action move [?person ?room2 ?room1]
                                     {:preconditions [(not (= ?room1 ?room2))
                                                      (in ?person ?room2)
                                                      (open (door ?room1))
                                                      (open (door ?room2))]

                                      :additions     [(in ?person ?room1)]

                                      :deletions     [(in ?person ?room2)]})

                      (define-action interrogate [?p ?room]
                                     {:preconditions [(in commander ?room)
                                                      (in ?p ?room)
                                                      (imprisoned ?p)]

                                      :additions     [(interrogates commander ?p)]

                                      :deletions     []})

                      (define-action stay-put [?x ?y]
                                     {:preconditions [(sameroom ?x ?y)]

                                      :additions     [(sameroom ?x ?y)]

                                      :deletions     []})
                      ]
         }

 :goals {G1 {:priority 10.0
             :state    [(not (open (door room1)))]}

         G2 {:priority 10.0
             :state    [(in prisoner room1)]}

         G3 {:priority 10.0
             :state    [(forall [?room]
                                (if (in prisoner ?room)
                                  (in self ?room)))]}
         G4 {:priority 3.0
             :state    [(in prisoner room2)
                        (in self room2)]}
         G5 {:priority 1.0

             :state    [(interrogates commander prisoner)]}}

 }