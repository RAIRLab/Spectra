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
                     (room hallway)
                     (not (= room1 room2))
                     (not (= room1 hallway))
                     (not (= room2 hallway))

                     (in prisoner room1)
                     (in commander room2)
                     (in guard room1)
                     (in guide hallway)

                     (not (open (door room1)))
                     (open (door room2))
                     (open (door hallway))

                      (forall [?x ?y] (if (accompanies ?x ?y) (accompanies ?y ?x)))
                      (forall (?room ?x ?y) (implies (and (in ?x ?room) (and (in ?y ?room) (not (= ?x ?y)))) (sameroom ?x ?y)))
                      (forall (?room2 ?x ?room1) (implies (and (not (= ?room1 ?room2)) (in ?x ?room1)) (not (in ?x ?room2))))
                      (forall (?room ?x ?y) (implies (and (sameroom ?x ?y) (in ?x ?room)) (in ?y ?room)))
                      ]

         :goal       []

         :actions
                     [

                      (define-action move [?actor ?room1 ?room2]
                                     {:preconditions [(robot ?actor)
                                                      (room ?room1)
                                                      (room ?room2)
                                                      (in ?actor ?room1)
                                                      (open (door ?room1))
                                                      (open (door ?room2))
                                                      (not (= ?room1 ?room2))]
                                      :additions     [(in ?actor ?room2)]
                                      :deletions     [(in ?actor ?room1)]
                                      })

                      (define-action keepDoorClosed [?actor ?room]
                                     {:preconditions [(robot ?actor)
                                                      (room ?room)
                                                      (in ?actor ?room)
                                                      (not (open (door ?room)))]
                                      :additions     []
                                      :deletions     [(open (door ?room))]
                                      })

                      (define-action accompany [?actor ?person1 ?person2 ?room1 ?room2]
                                     {:preconditions [(robot ?actor)
                                                      (person ?person1)
                                                      (room ?room1)
                                                      (room ?room2)
                                                      (in ?actor ?room1)
                                                      (in ?person1 ?room1)
                                                      (in ?person2 ?room2)
                                                      (open (door ?room1))
                                                      (open (door ?room2))
                                                      (not (= ?actor ?person1))
                                                      (not (= ?actor ?person2))
                                                      (not (= ?room1 ?room2))]
                                      :additions     [(accompanies ?person1 ?person2)
                                                      (in ?actor ?room2)
                                                      (in ?person1 ?room2)]
                                      :deletions     [(in ?actor ?room1)
                                                      (in ?person1 ?room1)]
                                      })

                      (define-action accompany2 [?actor ?person1 ?person2]
                                     {:preconditions [(robot ?actor)
                                                      (person ?person1)
                                                      (person ?person2)
                                                      (in ?actor ?room)
                                                      (in ?person1 ?room)
                                                      (in ?person2 ?room)
                                                      (not (= ?actor ?person1))
                                                      (not (= ?actor ?person2))
                                                      (not (= ?person1 ?person2))]
                                      :additions     [(accompanies ?actor ?person1 ?person2)]
                                      :deletions     []
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

         G4 {:priority 3.0
             :state    [(accompanies prisoner commander)]}
         }

 }