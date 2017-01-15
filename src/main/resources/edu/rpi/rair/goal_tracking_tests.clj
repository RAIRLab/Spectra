{:definitions
 {:name       "Moving Between Rooms"
  :background [(not (= room1 room2))
               (forall [?x] (iff (Locked ?x) (not (Open ?x))))
               (forall [?x ?y ?z] (if (and (In ?x ?y) (not (= ?z ?y)))
                                    (not (In ?x ?z))))]
  :start      [(In self room1)
               (In commander room2)
               (In prisoner room1)
               (Open (door room2))
               (not (Open (door room1)))]
  :goal []
  :actions
              [(define-action open-door [?room]
                              {:preconditions [(not (Open (door ?room)))]
                               :additions     [(Open (door ?room))]
                               :deletions     [(not (Open (door ?room)))]})

               (define-action move-thing-from-to [?thing ?room1 ?room2]
                              {:preconditions [(not (= ?room1 ?room2))
                                               (In ?thing ?room1)
                                               (Open (door ?room1))
                                               (Open (door ?room2))]

                               :additions     [(In ?thing ?room2)]
                               :deletions     [(In ?thing ?room1)
                                               (In self ?room1)]})
               (define-action accompany-from-to [?thing ?room1 ?room2]
                              {:preconditions [(not (= ?room1 ?room2))
                                               (In self ?room1)
                                               (In ?thing ?room1)
                                               (Open (door ?room1))
                                               (Open (door ?room2))]

                               :additions     [(In ?thing ?room2)
                                               (In ?self ?room2)]
                               :deletions     [(In ?thing ?room1)
                                               (In self ?room1)]})

               (define-action interrogate [?A ?B]
                              {:preconditions [(In ?room ?A)
                                               (In ?room ?B)]

                               :additions     [(Interrogates ?A ?B)]
                               :deletions     [(In ?thing ?room1)
                                               (In self ?room1)]})]
}

  :goals      {G1 {:priority 1
                   :state    [(not (Open (door room1)))]}

               G2 {:priority 1
                   :state    [(In prisoner room1)]}

               G3 {:priority 1
                   :state    [(forall [?room]
                                      (if (In prisoner ?room)
                                        (In self ?room)))]
                   }

               }

 }