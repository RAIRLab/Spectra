{:name          "test 1"
 :background    [P]
 :start         [Q]
 :goal          [R]
 :actions
                [(define-action a1 ()
                                {:preconditions [(or Q R)]
                                 :additions     [R]
                                 :deletions     [Q]})]

 :expected-plans ([a1])
 }



{:name           "simple killing"
 :background     []
 :start          [(forall ?x (Alive ?x))]
 :goal           [(forall ?x (Dead ?x))]
 :actions
                 [(define-action kill ()
                                 {:preconditions [(Alive ?x)]
                                  :additions     [(Dead ?x)]
                                  :deletions     [(Alive ?x)]})]

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


{:name       "bidding problem"
 :background []
 :start      [(bid 0)]
 :goal       [(bid 5)]
 :actions
             [(define-action post-new-bid (?number)
                             {:preconditions [(bid ?number)]
                              :additions     [(bid ($$sum 1 ?number))]
                              :deletions     [(bid ?number)]})]

 :expected-plans ([(post-new-bid 0)
                   (post-new-bid 1)
                   (post-new-bid 2)
                   (post-new-bid 3)
                   (post-new-bid 4)])}

{:name       "Moving Between Rooms"
 :background [ (not (= room1 room2))]
 :start      [(In self room1)
              (In commander room2)
              (In prisoner room1)
              (Open (door room2))
              (not (Open (door room1))) ]
 :goal       [(In prisoner room2)]
 :actions
             [(define-action open-door [?room]
                             {:preconditions [(not (Open (door ?room)))]
                              :additions [(Open (door ?room))]
                              :deletions [(not (Open (door ?room)))]})

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
                                              (In self ?room1)]})]

 :expected-plans ([(open-door room1)
                   (accompany-from-to prisoner room1 room2)]

                   [(open-door room1)
                   (move-thing-from-to prisoner room1 room2)])

 }

