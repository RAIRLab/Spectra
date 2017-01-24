{:definitions
        {:name       "demo 1"
         :background []
         :start      [
                     (person prisoner)
                     (person commander)
                     (commander commander)
                     (robot guard)
                     (robot guide)

                     (not (= prisoner commander))
                     (not (= prisoner guard))
                     (not (= prisoner guide))
                     (not (= commander guard))
                     (not (= commander guide))
                     (not (= guard guide))

                     (room room1)
                     (room room2)
                     (not (= room1 room2))

                     (in prisoner room1)
                     (in commander room2)
                     (in guard room1)
                     (in guide room2)

                     (not (open (door room1)))
                     (open (door room2))

                     (forall (?room ?x ?y) (implies (and (in ?x ?room) (in ?y ?room)) (sameroom ?x ?y)))
                     (forall (?room2 ?x ?room1) (implies (and (not (= ?room1 ?room2)) (in ?x ?room1)) (not (in ?x ?room2))))
                     (forall (?room ?x ?y) (implies (and (sameroom ?x ?y) (in ?x ?room)) (in ?y ?room)))
                      ]

         :goal       []

         :actions
                     [

                      (define-action move [?actor ?person ?room2 ?room1]
                                     {:preconditions [(robot ?actor)
                                                      (person ?person)
                                                      (room ?room1)
                                                      (room ?room2)
                                                      (not (= ?room1 ?room2))
                                                      (open (door ?room1))
                                                      (in ?actor ?room2)
                                                      (in ?person ?room2)
                                                      (open (door ?room2))]
                                      :additions     [(in ?person ?room1)]
                                      :deletions     [(in ?person ?room2)]
                                      })
                      (define-action keepDoorClosed [?actor ?room]
                                     {:preconditions [(robot ?actor)
                                                      (room ?room)
                                                      (in ?actor ?room)
                                                      (not (open (door ?room)))]
                                      :additions     []
                                      :deletions     [(open (door ?room))]
                                      })
                      (define-action accompany [?actor ?person ?room1 ?room2]
                                     {:preconditions [(robot ?actor)
                                                      (person ?person)
                                                      (room ?room1)
                                                      (room ?room2)
                                                      (not (= ?room1 ?room2))
                                                      (open (door ?room1))
                                                      (in ?person ?room1)
                                                      (open (door ?room2))
                                                      (not (= ?person ?actor))
                                                      (in ?actor ?room1)]
                                      :additions     [(in ?actor ?room2)
                                                      (in ?person ?room2)]
                                      :deletions     [(in ?person ?room1)
                                                      (in ?actor ?room1)]
                                      })
                      (define-action openDoor [?actor ?room]
                                     {:preconditions [(robot ?actor)
                                                      (room ?room)
                                                      (in ?actor ?room)
                                                      (not (open (door ?room)))]
                                      :additions     [(open (door ?room))]
                                      :deletions     [(not (open (door ?room)))]
                                      })
                      (define-action stayInRoom [?actor ?room]
                                     {:preconditions [(in ?actor ?room)
                                                      (room ?room)]
                                      :additions     [(in ?actor ?room)]
                                      :deletions     []
                                      })
                      (define-action interrogate [?actor ?person]
                                     {:preconditions [(person ?person)
                                                      (commander ?actor)
                                                      (in ?actor ?room)
                                                      (in ?person ?room)
                                                      (room ?room)]
                                      :additions     [(interrogates ?actor ?person)]
                                      :deletions     []
                                      })
                      (define-action staySameRoom [?actor ?person]
                                     {:preconditions [(robot ?actor)
                                                      (sameroom ?actor ?person)
                                                      (person ?person)
                                                      (in ?actor ?room)
                                                      (in ?person ?room)
                                                      (room ?room)]
                                      :additions     [(sameroom ?actor ?person)]
                                      :deletions     []
                                      })

                      ]
         }

 :goals {G1 {:priority 1.0
             :state    [(not (open (door room1)))]}

         G2 {:priority 1.0
             :state    [(in prisoner room1)]}

         G3 {:priority 1.0
             :state    [(sameroom guard prisoner)]}

         G4 {:priority 6.0
             :state    [(interrogates commander prisoner)]}
         }

 }