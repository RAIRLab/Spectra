{:name           "test 1"
 :background     [p]
 :start          [q]
 :goal           [r]
 :actions
                 [(define-action a1 ()
                                 {:preconditions [(or q r)]
                                  :additions     [r]
                                  :deletions     [q]})]

 :expected-plans ([a1])
 }



{:name           "simple killing"
 :background     []
 :start          [(forall ?x (alive ?x))]
 :goal           [(forall ?x (dead ?x))]
 :actions
                 [(define-action kill ()
                                 {:preconditions [(alive ?x)]
                                  :additions     [(dead ?x)]
                                  :deletions     [(alive ?x)]})]

 :expected-plans ([kill])

 }


{:name           "thirsty"
 :background     []
 :start          [thirsty]
 :goal           [(not thirsty)]
 :actions
                 [(define-action drink ()
                                 {:preconditions [thirsty]
                                  :additions     [(not thirsty)]
                                  :deletions     [thirsty]})
                  (define-action eat ()
                                 {:preconditions [hungry]
                                  :additions     [(not hungry)]
                                  :deletions     [hungry]})]

 :expected-plans ([drink])

 }


{:name           "hungry"
 :background     []
 :start          [hungry]
 :goal           [(not hungry)]
 :actions

                 [(define-action drink ()
                                 {:preconditions [thirsty]
                                  :additions     [(not thirsty)]
                                  :deletions     [thirsty]})

                  (define-action eat ()
                                 {:preconditions [hungry]
                                  :additions     [(not hungry)]
                                  :deletions     [hungry]})]

 :expected-plans ([eat])
 }


{:name           "hungry and thirsty"
 :background     []
 :start          [hungry thirsty]
 :goal           [(not (or hungry thirsty))]
 :actions
                 [(define-action drink ()
                                 {:preconditions [thirsty]
                                  :additions     [(not thirsty)]
                                  :deletions     [thirsty]})

                  (define-action eat ()
                                 {:preconditions [hungry]
                                  :additions     [(not hungry)]
                                  :deletions     [hungry]})]

 :expected-plans ([eat drink]
                   [drink eat])
 }

{:name           "hungry and thirsty"
 :background     []
 :start          [hungry thirsty work-unfinished]
 :goal           [work-finished]
 :actions

                 [(define-action drink ()
                                 {:preconditions [thirsty]
                                  :additions     [(not thirsty)]
                                  :deletions     [thirsty]})

                  (define-action eat ()
                                 {:preconditions [hungry]
                                  :additions     [(not hungry)]
                                  :deletions     [hungry]})

                  (define-action work ()
                                 {:preconditions [(and (not hungry) (not thirsty))]
                                  :additions     [work-finished]
                                  :deletions     [work-unfinished]})]

 :expected-plans ([eat drink work]
                   [drink eat work])}




{:name           "demo 1"
 :background     [
                  (not (= room1 room2))
                  (not (= prisoner commander))
                  (not (= self prisoner))
                  (not (= self commander))
                  (person prisoner)
                  (person commander)
                  ]
 :start          [(in self room1)
                  (in commander room2)
                  (in prisoner room1)
                  (open (door room2))
                  (not (open (door room1)))]

 :goal           [(interrogates commander prisoner)]

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

                  (define-action get-interrogated [?room]
                                 {:preconditions [(in commander ?room)
                                                  (in prisoner ?room)]

                                  :additions     [(interrogates commander prisoner)]

                                  :deletions     []})
                  ]

 :expected-plans ([(open-door room1)
                    (move commander room2 room1)
                    (get-interrogated room1)]

                   [(open-door room1)
                    (move prisoner room1 room2)
                    (get-interrogated room2)]

                   [(open-door room1)
                    (accompany prisoner room1 room2)
                    (get-interrogated room2)])}


